/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 * 
 * Copyright 2014 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.BombingGames.WurfelEngine;

import com.BombingGames.WurfelEngine.Core.AbstractMainMenu;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMainMenu;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMenuItem;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Console;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.EngineView;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.BombingGames.WurfelEngine.Core.WEScreen;
import com.BombingGames.WurfelEngine.Core.WorkingDirectory;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import java.io.File;

/**
 *The Main class of the engine. To create a new engine use  {@link WE#launch(java.lang.String, java.lang.String[])}
 * The Wurfel Engine needs Java >1.7 and the API libGDX1.5.0
 * @author Benedikt S. Vogler
 * @version 1.4.15
 */
public class WE {
    /**
     * The version of the Engine
     */
    public static final String VERSION = "1.4.15";    
    private static File workingDirectory;
    private static boolean fullscreen = false;
    private static WEGame game;
    private static GameplayScreen gameplayScreen;
    private static AbstractMainMenu mainMenu;
    private static final AssetManager assetManager = new AssetManager();
    private static LwjglApplicationConfiguration config;
    private static Console console;
    private static EngineView engineView;
	private static LwjglApplication application;
	private static boolean skipintro =false;
	
    /**
     * Pass the mainMenu which get's displayed when you call launch().
     * @param mainMenu 
     * @see #launch() 
     */
    public static void setMainMenu(final AbstractMainMenu mainMenu) {
        WE.mainMenu = mainMenu;
    }
    
	/**
	 * set a screen as active
	 * @param screen 
	 */
	public static void setScreen(WEScreen screen){
		engineView.resetInputProcessors();
		game.setScreen(screen);
	}
    
    /**
     * Start the engine. You should have passed a main menu first.
	 * @param title The title, which is displayed in the window.
     * @param args launch parameters. For a list look in the wiki.
     * @see #setMainMenu(com.BombingGames.WurfelEngine.Core.MainMenuInterface)
     */
    public static void launch(final String title, final String[] args){
		System.out.println("Load Engine…");
		
		workingDirectory = WorkingDirectory.getWorkingDirectory();//set save-folder
		System.out.println("Init Engine CVars…");
		CVar.initEngineVars();
		
		//load cvars
		System.out.println("Loading Custom CVars…");
		CVar.loadFromFile();
		
		// set the name of the application menu item on mac
        if (System.getProperty("os.name").toLowerCase().contains("mac"))
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);
        
        config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
        config.fullscreen = true;
        config.vSyncEnabled = false;//if set to true the FPS is locked to 60
        config.foregroundFPS = CVar.get("limitFPS").getValuei();//don't lock FPS
		config.backgroundFPS = 60;//60 FPS in background
         
