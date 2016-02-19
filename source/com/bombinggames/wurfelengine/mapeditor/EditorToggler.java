package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.EngineView;
import com.bombinggames.wurfelengine.core.GameView;

/**
 * Shows buttons to enter and leave the editor.
 *
 * @author Benedikt Vogler
 */
public class EditorToggler {

	private Image pauseButton;
	private Image resetButton;
	private Image playButton;
	private final float offsetX = 50;
	private final float offsetY = 50;
	private GameView gameView;
	private boolean visible = true;

	/**
	 * Set the gameView which are used for the current game.
	 *
	 * @param view the gameView used for play-mode
	 */
	public void setGameplayManagers(GameView view) {
		this.gameView = view;
	}

	/**
	 * Adds the buttons to the stage if missing
	 *
	 * @param view The gameView which renders the buttons.
	 * @param dt
	 */
	public void update(EngineView view, float dt) {
		if (WE.isInEditor() && visible) {
			if (playButton == null && this.gameView != null) {
				TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");
				//add play button
				playButton = new Image(spritesheet.findRegion("play_button"));
				playButton.setX(Gdx.graphics.getWidth() - offsetX);
				playButton.setY(Gdx.graphics.getHeight() - offsetY);
				playButton.addListener(new PlayButton(this.gameView, false));
				view.getStage().addActor(playButton);
			}
		} else if (playButton != null) {
			playButton.remove();
			playButton = null;
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
							WE.startEditor();
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
							Controller.loadMap(Controller.getMap().getPath(), WE.getGameplay().getController().getSaveSlot());
							if (!WE.isInEditor()) {
								WE.startEditor();
							}
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

	/**
	 *
	 * @param visible
	 */
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

		private final boolean replay;
		private final GameView gameView;

		/**
		 *
		 * @param controller
		 * @param gameview
		 * @param replay ignored at the moment
		 */
		private PlayButton(GameView gameview, boolean replay) {
			this.gameView = gameview;
			this.replay = replay;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			WE.switchView(gameView, false);
			return true;
		}
	}

}
