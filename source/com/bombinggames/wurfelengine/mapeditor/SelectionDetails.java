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

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Saves the current "color"(block) selection in the editor.
 * @author Benedikt Vogler
 */
public class SelectionDetails extends WidgetGroup {
	private byte id;
	private byte value;
	private final Label label;
	private final Label blockPosition;
	private Class<? extends AbstractEntity> entityClass;
	private boolean placeBlocks = true;
	private final Slider slider;
	/** parent stage*/
	private final Stage stage;
	private PlacableTable table;

	/**
	 *
	 * @param stage parent stage
	 * @param selection the selection-Entity where the color comes from
	 * @param left left mouse button tool?
	 */
	public SelectionDetails(Stage stage, Cursor selection, boolean left) {
		this.stage = stage;

		slider = new Slider(-1, Block.VALUESNUM - 1, 1, false, WE.getEngineView().getSkin());
		slider.setPosition(0, 20);
		slider.addListener(new ChangeListenerImpl(this));
		addActor(slider);

		label = new Label(Integer.toString(getId()) + " - " + Integer.toString(getValue()), WE.getEngineView().getSkin());
		addActor(label);

		if (selection != null && selection.hasPosition()) {
			blockPosition = new Label(selection.getPosition().toCoord().toString(), WE.getEngineView().getSkin());
		} else {
			blockPosition = new Label("no cursor found", WE.getEngineView().getSkin());
		}
		blockPosition.setPosition(60, 30);

		if (left) {
			setPosition(200, stage.getHeight()*0.9f);
		} else {
			setPosition(stage.getWidth() - 200, stage.getHeight()*0.9f);
		}

		addActor(blockPosition);
	}

	/**
	 * 
	 * @param selection the selection entity of the editor
	 */
	public void update(Cursor selection){
		if (selection.hasPosition())
			blockPosition.setText(selection.getPosition().toCoord().toString());
	}

	/**
	 *
	 * @return
	 */
	public byte getId() {
		return id;
	}
		
	/**
	 *
	 * @return
	 */
	public byte getValue() {
		return value;
	}	
	
	/**
	 *
	 * @param id
	 * @param value
	 */
	public void setBlock(byte id, byte value) {
		this.id = id;
		this.value = value;
		label.setText(Block.getName(id, value) + " "+ id + " - "+ value);
	}

	/**
	 *Set the value byte of the block or entity
	 * @param value
	 */
	public void setValue(byte value) {
		if (placeBlocks) {
			this.value = value;
			label.setText(Block.getName(id, value) + " "+ id + " - "+ value);
		} else if (value == -1) {
			label.setText(Block.getName(id, value) + " "+ id + " - "+ value);
		} else {
			label.setText(Block.getName(id, value) + " "+ id + " - "+ value);
		}
	}
	
	/**
	 * Trys returning a new instance of a selected entity class.
	 * @return if it fails returns null 
	 */
	public AbstractEntity getEntity(){
		if (entityClass == null) return null;
		try {
			AbstractEntity ent = entityClass.newInstance();
			if (slider.getValue()>-1)
				ent.setSpriteValue((byte) slider.getValue());
			return ent;
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(SelectionDetails.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Sets the current color to this entity class.
	 * @param name name which gets displayed
	 * @param entclass
	 */
	public void setEntity(String name, Class<? extends AbstractEntity> entclass) {
		entityClass = entclass;
		label.setText(name);
	}

	/**
	 *
	 * @param mode
	 */
	public void setMode(boolean mode) {
		this.placeBlocks = mode;
	}
	
	/**
	 *
	 * @return
	 */
	public boolean getMode() {
		return placeBlocks;
	}

	/**
	 * Relative movement.
	 * @param amount 
	 */
	void moveToCenter(float amount) {
		if (getX() < stage.getWidth()/2)
			setX(getX()+amount);
		else
			setX(getX()-amount);
	}

	/**
	 * Absolute position.
	 * @param amount 
	 */
	void moveToBorder(float amount) {
		if (getX() < stage.getWidth()/2)
			setX(amount);
		else
			setX(stage.getWidth()-amount);
	}

	void hide() {
		setVisible(false);
	}

	public void setTable(PlacableTable table) {
		this.table = table;
	}

	public void setId(byte id) {
		this.id = id;
	}

	public int getBlock() {
		return id+(value<<8);
	}

	private static class ChangeListenerImpl extends ChangeListener {
		private final SelectionDetails parent;

		ChangeListenerImpl(SelectionDetails parent) {
			this.parent = parent;
		}

		@Override
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			if (((ProgressBar) actor).getValue() > -1) {
				parent.setValue((byte) ((ProgressBar) actor).getValue());
				parent.table.setValue((byte) ((ProgressBar) actor).getValue());
			}
		}
	}
}
