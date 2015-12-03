/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class EntityShadow extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	/**
	 * the parent class. The object where this is the shadow
	 */
	private final AbstractEntity character;

	/**
	 *
	 * @param character
	 */
	protected EntityShadow(AbstractEntity character) {
		super((byte) 32);
		this.disableShadow();
		this.setName("Shadow");
		this.character = character;
	}

	@Override
	public void update(float dt) {
		setSaveToDisk(false);
		if (character == null || !character.hasPosition() || !hasPosition() || character.isHidden()) {
			dispose();
		} else {
			//find height of shadow surface
			Coordinate newHeight = character.getPosition().toCoord();//start at same height
			Block block = newHeight.getBlock();
			while (newHeight.getZ() > 0
				&& (block == null || block.isTransparent())) {
				newHeight.addVector(0, 0, -1);
				block = newHeight.getBlock();
			}

			setPosition(character.getPosition().cpy());
			getPosition().setZ(newHeight.addVector(0, 0, 1).toPoint().add(0, 0, -2).getZ());
		}
	}

	@Override
	public void render(GameView view, Camera camera) {
		if (character == null || !character.hasPosition() || !hasPosition() || character.isHidden()) {
			dispose();
		} else {
			setScaling(0);
			setColor(
				new Color(
					.5f,
					.5f,
					.5f,
					1 - (character.getPosition().getZ() - getPosition().getZ()) / 2 / Block.GAME_EDGELENGTH+0.1f
				)
			);
			super.render(view, camera);
			//always visible smaller shadow
			if (getColor().a < 0.9f) {//render only if small shadow would be visible
				setColor(
					new Color(.5f, .5f, .5f, 0.2f)
				);
				setScaling(-0.5f);
				super.render(view, camera);
			}
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
