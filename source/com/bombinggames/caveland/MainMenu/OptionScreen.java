package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Game.ActionBox;
import static com.bombinggames.caveland.MainMenu.MainMenuScreen.MANAGER;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.WEScreen;
import com.bombinggames.wurfelengine.core.WorkingDirectory;

/**
 *
 * @author Benedikt Vogler
 */
public class OptionScreen extends WEScreen {
	private final Stage stage;
	private final ShapeRenderer shr;
    private final SpriteBatch batch;
    private final BitmapFont font;
	private CheckBox fullscreenCB;
	private CheckBox vsyncCB;
	private TextButton applyButton;
	private TextButton cancelButton;
	private CheckBox limitFPSCB;
	private Slider musicSlider;
	private Slider soundSlider;
	private CheckBox aoCB;

	/**
	 *
	 * @param batch
	 */
	public OptionScreen(SpriteBatch batch) {
		this.batch = batch;
		shr = new ShapeRenderer();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		
		fillStage();
		
        font = new BitmapFont();
        font.setColor(Color.WHITE);
	}
	
	private void fillStage() {
		final SelectBox<String> sbox = new SelectBox<>(WE.getEngineView().getSkin());
		//fill with display modes
		DisplayMode[] dpms = Gdx.graphics.getDisplayModes();
		//try to find current displaymode
		Array<String> arstr = new Array<>();
		int indexCurrentDPM = 0;
		for (int i = 0; i < dpms.length; i++) {
			arstr.add(dpms[i].toString().substring(0, dpms[i].toString().indexOf(',')));
			if (dpms[i].width == Gdx.graphics.getWidth() && dpms[i].height == Gdx.graphics.getHeight()) {
				indexCurrentDPM = i;
			}
		}

		sbox.setItems(arstr);
		sbox.setSelectedIndex(indexCurrentDPM);
		sbox.setWidth(150);
		sbox.setPosition(stage.getWidth()/2-300, 500);

		stage.addActor(sbox);
		
		
		Label musicLabel = new Label("Music volume", WE.getEngineView().getSkin());
		musicLabel.setPosition(stage.getWidth()/2-300, 420);
		stage.addActor(musicLabel);
		musicSlider = new Slider(0, 1, 0.1f, false, WE.getEngineView().getSkin());
		musicSlider.setPosition(stage.getWidth()/2-300, 400);
		musicSlider.setValue(WE.getCVars().getValueF("music"));
		musicSlider.addListener(
			new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					WE.getCVars().get("music").setValue(musicSlider.getValue());
				}
			}
		);
		stage.addActor(musicSlider);
		
		Label soundLabel = new Label("Sound volume", WE.getEngineView().getSkin());
		soundLabel.setPosition(stage.getWidth()/2-300, 370);
		stage.addActor(soundLabel);
		
		soundSlider = new Slider(0, 1, 0.1f, false, WE.getEngineView().getSkin());
		soundSlider.setPosition(stage.getWidth() / 2 - 300, 350);
		soundSlider.setValue(1f);
		stage.addActor(soundSlider);

		vsyncCB = new CheckBox("V-Sync", WE.getEngineView().getSkin());
		vsyncCB.setPosition(stage.getWidth() / 2 + 100, 400);
		stage.addActor(vsyncCB);
		
		fullscreenCB = new CheckBox("Fullscreen", WE.getEngineView().getSkin());
		fullscreenCB.setPosition(stage.getWidth() / 2 + 100, 450);
		fullscreenCB.setChecked(Gdx.graphics.isFullscreen());
		stage.addActor(fullscreenCB);
		
		limitFPSCB = new CheckBox("limit FPS (recommended)", WE.getEngineView().getSkin());
		limitFPSCB.setChecked(WE.getCVars().getValueI("limitFPS") > 0);
		limitFPSCB.setPosition(stage.getWidth() / 2 + 100, 500);
		stage.addActor(limitFPSCB);
		
		aoCB = new CheckBox("Ambient Occlusion", WE.getEngineView().getSkin());
		aoCB.setPosition(stage.getWidth() / 2 + 100, 550);
		aoCB.setChecked(WE.getCVars().getValueF("ambientocclusion")>0);
		stage.addActor(aoCB);

		applyButton = new TextButton("Apply", WE.getEngineView().getSkin());
		applyButton.setPosition(stage.getWidth() / 2 - 100, 100);
		applyButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				Gdx.graphics.setVSync(vsyncCB.isChecked());
				Graphics.DisplayMode dpm = Gdx.graphics.getDisplayModes()[sbox.getSelectedIndex()];
				//Gdx.graphics.setDisplayMode(dpm.width, dpm.height, fullscreenCB.isChecked());
				WE.getCVars().get("resolutionx").setValue(dpm.width);
				WE.getCVars().get("resolutiony").setValue(dpm.height);

				MainMenuScreen.MANAGER.setActionBox(
					stage,
					new ActionBox("Restart", ActionBox.BoxModes.SIMPLE, "You need to restart the game in order to aply the resolution.")
				);
				
				//get FPS limit
				if (limitFPSCB.isChecked()) {
					WE.getCVars().get("limitFPS").setValue(60);
				} else {
					WE.getCVars().get("limitFPS").setValue(0);
				}
				WE.getLwjglApplicationConfiguration().foregroundFPS = WE.getCVars().getValueI("limitFPS");
				
				if (aoCB.isChecked()) {
					WE.getCVars().get("ambientocclusion").setValue(0.5f);
				} else {
					WE.getCVars().get("ambientocclusion").setValue(0f);
				}
				
				//apply sound changes
				WE.getCVars().get("music").setValue(musicSlider.getValue());
				WE.getCVars().get("sound").setValue(soundSlider.getValue());
			}
		});
		stage.addActor(applyButton);
		addButton(applyButton);
		
		cancelButton = new TextButton("Back to Menu", WE.getEngineView().getSkin());
		cancelButton.setPosition(stage.getWidth() / 2 + 100, 100);
		cancelButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.showMainMenu();
			}
		});
		stage.addActor(cancelButton);
		addButton(cancelButton);
		
		TextButton resetButton = new TextButton("Reset Game (erases save files)", WE.getEngineView().getSkin());
		resetButton.setPosition(stage.getWidth()/2+100, 300);
		resetButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				ActionBox warning = new ActionBox("Reset Game", ActionBox.BoxModes.SIMPLE, "Warning! You are about to reinstall the game. All save games will be lost. Use right mouse button to cancel. Use left mosue button to confirm that you want to reset the game.");
				warning.setConfirmAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
					WorkingDirectory.delete();
					System.exit(0);//exiting the game writes the cvars
				});
				MainMenuScreen.MANAGER.setActionBox(
					stage,
					warning
				);
				
			}
		});
		stage.addActor(resetButton);
	}
	

	@Override
	public void renderImpl(float dt) {
		//update
		stage.act(dt);
			
		//render
		 //clear & set background to black
        Gdx.gl20.glClearColor( 0.36f, 0.76f, 0.98f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
		stage.draw();
				
        batch.begin();
			font.draw(batch, "FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 20);
			if (WE.getCVars().getValueB("DevMode"))
				font.draw(batch, Gdx.input.getX()+ ","+Gdx.input.getY(), Gdx.input.getX(), Gdx.input.getY());
        batch.end();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.graphics.setTitle("Wurfelengine V" + WE.VERSION + " " + Gdx.graphics.getWidth() + "x"+Gdx.graphics.getHeight());
		//stage.getViewport().setWorldSize(width, height);
        HdpiUtils.glViewport(0, 0, width, height);
//		stage.clear();
//		stage = new Stage( new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
//		fillStage();
//		WE.getEngineView().addInputProcessor(stage);
//		WE.getEngineView().addInputProcessor(new InputListener());
	}

	@Override
	public void show() {
		WE.getEngineView().addInputProcessor(MANAGER);
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
