/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.wurfelengine;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.bombinggames.wurfelengine.core.AbstractMainMenu;
import com.bombinggames.wurfelengine.core.BasicMainMenu.BasicMainMenu;
import com.bombinggames.wurfelengine.core.BasicMainMenu.BasicMenuItem;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.EngineView;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.Loading.LoadingScreen;
import com.bombinggames.wurfelengine.core.WEScreen;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.console.Console;
import com.bombinggames.wurfelengine.soundengine.SoundEngine;
import java.io.File;
import java.util.ArrayList;

/**
 * The main class of the engine. To create a new engine use
 * {@link WE#launch(String, String[])} The Wurfel Engine
 * needs Java &gt;= v1.8 and the API libGDX v1.6.2 (may work with older
 * versions).
 *
 * @author Benedikt S. Vogler
 * @version 1.6.6
 */
public class WE {

	/**
	 * The version of the Engine
	 */
	public static final String VERSION = "1.6.6";
	private static final File workingDirectory = WorkingDirectory.getWorkingDirectory();

	/**
	 * The CVar system used by the engine.
	 */
	private static CVarSystem cvars;
	/**
     *The sound engine managing the sfx.
     */
	public static final SoundEngine SOUND = new SoundEngine();
	private static final WEGame game = new WEGame();
	private static GameplayScreen gameplayScreen;
	private static AbstractMainMenu mainMenu;
	private static final AssetManager assetManager = new AssetManager();
	private static final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	private static Console console;
	private static EngineView engineView;
	private static LwjglApplication application;
	private static boolean skipintro = false;
	private static String iconPath = null;
	private static final ArrayList<LaunchCommand> commandsAfterLaunch = new ArrayList<>(0);
	/**
	 * default is that every manager state is game.
	 */
	private static boolean inGame = false;
	private static boolean inEditor = false;

	/**
	 * Pass the mainMenu which gets displayed when you call launch().
	 *
	 * @param mainMenu
	 * @see WE#launch(java.lang.String, java.lang.String[])
	 */
	public static void setMainMenu(final AbstractMainMenu mainMenu) {
		WE.mainMenu = mainMenu;
	}

	/**
	 * Set a screen as active.
	 *
	 * @param screen
	 */
	public static void setScreen(WEScreen screen) {
		engineView.resetInputProcessors();
		game.setScreen(screen);
	}

	/**
	 * Add commands which will be executed after the engine launched.
	 *
	 * @param command
	 */
	public static void addPostLaunchCommands(LaunchCommand command) {
		commandsAfterLaunch.add(command);
	}

	/**
	 * 
	 * @param internalPath 
	 */
	public static void addIcon(String internalPath) {
		iconPath = internalPath;
	}

