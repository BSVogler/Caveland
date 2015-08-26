/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
	private final Controller controller;

	public EditorToggler(Controller controller) {
		this.controller = controller;
	}

	/**
	 * Adds the buttons to the stage if missing
	 *
	 * @param view The view which renders the buttons.
	 * @param dt
	 */
	public void update(GameView view, float dt) {

		if (view instanceof MapEditorView) {
			if (pauseButton != null) {
				pauseButton.remove();
				pauseButton = null;
			}
			if (stopButton != null) {
				stopButton.remove();
				stopButton = null;
			}
			
			if (playButton == null) {
				TextureAtlas spritesheet = WE.getAsset("com/bombinggames/wurfelengine/core/skin/gui.txt");
				//add play button
				playButton = new Image(spritesheet.findRegion("play_button"));
				playButton.setX(Gdx.graphics.getWidth() - offsetX);
				playButton.setY(Gdx.graphics.getHeight() - offsetY);
				playButton.addListener(new PlayButton(controller, false));
				view.getStage().addActor(playButton);
			}
		} else {
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
							WE.loadEditor(false);
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
								WE.loadEditor(true);
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

		private final MapEditorController controller;
		private final boolean replay;

		private PlayButton(Controller controller, boolean replay) {
			this.controller = (MapEditorController) controller;
			this.replay = replay;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			controller.switchToGame(replay);
			return true;
		}
	}

}
