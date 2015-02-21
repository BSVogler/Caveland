package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.Caveland.Game.CustomGameController;
import com.BombingGames.Caveland.Game.CustomGameView;
import com.BombingGames.WurfelEngine.Core.WEScreen;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
	private final SpriteBatch batch;
	private final OrthographicCamera camera;

	public CoopControlsSelectionScreen(SpriteBatch batch) {
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		this.batch = batch;
		
		background = new Texture(Gdx.files.internal("com/BombingGames/Caveland/MainMenu/controlScreen.jpg"));
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//stage
		if (Controllers.getControllers().size > 1) {//setDisabled(true) does not work
			TextButton twoControllersButton = new TextButton("Choose", WE.getEngineView().getSkin());
			twoControllersButton.setPosition(200, 600);
			twoControllersButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					CustomGameView view = new CustomGameView();
					view.enableCoop(2);
					WE.initAndStartGame(new CustomGameController(), view, new CustomLoading());
				}
			});
			stage.addActor(twoControllersButton);
		} else {
			Label label = new Label("No second controller found.", WE.getEngineView().getSkin());
			label.setPosition(200, 600);
			stage.addActor(label);
		}
		
		
		if (Controllers.getControllers().size > 0) {
			TextButton splitControlsButton = new TextButton("Choose", WE.getEngineView().getSkin());
			splitControlsButton.setPosition(800, 400);
			splitControlsButton.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					CustomGameView view = new CustomGameView();
					view.enableCoop(1);
					WE.initAndStartGame(new CustomGameController(), view, new CustomLoading());
				}
			});
			stage.addActor(splitControlsButton);
		} else {
			Label label = new Label("No controller found.", WE.getEngineView().getSkin());
			label.setPosition(800, 400);
			stage.addActor(label);
		}
		
		TextButton twoKeyboardButton = new TextButton("Choose", WE.getEngineView().getSkin());
		twoKeyboardButton.setPosition(1200, 200);
		twoKeyboardButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				CustomGameView view = new CustomGameView();
				view.enableCoop(0);
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
