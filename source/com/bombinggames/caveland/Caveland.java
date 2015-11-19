package com.bombinggames.caveland;

import com.badlogic.gdx.audio.Sound;
import com.bombinggames.caveland.Game.CLGameController;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.GameObjects.Bird;
import com.bombinggames.caveland.GameObjects.ExitPortal;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.caveland.GameObjects.PathfindingTest;
import com.bombinggames.caveland.GameObjects.Robot;
import com.bombinggames.caveland.GameObjects.Spaceship;
import com.bombinggames.caveland.GameObjects.SpiderRobot;
import com.bombinggames.caveland.GameObjects.Vanya;
import com.bombinggames.caveland.GameObjects.collectibles.Bausatz;
import com.bombinggames.caveland.GameObjects.collectibles.TFlint;
import com.bombinggames.caveland.GameObjects.collectibles.TorchCollectible;
import com.bombinggames.caveland.MainMenu.CustomLoading;
import com.bombinggames.caveland.MainMenu.MainMenuScreen;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.CVar.BooleanCVar;
import com.bombinggames.wurfelengine.core.CVar.CVarSystem;
import com.bombinggames.wurfelengine.core.CVar.FloatCVar;
import com.bombinggames.wurfelengine.core.CVar.IntCVar;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.Map.Map;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
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
	public static final String VERSION = "Alpha 6";

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		WorkingDirectory.setApplicationName("Caveland");
		//game cvars
		WE.CVARS.register(new IntCVar(50), "worldSpinAngle");
		WE.CVARS.register(new BooleanCVar(true), "shouldLoadMap");
		WE.CVARS.register(new BooleanCVar(true), "enableLightEngine");
		WE.CVARS.register(new BooleanCVar(true), "enableFog");
		WE.CVARS.register(new BooleanCVar(false), "enableAutoShade");
		WE.CVARS.register(new BooleanCVar(true), "LEnormalMapRendering");
		WE.CVARS.register(new BooleanCVar(true), "coopVerticalSplitScreen");
		WE.CVARS.register(new FloatCVar(150), "PlayerTimeTillImpact");
		WE.CVARS.register(new BooleanCVar(false), "ignorePlayer");
		WE.CVARS.register(new BooleanCVar(false), "godmode");
		WE.CVARS.register(new FloatCVar(600f), "playerItemDropTime");//time in ms for item drop
		WE.CVARS.register(new FloatCVar(0.85f), "coopZoom");
		WE.CVARS.register(new BooleanCVar(false), "experimentalCameraJoin");
		
		//register map cvars
		CVarSystem.setCustomMapCVarRegistration(new CavelandMapCVars());

		//configure
		WE.setMainMenu(new MainMenuScreen());
		Block.setCustomBlockFactory(new CavelandBlocks());
		AbstractGameObject.setCustomSpritesheet("com/bombinggames/caveland/Spritesheet");

		//register entities
		AbstractEntity.registerEntity("Emitter Test", ParticleEmitter.class);
		AbstractEntity.registerEntity("TFlint", TFlint.class);
		AbstractEntity.registerEntity("Torch", TorchCollectible.class);
		AbstractEntity.registerEntity("Construction Kit", Bausatz.class);
		AbstractEntity.registerEntity("Mine Cart", MineCart.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Robot", Robot.class);
		AbstractEntity.registerEntity("Spider Robot", SpiderRobot.class);
		AbstractEntity.registerEntity("Bird", Bird.class);
		AbstractEntity.registerEntity("Pathfinding Test", PathfindingTest.class);
		AbstractEntity.registerEntity("Exit Portal", ExitPortal.class);

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
