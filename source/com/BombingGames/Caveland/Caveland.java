package com.BombingGames.Caveland;

import com.BombingGames.Caveland.Game.CustomBlockFactory;
import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.Caveland.GameObjects.Enemy;
import com.BombingGames.Caveland.GameObjects.Flint;
import com.BombingGames.Caveland.GameObjects.Lore;
import com.BombingGames.Caveland.GameObjects.Spaceship;
import com.BombingGames.Caveland.GameObjects.Vanya;
import com.BombingGames.Caveland.MainMenu.MainMenuScreen;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
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
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        CVar.register("worldSpinAngle", 50, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("shouldLoadMap", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableLightEngine", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableFog", true, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("enableAutoShade", false, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("groundBlockID", 1, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveX", 0, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveY", 0, CVar.CVarFlags.CVAR_ARCHIVE);
		CVar.register("PlayerLastSaveZ", 0, CVar.CVarFlags.CVAR_ARCHIVE);
		
		//configure
        WE.setMainMenu(new MainMenuScreen());
		WorkingDirectory.setApplicationName("Caveland");
		Block.setCustomBlockFactory(new CustomBlockFactory());
		AbstractGameObject.setCustomSpritesheet("com/BombingGames/Caveland/Spritesheet");
		
		//register entities
		AbstractEntity.registerEntity("Flint", Flint.class);
		AbstractEntity.registerEntity("Lore", Lore.class);
		AbstractEntity.registerEntity("Spaceship", Spaceship.class);
		AbstractEntity.registerEntity("CustomPlayer", CustomPlayer.class);
		AbstractEntity.registerEntity("Vanya", Vanya.class);
		AbstractEntity.registerEntity("Enemy", Enemy.class);
        WE.launch("Wurfelengine V" + WE.VERSION, args);
		
		//unpack map
		if (!new File(WorkingDirectory.getMapsFolder()+"/default").exists()){
			InputStream in = Caveland.class.getClassLoader().getResourceAsStream("com/BombingGames/Caveland/defaultmap.zip");
			WorkingDirectory.unpackMap(
				"default",
				in
			);
		}
    }

	public static String getCredits(){
		return "Caveland\n" +
			"\n" +
			"ein Spiel von\n" +
			"Benedikt S. Vogler\n" +
			"\n" +
			"Graphik\n" +
			"Frederic Brückner\n" +
			"\n" +
			"Musik & Sound\n" +
			"\"SteinImBrett\":\n" +
			"Felix von Dohlen\n" +
			"Marcel Gohsen\n" +
			"\n" +
			"Qualitätssicherung\n" +
			"Thomas Vogt\n" +
			"\n" +
			"Web Development\n" +
			"René Weißer\n" +
			"\n" +
			"Speziellen Dank an\n" +
			"Felix Günther\n" +
			"Vanya Gercheva\n" +
			"Ulrike Vogler\n" +
			"Gereon Vogler\n" +
			"Bernhard Vogler\n" +
			"Pia Lenßen\n" +
			"reddit.com/r/gamedev\n" +
			"Bauhaus Universität Weimar";
	
	}	
    
}
