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

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 * Saves the current "color"(block) selection in the editor.
 *
 * @author Benedikt Vogler
 */
public class CursorInfo extends WidgetGroup {

	private final Label label;
	/**
	 * parent stage
	 */
	private final Stage stage;
	private final Cursor cursor;

	/**
	 *
	 * @param stage parent stage
	 * @param cursor the selection-Entity where the block comes from
	 */
	public CursorInfo(Stage stage, Cursor cursor) {
		this.stage = stage;
		this.cursor = cursor;
		label = new Label("nothing selected", WE.getEngineView().getSkin());
		addActor(label);

		setPosition(stage.getWidth() * 0.8f, stage.getHeight() * 0.02f);
	}

	public void updateFrom(int block, Coordinate coord) {
		byte id = (byte) (block & 255);
		byte value = (byte) ((block >> 8) & 255);
		label.setText(RenderCell.getName(id, value) + " " + id + " - " + value + "@" + cursor.getPosition().toCoord().toString());
	}

	/**
	 * Relative movement.
	 *
	 * @param amount
	 */
	void moveToCenter(float amount) {
		if (getX() < stage.getWidth() / 2) {
			setX(getX() + amount);
		} else {
			setX(getX() - amount);
		}
	}

	/**
	 * Absolute position.
	 *
	 * @param amount
	 */
	void moveToBorder(float amount) {
		if (getX() < stage.getWidth() / 2) {
			setX(amount);
		} else {
			setX(stage.getWidth() - amount);
		}
	}

	void hide() {
		setVisible(false);
	}

}
