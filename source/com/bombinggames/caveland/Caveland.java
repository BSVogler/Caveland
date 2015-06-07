package com.bombinggames.caveland;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.GameObjects.Bird;
import com.bombinggames.caveland.GameObjects.Enemy;
import com.bombinggames.caveland.GameObjects.Flint;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.caveland.GameObjects.SmokeEmitter;
import com.bombinggames.caveland.GameObjects.Spaceship;
import com.bombinggames.caveland.GameObjects.Vanya;
import com.bombinggames.caveland.MainMenu.MainMenuScreen;
import com.bombinggames.wurfelengine.Core.CVar.BooleanCVar;
import com.bombinggames.wurfelengine.Core.CVar.CVar;
import com.bombinggames.wurfelengine.Core.CVar.FloatCVar;
import com.bombinggames.wurfelengine.Core.CVar.IntCVar;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.Core.Gameobjects.Block;
import com.bombinggames.wurfelengine.Core.Map.AbstractMap;
import com.bombinggames.wurfelengine.Core.WorkingDirectory;
import com.bombinggames.wurfelengine.WE;
import java.io.File;
import java.io.InputStream;

/**
 *A test project to test if the engine can run as a library.
 * @author Benedikt Vogler
 */
public class Caveland {
	/**
	 * version string of the game Caveland
	 */
	public static final String VERSION = "Alpha 3";
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		WorkingDirectory.setApplicationName("Caveland");
		//game cvars
		WE.CVARS.register(new IntCVar(50), "worldSpinAngle", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "shouldLoadMap", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "enableLightEngine", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "enableFog", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(false), "enableAutoShade", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register( new BooleanCVar(true), "LEnormalMapRendering", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new BooleanCVar(true), "coopVerticalSplitScreen", CVar.CVarFlags.CVAR_ARCHIVE);
		WE.CVARS.register(new FloatCVar(150), "PlayerTimeTillImpact", CVar.CVarFlags.CVAR_ARCHIVE);
		
		//register map cvars
		AbstractMap.setCustomMapCVarRegistration(new CavelandMapCVars());
		
		//configure
        WE.setMainMenu(new MainMenuScreen());
		Block.setCustomBlockFactory(new CavelandBlocks());
		AbstractGameObject.setCustomSpritesheet("com/bombinggames/Caveland/Spritesheet");
		
		//register entities
		AbstractEntity.registerEntity("Emitter", SmokeEmitter.class);
		AbstractEntity.registerEntity("Flint", Flint.class);
		AbstractEntity.registerEntity("Mine Cart", MineCart.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("Portal", Portal.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Enemy", Enemy.class);
		AbstractEntity.registerEntity("Bird", Bird.class);
		
		AbstractMap.setDefaultGenerator(new ChunkGenerator());
				
        WE.launch("Caveland " + VERSION, args);
		
		//unpack map
		if (!new File(WorkingDirectory.getMapsFolder()+"/default").exists()){
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/Caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		} else {
			//checck if old format is already there. delete it
			if (new File(WorkingDirectory.getMapsFolder()+"/default/map.wem").exists()) {
				deleteDirectory(new File(WorkingDirectory.getMapsFolder()+"/default/"));
				InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/bombinggames/Caveland/defaultmap.zip");
				WorkingDirectory.unpackMap(
					"default",
					in
				);
			}
		}
    }

	public static boolean deleteDirectory(File directory) {
		if(directory.exists()){
			File[] files = directory.listFiles();
			if(null!=files){
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return(directory.delete());
	}
	
	/**
	 * Credtis of caveland.
	 * @return 
	 */
	public static String getCredits(){
		return "Caveland\n" +
			"\n" +
			"a game by\n" +
			"Benedikt S. Vogler\n" +
			"\n" +
			"Art\n" +
			"Frederic Brueckner\n" +
			"\n" +
			"Music & Sound\n" +
			"\"SteinImBrett\":\n" +
			"Felix von Dohlen\n" +
			"Marcel Gohsen\n" +
			"\n" +
			"Quality Assurance\n" +
			"Thomas Vogt\n" +
			"\n" +
			"Web Development\n" +
			"Rene Weiszer\n" +
			"\n" +
			"Special Thanks to\n" +
			"Felix Guenther\n" +
			"Vanya Gercheva\n" +
			"Ulrike Vogler\n" +
			"Gereon Vogler\n" +
			"Bernhard Vogler\n" +
			"Pia Lenszen\n" +
			"reddit.com/r/gamedev\n" +
			"Bauhaus University Weimar\n\n"
			+ "Wurfel Engine uses libGDX.\n";
	
	}	
    
}
