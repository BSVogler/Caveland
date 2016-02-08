/*
 * Copyright 2015 Benedikt Vogler.
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
package com.bombinggames.wurfelengine.core.basicmainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;

/**
 * The View manages the graphical ouput and input.
 *
 * @author Benedikt
 */
public class MenuView {

	private final Sprite lettering;
	private final SpriteBatch batch;
	private final BitmapFont font;
	private float alpha = 0;
	private final ShapeRenderer sr;
	private final MenuController controller;

	/**
	 * Creates alpha View.
	 *
	 * @param controller
	 */
	protected MenuView(MenuController controller) {
		this.controller = controller;
		//load textures
		lettering = new Sprite(new Texture(Gdx.files.internal("com/bombinggames/wurfelengine/Core/BasicMainMenu/Images/Lettering.png")));
		lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth()) / 2);
		lettering.setY(Gdx.graphics.getHeight() - 150);

		batch = new SpriteBatch();

		font = new BitmapFont();
		font.setColor(Color.WHITE);

		sr = new ShapeRenderer();
	}

	/**
	 *
	 * @param dt time in ms
	 */
	protected void update(float dt) {
		alpha += dt / 1000f;
		if (alpha > 1) {
			alpha = 1;
		}
	}

	/**
	 * renders the scene
	 *
	 * @param warning Render alpha warning about no custom main menu in use.
	 */
	protected void render(boolean warning) {
		//clear & set background to black
		Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// render the lettering
		batch.begin();
		//lettering.setColor(1, 1, 1, alpha);
		lettering.draw(batch);

		// Draw the menu items
		for (BasicMenuItem mI : BasicMainMenu.getController().getMenuItems()) {
			mI.render(font, batch, sr);
		}

		//draw warnings
		font.draw(batch, "FPS:" + Gdx.graphics.getFramesPerSecond(), 20, 20);
		font.draw(batch, Gdx.input.getX() + "," + Gdx.input.getY(), Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
		if (warning) {
			font.draw(batch, "No custom main menu used. This is the engine's basic main menu.", 20, 200);
			font.draw(batch, "You can hide this warning with BasicMainMenu#supressWarning().", 20, 100);
		}

		//font.scale(-0.5f);
		font.draw(batch, WE.getCredits(), 50, Gdx.graphics.getHeight() - 150);
		//font.scale(0.5f);
		batch.end();
	}

	/**
	 *
	 */
	protected void show() {
		Cursor cursor = Gdx.graphics.newCursor(
			new Pixmap(Gdx.files.internal("com/bombinggames/wurfelengine/Core/images/wecursor.png")),
			0,
			0
		);
		Gdx.graphics.setCursor(cursor);

		WE.getEngineView().addInputProcessor(new InputListener(controller));
	}

	private class InputListener implements InputProcessor {

		private final MenuController controller;

		InputListener(MenuController controller) {
			this.controller = controller;
		}

		@Override
		public boolean keyDown(int keycode) {
			if (keycode == Input.Keys.ESCAPE) {
				Gdx.app.exit();
			}
			if (keycode == Input.Keys.DOWN && BasicMenuItem.getHighlight() < controller.getMenuItems().length - 1) {
				BasicMenuItem.setHighlight(BasicMenuItem.getHighlight() + 1);
			}
			if (keycode == Input.Keys.UP && BasicMenuItem.getHighlight() > 0) {
				BasicMenuItem.setHighlight(BasicMenuItem.getHighlight() - 1);
			}
			if (keycode == Input.Keys.ENTER) {
				controller.getMenuItems()[BasicMenuItem.getHighlight()].action();
			}
			return true;
		}

		@Override
		public boolean keyUp(int keycode) {
			return true;
		}

		@Override
		public boolean keyTyped(char character) {
			return true;
		}

		@Override
		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			return true;
		}

		@Override
		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			return true;
		}

		@Override
		public boolean touchDragged(int screenX, int screenY, int pointer) {
			return true;
		}

		@Override
		public boolean mouseMoved(int screenX, int screenY) {
			return true;
		}

		@Override
		public boolean scrolled(int amount) {
			return true;
		}
	}

}
