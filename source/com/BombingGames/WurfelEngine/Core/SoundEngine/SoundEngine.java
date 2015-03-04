package com.BombingGames.WurfelEngine.Core.SoundEngine;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the sounds in the game world.
 * @author Benedikt Vogler
 */
public class SoundEngine {
	private final HashMap<String, Sound> sounds = new HashMap<>(10);
	private ArrayList<Camera> cameras;

	public SoundEngine() {
		register("landing", "com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/landing.wav");
		register("splash", "com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/splash.ogg");
		register("wind", "com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/wind.ogg");
		register("explosion", "com/BombingGames/WurfelEngine/Core/SoundEngine/Sounds/explosion2.ogg");
	}
	
	/**
	 * Registers a sound. The sound must be loaded via asset manager.
	 * You can not register a sound twice.
	 * @param identifier name of sound
	 * @param path path of the sound
	 */
	public void register(String identifier, String path){
		if (!sounds.containsKey(identifier)){
			sounds.put(identifier, (Sound) WE.getAsset(path));
		}
	}

	/**
	 * 
	 * @param cameras 
	 */
	public void setCameras(ArrayList<Camera> cameras) {
		this.cameras = cameras;
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
	 * Plays sound with decreasing volume depending on distance.
	 * @param identifier name of sound
	 * @param pos the position of the sound in the world
	 */
	public void play(String identifier, AbstractPosition pos){
		Sound result = sounds.get(identifier);
		if (result != null){
			float volume = 1;
			if (cameras != null) {
				//calculate minimal distance to camera
				float minDistance = Float.POSITIVE_INFINITY;
				for (Camera camera : cameras) {
					float distance = pos.getPoint().distanceTo(camera.getCenter().getPoint());
					if (distance < minDistance)
						minDistance = distance;
				}
				
				int decay = CVar.get("soundDecay").getValuei();
				volume = decay*AbstractGameObject.GAME_EDGELENGTH / (minDistance*minDistance + decay*AbstractGameObject.GAME_EDGELENGTH);//loose energy radial
				if (volume > 1)
					volume = 1;
			}
			if (volume >= 0.1) //only play sound louder>10%
				result.play(volume);
		}
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
	public long play(String identifier, float volume, float pitch){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume, pitch, pitch);
		return 0;
	}
	
	/***
	 * 
	 * @param identifier name of sound
	 * @param volume the volume in the range [0,1]
	 * @param pitch the pitch multiplier, 1 == default, >1 == faster, 1 == slower, the value has to be between 0.5 and 2.0
	 * @param pan panning in the range -1 (full left) to 1 (full right). 0 is center position.
	 * @return 
	 */
	public long play(String identifier, float volume, float pitch, float pan){
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.play(volume, pitch, pan);
		return 0;
	}
	
	/**
	 * loops a sound
	 * @param identifier name of sound
	 * @return the instance id
	 * @see com.​badlogic.​gdx.​audio.​Sound#loop
	 */
	public long loop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.loop();
		return 0;
	}
	
	/**
	 * loops a sound. Sound decay not working.
	 * @param identifier name of sound
	 * @param pos the position of the sound in the game world
	 * @return the instance id
	 * @see com.​badlogic.​gdx.​audio.​Sound#loop
	 */
	public long loop(String identifier, AbstractPosition pos) {
		Sound result = sounds.get(identifier);
		if (result != null)
			return result.loop();
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
	 * @param identifier name of sound
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @see com.​badlogic.​gdx.​audio.​Sound#stop
	 */
	public void stop(String identifier, long instance) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.stop(instance);
	}

	/**
	 * Set the volume of a playing instance.
	 * @param identifier name of sound
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String) }.
	 * @param volume 
	 * @see com.​badlogic.​gdx.​audio.​Sound#setVolume
	 */
	public void setVolume(String identifier, long instance, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null)
			result.setVolume(instance, volume);
	}

}
