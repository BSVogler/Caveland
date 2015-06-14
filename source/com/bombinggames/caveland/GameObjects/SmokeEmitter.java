/*
 *
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
package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Particle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class SmokeEmitter extends AbstractEntity {
	private static final long serialVersionUID = 2L;
	private boolean active = false;
	private float timer;
	private float timeEachSpawn = 100;
	//private final Class<? extends MovableEntity> particleClass;
	private Vector3 startingVector = new Vector3(0, 0, 0);
	private Vector3 spread = new Vector3(0, 0, 0);
	private float TTL;

	/**
	 *
	 * @param emitterClass
	 */
	//public Emitter(Class<MovableEntity> emitterClass) {
	public SmokeEmitter() {
		super((byte) 14);
		//this.particleClass = Dust.class;
		disableShadow();
		setIndestructible(true);
		setName("Smoke Emitter");
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (active) {
			setColor(new Color(1, 0, 0, 1));
			timer+=dt;
			if (timer >= timeEachSpawn){
				timer %= timeEachSpawn;
				Particle particle = (Particle) new Particle((byte) 22, TTL).spawn(getPosition().cpy());
				particle.setType(Particle.ParticleType.FIRE);
				particle.setColor(new Color(1, 0.5f, 0.1f, 1));
				particle.addMovement(
					startingVector.add(
						(float) (Math.random()-0.5f)*2*spread.x,
						(float) (Math.random()-0.5f)*2*spread.y,
						(float) (Math.random()-0.5f)*2*spread.z
					)
				);
			}
		} else {
			setColor(new Color(0.5f, 0.5f, 0.5f, 1));
		}
	}
	
	
	public void toggle(){
		active= !active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	/**
	 * 
	 * @param dir the direction and speed where the particles leave, in m/s without unit
	 */
	public void setParticleStartMovement(Vector3 dir){
		if (dir!=null)
			this.startingVector = dir;
	}
	
	/**
	 * 
	 * @param spread the range in which random noise gets aplied, in m/s without unit
	 */
	public void setParticleSpread(Vector3 spread){
		if (spread!=null)
			this.spread = spread;
	}

	/**
	 * 
	 * @param timeEachSpawn time in ms
	 */
	public void setParticleDelay(float timeEachSpawn) {
		this.timeEachSpawn = timeEachSpawn;
	}

	void setParticleTTL(int ttl) {
		this.TTL = ttl;
	}
}