	/**
	 * Start the engine. You should have passed a main menu first.<br> Until the
	 * engine is launched it can take a while. Code that can only be run after
	 * the engine has openend should be run in the screen class.
	 *
	 * @param title The title, which is displayed in the window.
	 * @param args Wurfel Engine launch parameters. For a list look in the wiki.
	 * @see #setMainMenu(com.bombinggames.wurfelengine.Core.MainMenuInterface)
	 */
	public static void launch(final String title, final String[] args) {
		config.resizable = false;
		config.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		config.fullscreen = true;
		config.vSyncEnabled = false;//if set to true the FPS is locked to 60
		
		//arguments
		if (args.length > 0) {
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
						config.width = Integer.parseInt(args[i + 1]);
						i++;
						break;
					case "-h":
						//set the height
						config.height = Integer.parseInt(args[i + 1]);
						i++;
						break;
					case "-skipintro":
						skipintro = true;
						break;
					case "-v":
						System.out.println(WE.VERSION);
						System.exit(0);
						return;
					default:
						System.out.println("Unknown launch parameter "+args[i]);
				}
			}
		}
		
		cvars = CVarSystem.getInstanceEngineSystem(new File(workingDirectory + "/engine.wecvars"));
		//load cvars
		cvars.load();

		// set the name of the application menu item on mac
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", title);
		}

		config.foregroundFPS = cvars.getValueI("limitFPS");//don't lock FPS
		config.backgroundFPS = 60;//60 FPS in background
		//config.addIcon("com/BombingGames/caveland/icon.png", Files.FileType.Internal); //commented this line because on mac this get's overwritten by something during runtime. mac build is best made via native packaging
		if (iconPath != null) {
			config.addIcon(iconPath, Files.FileType.Internal);//windows and linux?
		}
		
		//load saved resolution
		int width = cvars.getValueI("resolutionx");
		if (width > 0 && config.width == 0) {
			config.width = width;
		}

		int height = cvars.getValueI("resolutiony");
		if (height > 0 && config.height == 0) {
			config.height = cvars.getValueI("resolutiony");
		}
		
		//find dpm with biggest width
		Graphics.DisplayMode[] dpms = LwjglApplicationConfiguration.getDisplayModes();
		Graphics.DisplayMode maxDPM = dpms[0];
		for (Graphics.DisplayMode dpm : dpms) {
			if (dpm.width > maxDPM.width) {
				maxDPM = dpm;
			}
		}
		
		//limit resolution to maximum
		if (config.width > maxDPM.width)
			config.width = maxDPM.width;
		
		if (config.height > maxDPM.height)
			config.height = maxDPM.height;

		config.title = title + " " + config.width + "x" + config.height;

		//register entitys
		AbstractEntity.registerEngineEntities();

		System.out.println("Fire Engine…");
		application = new LwjglApplication(game, config);
		application.setLogLevel(Application.LOG_DEBUG);
	}

	/**
	 * returns the pointer to the LWJGL configuration of the game window.
	 *
	 * @return
	 */
	public static LwjglApplicationConfiguration getLwjglApplicationConfiguration() {
		return config;
	}

	/**
	 * Initialize the main game with you custom controller and view. This call
	 * shows the loadingScreen and disposes the menu.
	 *
	 * @param controller
	 * @param view
	 * @param customLoadingScreen
	 * @see com.bombinggames.wurfelengine.WE#startGame()
	 */
	public static void initAndStartGame(final Controller controller, final GameView view, LoadingScreen customLoadingScreen) {
		if (game != null) {
			Gdx.app.log("Wurfel Engine", "Initializing game using Controller:" + controller.toString());
			Gdx.app.log("Wurfel Engine", "and View:" + view.toString());
			Gdx.app.log("Wurfel Engine", "and Config:" + config.toString());

			getEngineView().getEditorToggler().setGameplayManagers(
				view
			);

			if (gameplayScreen != null) {
				gameplayScreen.dispose();//remove gameplayscreen if it already exists
			}
			gameplayScreen = new GameplayScreen(
				controller,
				view,
				customLoadingScreen
			);
			getConsole().setGameplayRef(gameplayScreen);
			mainMenu.dispose();
		} else {
			Gdx.app.error("Wurfel Engine", "You must construct a WE instance first before calling initGame.");
		}
	}

	/**
	 * Use different controller and views, which reinitializes them. Enter the
	 * editor via {@link #startEditor(boolean)}
	 *
	 * @param controller the new controller
	 * @param view the new view
	 */
	public static void switchSetupWithInit(final Controller controller, final GameView view) {
		Gdx.app.debug("Wurfel Engine", "Switching setup and ReInit using Controller:" + controller.toString());
		Gdx.app.debug("Wurfel Engine", "and View:" + view.toString());
		inGame = true;
		inEditor = false;
		getEngineView().getEditorToggler().setGameplayManagers(
			view
		);
		engineView.resetInputProcessors();
		gameplayScreen.getController().exit();
		gameplayScreen.getView().exit();
		gameplayScreen.setController(controller);

		//initialize
		Controller.getMap().disposeEntities();
		controller.init();
		view.init(controller, gameplayScreen.getView());
		
		gameplayScreen.setView(view);
		//enter
		controller.enter();
		view.enter();
	}

	/**
	 *
	 * @param controller
	 * @param view
	 */
	public static void switchSetup(final Controller controller, final GameView view) {
		switchSetup(controller, view, false);
	}

	/**
	 * Continue to use already initialized controller and view. Enter the editor
	 * via {@link #startEditor(boolean)}
	 *
	 * @param controller the new controller
	 * @param view the new view
	 * @param editor true if this is the editor setup
	 */
	private static void switchSetup(final Controller controller, final GameView view, final boolean editor) {
		Gdx.app.debug("Wurfel Engine", "Switching setup using Controller: " + controller.toString());
		Gdx.app.debug("Wurfel Engine", "and View: " + view.toString());
		inGame = true;
		inEditor = editor;
		if (!inEditor) {
			getEngineView().getEditorToggler().setGameplayManagers(
				view
			);
		}
		engineView.resetInputProcessors();
		gameplayScreen.getController().exit();
		gameplayScreen.getView().exit();
		gameplayScreen.setController(controller);

		//init if not initialized
		if (!controller.isInitalized()) {
			controller.init();
		}
		if (!view.isInitalized()) {
			view.init(controller, gameplayScreen.getView());
		}
		
		gameplayScreen.setView(view);
		//enter
		view.enter();
		controller.enter();
	}

	public static CVarSystem getCvars() {
		return cvars;
	}
	
	/**
	 * 
	 * @param view
	 * @param editor true if this is the editor setup
	 */
	public static void switchView(final GameView view, boolean editor){
		Gdx.app.debug("Wurfel Engine", "View switch: " + view.toString());
		inEditor = editor;
		if (!inEditor) {
			getEngineView().getEditorToggler().setGameplayManagers(
				view
			);
		}
		engineView.resetInputProcessors();
		gameplayScreen.getView().exit();

		//init if not initialized
		if (!view.isInitalized()) {
			view.init(gameplayScreen.getController(), gameplayScreen.getView());
		}
		gameplayScreen.setView(view);
		//enter
		view.enter();
	}

	/**
	 * Switch into the map editor. If the editor is already running it will restart it.
	 *
	 */
	public static void startEditor() {
		switchView(gameplayScreen.getEditorView(), true);
	}

	/**
	 *
	 * @return
	 */
	public static boolean isInEditor() {
		return inEditor;
	}

	/**
	 * Starts the actual game using the custom gameplayScreen. This is called
	 * after the loading screen.
	 */
	public static void startGame() {
		SOUND.disposeMusic();
		Gdx.app.log("Wurfel Engine", "Starting the gameplay…");
		game.setScreen(gameplayScreen);
		inGame = true;
		inEditor = false;
	}

	/**
	 *
	 * @return true when in gameplay (editor counts as gameplay)
	 */
	public static boolean isInGameplay() {
		return inGame;
	}

	/**
	 * Starts the actual game using the gameplayScreen you initialized with
	 * <i>initGame(Controller controller, GameView view)</i>. This is called
	 * after the loading screen.
	 */
	public static void showMainMenu() {
		if (gameplayScreen != null) {
			gameplayScreen.dispose();
		}
		inEditor = false;
		inGame = false;
		gameplayScreen = null;
		engineView.resetInputProcessors();
		game.setScreen(mainMenu);
	}

	/**
	 * Get the credits of the engine.
	 *
	 * @return a long string with breaks
	 */
	public final static String getCredits() {
		String newline = System.getProperty("line.separator");
		return "Wurfel Engine (" + VERSION + ")" + newline + newline
			+ "Created by:" + newline
			+ "Benedikt S. Vogler" + newline + newline
			+ "Quality Assurance:" + newline
			+ "Thomas Vogt" + newline + newline
			+ "Wurfel Engine uses libGDX." + newline;
	}

	/**
	 * Returns the save file folder, wich is different on every OS.
	 *
	 * @return a folder
	 */
	public static File getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * You can switch to fullscreen. It only works if the current window
	 * resolution is supported by your hardware.
	 *
	 * @param fullscreen
	 */
	public static void setFullscreen(final boolean fullscreen) {
		Gdx.graphics.setDisplayMode(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), fullscreen);
		config.fullscreen = Gdx.graphics.isFullscreen();
		Gdx.app.debug("Wurfel Engine", "Set to fullscreen:" + fullscreen + " It is now:" + WE.isFullscreen());
	}

	/**
	 * Get an asset from the asset manager
	 *
	 * @param <T>
	 * @param filename the name of the file
	 * @return returns the asset
	 */
	public static <T> T getAsset(String filename) {
		return assetManager.get(filename);
	}

	/**
	 * Check if the game is running in fullscreen.
	 *
	 * @return true when running in fullscreen, false if in window mode
	 */
	public static boolean isFullscreen() {
		if (game != null) {
			return Gdx.graphics.isFullscreen();
		} else {
			Gdx.app.error("Wurfel Engine", "There is no instance of the engine. You should call initGame first.");
			return false;
		}
	}

	/**
	 * To load assets you can use getAsset(String filename)
	 *
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
	 * Returns the Console. Use {@link com.bombinggames.wurfelengine.Core.Console#add(java.lang.String)
	 * }to add messages to the console.
	 *
	 * @return The console.
	 */
	public static Console getConsole() {
		if (console == null) {
			System.err.println("Engine not running yet.");
		}
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
	 *
	 * @param dt time in ms
	 */
	public static void updateAndRender(float dt) {
		console.update(dt);
		engineView.update(dt);
		SOUND.update(dt);
		
		engineView.getStage().act(dt);
		engineView.getStage().draw();
	}

	/**
	 * should rarely used because allows global access
	 *
	 * @return
	 */
	public static GameplayScreen getGameplay() {
		return gameplayScreen;
	}

	/**
	 *
	 * @return
	 */
	public static CVarSystem getLoadedCVarSystemMap() {
		return cvars.getChildSystem();
	}

	/**
	 *
	 * @return
	 */
	public static CVarSystem getLoadedCVarSystemSave() {
		if (cvars.getChildSystem() != null) {
			return cvars.getChildSystem().getChildSystem();
		}
		return null;
	}

	private static class WurfelEngineIntro extends WEScreen {

		private final Sprite lettering;
		private final SpriteBatch batch;
		private float alpha = 0;
		private boolean increase = true;
		private final Sound startupsound;
		private final Interpolation interpolate;

		WurfelEngineIntro() {
			batch = new SpriteBatch();
			lettering = new Sprite(new Texture(Gdx.files.internal("com/bombinggames/wurfelengine/core/BasicMainMenu/Images/Lettering.png")));
			lettering.setX((Gdx.graphics.getWidth() - lettering.getWidth()) / 2);
			lettering.setY((Gdx.graphics.getHeight() - lettering.getHeight()) / 2);
			startupsound = Gdx.audio.newSound(Gdx.files.internal("com/bombinggames/wurfelengine/soundengine/sounds/startup.mp3"));
			startupsound.play();
			interpolate = new Interpolation.ExpOut(2, 7);
			//lettering.flip(false, true);
		}

		@Override
		public void renderImpl(float dt) {
			if (increase) {
				if (alpha >= 1) {
					alpha = 1;
					increase = false;
				} else {
					alpha += dt / 1500f;
				}
				drawLettering();
			} else if (alpha <= 0) {
				alpha = 0;
				dispose();
			} else {
				alpha -= dt / 1000f;
				drawLettering();
			}
		}

		void drawLettering() {
			lettering.setColor(1f, 1f, 1f, interpolate.apply(alpha));

			//clear & set background to black
			Gdx.gl20.glClearColor(0f, 0f, 0f, 1f);
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
			if (!skipintro) {
				game.setScreen(new WurfelEngineIntro());
			}

			if (mainMenu == null) {
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
			engineView.init(null, null);

			console = new Console(
				engineView.getSkin(),
				50,
				Gdx.graphics.getHeight() - 700
			);

			Gdx.app.debug("WE", "Initializing main menu...");
			mainMenu.init();

			if (skipintro) {
				setScreen(mainMenu);
			}

			commandsAfterLaunch.forEach(a -> {
				a.perform();
			});
		}

		@Override
		public void dispose() {
			super.dispose();
			cvars.dispose();
			SOUND.dispose();
		}

	}

}
