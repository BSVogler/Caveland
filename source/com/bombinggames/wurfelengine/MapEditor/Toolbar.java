/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.bombinggames.wurfelengine.Command;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 * A toolbar for the editor.
 *
 * @author Benedikt Vogler
 */
public class Toolbar extends Window {

	private final Cursor cursor;

	/**
	 * a enum listing the available tools
	 */
	public static enum Tool {

		/**
		 * tool to draw blocks
		 */
		DRAW(0, "draw_button", true, false, true),
		/**
		 * tool to cover an area with blocks
		 */
		BUCKET(1, "bucket_button", true, false, false),
		/**
		 * "repaints" blocks
		 */
		REPLACE(2, "replace_button", true, false, false),
		/**
		 * select and move entities
		 */
		SELECT(3, "pointer_button", false, false, false),
		/**
		 * spawn new entities
		 */
		SPAWN(4, "entity_button", false, true, true),
		/**
		 * replace blocks with air
		 */
		ERASE(5, "eraser_button", false, false, false);

		private final int id;
		private final String name;
		private final boolean selectFromBlocks;
		private final boolean selectFromEntities;
		private final boolean showNormal;

		private Tool(int id, String name, boolean worksOnBlocks, boolean worksOnEntities, boolean showNormal) {
			this.id = id;
			this.name = name;
			this.selectFromBlocks = worksOnBlocks;
			this.selectFromEntities = worksOnEntities;
			this.showNormal = showNormal;
		}

		/**
		 *
		 * @return
		 */
		public int getId() {
			return id;
		}

		/**
		 *
		 * @return
		 */
		public boolean selectFromBlocks() {
			return selectFromBlocks;
		}

		/**
		 *
		 * @return
		 */
		public boolean selectFromEntities() {
			return selectFromEntities;
		}
		
		public Command getCommand(GameView view, Cursor selection, PlacableGUI placableGUI){
			if (null != this) switch (this) {
				case DRAW:
					return new Command() {
						private Coordinate coord;
						private Block previous;
						private Block block;
						
						public void init(){
							
						}
						
						@Override
						public void execute() {
							if (coord==null) {
								coord = selection.getCoordInNormalDirection();
								block = placableGUI.getBlock();
								previous = coord.getBlock();
							}
							Controller.getMap().setBlock(coord, block);
						}
						
						@Override
						public void undo() {
							Controller.getMap().setBlock(coord, previous);
						}
					};
				case REPLACE:
					return new Command() {
						private Coordinate coord;
						private Block previous;
						private Block block;
						
						@Override
						public void execute() {
							if (coord==null) {
								coord = selection.getPosition().toCoord();
								block = placableGUI.getBlock();
								previous = coord.getBlock();
							}
							Controller.getMap().setBlock(coord, block);
						}
						
						@Override
						public void undo() {
							Controller.getMap().setBlock(coord, previous);
						}
					};
				case SPAWN:
					return new Command() {
						private AbstractEntity ent = null;
						private Point point;
						
						@Override
						public void execute() {
							if (point == null) {
								point = selection.getNormal().getPosition();
								ent = placableGUI.getEntity();
							}
							ent = placableGUI.getEntity();
							ent.spawn(point.cpy());
						}
						
						@Override
						public void undo() {
							ent.dispose();
						}
					}; 
					
			}
			//erase
			return new Command() {
				private Block previous;
				private Coordinate coord;
				
				@Override
				public void execute() {
					if (coord==null) {
						coord = selection.getPosition().toCoord();
						previous = coord.getBlock();
					}
					Controller.getMap().setBlock(coord, null);
				}

				@Override
				public void undo() {
					Controller.getMap().setBlock(coord, previous);
				}
			};
		}
	}

	private Tool selectionLeft = Tool.DRAW;
	private Tool selectionRight = Tool.ERASE;

	private final GameView view;

	private final Image[] items = new Image[Tool.values().length];
	private final PlacableTable leftTable;
	private final PlacableTable rightTable;
	
	/**
	 * creates a new toolbar
	 *
	 * @param view
	 * @param sprites
	 * @param left
	 * @param right
	 * @param cursor
	 */
	public Toolbar(GameView view, TextureAtlas sprites, PlacableTable left, PlacableTable right, Cursor cursor) {
		super("Tools", WE.getEngineView().getSkin());
		
		this.view = view;
		
		setPosition(
			view.getStage().getWidth() / 2 - items.length * 50 / 2,
			view.getStage().getHeight() - 100
		);
		setWidth(Tool.values().length * 25);
		setHeight(45);
		
		this.leftTable = left;
		this.rightTable = right;
		this.cursor = cursor;

		for (int i = 0; i < items.length; i++) {
			items[Tool.values()[i].id] = new Image(sprites.findRegion(Tool.values()[i].name));
			items[i].setPosition(i * 25, 2);
			items[i].addListener(new ToolSelectionListener(Tool.values()[i]));
			addActor(items[i]);
		}

		//initialize selection
		if (selectionLeft.selectFromBlocks) { //show entities on leftTable
			left.showBlocks(view);
		} else {//show blocks on leftTable
			if (selectionLeft.selectFromEntities) {
				left.showEntities(view);
			} else {
				left.hide(true);
			}
		}

		if (selectionRight.selectFromBlocks) { //show entities on leftTable
			right.showBlocks(view);
		} else { //show blocks on leftTable
			if (selectionRight.selectFromEntities) {
				right.showEntities(view);
			} else {
				right.hide(true);
			}
		}
	}

	/**
	 * renders the toolbar outline
	 *
	 * @param shR
	 */
	public void render(ShapeRenderer shR) {
		shR.translate(getX(), getY(), 0);
		shR.begin(ShapeRenderer.ShapeType.Line);
		//draw leftTable	
		shR.setColor(Color.GREEN);
		shR.rect(items[selectionLeft.id].getX() - 1, items[selectionLeft.id].getY() - 1, 22, 22);
		//draw rightTable
		shR.setColor(Color.BLUE);
		shR.rect(items[selectionRight.id].getX() - 2, items[selectionLeft.id].getY() - 2, 24, 24);

		shR.end();
		shR.translate(-getX(), -getY(), 0);
	}

	/**
	 * index of leftTable mouse button.
	 *
	 * @return
	 */
	public Tool getLeftTool() {
		return selectionLeft;
	}

	/**
	 * index of rightTable mouse button.
	 *
	 * @return
	 */
	public Tool getRightTool() {
		return selectionRight;
	}
	
	/**
	 * select a tool
	 * @param left
	 * @param tool 
	 */
	public void selectTool(boolean left, Tool tool){
		selectionLeft = tool;
		if (left) {
			if (tool.selectFromBlocks) { //show entities on leftTable
				this.leftTable.showBlocks(view);
			} else {//show blocks on leftTable
				if (tool.selectFromEntities) {
					this.leftTable.showEntities(view);
				} else {
					this.leftTable.hide(true);
				}
			}
			cursor.showNormal(tool.showNormal);
		} else {
			if (tool.selectFromBlocks) { //show entities on leftTable
				rightTable.showBlocks(view);
			} else { //show blocks on leftTable
				if (tool.selectFromEntities) {
					rightTable.showEntities(view);
				} else {
					rightTable.hide(true);
				}
			}
		}
	}

	//class to detect clicks
	private class ToolSelectionListener extends InputListener {

		private final Tool tool;

		ToolSelectionListener(Tool tool) {
			this.tool = tool;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (button == Buttons.LEFT) {
				selectTool(true, tool);
			} else if (button == Buttons.RIGHT) {
				selectTool(false, tool);
			}

			return true;
		}

	}
}
