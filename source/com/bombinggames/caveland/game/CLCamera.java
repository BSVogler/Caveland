/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
package com.bombinggames.caveland.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.map.Point;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class CLCamera extends Camera {
	
		/**
	 * the opacity of thedamage overlay
	 */
	private float damageoverlay = 0f;

	public CLCamera(GameView view) {
		super(view);
	}

	public CLCamera(GameView view, int x, int y, int width, int height) {
		super(view, x, y, width, height);
	}

	public CLCamera(GameView view, int x, int y, int width, int height, Point center) {
		super(view, x, y, width, height, center);
	}

	public CLCamera(GameView view, int x, int y, int width, int height, AbstractEntity focusentity) {
		super(view, x, y, width, height, focusentity);
	}
	
	/**
	 *
	 * @param opacity
	 */
	public void setDamageoverlayOpacity(float opacity) {
		this.damageoverlay = opacity;
	}

	@Override
	public void render(GameView view, Camera camera) {
		super.render(view, camera);
		if (damageoverlay > 0.0f) {
			try {
				//WE.getEngineView().getSpriteBatch().setShader(new custom shader);
				Texture texture = WE.getAsset("com/bombinggames/caveland/game/bloodblur.png");
				WE.getEngineView().getSpriteBatch().begin();
				Sprite overlay = new Sprite(texture);
				overlay.setOrigin(0, 0);
				//somehow reverse the viewport transformation, needed for split-screen
				overlay.setSize(
					getWidthInScreenSpc(),
					getHeightInScreenSpc() * (float) Gdx.graphics.getHeight() / getHeightInScreenSpc()
				);
				overlay.setColor(1, 0, 0, damageoverlay);
				overlay.draw(WE.getEngineView().getSpriteBatch());
				WE.getEngineView().getSpriteBatch().end();
			} catch (FileNotFoundException ex) {
				Logger.getLogger(CLCamera.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
	
	
	
}
