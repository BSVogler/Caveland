package com.bombinggames.caveland.MainMenu;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Loading.LoadingScreen;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLoading extends LoadingScreen {

	@Override
	public void customLoading(AssetManager manager) {
		if (!WE.getCVars().getValueB("ignorePlayer")) {
			manager.load("com/bombinggames/caveland/playerSheet.txt", TextureAtlas.class);
			manager.load("com/bombinggames/caveland/playerSheetNormal.png", Texture.class);
		}
		manager.load("com/bombinggames/caveland/sounds/turret.ogg", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/jetpack.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/step.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/collect.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/ha.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/loadAttack.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/release.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robotHit.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/huhu.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/urf_jump.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/urfHurt.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/vanya_jump.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/impact.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/poch.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robot1destroy.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robot1Wobble.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/wagon.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/sword.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/hiss.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/treehit.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/metallic.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/construct.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/throwFail.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/droneLoop.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robot2walk.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robotScream.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/craft.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/moneyPickup.wav", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/merchantAha.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/merchantWelcome.mp3", Sound.class);
		manager.load("com/bombinggames/caveland/sounds/robotWeep.wav", Sound.class);
	}
	
}
