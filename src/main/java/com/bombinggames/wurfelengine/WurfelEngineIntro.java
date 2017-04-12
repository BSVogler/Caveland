/*
 * Copyright 2017 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.wurfelengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.bombinggames.wurfelengine.core.WEScreen;

/**
 *
 * @author Benedikt Vogler
 */
class WurfelEngineIntro extends WEScreen {

		private final Sprite lettering;
		private final SpriteBatch batch;
		private float alpha = 0;
		private boolean increase = true;
		private final Sound startupsound;
		private final Interpolation interpolate;

		WurfelEngineIntro() {
			batch = new SpriteBatch();
			lettering = new Sprite(new Texture(Gdx.files.internal("com/bombinggames/wurfelengine/lettering.png")));
			lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth()) / 2);
			lettering.setY((Gdx.graphics.getHeight() - lettering.getHeight()) / 2);
			startupsound = Gdx.audio.newSound(Gdx.files.internal("com/bombinggames/wurfelengine/soundengine/sounds/startup.mp3"));
			startupsound.play();
			interpolate = new Interpolation.ExpOut(2, 7);
			//lettering.flip(false, true);
		}

		@Override
		public void renderImpl(float dt) {
			if (increase) {
				if (alpha >= 1) {
					alpha = 1;
					increase = false;
				} else {
					alpha += dt / 1500f;
				}
				drawLettering();
			} else if (alpha <= 0) {
				alpha = 0;
				dispose();
			} else {
				alpha -= dt / 1000f;
				drawLettering();
			}
		}

		void drawLettering() {
			lettering.setColor(1f, 1f, 1f, interpolate.apply(alpha));

			//clear & set background to black
			Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.begin();
			lettering.draw(batch);
			batch.end();
		}

		@Override
		public void resize(int width, int height) {
		}

		@Override
		public void show() {
			Gdx.app.debug("Intro", "Showing intro…");
		}

		@Override
		public void hide() {
		}

		@Override
		public void pause() {
		}

		@Override
		public void resume() {
		}

		@Override
		public void dispose() {
			startupsound.dispose();
			WE.showMainMenu();
		}
	}