        //arguments
        if (args.length > 0){
            //look if contains launch parameters
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-fullscreen":
                    case "-f":
                        //start in fullscreen
                        config.fullscreen = true;
                        break;
                    case "-windowed":
                        //start in windowed mode
                        config.fullscreen = false;
                        break;
                    case "-w":
                        //set the width
                        config.width = Integer.parseInt(args[i+1]);
                        break;
                    case "-h":
                        //set the height
                        config.height = Integer.parseInt(args[i+1]);
                        break;
					case "-skipintro":
						skipintro=true;
                        break;
                }
            }
        }    
        
        config.title = title + " " + config.width + "x"+config.height;

        //LIBGDX: no equivalent found in libGDX yet
        //setUpdateOnlyWhenVisible(true);        
        //setMaximumLogicUpdateInterval(200);//delta can not be bigger than 200ms ^= 5 FPS
        //setMinimumLogicUpdateInterval(1);//delta can not be smaller than 1 ^= 1000FPS  
				
		//register entitys
		AbstractEntity.registerEngineEntities();
		
        System.out.println("Fire Engine…");
		game = new WEGame();
        application = new LwjglApplication(game, config);
        application.setLogLevel(Application.LOG_DEBUG);
    }

	public static LwjglApplicationConfiguration getLwjglApplicationConfiguration() {
		return config;
	}
    
    /**
     * Initialize the main game with you custom controller and view. This call shows the loadingScreen and disposes the menu.
     * @param controller
     * @param view 
	 * @param customLoadingScreen 
     * @see com.BombingGames.WurfelEngine.WE#startGame()
     */
    public static void initAndStartGame(final Controller controller, final GameView view, LoadingScreen customLoadingScreen){
        if (game != null) {
            Gdx.app.log("Wurfel Engine", "Initializing game using Controller:" + controller.toString());
            Gdx.app.log("Wurfel Engine", "and View:" + view.toString());
            Gdx.app.log("Wurfel Engine", "and Config:" + config.toString());
            
            if (gameplayScreen != null)
                gameplayScreen.dispose();//remove gameplayscreen if it already exists
            gameplayScreen = new GameplayScreen(
                controller,
                view,
				customLoadingScreen
            );
            getConsole().setGameplayRef(gameplayScreen);
			mainMenu.dispose();
        } else
            Gdx.app.error("Wurfel Engine", "You must construct a WE instance first before calling initGame.");
    }
    
    /**
     * Use this if you want to use different controller and views. This reinitializes them.
     * @param controller the new controller
     * @param view the new view
     */
    public static void switchSetupWithInit(final Controller controller, final GameView view){
        Gdx.app.debug("Wurfel Engine", "Switching setup and ReInit using Controller:" + controller.toString());
        Gdx.app.debug("Wurfel Engine", "and View:" + view.toString());
        engineView.resetInputProcessors();
		gameplayScreen.getController().exit();
        gameplayScreen.setController(controller);
        gameplayScreen.setView(view);
        //initialize
		Controller.getMap().getEntitys().clear();
        controller.init();
        view.init(controller);
        //enter
        controller.enter();
        view.enter();
    }
    
    /**
     * Use this if you want to continue to use a different controller and view.
     * @param controller the new controller
     * @param view the new view
     */
    public static void switchSetup(final Controller controller, final GameView view){
        Gdx.app.debug("Wurfel Engine", "Switching setup using Controller: " + controller.toString());
        Gdx.app.debug("Wurfel Engine", "and View: " + view.toString());
        engineView.resetInputProcessors();
        gameplayScreen.getController().exit();
        gameplayScreen.setController(controller);
        gameplayScreen.setView(view);
        //init if not initialized
        if (!controller.isInitalized()) controller.init();
        if (!view.isInitalized()) view.init(controller);
        //enter
        view.enter();
        controller.enter();
    }
    
    /**
     * Switch into the map editor
     * @param reverseMap reverse to the map at the point where you exited the editor?
     */
    public static void loadEditor(boolean reverseMap){
        gameplayScreen.getEditorController().setReverseMap(reverseMap);
        WE.switchSetup(gameplayScreen.getEditorController(), gameplayScreen.getEditorView());
    }
    
    /**
     * Starts the actual game using the custom gameplayScreen. This is called after the loading screen.
     */
    public static void startGame(){
		engineView.disposeMusic();
		Gdx.app.log("Wurfel Engine", "Starting the gameplay…");
		game.setScreen(gameplayScreen);
    }
    
     /**
     * Starts the actual game using the gameplayScreen you initialized with <i>initGame(Controller controller, GameView view)</i>. This is called after the loading screen.
     */
    public static void showMainMenu(){
        if (gameplayScreen != null) gameplayScreen.dispose();
        gameplayScreen = null;
        engineView.resetInputProcessors();
        game.setScreen(mainMenu);
    }
    
    /**
     * Get the credits of the engine.
     * @return a long string with breaks
     */
    public static String getCredits() {
        String newline = System.getProperty("line.separator");
        return "Created by:"+newline
            + "Benedikt Vogler"+newline+newline
            + "Programming:"+newline
            + "Benedikt Vogler"+newline+newline
            + "2D Art:"+newline
            + "Benedikt Vogler"+newline+newline
            + "3D Art"+newline
            + "Pia Lenßen"+newline
            + "Benedikt Vogler"+newline+newline
            + "Sound:"+newline
            + "Benedikt Vogler"+newline+newline
            + "Quality Assurance:"+newline
            + "Thomas Vogt"+newline+newline
            + "Want your name here? Contact me and join us!";
    }
    
   /**
     * Returns the save file folder, wich is different on every OS.
     * @return a folder
     */
    public static File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     *You can switch to fullscreen. It only works if the current window resolution is supported by your hardware.
     * @param fullscreen
     */
    public static void setFullscreen(final boolean fullscreen) {
        Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fullscreen);
        WE.fullscreen = Gdx.graphics.isFullscreen();
        Gdx.app.debug("Wurfel Engine","Set to fullscreen:"+fullscreen + " It is now:"+WE.isFullscreen());
    }
    
    /**
     *Get an asset from the asset manager
     * @param <T>
     * @param filename the name of the file
     * @return returns the asset
     */
    public static <T> T getAsset(String filename){
        return assetManager.get(filename);
    }

    /**
     *Check if the game is running in fullscreen.
     * @return true when running in fullscreen, false if in window mode
     */
    public static boolean isFullscreen() {
         if (game != null) {
            return fullscreen;
        } else {
            Gdx.app.error("Wurfel Engine", "There is no instance of the engine. You should call initGame first.");
            return false;
        }
    } 

    /**
     * To load assets you can use getAsset(String filename)
     * @return the asset manager.
     */
    public static AssetManager getAssetManager() {
        if (game != null) {
            return assetManager;
        } else {
            Gdx.app.error("Wurfel Engine", "There is no instance of the engine. You should call initGame first.");
            return null;
        }
    }
    
	/**
	 * 
	 * @return 
	 * @since 1.3.4
	 */
	public static boolean editorHasMapCopy(){
		if (gameplayScreen!=null && gameplayScreen.getEditorController()!=null)
			return gameplayScreen.getEditorController().hasMapSave();
		return false;
	}
	
    /**
     * Returns the Console. Use {@link com.BombingGames.WurfelEngine.Core.Console#add(java.lang.String) }to add messages to the console.
     * @return The console.
     */
    public static Console getConsole() {
        return console;
    }

    /**
     * 
     * @return Get the view independent view.
     */
    public static EngineView getEngineView() {
        return engineView;
    }
    
    
    
        /**
     * updates and render the global things e.g. the console
     * @param dt time in ms
     */
    public static void updateAndRender(float dt) {
        console.update(dt);
        engineView.getStage().act(dt);
        console.render(engineView.getBatch());
        engineView.getStage().draw();
    }

	/**
	 * should rarely used because allows global access
	 * @return 
	 */
	public static GameplayScreen getGameplay() {
		return gameplayScreen;
	}
	
	private static class WurfelEngineIntro extends WEScreen {
		private final Sprite lettering;
		private final SpriteBatch batch;
		private float alpha =0;
		private boolean increase =true;
		private final Sound startupsound;
		private final Interpolation interpolate;

		WurfelEngineIntro() {
			batch = new SpriteBatch();
			lettering = new Sprite(new Texture(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/BasicMainMenu/Images/Lettering.png")));
			lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth())/2);
			lettering.setY((Gdx.graphics.getHeight() - lettering.getHeight())/2);
			startupsound = Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/Sounds/startup.mp3"));
			startupsound.play();
			interpolate = new Interpolation.ExpOut(2, 7);
			//lettering.flip(false, true);
		}

		@Override
		public void renderImpl(float dt) {
			if (increase){
				if (alpha>=1){
					alpha=1;
					increase=false;
				} else
					alpha += dt/1500f;
				drawLettering();
			} else 
				if (alpha<=0){
					alpha=0;
					dispose();
				} else {
					alpha -= dt/1000f;
					drawLettering();
				}
		}
		
		void drawLettering(){
			lettering.setColor(1f, 1f, 1f, interpolate.apply(alpha));
						
			//clear & set background to black
			Gdx.gl20.glClearColor( 0f, 0f, 0f, 1f );
			Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
			batch.begin();
			lettering.draw(batch);
			batch.end();
		}

		@Override
		public void resize(int width, int height) {
		}

		@Override
		public void show() {
			Gdx.app.debug("Intro", "Showing intro…");
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
			startupsound.dispose();
			WE.showMainMenu();
		}
	}

	private static class WEGame extends Game {

		@Override
		public void create() {
			if (!skipintro)
				game.setScreen(new WurfelEngineIntro());
			
			if (mainMenu==null){
				Gdx.app.error("WEMain", "No main menu object could be found. Pass one with 'setMainMenu()' before launching.");
				Gdx.app.error("WEMain", "Using a predefined BasicMainMenu.");
				BasicMenuItem[] menuItems = new BasicMenuItem[]{
					new BasicMenuItem(0, "Test Engine", Controller.class, GameView.class),
					new BasicMenuItem(1, "Options"),
					new BasicMenuItem(2, "Exit")
				};   
				mainMenu = new BasicMainMenu(menuItems);
			}
			engineView = new EngineView();

			Gdx.app.debug("WE","Initializing main menu...");
			mainMenu.init();

			console = new Console(
				engineView.getSkin(),
				Gdx.graphics.getWidth()/2,
				Gdx.graphics.getHeight()/4
			);

			if (skipintro){
				setScreen(mainMenu);
			}
		}

		@Override
		public void dispose() {
			super.dispose();
			CVar.dispose();
		}
		
	}

}