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

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Shows the current "color"(block) selection in the editor.
 * @author Benedikt Vogler
 */
public class PlacableGUI extends WidgetGroup {
	private int id;
	private int value;
	private Image image;
	private Label label;


	public PlacableGUI(Stage stage) {
		setPosition(stage.getWidth()-200, stage.getHeight()-300);
		image = new Image(new BlockDrawable(id,value,-0.4f));
		image.setPosition(50, 60);
		addActor(image);
		Slider slider = new Slider(0, 10, 1, false, WE.getEngineView().getSkin());
		slider.setPosition(0, 20);
		slider.addListener(new ChangeListenerImpl(this));
		addActor(slider);
		label = new Label(Integer.toString(id) + " - "+ Integer.toString(value), WE.getEngineView().getSkin());
		addActor(label);
	}
	

	public int getId() {
		return id;
	}
		
	public int getValue() {
		return value;
	}	
	
	
	void setBlock(int id, int value) {
		this.id = id;
		this.value = value;
		label.setText(Integer.toString(id) + " - "+ Integer.toString(value));
		image.setDrawable(new BlockDrawable(id,value,-0.4f));
	}

	public void setId(int id) {
		this.id = id;
		label.setText(Integer.toString(id) + " - "+ Integer.toString(value));
		image.setDrawable(new BlockDrawable(id,value,-0.4f));
	}

	public void setValue(int value) {
		this.value = value;
		label.setText(Integer.toString(id) + " - "+ Integer.toString(value));
		image.setDrawable(new BlockDrawable(id,value,-0.4f));
	}
	
	/**
	 * 
	 * @param coord the position of the block instance
	 * @return a new Block instance of the selected id and value.
	 */
	public Block getBlock(Coordinate coord){
		return Block.getInstance(id, value);
	}

	private static class ChangeListenerImpl extends ChangeListener {
		private PlacableGUI parent;

		ChangeListenerImpl(PlacableGUI parent) {
			this.parent = parent;
		}

		@Override
		public void changed(ChangeListener.ChangeEvent event, Actor actor) {
			parent.setValue((int) ((Slider)actor).getValue());
		}
	}
}
