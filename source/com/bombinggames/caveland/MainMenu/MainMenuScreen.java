package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.bombinggames.caveland.Caveland;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.AbstractMainMenu;

/**
 *
 * @author Benedikt Vogler
 */
public class MainMenuScreen extends AbstractMainMenu {
	private final MenuItem[] menuItems = new MenuItem[5];
	private Stage stage;
	private Image lettering;
	private Image alphaTag; 
    private Texture background;
    private SpriteBatch batch;
    private BitmapFont font;
    private float alpha =0;
	private int selectionIndex =0;
	
	private Sound selectionSound;
	private Sound abortSound;


	@Override
	public void init() {
		batch = new SpriteBatch();
		
		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);
		
		//load textures
        lettering = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/Lettering.png")));
		lettering.scaleBy(WE.getEngineView().getEqualizationScale()-1);
		stage.addActor(lettering);
		alphaTag = new Image(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/alphaTag.png")));
		alphaTag.scaleBy(WE.getEngineView().getEqualizationScale()-1);
		stage.addActor(alphaTag);
		
		MenuItem button1Player = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/1player.png")));
		button1Player.setPosition(
			stage.getWidth()/2-button1Player.getWidth()/2-200,
			stage.getHeight()/2-200
		);
		stage.addActor(button1Player);
		menuItems[0] = button1Player;
		button1Player.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				WE.setScreen(new SaveSelectionScreen(-1,batch, background));
				return true;
			}
		});
		
		MenuItem button2Player = new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/2players.png")));
		button2Player.setPosition(
			stage.getWidth()/2-button1Player.getWidth()/2+200,
			stage.getHeight()/2-200
		);
		stage.addActor(button2Player);
		menuItems[1] = button2Player;
		button2Player.addAction(new Action() {
			@Override
			public boolean act(float delta) {
				WE.setScreen(new CoopControlsSelectionScreen(batch, background));
				return true;
			}
		});
		
		//add buttons
		int i=2;
		final int top = (int) (stage.getHeight()*0.1f);
		
		menuItems[i]= new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_options.png")));
		menuItems[i].addAction(new Action() {
			@Override
			public boolean act(float delta) {
				WE.setScreen(new OptionScreen(batch));
				return true;
			}
		});
		menuItems[i].setPosition(stage.getWidth()/2-400, top);
		
		i++;
		menuItems[i]= new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_credits.png")));
		menuItems[i].addAction(new Action() {
			@Override
			public boolean act(float delta) {
				WE.setScreen(new CreditsScreen());
				return true;
			}
		});
			menuItems[i].setPosition(stage.getWidth()/2-menuItems[i].getWidth()/2, top);
		
		i++;
		menuItems[i]= new MenuItem(new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/mi_exit.png")));
		menuItems[i].addAction(new Action() {
			@Override
			public boolean act(float delta) {
				Gdx.app.exit();
				return true;
			}
		});
		menuItems[i].setPosition(stage.getWidth()/2+150, top);
		
		for (Image menuItem : menuItems) {
			stage.addActor(menuItem);
		}
		
        background = new Texture(Gdx.files.internal("com/bombinggames/caveland/MainMenu/background.jpg"));
		
        font = new BitmapFont();
		
		selectionSound = Gdx.audio.newSound(Gdx.files.internal("com/bombinggames/caveland/MainMenu/menusound.wav"));
		//to-do play when button is pressed
		abortSound = Gdx.audio.newSound(Gdx.files.internal("com/bombinggames/caveland/MainMenu/menusoundAbort.wav"));
	}

	@Override
	public void renderImpl(float dt) {
		//update

		alpha += dt/1000f;
		if (alpha>1) alpha=1;
		
		
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
			Gdx.graphics.getHeight()- lettering.getHeight()*lettering.getScaleY()-30
			+ (Gdx.input.getY()/(float) Gdx.graphics.getHeight()-0.5f)*10//move by cursor
		);
		//lettering.setScale(WE.getEngineView().getEqualizationScale());
		alphaTag.setX(
			(Gdx.graphics.getWidth() - alphaTag.getWidth()*alphaTag.getScaleX())/2
			- (Gdx.input.getX()/(float) Gdx.graphics.getWidth()-0.6f)*5//move by cursor
			+300*alphaTag.getScaleX()
		);
        alphaTag.setY(
			Gdx.graphics.getHeight()- (alphaTag.getHeight()+350)*alphaTag.getScaleY()
			+ (Gdx.input.getY()/(float) Gdx.graphics.getHeight()-0.6f)*5//move by cursor
		);
		//alphaTag.setScale(WE.getEngineView().getEqualizationScale());
		
		//render
		 //clear & set background to black
        Gdx.gl20.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        //Background        
        batch.begin();
			batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			// render the lettering
			lettering.setColor(1, 1, 1, alpha);

			font.draw(batch, Caveland.VERSION+", FPS:"+ Gdx.graphics.getFramesPerSecond(), 20, 30);
        batch.end();
		
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().setWorldSize(width, height);
	}

	@Override
	public void show() {
		WE.getEngineView().addInputProcessor(stage);
		WE.getEngineView().addInputProcessor(new InputListener());
		if (!WE.getEngineView().isMusicPlaying())
			WE.getEngineView().setMusic(Gdx.files.internal("com/bombinggames/caveland/music/title.mp3").path());
		abortSound.play();
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
	
	private class InputListener implements InputProcessor {

        @Override
        public boolean keyDown(int keycode) {
            if (keycode == Keys.DOWN || keycode == Keys.S){
                selectionIndex++;
				selectionSound.play();
				if (selectionIndex>=menuItems.length)
					selectionIndex=0;
			}
			
            if (keycode == Keys.UP || keycode == Keys.W){
				selectionIndex--;
				selectionSound.play();
				if (selectionIndex<0)
					selectionIndex=menuItems.length-1;
			}
			
            if (keycode == Keys.ENTER || Gdx.input.isKeyPressed(Keys.SPACE))
                menuItems[selectionIndex].fire(new ChangeEvent());
			
            return true;
			
        }

        @Override
        public boolean keyUp(int keycode) {
            return true;
        }

        @Override
        public boolean keyTyped(char character) {
            return true;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return true;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return true;
        }

        @Override
        public boolean scrolled(int amount) {
            return true;
        }
    }
	
}
