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

import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMainMenu;
import com.BombingGames.WurfelEngine.Core.BasicMainMenu.BasicMenuItem;
import com.BombingGames.WurfelEngine.Core.Configuration;
import com.BombingGames.WurfelEngine.Core.Console;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.EngineView;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.MainMenuInterface;
import com.BombingGames.WurfelEngine.Core.WorkingDirectory;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.Texture;
import java.io.File;

/**
 *The Main class of the engine. To create a new engine use  {@link com.BombingGames.WurfelEngine.WE#construct(java.lang.String, java.lang.String[]) }
 * The Wurfel Engine needs Java >1.7 and the API libGDX0.9.9
 * @author Benedikt S. Vogler
 * @version 1.2.26
 */
public class WE extends Game {
    /**
     * The version of the Engine
     */
    public static final String VERSION = "1.2.26";    
    private static File workingDirectory;
    private static boolean fullscreen = false;
    private static WE instance;
    private static GameplayScreen gameplayScreen;
    private static MainMenuInterface mainMenu;
    private static final AssetManager assetManager = new AssetManager();
    private static LwjglApplicationConfiguration config;
    private static Console console;

    /**
     * Create the Engine. Don't use this constructor. Use construct() instead. 
     * @param title The title, which is displayed in the window.
     * @param args custom display resolution: [0] width, [1] height, [2] fullscreen
     */
    private WE(String title, final String[] args){       
        // set the name of the application menu item on mac
        if (System.getProperty("os.name").toLowerCase().contains("mac"))
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);
        
        config = new LwjglApplicationConfiguration();
        
        config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
        config.fullscreen = true;
        config.vSyncEnabled = false;//if set to true the FPS is locked to 60
        config.foregroundFPS = 0;//don't lock FPS
        config.useGL20 = false;
         
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
                }
            }
        }    
        
        config.title = title + " " + config.width + "x"+config.height;

        workingDirectory = WorkingDirectory.getWorkingDirectory("Wurfel Engine");//set save-folder
        
        Texture.setEnforcePotImages(false);//allow non-power-of-two textures on system which support them
       
        //LIBGDX: no equivalent found in libGDX yet
        //setUpdateOnlyWhenVisible(true);        
        //setMaximumLogicUpdateInterval(200);//delta can not be bigger than 200ms ^= 5 FPS
        //setMinimumLogicUpdateInterval(1);//delta can not be smaller than 1 ^= 1000FPS  
    }
    
    /**
         * You don't have to manually call this method. Starts init() process
         */
    @Override
    public void create() {
        if (mainMenu==null){
            Gdx.app.error("WEMain", "No main menu object could be found. Pass one with 'setMainMenu()' before launching.");
            Gdx.app.error("WEMain", "Using a predefined BasicMainMenu.");
            BasicMenuItem[] menuItems = new BasicMenuItem[]{
                new BasicMenuItem(0, "Test Engine", Controller.class, GameView.class, new Configuration()),
                new BasicMenuItem(1, "Options"),
                new BasicMenuItem(2, "Exit")
            };   
            mainMenu = new BasicMainMenu(menuItems);
        }
        System.out.println("Initializing main menu...");
        mainMenu.init();
        EngineView.init();
        setScreen(mainMenu);
        
        console = new Console();
        console.init(EngineView.getSkin(),Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/4);
    }

    /**
     * Pass the mainMenu which get's displayed when you call launch().
     * @param mainMenu 
     * @see  #launch() 
     */
    public static void setMainMenu(final MainMenuInterface mainMenu) {
        WE.mainMenu = mainMenu;
    }
    
   /**
     * Create a new instance of the engine.
     * @param title The title, which is displayed in the window.
     * @param args launch parameters. For a list look in the wiki.
     */
    public static void construct(final String title, final String[] args){
        instance = new WE(title,args);
    }
    
    /**
     * Start the engine. You have to pass a main menu first.
     * @see #setMainMenu(com.BombingGames.WurfelEngine.Core.MainMenuInterface)
     */
    public static void launch(){
        System.out.println("Launching engine...");
        LwjglApplication application = new LwjglApplication(instance, config);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }
    
    /**
     * Singleton method to get the only living instance.
     * @return the wurfelengine's main class containing everything
     */
    public static WE getInstance(){
        return instance;
    }
    
    /**
     * Initialize the main game with you custom controller and view. This call shows the loadingScreen.
     * @param controller
     * @param view 
     * @param config 
     * @see com.BombingGames.WurfelEngine.WE#startGame()
     */
    public static void initGame(final Controller controller, final GameView view, final Configuration config){
        if (instance != null) {
            Gdx.app.log("Wurfel Engine", "Initializing game using Controller:" + controller.toString());
            Gdx.app.log("Wurfel Engine", "and View:" + view.toString());
            Gdx.app.log("Wurfel Engine", "and Config:" + config.toString());
            
            if (gameplayScreen != null)
                gameplayScreen.dispose();//remove gameplayscreen if it already exists
            gameplayScreen = new GameplayScreen(
                controller,
                view,
                config
            );
            getConsole().setGameplayRef(gameplayScreen);
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
        GameView.resetInputProcessors();
        gameplayScreen.setController(controller);
        gameplayScreen.setView(view);
        //initialize
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
        Gdx.app.debug("Wurfel Engine", "Switching setup using Controller:" + controller.toString());
        Gdx.app.debug("Wurfel Engine", "and View:" + view.toString());
        GameView.resetInputProcessors();
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
     * Starts the actual game using the gameplayScreen you initialized with <i>initGame(Controller controller, GameView view)</i>. This is called after the loading screen.
     */
    public static void startGame(){
        if (instance != null) {
            Gdx.app.log("Wurfel Engine", "Starting the gameplay…");
            instance.setScreen(gameplayScreen);
        } else
            Gdx.app.error("Wurfel Engine", "You should call initGame first.");
    }
    
     /**
     * Starts the actual game using the gameplayScreen you initialized with <i>initGame(Controller controller, GameView view)</i>. This is called after the loading screen.
     */
    public static void showMainMenu(){
        if (gameplayScreen != null) gameplayScreen.dispose();
        gameplayScreen = null;
        instance.setScreen(mainMenu);
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
         if (instance != null) {
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
        if (instance != null) {
            return assetManager;
        } else {
            Gdx.app.error("Wurfel Engine", "There is no instance of the engine. You should call initGame first.");
            return null;
        }
    }
    
    /**
     *
     * @return the config in use
     */
    public static Configuration getCurrentConfig(){
        if (gameplayScreen==null)
            return new Configuration();
        return gameplayScreen.getConfig();
    }
    
    /**
     * Returns the Console. Use {@link com.BombingGames.WurfelEngine.Core.Console#add(java.lang.String) }to add messages to the console.
     * @return The console.
     */
    public static Console getConsole() {
        return console;
    }
    
        /**
     * updates and render the global things e.g. the console
     * @param delta time in ms
     */
    public static void updateAndRender(float delta) {
        console.update(delta);
        EngineView.getStage().act(delta);
        console.render(EngineView.getBatch());
        EngineView.getStage().draw();
    }
}