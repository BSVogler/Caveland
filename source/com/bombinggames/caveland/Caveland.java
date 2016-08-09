package com.bombinggames.caveland;

import com.badlogic.gdx.audio.Sound;
import com.bombinggames.caveland.game.CLGameController;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.game.ChunkGenerator;
import com.bombinggames.caveland.gameobjects.Bird;
import com.bombinggames.caveland.gameobjects.ExitPortal;
import com.bombinggames.caveland.gameobjects.MineCart;
import com.bombinggames.caveland.gameobjects.PathfindingTest;
import com.bombinggames.caveland.gameobjects.Quadrocopter;
import com.bombinggames.caveland.gameobjects.Robot;
import com.bombinggames.caveland.gameobjects.Shopkeeper;
import com.bombinggames.caveland.gameobjects.Spaceship;
import com.bombinggames.caveland.gameobjects.SpiderRobot;
import com.bombinggames.caveland.gameobjects.Vanya;
import com.bombinggames.caveland.gameobjects.collectibles.ConstructionKit;
import com.bombinggames.caveland.gameobjects.collectibles.TFlint;
import com.bombinggames.caveland.gameobjects.collectibles.TorchCollectible;
import com.bombinggames.caveland.mainmenu.CustomLoading;
import com.bombinggames.caveland.mainmenu.MainMenuScreen;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.cvar.BooleanCVar;
import com.bombinggames.wurfelengine.core.cvar.CVarSystemMap;
import com.bombinggames.wurfelengine.core.cvar.FloatCVar;
import com.bombinggames.wurfelengine.core.cvar.IntCVar;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.map.Map;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.io.File;
import java.io.InputStream;

/**
 *
 * @author Benedikt Vogler
 */
public class Caveland {

	/**
	 * version string of the game Caveland
	 */
	public static final String VERSION = "Alpha 7";

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		WorkingDirectory.setApplicationName("Caveland");
		//game cvars
		WE.getCVars().register(new IntCVar(50), "worldSpinAngle");
		WE.getCVars().register(new BooleanCVar(true), "shouldLoadMap");
		WE.getCVars().register(new BooleanCVar(true), "enableLightEngine");
		WE.getCVars().register(new BooleanCVar(true), "enableFog");
		WE.getCVars().register(new BooleanCVar(false), "enableAutoShade");
		WE.getCVars().register(new BooleanCVar(true), "LEnormalMapRendering");
		WE.getCVars().register(new BooleanCVar(true), "coopVerticalSplitScreen");
		WE.getCVars().register(new FloatCVar(150), "PlayerTimeTillImpact");
		WE.getCVars().register(new BooleanCVar(false), "ignorePlayer");
		WE.getCVars().register(new BooleanCVar(false), "godmode");
		WE.getCVars().register(new FloatCVar(600f), "playerItemDropTime");//time in ms for item drop
		WE.getCVars().register(new FloatCVar(0.85f), "coopZoom");
		WE.getCVars().register(new BooleanCVar(false), "experimentalCameraJoin");
		WE.getCVars().register(new FloatCVar(400f), "jetpackMaxTime");
		WE.getCVars().register(new FloatCVar(0.03f), "jetpackPower");
		WE.getCVars().register(new FloatCVar(5f), "jetpackMaxSpeed");
		
		//register map cvars
		CVarSystemMap.setCustomMapCVarRegistration(new CavelandMapCVars());

		//configure
		WE.setMainMenu(new MainMenuScreen());
		RenderCell.setCustomBlockFactory(new CavelandBlocks());
		AbstractGameObject.setCustomSpritesheet("com/bombinggames/caveland/Spritesheet");

		//register entities
		AbstractEntity.registerEntity("Emitter Test", ParticleEmitter.class);
		AbstractEntity.registerEntity("TFlint", TFlint.class);
		AbstractEntity.registerEntity("Torch", TorchCollectible.class);
		AbstractEntity.registerEntity("Construction Kit", ConstructionKit.class);
		AbstractEntity.registerEntity("Mine Cart", MineCart.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Robot", Robot.class);
		AbstractEntity.registerEntity("Spider Robot", SpiderRobot.class);
		AbstractEntity.registerEntity("Bird", Bird.class);
		AbstractEntity.registerEntity("Pathfinding Test", PathfindingTest.class);
		AbstractEntity.registerEntity("Exit Portal", ExitPortal.class);
		AbstractEntity.registerEntity("Quadrocopter", Quadrocopter.class);
		AbstractEntity.registerEntity("Shopkeeper", Shopkeeper.class);

		Map.setDefaultGenerator(new ChunkGenerator());

		if (args.length > 0) {
			//look if contains launch parameters
			for (String arg : args) {
				switch (arg) {
					case "-quickstart":
						WE.addPostLaunchCommands(() -> {
							CLGameController controller = new CLGameController();
							controller.useSaveSlot(0);
							WE.initAndStartGame(controller, new CLGameView(), new CustomLoading());
						});
						break;
				}
			}
		}

		WE.addPostLaunchCommands(() -> {
			WE.getConsole().addCommand(new GiveCommand());
			WE.getConsole().addCommand(new PortalTargetCommand());
			WE.getConsole().addCommand(new TeleportPlayerCommand());

			//load the needed assets
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/menusound.wav", Sound.class);
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/menusoundAbort.wav", Sound.class);
			WE.getAssetManager().load("com/bombinggames/caveland/MainMenu/bong.wav", Sound.class);
			WE.getAssetManager().finishLoading();

			WE.SOUND.register("menuSelect", "com/bombinggames/caveland/MainMenu/menusound.wav");
			WE.SOUND.register("menuAbort", "com/bombinggames/caveland/MainMenu/menusoundAbort.wav");
			WE.SOUND.register("menuConfirm", "com/bombinggames/caveland/MainMenu/bong.wav");
		});

		WE.launch("Caveland " + VERSION, args);

		//unpack map
		if (!new File(WorkingDirectory.getMapsFolder() + "/default").exists()) {
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		} else //checck if old format is already there. delete it. also delete if there is 
		if (new File(WorkingDirectory.getMapsFolder() + "/default/map.wem").exists()) {
			WorkingDirectory.deleteDirectory(new File(WorkingDirectory.getMapsFolder() + "/default/"));
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		}
	}

	/**
	 * Credtis of caveland.
	 *
	 * @return
	 */
	public static String getCredits() {
		return "Caveland\n"
			+ "\n"
			+ "a game by\n"
			+ "Benedikt S. Vogler\n"
			+ "\n"
			+ "Art\n"
			+ "Frederic Brueckner\n"
			+ "\n"
			+ "Music & Sound\n"
			+ "\"SteinImBrett\":\n"
			+ "Felix von Dohlen\n"
			+ "Marcel Gohsen\n"
			+ "\n"
			+ "Special Thanks to\n"
			+ "Felix Guenther\n"
			+ "Vanya Gercheva\n"
			+ "Ulrike Vogler\n"
			+ "Gereon Vogler\n"
			+ "Rene Weiszer\n"
			+ "Bernhard Vogler\n"
			+ "Gianluca Pandolfo\n"
			+ "Thomas Vogt\n"
			+ "\"Janosch\" Friedrich\n"
			+ "Pia Lenszen\n"
			+ "reddit.com/r/gamedev\n"
			+ "Bauhaus University Weimar\n\n"
			+ "Wurfel Engine uses libGDX.\n";
	}
}
