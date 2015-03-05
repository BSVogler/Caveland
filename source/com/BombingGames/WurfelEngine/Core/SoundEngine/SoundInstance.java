package com.BombingGames.WurfelEngine.Core.SoundEngine;

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.badlogic.gdx.audio.Sound;

/**
 *
 * @author Benedikt Vogler
 */
class SoundInstance {
	private final SoundEngine parent;
	protected AbstractPosition pos;
	protected Sound sound;
	protected long id;

	protected SoundInstance(SoundEngine parent, Sound sound, long id, AbstractPosition pos) {
		this.id = id;
		this.sound = sound;
		this.pos = pos;
		this.parent = parent;
	}

	protected void update(){
		sound.setVolume(id, parent.getVolume(pos));
	}

	
}
