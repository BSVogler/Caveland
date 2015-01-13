package com.BombingGames.Caveland.MainMenu;

import com.BombingGames.WurfelEngine.Core.Loading.LoadingScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLoading extends LoadingScreen {

	@Override
	public void customLoading(AssetManager manager) {
		//manager.load("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg", Sound.class);
        manager.load("com/BombingGames/Caveland/sounds/jump_man.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/jetpack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/step.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/ha.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/loadAttack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/attack.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/urf_jump.wav", Sound.class);
		manager.load("com/BombingGames/Caveland/sounds/vanya_jump.wav", Sound.class);
	}
	
}
