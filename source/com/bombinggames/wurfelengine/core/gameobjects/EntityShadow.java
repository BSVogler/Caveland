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
package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

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
			getPoint().set(character.getPosition());//start at character
			while (getPoint().getZ() > 0
				&& (RenderCell.isTransparent(getPoint().getBlock()))
			) {
				getPoint().add(0, 0, -RenderCell.GAME_EDGELENGTH);
			}
			if (character.getPosition().getZ()<RenderCell.GAME_EDGELENGTH) {
				getPoint().setZ(0);
			} else {
				getPoint().setZ((getPoint().getZGrid()+1)*RenderCell.GAME_EDGELENGTH);
			}
		}
	}

	@Override
	public void render(GameView view, Camera camera) {
		if (character == null || !character.hasPosition() || !hasPosition() || character.isHidden()) {
			dispose();
		} else {
			setScaling(1);
			setColor(new Color(
					.5f,
					.5f,
					.5f,
					1 - (character.getPosition().getZ() - getPosition().getZ()) / 2 / RenderCell.GAME_EDGELENGTH+0.1f
				)
			);
			super.render(view, camera);
			//always visible smaller shadow
			if (getColor().a < 0.9f) {//render only if small shadow would be visible
				setColor(
					new Color(.5f, .5f, .5f, 0.2f)
				);
				setScaling(0.5f);
				super.render(view, camera);
			}
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
