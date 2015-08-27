package com.bombinggames.wurfelengine.MapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;

/**
 *
 * @author Benedikt Vogler
 */
public class EditorToggler {

	private Image pauseButton;
	private Image stopButton;
	private Image playButton;
	private final float offsetX = 50;
	private final float offsetY = 50;
	private Controller controller;
	private GameView view;

	/**
	 * @param controller the controller used for play-mode
	 * @param view the view used for play-mode
	 */
	public void setGameplayManagers(Controller controller, GameView view) {
		this.controller = controller;
		this.view = view;
	}

	/**
	 * Adds the buttons to the stage if missing
	 *
	 * @param view The view which renders the buttons.
	 * @param dt
	 */
	public void update(GameView view, float dt) {
		if (WE.isInEditor()) {
			if (pauseButton != null) {
				pauseButton.remove();
				pauseButton = null;
			}
			if (stopButton != null) {
				stopButton.remove();
				stopButton = null;
			}

			if (playButton == null && controller != null && this.view != null) {
				TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");
				//add play button
				playButton = new Image(spritesheet.findRegion("play_button"));
				playButton.setX(Gdx.graphics.getWidth() - offsetX);
				playButton.setY(Gdx.graphics.getHeight() - offsetY);
				playButton.addListener(new PlayButton(controller, this.view, false));
				view.getStage().addActor(playButton);
			}
		} else if (WE.isInGame()) {
			if (playButton != null) {
				playButton.remove();
				playButton = null;
			}

			if (pauseButton == null || stopButton == null) {
				TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");

				if (pauseButton == null) {
					//add editor button
					pauseButton = new Image(spritesheet.findRegion("pause_button"));
					pauseButton.setX(Gdx.graphics.getWidth() - offsetX);
					pauseButton.setY(Gdx.graphics.getHeight() - offsetY);
					pauseButton.addListener(
						new ClickListener() {
						@Override
						public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
							WE.startEditor(false);
							return true;
						}
					}
					);
				}
				view.getStage().addActor(pauseButton);

				if (WE.editorHasMapCopy()) {
					if (stopButton == null) {
						//add reverse editor button
						stopButton = new Image(spritesheet.findRegion("stop_button"));
						stopButton.setX(Gdx.graphics.getWidth() - offsetX * 2);
						stopButton.setY(Gdx.graphics.getHeight() - offsetY);
						stopButton.addListener(
							new ClickListener() {
							@Override
							public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
								WE.startEditor(true);
								return true;
							}
						}
						);
					}
					view.getStage().addActor(stopButton);
				}
			}
		}
	}

	/**
	 * disposes the dev tool
	 */
	public void dispose() {
		if (pauseButton != null) {
			pauseButton.remove();
		}
		if (stopButton != null) {
			stopButton.remove();
		}
	}

	private static class PlayButton extends ClickListener {

		private final Controller controller;
		private final boolean replay;
		private final GameView gameview;

		/**
		 *
		 * @param controller
		 * @param gameview
		 * @param replay ignored at the moment
		 */
		private PlayButton(Controller controller, GameView gameview, boolean replay) {
			this.controller = controller;
			this.gameview = gameview;
			this.replay = replay;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			WE.switchSetupWithInit(controller, gameview);
			return true;
		}
	}

}
