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
 * Shows buttons to enter and leave the editor.
 * @author Benedikt Vogler
 */
public class EditorToggler {

	private Image pauseButton;
	private Image resetButton;
	private Image playButton;
	private final float offsetX = 50;
	private final float offsetY = 50;
	private Controller controller;
	private GameView view;
	private boolean visible = true;

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
		if (WE.isInEditor() && visible) {
			if (playButton == null && controller != null && this.view != null) {
				TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");
				//add play button
				playButton = new Image(spritesheet.findRegion("play_button"));
				playButton.setX(Gdx.graphics.getWidth() - offsetX);
				playButton.setY(Gdx.graphics.getHeight() - offsetY);
				playButton.addListener(new PlayButton(controller, this.view, false));
				view.getStage().addActor(playButton);
			}
		} else {
			if (playButton != null) {
				playButton.remove();
				playButton = null;
			}
		}
			
		if (WE.isInGameplay() && visible) {
			if (pauseButton == null || resetButton == null) {
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

				if (resetButton == null) {
					//add reverse editor button
					resetButton = new Image(spritesheet.findRegion("reset_button"));
					resetButton.setX(Gdx.graphics.getWidth() - offsetX * 2);
					resetButton.setY(Gdx.graphics.getHeight() - offsetY);
					resetButton.addListener(
						new ClickListener() {
							@Override
							public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
								Controller.loadMap(Controller.getMap().getPath(), controller.getSaveSlot());
								if (!WE.isInEditor())
									WE.startEditor(false);
								return true;
							}
						}
					);
				}
				view.getStage().addActor(resetButton);
				
				//stop button
				//if (WE.editorHasMapCopy()) {
	//				resetButton.addListener(
	//							new ClickListener() {
	//								@Override
	//								public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
	//									WE.startEditor(true);
	//									return true;
	//								}
	//							}
	//						);
				//}
			}
		} else {
			if (pauseButton != null) {
				pauseButton.remove();
				pauseButton = null;
			}
			if (resetButton != null) {
				resetButton.remove();
				resetButton = null;
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
		if (resetButton != null) {
			resetButton.remove();
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
		if (!visible) {
			if (playButton != null) {
				playButton.remove();
				playButton = null;
			}
			if (pauseButton != null) {
				pauseButton.remove();
				pauseButton = null;
			}
			if (resetButton != null) {
				resetButton.remove();
				resetButton = null;
			}
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
