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

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.WEScreen;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 *
 * @author Benedikt Vogler
 */
public class OptionScreen extends WEScreen {
	private Stage stage;
	private ShapeRenderer shr;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private BitmapFont font;
	private final CheckBox fullscreenCB;
	private final CheckBox vsyncCB;
	private final TextButton applyButton;
	private final TextButton cancelButton;
	private final CheckBox limitFPSCB;
	private final Slider musicSlider;
	private final Slider soundSlider;

	
	public OptionScreen() {
		batch = new SpriteBatch();
		shr = new ShapeRenderer();
		OrthographicCamera libgdxcamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		stage = new Stage(new ScreenViewport(libgdxcamera), batch);
		
		final SelectBox<String> sbox = new SelectBox<>(WE.getEngineView().getSkin());
		//fill with display modes
		Array<String> arstr = new Array<>();
		Graphics.DisplayMode[] dpms = Gdx.graphics.getDisplayModes();
		int indexCurrentDPM = 0;
		for (
			int i = 0; i < dpms.length; i++
		) {
			Graphics.DisplayMode dpm = dpms[i];
			arstr.add(dpm.toString());
			if (dpm.width==Gdx.graphics.getWidth() && dpm.height==Gdx.graphics.getHeight())
				indexCurrentDPM = i;
		}
		
		sbox.setItems(arstr);
		sbox.setSelectedIndex(indexCurrentDPM);
		sbox.setWidth(300);
		sbox.setPosition(500, 500);			
		
		stage.addActor(sbox);
		
		musicSlider = new Slider(0, 1, 0.1f,false, WE.getEngineView().getSkin());
		musicSlider.setPosition(500, 400);
		musicSlider.setValue(1f);
		musicSlider.addListener(
			new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					WE.getEngineView().setMusicLoudness(((Slider)actor).getValue());
				}
			}
		);
		stage.addActor(musicSlider);
		
		soundSlider = new Slider(0, 1, 0.1f,false, WE.getEngineView().getSkin());
		soundSlider.setPosition(500, 350);
		soundSlider.setValue(1f);
		stage.addActor(soundSlider);
		
		fullscreenCB = new CheckBox("Fullscreen", WE.getEngineView().getSkin());
		fullscreenCB.setPosition(900, 600);
		
		stage.addActor(fullscreenCB);
		
		vsyncCB = new CheckBox("V-Sync", WE.getEngineView().getSkin());
		vsyncCB.setPosition(900, 500);
		stage.addActor(vsyncCB);
		
		limitFPSCB = new CheckBox("limit FPS (recommended)", WE.getEngineView().getSkin());
		limitFPSCB.setChecked(CVar.get("limitFPS").getValuei() > 0);
		limitFPSCB.setPosition(900, 400);
		stage.addActor(limitFPSCB);
		
		applyButton = new TextButton("Apply", WE.getEngineView().getSkin());
		applyButton.setPosition(800, 100);
		applyButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				Gdx.graphics.setVSync(vsyncCB.isChecked());
				Graphics.DisplayMode dpm = Gdx.graphics.getDisplayModes()[sbox.getSelectedIndex()];
				Gdx.graphics.setDisplayMode(dpm.width, dpm.height, fullscreenCB.isChecked());
				
				//get FPS limit
				if (limitFPSCB.isChecked())
					CVar.get("limitFPS").setValue("60");
				else CVar.get("limitFPS").setValue("0");
				WE.getLwjglApplicationConfiguration().foregroundFPS = CVar.get("limitFPS").getValuei();
				
				//apply sound changes
				CVar.get("music").setValuef(musicSlider.getValue());
				WE.getEngineView().setMusicLoudness(musicSlider.getValue());
				CVar.get("sound").setValuef(soundSlider.getValue());
			}
		});
		stage.addActor(applyButton);
		
		cancelButton = new TextButton("Back to Menu", WE.getEngineView().getSkin());
		cancelButton.setPosition(900, 100);
		cancelButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.showMainMenu();
			}
		});
		stage.addActor(cancelButton);
                
        //set the center to the top left
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        font = new BitmapFont(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/arial.fnt"), true);
        font.setColor(Color.WHITE);
		
	}

	@Override
	public void renderImpl(float dt) {
		//update
		//stage.act(delta);
			
		
		//render
		 //clear & set background to black
        Gdx.gl20.glClearColor( 0.1f, 0f, 0f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //update camera and set the projection matrix
        camera.update();
        batch.setProjectionMatrix(camera.combined);
		shr.setProjectionMatrix(camera.combined);
        
		stage.draw();
				
        batch.begin();
			font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
			font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setTitle("Wurfelengine V" + WE.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
        Gdx.gl.glViewport(0, 0, width,height);
		//stage.setViewport(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	public void show() {
		WE.getEngineView().addInputProcessor(stage);
		WE.getEngineView().addInputProcessor(new InputListener());
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
		stage.dispose();
		batch.dispose();
		shr.dispose();
	}
	
	private class InputListener implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
			//update
			if (keycode == Input.Keys.ESCAPE)
				WE.showMainMenu();
			
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
