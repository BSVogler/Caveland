package com.bombinggames.caveland.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Caveland;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.AbstractMainMenu;

/**
 *
 * @author Benedikt Vogler
 */
public class MainMenuScreen extends AbstractMainMenu {

	/**
	 *
	 */
	public static final ModalDialogueManager MANAGER = new ModalDialogueManager();
	
	/**
	 * for acces via keys
	 */
	private final MenuItem[] menuItems = new MenuItem[5];
	private Stage stage;
	private Image lettering;
	private Image alphaTag;
	private Texture background;
	private SpriteBatch batch;
	private BitmapFont font;
	private float alpha = 0;
	private int selectionIndex = 0;

	private float backgroundPosY;
	private boolean fadeout;
	private Action fadeOutAction;


	@Override
	public void init() {
		batch = new SpriteBatch();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		
		//load textures
        lettering = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/mainmenu/lettering.png")));
		lettering.scaleBy(WE.getEngineView().getEqualizationScale()-1);
		stage.addActor(lettering);
		alphaTag = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/mainmenu/alphaTag.png")));
		alphaTag.scaleBy(WE.getEngineView().getEqualizationScale()-1);
		stage.addActor(alphaTag);
		
		//one player button
		Image onePlayer = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/mainmenu/1player.png")));
		onePlayer.setPosition(
			stage.getWidth()/2-onePlayer.getWidth()/2-200,
			stage.getHeight()*0.25f
		);
		stage.addActor(onePlayer);
		
		MenuItem button1Player = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/mainmenu/1playerButton.png")));
		button1Player.setPosition(
			stage.getWidth()/2-button1Player.getWidth()/2-200,
			stage.getHeight()*0.2f
		);
		
		menuItems[0] = button1Player;
		button1Player.addAction(
			new Action() {
				@Override
				public boolean act(float delta) {
					fadeOut(
						new Action() {

							@Override
							public boolean act(float delta) {
								WE.setScreen(new SaveSelectionScreen(-1,batch, background));
								return true;
							}
						}
					);
					return true;
				}
			}
		);
		
		//two players game
		Image twoPlayer = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/2players.png")));
		twoPlayer.setPosition(
			stage.getWidth()/2-twoPlayer.getWidth()/2+200,
			stage.getHeight()*0.25f
		);
		stage.addActor(twoPlayer);
		
		MenuItem button2Player = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/2playersButton.png")));
		button2Player.setPosition(
			stage.getWidth()/2-button1Player.getWidth()/2+200,
			stage.getHeight()*0.2f
		);
		menuItems[1] = button2Player;
		button2Player.addAction(
			new Action() {
				@Override
				public boolean act(float delta) {
					fadeOut(
						new Action() {

							@Override
							public boolean act(float delta) {
								WE.setScreen(new CoopControlsSelectionScreen(batch, background));
								return true;
							}
						}
					);
					return true;
				}
			}
		);
		
		//add buttons
		int i=2;
		final int top = (int) (stage.getHeight()*0.05f);
		
		menuItems[i] = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_options.png")));
		menuItems[i].addAction(
			new Action() {
			@Override
			public boolean act(float delta) {
				fadeOut(
					new Action() {

					@Override
					public boolean act(float delta) {
						WE.setScreen(new OptionScreen(batch));
						return true;
					}
				}
				);
				return true;
			}
		}
		);
		menuItems[i].setPosition(stage.getWidth() / 2 - 400, top-20);

		i++;
		menuItems[i] = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_credits.png")));
		menuItems[i].addAction(new Action() {
			@Override
			public boolean act(float delta) {
				WE.setScreen(new CreditsScreen());
				return true;
			}
		});
		menuItems[i].setPosition(stage.getWidth() / 2 - menuItems[i].getWidth() / 2, top);

		i++;
		menuItems[i] = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_exit.png")));
		menuItems[i].addAction(new Action() {
			@Override
			public boolean act(float delta) {
				Gdx.app.exit();
				return true;
			}
		});
		menuItems[i].setPosition(stage.getWidth() / 2 + 150, top);

		for (Image menuItem : menuItems) {
			stage.addActor(menuItem);
			addButton(menuItem);
		}

		background = new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/background.jpg"));
		backgroundPosY = -Gdx.graphics.getHeight();

		font = new BitmapFont();
	}

	@Override
	public void renderImpl(float dt) {
		//update

		//tweening on start
		if (alpha >= 1) {
			alpha=1;
		} else {
			alpha += dt/1000f;
		}
		
		if (fadeout==false) {
			backgroundPosY += dt;
			if (backgroundPosY >= 0) {
				backgroundPosY = 0;
			} 
		} else {
				backgroundPosY -= dt*3; //3px/ms
				
			if (backgroundPosY <= -Gdx.graphics.getHeight()) {
				backgroundPosY = -Gdx.graphics.getHeight();
				fadeout=false;
				fadeOutAction.act(dt);
			}
		}
		
		for (int i = 0; i < menuItems.length; i++) {
			Image menuItem = menuItems[i];
			if 	(i==selectionIndex)
				menuItem.setColor(1, 0, 0, 1);
			else menuItem.setColor(1, 1, 1, 1);
		}
		
		lettering.setX(
			(Gdx.graphics.getWidth() - lettering.getWidth()*lettering.getScaleX())/2
			- (Gdx.input.getX()/(float) Gdx.graphics.getWidth()-0.5f)*10//move by cursor
		);
        lettering.setY(
			Gdx.graphics.getHeight()- lettering.getHeight()*lettering.getScaleX()-10
			+ (Gdx.input.getY()/(float) Gdx.graphics.getHeight()-0.5f)*10//move by cursor
		);
		//lettering.setScale(WE.getEngineView().getEqualizationScale());
		alphaTag.setX(
			(Gdx.graphics.getWidth() - alphaTag.getWidth()*alphaTag.getScaleX())/2
			- (Gdx.input.getX()/(float) Gdx.graphics.getWidth()-0.6f)*5//move by cursor
			+300*alphaTag.getScaleX()
		);
        alphaTag.setY(
			Gdx.graphics.getHeight()- (alphaTag.getHeight()+250)*alphaTag.getScaleY()
			+ (Gdx.input.getY()/(float) Gdx.graphics.getHeight()-0.6f)*5//move by cursor
		);
		//alphaTag.setScale(WE.getEngineView().getEqualizationScale());
		
		
		//fadeout
		
		//render
		 //clear & set background to black
        Gdx.gl20.glClearColor(0.36f, 0.76f, 0.98f, 1f);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //Background        
		batch.begin();
			batch.draw(background, 0, backgroundPosY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		  // render the lettering
			lettering.setColor(1, 1, 1, alpha);

		  font.draw(batch, Caveland.VERSION + ", FPS:" + Gdx.graphics.getFramesPerSecond(), 20, 30);
		batch.end();

		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setWorldSize(width, height);
	}

	@Override
	public void show() {
		WE.getEngineView().addInputProcessor(MANAGER);
		WE.getEngineView().addInputProcessor(stage);
		WE.getEngineView().addInputProcessor(new MenuInputListener(this));
		if (!WE.SOUND.isMusicPlaying() && WE.getCVars().getValueF("music") > 0)
			WE.SOUND.setMusic(Gdx.files.internal("com/bombinggames/caveland/music/title.mp3").path());
		WE.SOUND.play("menuAbort");
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
	}
	
	/**
	 * first fades out then performs action
	 * @param action 
	 */
	public void fadeOut(Action action){
		fadeout = true;
		fadeOutAction = action;
	}
	
	
}
