package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLoading extends LoadingScreen {

	@Override
	public void customLoading(AssetManager manager) {
		//manager.load("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg", Sound.class);
		manager.load("com/BombingGames/Caveland/playerSheet.txt", TextureAtlas.class);
		manager.load("com/BombingGames/Caveland/playerSheetNormal.png", Texture.class);
        manager.load("com/BombingGames/Caveland/sounds/jump_man.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/jetpack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/step.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/ha.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/loadAttack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/attack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/urf_jump.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/urfHurt.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/vanya_jump.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/impact.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/poch.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/robot1destroy.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/wagon.mp3", Sound.class);
	}
	
}
