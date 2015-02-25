package com.BombingGames.WurfelEngine.Core.SoundEngine;

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import java.util.HashMap;

/**
 * Manages the sounds in the game world.
 * @author Benedikt Vogler
 */
public class SoundEngine {
	private final HashMap<String, Sound> sounds = new HashMap<>(10);

	public SoundEngine() {
		sounds.put("landing", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/landing.wav"));
		sounds.put("splash", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/splash.ogg"));
		sounds.put("wind", (Sound) WE.getAsset("com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/wind.ogg"));
	}
	
	/**
	 * Register a sound.
	 * @param identifier
	 * @param path 
	 */
	public void register(String identifier, String path){
		sounds.put(identifier, (Sound) WE.getAsset(path));
	}
	
	/***
	 * 
	 * @param identifier name of sound
	 */
	public void play(String identifier){
		Sound result = sounds.get(identifier);
		if (result!=null)
			result.play();
	}
	
	/***
	 * 
	 * @param identifier name of sound
	 * @param coord the position of the sound in the world
	 */
	public void play(String identifier, AbstractPosition coord){
		Sound result = sounds.get(identifier);
		if (result != null)
			result.play();
	}
	
	/***
	 * 
	 * @param identifier name of sound
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
	 * @param identifier name of sound
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
	 * @param identifier name of sound
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

	/**
	 * Stops all instances of this sound.
	 * @param identifier name of sound
	 */
	public void stop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.stop();
	}
	
	/**
	 * Stops a specifiy instance of the sound.
	 * @param identifier 
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @see com.​badlogic.​gdx.​audio.​Sound#stop
	 */
	public void stop(String identifier, long instance) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.stop(instance);
	}

	/**
	 * currently not working
	 * @param identifier
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @param volume 
	 * @see com.​badlogic.​gdx.​audio.​Sound#setVolume
	 */
	public void setVolume(String identifier, long instance, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.setVolume(instance, volume);
	}

	public long loop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.loop();
		return 0;
	}
	
}
