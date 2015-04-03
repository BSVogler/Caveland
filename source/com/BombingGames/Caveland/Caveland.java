package com.BombingGames.Caveland;

import com.BombingGames.Caveland.Game.ChunkGenerator;
import com.BombingGames.Caveland.Game.CustomBlockFactory;
import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.Caveland.GameObjects.Enemy;
import com.BombingGames.Caveland.GameObjects.Flint;
import com.BombingGames.Caveland.GameObjects.MineCart;
import com.BombingGames.Caveland.GameObjects.Spaceship;
import com.BombingGames.Caveland.GameObjects.Vanya;
import com.BombingGames.Caveland.MainMenu.MainMenuScreen;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.AbstractMap;
import com.BombingGames.WurfelEngine.Core.WorkingDirectory;
import com.BombingGames.WurfelEngine.WE;
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
	public static final String VERSION = "Alpha 2";
	
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CVar.register("worldSpinAngle", 50, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("shouldLoadMap", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableLightEngine", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableFog", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableAutoShade", false, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("groundBlockID", 1, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveX", 2, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveY", 15, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveZ", 10, CVar.CVarFlags.CVAR_ARCHIVE);
		
		//configure
        WE.setMainMenu(new MainMenuScreen());
		WorkingDirectory.setApplicationName("Caveland");
		Block.setCustomBlockFactory(new CustomBlockFactory());
		AbstractGameObject.setCustomSpritesheet("com/BombingGames/Caveland/Spritesheet");
		
		//register entities
		AbstractEntity.registerEntity("Flint", Flint.class);
		AbstractEntity.registerEntity("Lore", MineCart.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("CustomPlayer", CustomPlayer.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Enemy", Enemy.class);
		
		AbstractMap.setDefaultGenerator(new ChunkGenerator());
				
        WE.launch("Caveland " + VERSION, args);
		
		//unpack map
		if (!new File(WorkingDirectory.getMapsFolder()+"/default").exists()){
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/BombingGames/Caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		}
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
