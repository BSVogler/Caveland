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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 * A toolbar for the editor.
 *
 * @author Benedikt Vogler
 */
public class Toolbar extends Window {

	private final Cursor cursor;
	private final Tool selectionRight = Tool.ERASE;
	private final EntityTable entTable;
	private final BlockTable blockTable;
	private final TextField valuesActor;
	private final GameView view;
	private final Image[] items = new Image[Tool.values().length];

	private Tool selectionLeft = Tool.DRAW;
	private final TextButton aplyValuesButton;

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

		this.entTable = new EntityTable();
		this.blockTable = new BlockTable();
        stage.addActor(entTable);
		entTable.hide();
		stage.addActor(blockTable);
		
		for (int i = 0; i < items.length; i++) {
			items[Tool.values()[i].id] = new Image(sprites.findRegion(Tool.values()[i].name));
			items[i].setPosition(i * 25, 40);
			items[i].addListener(new ToolSelectionListener(Tool.values()[i]));
			addActor(items[i]);
		}
		//row();

		//initialize selection
		showTable(selectionLeft);

		valuesActor = new TextField("1", WE.getEngineView().getSkin());
		valuesActor.setWidth(30);
		valuesActor.setPosition(2, 2);
		valuesActor.setMaxLength(1 + RenderCell.VALUESNUM / 100);
		valuesActor.setTextFieldFilter((TextField textField, char c) -> Character.isDigit(c));
		valuesActor.addListener(new TextFieldChangedListener(this));
		addActor(valuesActor);
		
		aplyValuesButton = new TextButton("Edit Value", WE.getEngineView().getSkin());
		aplyValuesButton.setPosition(valuesActor.getWidth()+valuesActor.getX()+3, 2);
		aplyValuesButton.addListener(
			new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				valuesActor.setDisabled(!valuesActor.isDisabled());
				valuesActor.setVisible(!valuesActor.isDisabled());
			}
		});
		
		addActor(aplyValuesButton);
		
	}
	
	/**
	 * Table which is associated with the current tool
	 * @return can return null
	 */
	protected AbstractPlacableTable getActiveTable(){
		if (selectionLeft.selectFromBlocks) { //show entities on leftTable
			return blockTable;
		} else if (selectionLeft.selectFromEntities) {//show blocks on leftTable
			return entTable;
		} else {
			return null;
		}
	}
	
	/**
	 * Show/hide tables according to the tool.
	 * @param tool 
	 */
	private void showTable(Tool tool){
		if (tool.selectFromBlocks) { //show entities on leftTable
			blockTable.show(view);
			entTable.hide();
		} else if (tool.selectFromEntities) {//show blocks on leftTable
			entTable.show(view);
			blockTable.hide();
		} else {
			blockTable.hide();
			entTable.hide();
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
	 *
	 * @param tool
	 */
	public void selectTool(Tool tool) {
		selectionLeft = tool;
		showTable(tool);
		cursor.setTool(tool);
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
				selectTool(tool);
			}

			return true;
		}
	}

	private static class TextFieldChangedListener extends ChangeListener {

		private final Toolbar parent;

		TextFieldChangedListener(Toolbar parent) {
			this.parent = parent;
		}

		@Override
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			//only send value to table if valid
			if (parent.getValue() > -1) {
				parent.getActiveTable().setValue(parent.getValue());
			}
			if (parent.valuesActor.getText().length() > 1) {
				parent.valuesActor.selectAll();
			}
		}
	}
}
