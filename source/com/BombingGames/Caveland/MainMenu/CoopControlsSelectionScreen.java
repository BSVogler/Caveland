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
package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.Caveland.Game.CustomGameController;
import com.BombingGames.Caveland.Game.CustomGameView;
import com.BombingGames.WurfelEngine.Core.WEScreen;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 *
 * @author Benedikt Vogler
 */
public class CoopControlsSelectionScreen extends WEScreen {
	private final Texture background;
	private Stage stage;
	private final TextButton twoControllersButton;
	private final SpriteBatch batch;
	private final OrthographicCamera camera;

	public CoopControlsSelectionScreen(SpriteBatch batch) {
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		this.batch = batch;
		
		background = new Texture(Gdx.files.internal("com/BombingGames/Caveland/MainMenu/controlScreen.png"));
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//stage
		twoControllersButton = new TextButton("Choose", WE.getEngineView().getSkin());
		twoControllersButton.setPosition(200, 600);
		twoControllersButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				CustomGameView view = new CustomGameView();
				view.enableCoop();
				WE.initAndStartGame(new CustomGameController(), view, new CustomLoading());
			}
		});
		stage.addActor(twoControllersButton);
		
		TextButton splitControlsButton = new TextButton("Choose", WE.getEngineView().getSkin());
		splitControlsButton.setPosition(800, 400);
		splitControlsButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				CustomGameView view = new CustomGameView();
				view.enableCoop();
				WE.initAndStartGame(new CustomGameController(), view, new CustomLoading());
			}
		});
		stage.addActor(splitControlsButton);
		
		TextButton twoKeyboardButton = new TextButton("Choose", WE.getEngineView().getSkin());
		twoKeyboardButton.setPosition(1200, 200);
		twoKeyboardButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				CustomGameView view = new CustomGameView();
				view.enableCoop();
				WE.initAndStartGame(new CustomGameController(), view, new CustomLoading());
			}
		});
		stage.addActor(twoKeyboardButton);
		
		TextButton backButton = new TextButton("Back", WE.getEngineView().getSkin());
		backButton.setPosition(100, 150);
		backButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.showMainMenu();
				dispose();
			}
		});
		stage.addActor(backButton);
	}

	
	@Override
	public void renderImpl(float dt) {
		Gdx.gl20.glClearColor( 0.1f, 0f, 0f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
        batch.setProjectionMatrix(camera.combined);
		batch.begin();
			batch.draw(background, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
        batch.end();
		
		stage.draw();
		
	}

	@Override
	public void show() {
		WE.getEngineView().addInputProcessor(stage);
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
	
}
