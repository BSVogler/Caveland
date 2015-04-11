package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.WurfelEngine.Core.WEScreen;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 *
 * @author Benedikt Vogler
 */
public class SaveSelectionScreen extends WEScreen {
	private SpriteBatch batch;
	private Stage stage;

	public SaveSelectionScreen(SpriteBatch batch) {
		this.batch = batch;
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		Skin skin = WE.getEngineView().getSkin();
		
		TextButton continueButton = new TextButton("Continue", skin);
		continueButton.setPosition(500, 600);
		stage.addActor(continueButton);
		
		TextButton newgameButton = new TextButton("New Game…", skin);
		newgameButton.setPosition(500, 500);
		stage.addActor(newgameButton);
		
		Array<String> arstr = new Array<>(2);
		arstr.add("Some random text that"); 
		arstr.add("isn't being displayed!"); 
		SelectBox<Object> sb = new SelectBox<>(skin); 
		sb.setWidth(40);
		sb.setItems(arstr);
		sb.setPosition(600, 600);
		stage.addActor(sb);
		
		TextButton backButton = new TextButton("Back…", skin);
		backButton.setPosition(20, 20);
		stage.addActor(backButton);
	}
	
	

	@Override
	public void renderImpl(float dt) {
		Gdx.gl20.glClearColor( 0.1f, 0f, 0f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
//		batch.begin();
//			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        batch.end();
		stage.act(dt);
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
