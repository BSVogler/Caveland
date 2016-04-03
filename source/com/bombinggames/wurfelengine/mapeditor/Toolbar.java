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
package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombinggames.wurfelengine.Command;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 * A toolbar for the editor.
 *
 * @author Benedikt Vogler
 */
public class Toolbar extends Window {

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

		/**
		 * 
		 *
		 * @param cursor
		 * @param placableGUI
		 * @return
		 */
		public Command getCommand(Cursor cursor, PlacableTable placableGUI) {
			switch (this) {
				case DRAW:
					return new Command() {
						private Coordinate coord;
						private int previous;
						private int block;

						public void init() {

						}

						@Override
						public void execute() {
							if (coord == null) {
								coord = cursor.getCoordInNormalDirection();
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
						private int previous;
						private int block;

						@Override
						public void execute() {
							if (coord == null) {
								coord = cursor.getPosition().toCoord();
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
								point = cursor.getNormal().getPosition();
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
				case ERASE:
				default:
					//erase
					return new Command() {
						private int previous;
						private Coordinate coord;

						@Override
						public void execute() {
							if (coord == null) {
								coord = cursor.getPosition().toCoord();
								previous = coord.getBlock();
							}
							Controller.getMap().setBlock(coord, (byte) 0);
						}

						@Override
						public void undo() {
							Controller.getMap().setBlock(coord, previous);
						}
					};
			}
		}
	}

	private final Cursor cursor;

	private Tool selectionLeft = Tool.DRAW;
	private final Tool selectionRight = Tool.ERASE;
	private PlacableTable table;
	private final TextField valuesActor;
	private final GameView view;

	private final Image[] items = new Image[Tool.values().length];

	/**
	 * creates a new toolbar
	 *
	 * @param view
	 * @param sprites
	 * @param cursor
	 * @param stage
	 */
	public Toolbar(GameView view, TextureAtlas sprites, Cursor cursor, Stage stage) {
		super("Tools", WE.getEngineView().getSkin());

		this.view = view;
		this.cursor = cursor;

		setPosition(
			view.getStage().getWidth() / 2 - items.length * 50 / 2,
			view.getStage().getHeight() - 100
		);
		setWidth(Tool.values().length * 25);
		setHeight(90);

		this.table = new PlacableTable(this);
        stage.addActor(table);
		
		for (int i = 0; i < items.length; i++) {
			items[Tool.values()[i].id] = new Image(sprites.findRegion(Tool.values()[i].name));
			items[i].setPosition(i * 25, 40);
			items[i].addListener(new ToolSelectionListener(Tool.values()[i]));
			addActor(items[i]);
		}
		//row();

		//initialize selection
		if (selectionLeft.selectFromBlocks) { //show entities on leftTable
			table.showBlocks(view);
		} else if (selectionLeft.selectFromEntities) {//show blocks on leftTable
			table.showEntities(view);
		} else {
			table.hide();
		}

		valuesActor = new TextField("1", WE.getEngineView().getSkin());
		valuesActor.setWidth(30);
		valuesActor.setPosition(2, 2);
		valuesActor.setMaxLength(1+RenderCell.VALUESNUM/100);
		valuesActor.setTextFieldFilter((TextField textField, char c) -> Character.isDigit(c));
		valuesActor.addListener(new ChangeListenerImpl(this));
		addActor(valuesActor);
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
	 * 
	 * @return 
	 */
	PlacableTable getTable() {
		return table;
	}

	/**
	 * select a tool
	 *
	 * @param left
	 * @param tool
	 */
	public void selectTool(boolean left, Tool tool) {
		selectionLeft = tool;
		if (left) {
			if (tool.selectFromBlocks) { //show entities on leftTable
				this.table.showBlocks(view);
			} else if (tool.selectFromEntities) { //show blocks on leftTable
				this.table.showEntities(view);
			} else {
				this.table.hide();
			}
			cursor.showNormal(tool.showNormal);
		}
	}

	/**
	 * Get the value of the text field.
	 * @return -1 if invalid number
	 */
	private byte getValue() {
		if (valuesActor.getText().isEmpty()) {
			return -1;
		}
		byte value;
		try {
			value = (byte) (Byte.valueOf(valuesActor.getText()));
		} catch (NumberFormatException ex) {
			return -1;
		}
		if (value > RenderCell.VALUESNUM || value < 0) {
			return -1;
		}
		return value;
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

	private static class ChangeListenerImpl extends ChangeListener {

		private final Toolbar parent;

		ChangeListenerImpl(Toolbar parent) {
			this.parent = parent;
		}

		@Override
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			//only send value to table if valid
			if (parent.getValue() > -1) {
				parent.table.setValue(parent.getValue());
			}
			if (parent.valuesActor.getText().length() > 1) {
				parent.valuesActor.selectAll();
			}
		}
	}
}
