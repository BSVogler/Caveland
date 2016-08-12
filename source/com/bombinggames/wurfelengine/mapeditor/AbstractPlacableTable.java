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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.bombinggames.wurfelengine.core.GameView;

/**
 * A table containing all blocks where you can choose your block.
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractPlacableTable extends Table {

	/**
	 * list position
	 */
	private byte selected = 1;
	/**
	 * game data
	 */
	private byte value;

	/**
	 *
	 */
	public AbstractPlacableTable() {
		setWidth(400);
		setHeight(Gdx.graphics.getHeight() * 0.80f);
		setY(10);
		setX(30);
	}

	/**
	 *
	 * @param view used for rendering
	 */
	public abstract void show(GameView view);

	/**
	 *
	 */
	public void hide() {
		if (hasChildren()) {
			clear();
		}

		if (isVisible()) {
			setVisible(false);
		}
	}

	/**
	 * selects the item
	 *
	 * @param pos the pos of the listener
	 */
	void selectItem(byte pos) {
		if (pos <= getChildren().size) {
			selected = pos;
			for (Actor c : getChildren()) {
				c.setScale(0.35f);
			}
			getChildren().get(selected).setScale(0.4f);
			value = 0;
		}
	}

	/**
	 * sets the value of the selected
	 *
	 * @param value
	 */
	void setValue(byte value) {
		this.value = value;
	}

	/**
	 *
	 * @return
	 */
	public byte getValue() {
		return value;
	}

}
