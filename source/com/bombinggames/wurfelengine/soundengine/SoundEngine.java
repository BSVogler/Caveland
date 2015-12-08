/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.wurfelengine.soundengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the sounds in the game world.
 *
 * @author Benedikt Vogler
 */
public class SoundEngine {

	private final HashMap<String, Sound> sounds = new HashMap<>(10);
	private final ArrayList<SoundInstance> playingLoops = new ArrayList<>(4);
	private GameView view;
	/**
	 * loudness of the musicLoudness 0-1
	 */
	private float musicLoudness = 1;
	private Music music;

	/**
	 * loads and registers the ig-sounds
	 */
	public void loadRegisterIGSounds() {
		WE.getAssetManager().load("com/bombinggames/wurfelengine/soundengine/sounds/landing.wav", Sound.class);
		WE.getAssetManager().load("com/bombinggames/wurfelengine/soundengine/sounds/splash.wav", Sound.class);
		WE.getAssetManager().load("com/bombinggames/wurfelengine/soundengine/sounds/wind.ogg", Sound.class);
		WE.getAssetManager().load("com/bombinggames/wurfelengine/soundengine/sounds/explosion2.wav", Sound.class);
		WE.getAssetManager().finishLoading();

		register("landing", "com/bombinggames/wurfelengine/soundengine/sounds/landing.wav");
		register("splash", "com/bombinggames/wurfelengine/soundengine/sounds/splash.wav");
		register("wind", "com/bombinggames/wurfelengine/soundengine/sounds/wind.ogg");
		register("explosion", "com/bombinggames/wurfelengine/soundengine/sounds/explosion2.wav");
	}

	/**
	 * Registers a sound. The sound must be loaded via asset manager. You can
	 * not register a sound twice.
	 *
	 * @param identifier name of sound
	 * @param path path of the sound
	 */
	public void register(String identifier, String path) {
		if (!sounds.containsKey(identifier)) {
			sounds.put(identifier, (Sound) WE.getAsset(path));
		}
	}

	/**
	 *
	 * @param identifier name of sound
	 */
	public void play(String identifier) {
		play(identifier, 1f);
	}

	/**
	 *
	 * Plays sound with decreasing volume depending on distance.
	 *
	 * @param identifier name of sound
	 * @param pos the position of the sound in the world. if it is null then
	 * play at center
	 */
	public void play(String identifier, AbstractPosition pos) {
		play(identifier, pos, 1f);
	}

