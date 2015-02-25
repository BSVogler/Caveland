package com.BombingGames.WurfelEngine.Core.SoundEngine;

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

/**
 *
 * @author Benedikt Vogler
 */
public class SoundEngine {
	private final HashMap<String, Sound> sounds = new HashMap<>(10);

	public SoundEngine() {
		sounds.put("landing", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/landing.wav"));
		sounds.put("splash", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/splash.ogg"));
		sounds.put("wind", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/wind.ogg"));
	}
	
	
	public void register(String identifier, String path){
		sounds.put(identifier, (Sound) WE.getAsset(path));
	}
	
	/***
	 * 
	 * @param identifier 
	 */
	public void play(String identifier){
		Sound result = sounds.get(identifier);
		if (result!=null)
			result.play();
	}
	
	/***
	 * 
	 * @param identifier 
	 * @param coord the position of the sound in the world
	 */
	public void play(String identifier, AbstractPosition coord){
		Sound result = sounds.get(identifier);
		if (result != null)
			result.play();
	}
	
	/***
	 * 
	 * @param identifier
	 * @param volume
	 * @return 
	 */
	public long play(String identifier, float volume){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume);
		return 0;
	}
	
	/***
	 * 
	 * @param identifier 
	 * @param volume 
	 * @param pitch
	 * @return 
	 */
	public long playSound(String identifier, float volume, float pitch){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume, pitch, pitch);
		return 0;
	}
	
	/***
	 * 
	 * @param identifier 
	 * @param volume 
	 * @param pitch 
	 * @param pan
	 * @return 
	 */
	public long play(String identifier, float volume, float pitch, float pan){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume, pitch, pan);
		return 0;
	}

	public void stop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.stop();
	}

	/**
	 * currently not working
	 * @param identifier
	 * @param volume 
	 */
	public void setVolume(String identifier, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.setVolume(0, volume);//todo needs soundId
	}

	public long loop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.loop();
		return 0;
	}
	
}
