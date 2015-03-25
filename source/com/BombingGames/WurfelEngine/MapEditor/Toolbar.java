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
package com.BombingGames.WurfelEngine.MapEditor;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

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
		DRAW(0, "draw_button", true, false),
		/**
		 * tool to cover an area with blocks
		 */
		BUCKET(1, "bucket_button", true, false),
		/**
		 * "repaints" blocks
		 */
		REPLACE(2, "replace_button", true, false),
		/**
		 * select and move entities
		 */
		SELECT(3, "pointer_button", false, false),
		/**
		 * spawn new entities
		 */
		SPAWN(4, "entity_button", false, true),
		/**
		 * replace blocks with air
		 */
		ERASE(5, "eraser_button", false, false);

		private int id;
		private String name;
		private boolean selectFromBlocks;
		private boolean selectFromEntities;

		private Tool(int id, String name, boolean worksOnBlocks, boolean worksOnEntities) {
			this.id = id;
			this.name = name;
			this.selectFromBlocks = worksOnBlocks;
			this.selectFromEntities = worksOnEntities;
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
	}

	private Tool selectionLeft = Tool.DRAW;
	private Tool selectionRight = Tool.ERASE;

	private int leftPos;
	private int bottomPos;

	private final Image[] items = new Image[Tool.values().length];

	/**
	 * creates a new toolbar
	 *
	 * @param stage
	 * @param sprites
	 * @param left
	 * @param right
	 */
	public Toolbar(Stage stage, TextureAtlas sprites, PlacableSelector left, PlacableSelector right) {
		super("Tools", WE.getEngineView().getSkin());
		leftPos = (int) (stage.getWidth() / 2 - items.length * 50 / 2);
		bottomPos = (int) (stage.getHeight() - 100);

		setPosition(leftPos, bottomPos);
		setWidth(Tool.values().length * 25);
		setHeight(45);

		for (int i = 0; i < items.length; i++) {
			items[Tool.values()[i].id] = new Image(sprites.findRegion(Tool.values()[i].name));
			items[i].setPosition(i * 25, 2);
			items[i].addListener(new ToolSelectionListener(Tool.values()[i], left, right));
			addActor(items[i]);
		}

		//initialize selection
		if (selectionLeft.selectFromBlocks) { //show entities on left
			left.showBlocks();
		} else {//show blocks on left
			if (selectionLeft.selectFromEntities) {
				left.showEntities();
			} else {
				left.hide(true);
			}
		}

		if (selectionRight.selectFromBlocks) { //show entities on left
			right.showBlocks();
		} else { //show blocks on left
			if (selectionRight.selectFromEntities) {
				right.showEntities();
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
		//draw left	
		shR.setColor(Color.GREEN);
		shR.rect(items[selectionLeft.id].getX() - 1, items[selectionLeft.id].getY() - 1, 22, 22);
		//draw right
		shR.setColor(Color.BLUE);
		shR.rect(items[selectionRight.id].getX() - 2, items[selectionLeft.id].getY() - 2, 24, 24);

		shR.end();
		shR.translate(-getX(), -getY(), 0);
	}

	/**
	 * index of left mouse button.
	 *
	 * @return
	 */
	public Tool getLeftTool() {
		return selectionLeft;
	}

	/**
	 * index of right mouse button.
	 *
	 * @return
	 */
	public Tool getRightTool() {
		return selectionRight;
	}

	//class to detect clicks
	private class ToolSelectionListener extends InputListener {

		private final Tool tool;
		private final PlacableSelector left;
		private final PlacableSelector right;

		ToolSelectionListener(Tool tool, PlacableSelector left, PlacableSelector right) {
			this.tool = tool;
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (button == Buttons.LEFT) {
				selectionLeft = tool;
				if (tool.selectFromBlocks) { //show entities on left
					left.showBlocks();
				} else {//show blocks on left
					if (tool.selectFromEntities) {
						left.showEntities();
					} else {
						left.hide(true);
					}
				}
			} else if (button == Buttons.RIGHT) {
				selectionRight = tool;
				if (tool.selectFromBlocks) { //show entities on left
					right.showBlocks();
				} else { //show blocks on left
					if (tool.selectFromEntities) {
						right.showEntities();
					} else {
						right.hide(true);
					}
				}
			}

			return true;
		}

	}
}
