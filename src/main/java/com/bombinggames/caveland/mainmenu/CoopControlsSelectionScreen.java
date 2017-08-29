package com.bombinggames.caveland.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.WEScreen;

/**
 *
 * @author Benedikt Vogler
 */
public class CoopControlsSelectionScreen extends WEScreen {
	private final Texture content;
	private Stage stage;
	private final SpriteBatch batch;
	//private final Texture mainbg;

	/**
	 *
	 * @param batch
	 * @param background
	 */
	public CoopControlsSelectionScreen(final SpriteBatch batch, Texture background) {
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		this.batch = batch;
		
		this.content = new Texture(Gdx.files.internal("com/bombinggames/caveland/mainmenu/controlScreen.png"));
		//this.mainbg = background;
		
		//stage
		if (Controllers.getControllers().size <= 1) {
			Label label = new Label("No second controller found.", WE.getEngineView().getSkin());
			label.setPosition(400, 600);
			stage.addActor(label);
		}
		
		if (Controllers.getControllers().size > 0) {
			TextButton splitControlsButton = new TextButton("Choose", WE.getEngineView().getSkin());
			splitControlsButton.setPosition(600, 150);
			splitControlsButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					WE.setScreen(new SaveSelectionScreen(1, batch, null));
				}
			});
			stage.addActor(splitControlsButton);
		} else {
			Label label = new Label("No controller found.", WE.getEngineView().getSkin());
			label.setPosition(600, 150);
			stage.addActor(label);
		}
		
		TextButton twoKeyboardButton = new TextButton("Choose", WE.getEngineView().getSkin());
		twoKeyboardButton.setPosition(stage.getWidth()*0.7f, 150);
		twoKeyboardButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.setScreen(new SaveSelectionScreen(0, batch, null));
			}
		});
		stage.addActor(twoKeyboardButton);
		
		TextButton backButton = new TextButton("Back", WE.getEngineView().getSkin());
		backButton.setPosition(100, 50);
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
		Gdx.gl20.glClearColor( 0.36f, 0.76f, 0.98f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
			batch.draw(content, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
