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
	
	public void register(String identifier, String path){
		sounds.put(identifier, (Sound) WE.getAsset(path));
	}
	
	/***
	 * 
	 * @param identifier 
	 */
	public void playSound(String identifier){
		Sound result = sounds.get(identifier);
		if (result!=null)
			result.play();
	}
	
	/***
	 * 
	 * @param identifier 
	 * @param coord the position of the sound in the world
	 */
	public void playSound(String identifier, AbstractPosition coord){
		Sound result = sounds.get(identifier);
		if (result != null)
			result.play();
	}
	
}
