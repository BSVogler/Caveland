package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Game.CustomGameController;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Map.Map;
import com.bombinggames.wurfelengine.core.WEScreen;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import java.io.File;

/**
 *A screen to select the save file.
 * @author Benedikt Vogler
 */
public class SaveSelectionScreen extends WEScreen {
	private SpriteBatch batch;
	private Stage stage;
	private final SelectBox<String> selectBox;
	private int coop;
	//private final Texture background;
	private final Sprite ship;

	/**
	 * 
	 * @param coop flag - -1 disable, 0 keyboard only, 1 one controller, 2 two controllers
	 * @param batch 
	 * @param background 
	 */
	public SaveSelectionScreen(int coop, SpriteBatch batch, Texture background) {
		this.batch = batch;
		this.coop = coop;
		//this.background = background;
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		Skin skin = WE.getEngineView().getSkin();
		
		this.ship = new Sprite(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/ship.png")));
		this.ship.setPosition(Gdx.graphics.getWidth()*1.5f, Gdx.graphics.getHeight()*0.7f);
		
		int savesCount = Map.getSavesCount(new File(WorkingDirectory.getMapsFolder()+"/default"));
		
		TextButton continueButton = new TextButton("Continue", skin);
		continueButton.setBounds(stage.getWidth()/2-400/2, stage.getHeight()/2+50,400,150);
		continueButton.addListener(
				new ChangeListener() {

					@Override
					public void changed(ChangeListener.ChangeEvent event, Actor actor) {
						startGame(false);
					}
			}
		);
		stage.addActor(continueButton);
		if (savesCount<1) {
			continueButton.setText("No previous game found.");
			continueButton.setDisabled(true);
			selectBox = null;
		} else {
			//save slot selection
			Array<String> arstr = new Array<>(savesCount);
			for (int i = 0; i < savesCount; i++) {
				arstr.add(Integer.toString(i));
			}
			Label selectBoxLabel = new Label("Save Slot", WE.getEngineView().getSkin());
			selectBoxLabel.setPosition(stage.getWidth()/2+400/2+50, stage.getHeight()/2+200);
			stage.addActor(selectBoxLabel);
			selectBox = new SelectBox<>(skin); 
			selectBox.setItems(arstr);
			selectBox.setWidth(40);
			selectBox.setBounds(stage.getWidth()/2+400/2+50, stage.getHeight()/2+150,50,50);
			stage.addActor(selectBox);
		}
		
		//new game button
		TextButton newgameButton = new TextButton("New Game...", skin);
		newgameButton.setBounds(stage.getWidth()/2-400/2, stage.getHeight()/2-200,400,150);
		newgameButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				startGame(true);
			}
		});
		stage.addActor(newgameButton);
		
		//back button
		TextButton backButton = new TextButton("Back…", skin);
		backButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				WE.showMainMenu();
			}
		});
		backButton.setPosition(20, 20);
		stage.addActor(backButton);
	}
	
	/**
	 *
	 * @param newslot if true uses new slot, if false uses slot selected in selection box
	 */
	public void startGame(boolean newslot){
		CustomGameView view = new CustomGameView();
		view.enableCoop(coop);
		
		CustomGameController controller = new CustomGameController();
		if (newslot)
			controller.newSaveSlot();
		else
			controller.useSaveSlot(selectBox.getSelectedIndex());
		if (coop >-1) controller.activatePlayer2();
		WE.initAndStartGame(controller, view, new CustomLoading());
	}
	
	@Override
	public void renderImpl(float dt) {
		Gdx.gl20.glClearColor( 0.36f, 0.76f, 0.98f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
			ship.setX(ship.getX()-dt);
			if (ship.getX()<-5000)
				ship.setX(Gdx.graphics.getWidth());
			ship.draw(batch);
			//batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
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
