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
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *A screen to select the save file.
 * @author Benedikt Vogler
 */
public class SaveSelectionScreen extends WEScreen {
	private final SpriteBatch batch;
	private final Stage stage;
	private final SelectBox<String> selectBox;
	private final int coop;
	//private final Texture background;
	private final Sprite ship;
	private SelectBox<String> mapSelection;

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
		ArrayList<File> mapList = new ArrayList<>(1);
		mapList.addAll(Arrays.asList(WorkingDirectory.getMapsFolder().listFiles()));
		mapList.removeIf(item -> !item.isDirectory());
		
		TextButton continueButton = new TextButton("Continue", skin);
		continueButton.setBounds(stage.getWidth()/2-400/2, stage.getHeight()/2+50,400,150);
		continueButton.addListener(
				new ChangeListener() {

					@Override
					public void changed(ChangeListener.ChangeEvent event, Actor actor) {
						startGame(false, false);
					}
			}
		);
		stage.addActor(continueButton);
		
		if (mapList.size() > 1){
			//map selection
			Label mapBoxLabel = new Label("Map", WE.getEngineView().getSkin());
			mapBoxLabel.setPosition(stage.getWidth()/2+400/2+50, stage.getHeight()/2+200);
			stage.addActor(mapBoxLabel);
			
			mapSelection = new SelectBox<>(skin);
			//conver files to names
			Array<String> nameList = new Array<>(mapList.size());
			mapList.forEach((File i) -> {
				nameList.add(i.getName());
			});
			mapSelection.setItems(nameList);
			mapSelection.setBounds(stage.getWidth()/2+400/2+50, stage.getHeight()/2+150,150,50);
			stage.addActor(mapSelection);
		}
		
		//can load save save?
		if (mapList.size() > 1 && savesCount < 1) {
			continueButton.setText("No previous game found.");
			continueButton.setDisabled(true);
			selectBox = null;
		} else {
			//save slot selection
			Label selectBoxLabel = new Label("Save Slot", WE.getEngineView().getSkin());
			selectBoxLabel.setPosition(stage.getWidth()/2+400/2+50, stage.getHeight()/2+100);
			stage.addActor(selectBoxLabel);
			
			selectBox = new SelectBox<>(skin);
			Array<String> arstr = new Array<>(savesCount);
			for (int i = 0; i < savesCount; i++) {
				arstr.add(Integer.toString(i));
			}
			selectBox.setItems(arstr);
			selectBox.setBounds(stage.getWidth()/2+400/2+50, stage.getHeight()/2+50,50,50);
			stage.addActor(selectBox);
		}
		
		//new game button
		TextButton newgameButton = new TextButton("New Game", skin);
		newgameButton.setBounds(stage.getWidth()/2-400/2, stage.getHeight()/2-200,400,150);
		newgameButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				startGame(true, false);
			}
		});
		stage.addActor(newgameButton);
		
		//new empty map button
		TextButton newEmptyMap = new TextButton("New empty map", skin);
		newEmptyMap.setBounds(stage.getWidth()*0.8f, stage.getHeight()*0.2f,200,100);
		newEmptyMap.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				startGame(true, true);
			}
		});
		stage.addActor(newEmptyMap);
		
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
	 * Start the game.
	 * @param newslot if true uses new slot, if false uses slot selected in selection box
	 * @param newMap if a new map should be generated or the default used
	 */
	public void startGame(boolean newslot, boolean newMap){
		CustomGameView view = new CustomGameView();
		view.enableCoop(coop);
		
		CustomGameController controller = new CustomGameController();
		if (newMap) {
			controller.setMapName(Integer.toString(Math.abs(ZonedDateTime.now().hashCode())));
		} else {
			controller.setMapName(mapSelection.getSelected());
			if (newslot)
				controller.newSaveSlot();
			else
				controller.useSaveSlot(selectBox.getSelectedIndex());
		}
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
