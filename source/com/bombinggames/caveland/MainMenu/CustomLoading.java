package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bombinggames.wurfelengine.Core.Loading.LoadingScreen;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLoading extends LoadingScreen {

	@Override
	public void customLoading(AssetManager manager) {
		//manager.load("com/bombinggames/Caveland/sounds/victorcenusa_running.ogg", Sound.class);
		manager.load("com/bombinggames/Caveland/playerSheet.txt", TextureAtlas.class);
		manager.load("com/bombinggames/Caveland/playerSheetNormal.png", Texture.class);
        manager.load("com/bombinggames/Caveland/sounds/jump_man.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/jetpack.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/step.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/collect.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/ha.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/loadAttack.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/release.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/robotHit.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/urf_jump.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/urfHurt.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/vanya_jump.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/impact.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/poch.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/robot1destroy.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/robot1Wobble.mp3", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/wagon.mp3", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/sword.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/hiss.wav", Sound.class);
		manager.load("com/bombinggames/Caveland/sounds/treehit.wav", Sound.class);
	}
	
}
