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

import com.BombingGames.Caveland.CustomConfiguration;
import com.BombingGames.Caveland.Game.CustomGameController;
import com.BombingGames.Caveland.Game.CustomGameView;
import com.BombingGames.WurfelEngine.Core.MainMenuInterface;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 *
 * @author Benedikt Vogler
 */
public class MainMenuScreen implements MainMenuInterface {
	private final TextButton[] menuItems = new TextButton[3];
	private Stage stage;
	private ShapeRenderer shr;
	private Sprite lettering;    
    private Texture background;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
    private float alpha =0;



	@Override
	public void init() {
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), WE.getEngineView().getBatch());
        WE.getEngineView().addInputProcessor(stage);
		
		menuItems[0]=new TextButton("Start", WE.getEngineView().getSkin());
		menuItems[0].setPosition(stage.getWidth()/2, 500);
		//start the game
		menuItems[0].addListener(
			new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					WE.initGame(new CustomGameController(), new CustomGameView(), new CustomConfiguration());
				}
			}
		);
		
		menuItems[1]=new TextButton("Credits", WE.getEngineView().getSkin());
		menuItems[1].setPosition(stage.getWidth()/2, 400);
		menuItems[1].addListener(
			new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					WE.getInstance().setScreen(new CreditsScreen());
				}
			}
		);
		
		menuItems[2]=new TextButton("Exit", WE.getEngineView().getSkin());
		menuItems[2].setPosition(stage.getWidth()/2, 300);
		menuItems[2].addListener(
			new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					Gdx.app.exit();
				}
			}
		);
		
		for (TextButton menuItem : menuItems) {
			stage.addActor(menuItem);
		}
		
		//load textures
        lettering = new Sprite(new Texture(Gdx.files.internal("com/BombingGames/Caveland/MainMenu/Lettering.png")));
        lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth())/2);
        lettering.setY(50);
        lettering.flip(false, true);
        
        background = new Texture(Gdx.files.internal("com/BombingGames/Caveland/MainMenu/background.png"));
		
        //background.setX((Gdx.graphics.getWidth() - lettering.getWidth())/2);
        //background.setY(50);
        //background.flip(false, true);
        
        batch = new SpriteBatch();
		shr = new ShapeRenderer();
        
        //set the center to the top left
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        font = new BitmapFont(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/arial.fnt"), true);
        font.setColor(Color.WHITE);
	}

	@Override
	public void render(float delta) {
		delta *=1000;//in ms 
		//update
		
		
		alpha += delta/1000f;
		if (alpha>1) alpha=1;
		
		//render
		 //clear & set background to black
        Gdx.gl20.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //update camera and set the projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);
		shr.setProjectionMatrix(camera.combined);
        
        //Background        
        batch.begin();
			batch.draw(background, 0, Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), -Gdx.graphics.getHeight());
//			for (int x = 0; x-1 < Gdx.graphics.getWidth()/background.getWidth(); x++) {
//				for (int y = 0; y-1 < Gdx.graphics.getHeight()/background.getHeight(); y++) {
//					background.setPosition(x*background.getWidth(), y*background.getHeight());
//					background.draw(batch);
//				}
//			}
        
			// render the lettering
			lettering.setColor(1, 1, 1, alpha);
			lettering.draw(batch);

			font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
			font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
		
		stage.draw();
        
        font.scale(-0.5f);
        batch.begin();
        font.drawMultiLine(batch, WE.getCredits(), 50, 100);
        batch.end();
        font.scale(0.5f);
		
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
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
	}
	
}
