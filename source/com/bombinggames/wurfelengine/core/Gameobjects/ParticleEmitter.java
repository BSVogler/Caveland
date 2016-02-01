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
package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class ParticleEmitter extends AbstractEntity {

	private static final long serialVersionUID = 2L;
	private boolean active = false;
	/**
	 * counts the time
	 */
	private float timer;
	/**
	 * the amoutn of time to pass before a new object is spawned. In ms.
	 */
	private float timeEachSpawn = 100;
	//private final Class<? extends MovableEntity> particleClass;
	private Vector3 startingVector = new Vector3(0, 0, 0);
	private Vector3 spread = new Vector3(0, 0, 0);
	private PointLightSource lightsource;
	private Particle prototype = new Particle((byte) 22);

	/**
	 * active by default
	 */
	//public Emitter(Class<MovableEntity> emitterClass) {
	public ParticleEmitter() {
		super((byte) 14);
		//this.particleClass = Dust.class;
		disableShadow();
		setIndestructible(true);
		setName("Particle Emitter");
		setActive(true);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		checkLightSource();
		return this;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		if (active && hasPosition()) {
			setColor(new Color(1, 0, 0, 1));//only important if visible
			if (lightsource != null && prototype.getType() == ParticleType.FIRE) {
				lightsource.setPosition(getPosition());
				lightsource.update(dt);
			}

			timer += dt;
			while (timer >= timeEachSpawn) {
				timer -= timeEachSpawn;
				Particle particle = new Particle(prototype.getSpriteId(), prototype.getLivingTime());
				particle.setType(prototype.getType());
				particle.setColor(prototype.getColor().cpy());
				particle.setRotation((float) (Math.random()*360f));
				particle.addMovement(
					startingVector.add(
						(float) (Math.random() - 0.5f) * 2 * spread.x,
						(float) (Math.random() - 0.5f) * 2 * spread.y,
						(float) (Math.random() - 0.5f) * 2 * spread.z
					)
				);
				particle.spawn(getPosition().cpy());
			}
		} else {
			setColor(new Color(0.5f, 0.5f, 0.5f, 1));
		}
	}

	/**
	 *
	 */
	public void toggle() {
		active = !active;
	}

	/**
	 * Makes the emitter spawn objects
	 *
	 * @param active
	 */
	public void setActive(boolean active) {
		this.active = active;
		checkLightSource();
	}

	/**
	 *
	 * @param prototype
	 */
	public void setPrototype(Particle prototype) {
		this.prototype = prototype;
		checkLightSource();
	}

	/**
	 *
	 * @return
	 */
	public Particle getPrototype() {
		return prototype;
	}

	/**
	 *
	 * @param dir the direction and speed where the particles leave, in m/s
	 * without unit
	 */
	public void setParticleStartMovement(Vector3 dir) {
		if (dir != null) {
			this.startingVector = dir;
		}
	}

	/**
	 * Spread is applied in both directions.
	 *
	 * @param spread the range in which random noise gets aplied, in m/s without
	 * unit
	 */
	public void setParticleSpread(Vector3 spread) {
		this.spread = spread;
	}

	/**
	 *
	 * @param timeEachSpawn time in ms
	 */
	public void setParticleDelay(float timeEachSpawn) {
		this.timeEachSpawn = timeEachSpawn;
	}

	/**
	 * if it can emit light
	 *
	 * @param brightness
	 */
	public void setBrightness(float brightness) {
		checkLightSource();
		if (lightsource != null) {
			lightsource.setBrightness(brightness);
		}
	}

	/**
	 * checks if the config for the light source is okay
	 */
	private void checkLightSource() {
		if (hasPosition() && prototype.getType() == ParticleType.FIRE) {
			if (lightsource == null) {
				lightsource = new PointLightSource(Color.YELLOW, 5, 11, WE.getGameplay().getView());
				lightsource.setPosition(getPosition().cpy());
			} else {
				lightsource.getPosition().setValues(getPosition());
			}
			lightsource.enable();
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
