package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.WurfelEngine.Core.CVar.CVar;
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
    private BitmapFont font;
	private final CheckBox fullscreenCB;
	private final CheckBox vsyncCB;
	private final TextButton applyButton;
	private final TextButton cancelButton;
	private final CheckBox limitFPSCB;
	private final Slider musicSlider;
	private final Slider soundSlider;

	/**
	 *
	 * @param batch
	 */
	public OptionScreen(SpriteBatch batch) {
		this.batch = batch;
		shr = new ShapeRenderer();
		OrthographicCamera libgdxcamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		stage = new Stage(new ScreenViewport(libgdxcamera), batch);
		final SelectBox<String> sbox = new SelectBox<>(WE.getEngineView().getSkin());
		if (CVar.getValueB("DevMode"))  {
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
			sbox.setWidth(stage.getWidth()/6);
			sbox.setPosition(stage.getWidth()/2-300, 500);
		
			stage.addActor(sbox);
		}
		
		musicSlider = new Slider(0, 1, 0.1f,false, WE.getEngineView().getSkin());
		musicSlider.setPosition(stage.getWidth()/2-300, 400);
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
		soundSlider.setPosition(stage.getWidth()/2-300, 350);
		soundSlider.setValue(1f);
		stage.addActor(soundSlider);
		
		fullscreenCB = new CheckBox("Fullscreen", WE.getEngineView().getSkin());
		fullscreenCB.setPosition(stage.getWidth()/2+100, 600);
		
		stage.addActor(fullscreenCB);
		
		vsyncCB = new CheckBox("V-Sync", WE.getEngineView().getSkin());
		vsyncCB.setPosition(stage.getWidth()/2+100, 500);
		stage.addActor(vsyncCB);
		
		limitFPSCB = new CheckBox("limit FPS (recommended)", WE.getEngineView().getSkin());
		limitFPSCB.setChecked(CVar.getValueI("limitFPS") > 0);
		limitFPSCB.setPosition(stage.getWidth()/2+100, 400);
		stage.addActor(limitFPSCB);
		
		applyButton = new TextButton("Apply", WE.getEngineView().getSkin());
		applyButton.setPosition(stage.getWidth()/2-100, 100);
		applyButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				Gdx.graphics.setVSync(vsyncCB.isChecked());
				if (CVar.getValueB("DevMode"))  {
					Graphics.DisplayMode dpm = Gdx.graphics.getDisplayModes()[sbox.getSelectedIndex()];
					Gdx.graphics.setDisplayMode(dpm.width, dpm.height, fullscreenCB.isChecked());
				}
				
				//get FPS limit
				if (limitFPSCB.isChecked())
					CVar.get("limitFPS").setValue("60");
				else CVar.get("limitFPS").setValue("0");
				WE.getLwjglApplicationConfiguration().foregroundFPS = CVar.getValueI("limitFPS");
				
				//apply sound changes
				CVar.get("music").setValue(musicSlider.getValue());
				WE.getEngineView().setMusicLoudness(musicSlider.getValue());
				CVar.get("sound").setValue(soundSlider.getValue());
			}
		});
		stage.addActor(applyButton);
		
		cancelButton = new TextButton("Back to Menu", WE.getEngineView().getSkin());
		cancelButton.setPosition(stage.getWidth()/2+100, 100);
		cancelButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.showMainMenu();
			}
		});
		stage.addActor(cancelButton);
                
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
        
		stage.draw();
				
        batch.begin();
			font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
			if (CVar.getValueB("DevMode")) font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setTitle("Wurfelengine V" + WE.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
		stage.getViewport().setWorldSize(width, height);
        Gdx.gl.glViewport(0, 0, width,height);
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