	/**
	 *
	 * Plays sound with decreasing volume depending on distance.
	 *
	 * @param identifier name of sound
	 * @param pos the position of the sound in the world. if it is null then
	 * play at center
	 * @param volume default is 1
	 */
	public void play(String identifier, AbstractPosition pos, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			float pan = 0;
			if (pos != null) {
				volume = volume * getVolume(pos);
				if (view.getCameras().size() > 0) {
					pan = pos.getViewSpcX() - view.getCameras().get(0).getViewSpaceX();
					pan /= 500;//arbitrary chosen
					if (pan > 1) pan = 1;
					if (pan < -1) pan = -1;
				}
			} else {
				volume *= WE.getCvars().getValueF("sound");
			}
			if (volume >= 0.1) { //only play sound louder>10%
				result.play(volume, 1, pan);
			}
		}
	}

	/**
	 * *
	 *
	 * @param identifier name of sound
	 * @param volume default is 1
	 * @return
	 */
	public long play(String identifier, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			return result.play(volume * WE.getCvars().getValueF("sound"));
		}
		return 0;
	}
	
	/**
	 * *
	 *
	 * @param identifier name of sound
	 * @param volume
	 * @param pitch the pitch multiplier, 1 == default, >1 == faster, 1 == slower, the value has to be between 0.5 and 2.0
	 * @return
	 */
	public long play(String identifier, float volume, float pitch) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			return result.play(volume * WE.getCvars().getValueF("sound"), pitch, 0);
		}
		return 0;
	}

	/**
	 * *
	 *
	 * @param identifier name of sound
	 * @param volume the volume in the range [0,1]
	 * @param pitch the pitch multiplier, 1 == default, &gt;1 == faster, &lt;1
	 * == slower, the value has to be between 0.5 and 2.0
	 * @param pan panning in the range -1 (full left) to 1 (full right). 0 is
	 * center position.
	 * @return
	 */
	public long play(String identifier, float volume, float pitch, float pan) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			return result.play(volume * WE.getCvars().getValueF("sound"), pitch, pan);
		}
		return 0;
	}

	/**
	 * playing Loops a sound.
	 *
	 * @param identifier name of sound
	 * @return the instance id
	 * @see com.​badlogic.​gdx.​audio.​Sound#loop
	 */
	public long loop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			return result.loop(WE.getCvars().getValueF("sound"));
		}
		return 0;
	}

	/**
	 * Starts playing a loop. If already playing will start another instance
	 *
	 * @param identifier name of sound
	 * @param pos the position of the sound in the game world. Should be a
	 * reference to the position of the object and no copy so that it updates
	 * itself.
	 * @return the instance id
	 * @see com.badlogic.​gdx.​audio.​Sound#loop()
	 */
	public long loop(String identifier, AbstractPosition pos) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			long id = result.loop(WE.getCvars().getValueF("sound"));
			playingLoops.add(new SoundInstance(this, result, id, pos));
			return id;
		}
		return 0;
	}

	/**
	 * Stops all instances of this sound.
	 *
	 * @param identifier name of sound
	 */
	public void stop(String identifier) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			result.stop();
		}
		//remove from playing loops list
		playingLoops.removeIf(s -> s.sound.equals(result));
	}

	/**
	 * Stops a specifiy instance of the sound.
	 *
	 * @param identifier name of sound
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String)
	 * }.
	 * @see com.badlogic.gdx.audio.Sound#stop()
	 */
	public void stop(String identifier, long instance) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			result.stop(instance);
		}
		//remove from playing loops list if with this instance id
		playingLoops.removeIf(s -> s.sound.equals(result) && s.id == instance);
	}

	public void stopEverySound() {
		playingLoops.clear();
		sounds.values().forEach(s -> s.stop());
	}

	/**
	 * Set the volume of a playing instance.
	 *
	 * @param identifier name of sound
	 * @param instance the instance returned by {@link #play(String) } or {@link #loop(String)
	 * }.
	 * @param volume
	 * @see com.​badlogic.​gdx.​audio.​Sound#setVolume()
	 */
	public void setVolume(String identifier, long instance, float volume) {
		Sound result = sounds.get(identifier);
		if (result != null) {
			result.setVolume(instance, volume);
		}
	}

	/**
	 *
	 * @param dt
	 */
	public void update(float dt) {
		float loudness = WE.getCvars().getValueF("music");
		if (loudness != getMusicLoudness()) {
			setMusicLoudness(loudness);
		}
		for (SoundInstance sound : playingLoops) {
			sound.update();
		}
	}

	/**
	 * disposes the sounds. if you dispose the sounds they do not play if you
	 * reload a game. so stop them instead
	 */
	public void dispose() {
		for (Sound s : sounds.values()) {
			s.dispose();
		}
	}

	/**
	 * calculates the volume of a sound based on the positon in the game world.
	 * Compares to cameras.
	 *
	 * @param pos position in the world.
	 * @return multiplied with the settings for the volume
	 */
	protected float getVolume(AbstractPosition pos) {
		float volume = 1;
		if (view != null) {
			//calculate minimal distance to camera
			float minDistance = Float.POSITIVE_INFINITY;
			for (Camera camera : view.getCameras()) {
				float distance = pos.toPoint().distanceToHorizontal(camera.getCenter());
				if (distance < minDistance) {
					minDistance = distance;
				}
			}

			int decay = WE.getCvars().getValueI("soundDecay");
			volume = decay * Block.GAME_EDGELENGTH / (minDistance * minDistance + decay * Block.GAME_EDGELENGTH);//loose energy radial
			if (volume > 1) {
				volume = 1;
			}
		}
		return volume * WE.getCvars().getValueF("sound");
	}

	/**
	 * Set the gameplay view to calcualte sound based on the gameplay.
	 *
	 * @param view
	 */
	public void setView(GameView view) {
		this.view = view;
	}

	/**
	 *
	 * @return
	 */
	public float getMusicLoudness() {
		return musicLoudness;
	}

	/**
	 * Sets the volume and plays or pauses the music.
	 *
	 * @param loudness The volume must be given in the range [0,1] with 0 being
	 * silent and 1 being the maximum volume. musicLoudness &lt; 0 pauses it
	 * andc and &gt; 0 starts it
	 */
	private void setMusicLoudness(float loudness) {
		this.musicLoudness = loudness;
		if (music != null) {
			music.setVolume(musicLoudness);
			if (music.isPlaying() && musicLoudness == 0) {
				music.pause();
			} else if (!music.isPlaying() && musicLoudness > 0) {
				try {
					music.play();
				} catch (GdxRuntimeException ex) {
					System.err.println("Failed playing music.");
				}
			}
		}
	}

	/**
	 * Loads new music and plays them if a loudness is set.
	 *
	 * @param path
	 */
	public void setMusic(String path) {
		if (Gdx.files.internal(path).exists()) {
			this.music = Gdx.audio.newMusic(Gdx.files.internal(path));
			music.setLooping(true);
			setMusicLoudness(WE.getCvars().getValueF("music"));
		}
	}

	/**
	 * Check if music is playing
	 *
	 * @return true if music is playing
	 */
	public boolean isMusicPlaying() {
		if (music == null) {
			return false;
		}
		return music.isPlaying();
	}

	/**
	 *
	 */
	public void disposeMusic() {
		if (music != null) {
			music.dispose();
		}
	}

	public void pauseMusic() {
		if (music != null) {
			music.pause();
		}
	}

	public GameView getView() {
		return view;
	}
}
