package com.bombinggames.caveland.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Game.CLGameController;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.map.Map;
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
	private SelectBox<String> selectBox;
	private final int coop;
	//private final Texture background;
	private final Sprite ship;
	private SelectBox<String> mapSelection;
	private ArrayList<File> mapList;
	private Label selectBoxLabel;

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
		
		this.ship = new Sprite(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/ship.png")));
		this.ship.setPosition(Gdx.graphics.getWidth()*1.5f, Gdx.graphics.getHeight()*0.7f);
		
		Skin skin = WE.getEngineView().getSkin();
			
		
		mapList = new ArrayList<>(1);
		mapList.addAll(Arrays.asList(WorkingDirectory.getMapsFolder().listFiles()));
		mapList.removeIf(item -> !item.isDirectory());
		

		//map selection
		Label mapBoxLabel = new Label("Map", WE.getEngineView().getSkin());
		mapBoxLabel.setPosition(stage.getWidth()/2-400/2, stage.getHeight()*.8f);
		stage.addActor(mapBoxLabel);
		
		if (mapList.size() > 1){	
			mapSelection = new SelectBox<>(skin);
			//conver files to names
			Array<String> nameList = new Array<>(mapList.size());
			mapList.forEach((File i) -> {
				nameList.add(i.getName());
			});
			mapSelection.setItems(nameList);
			mapSelection.setBounds(stage.getWidth()/2-400/2, stage.getHeight()*.75f,150,50);
			mapSelection.addListener(new ChangeListener() {

				@Override
				public void changed(ChangeListener.ChangeEvent event, Actor actor) {
					setupMapButtons();
				}
			});
		}
		setupMapButtons();
	}
	
	private void setupMapButtons(){
		stage.addActor(mapSelection);
		
		Skin skin = WE.getEngineView().getSkin();
		
		int savesCount = Map.getSavesCount(new File(WorkingDirectory.getMapsFolder()+"/"+mapSelection.getSelected()));
		
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
		
		//can load save save?
		if (mapList.size() > 1 && savesCount < 1) {
			continueButton.setText("No previous game found.");
			continueButton.setDisabled(true);
			if (selectBoxLabel != null) {
				selectBoxLabel.remove();
				selectBoxLabel = null;
			}
			if (selectBox != null) {
				selectBox.remove();
				selectBox = null;
			}
		} else {
			//save slot selection
			selectBoxLabel = new Label("Save Slot", WE.getEngineView().getSkin());
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
		newEmptyMap.setBounds(stage.getWidth()*0.8f, stage.getHeight()*0.2f,200,50);
		newEmptyMap.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeListener.ChangeEvent event, Actor actor) {
				startGame(true, true);
			}
		});
		stage.addActor(newEmptyMap);
		
		Image newEmptyMapImage = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/grass.jpg")));
		newEmptyMapImage.setBounds(newEmptyMap.getX(), newEmptyMap.getY()+newEmptyMap.getHeight(), 200, 97);
		stage.addActor(newEmptyMapImage);
		
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
		CLGameView view = new CLGameView();
		view.enableCoop(coop);
		
		CLGameController controller = new CLGameController();
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
