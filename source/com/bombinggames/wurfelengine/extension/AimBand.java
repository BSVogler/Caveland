/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
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
package com.bombinggames.wurfelengine.extension;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Particle;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;

/**
 * A band which points to a point or entity
 * @author Benedikt Vogler
 */
public class AimBand {
	private Point goal;
	private AbstractEntity target;
	private final AbstractEntity parent;
	private final float timeEachSpawn = 200;
	private float timeTillNext;
	private final ArrayList<Particle> list = new ArrayList<>(10);

	public AimBand(AbstractEntity parent, AbstractPosition goal) {
		if (goal instanceof Point)
			this.goal = (Point) goal;
		else
			this.goal = goal.toPoint();
		this.parent = parent;
		this.target = null;
	}

	public AimBand(AbstractEntity parent, AbstractEntity target) {
		this.target = target;
		this.parent = parent;
		goal = null;
	}
	
	/**
	 * 
	 */
	public void update(){
		timeTillNext -= Gdx.graphics.getRawDeltaTime()*1000f;
		if (timeTillNext < 0 && (target != null || goal != null)) {
			timeTillNext += timeEachSpawn;
			Particle particle = new Particle();
			particle.setTTL(1500);
			particle.setColor(new Color(0.4f, 0.5f, 1, 0.3f));
			particle.setUseRawDelta(true);
			particle.setColiding(false);
			particle.spawn(parent.getPosition().cpy());
			list.add(particle);
		}
		
		if (getAim() != null) {
			//move particles
			for (Particle p : list) {
				p.getPosition().setValues(parent.getPosition()).lerp(getAim(), 1 - p.getPercentageOfLife());
			}

			list.removeIf(p -> p.shouldBeDisposed());
		} else {
			list.forEach(p -> p.dispose());
			list.clear();
		}
	}

	/**
	 * position at the end of the band
	 * @return 
	 */
	private Point getAim(){
		if (goal == null) {
			return target.getPosition();
		} else {
			return goal;
		}
	}
	/**
	 * 
	 * @param goal 
	 */
	public void setGoal(AbstractPosition goal) {
		this.goal = goal.toPoint();
		this.target = null;
	}

	/**
	 * 
	 * @param target 
	 */
	public void setTarget(AbstractEntity target) {
		this.target = target;
		this.goal = null;
	}

	public void dispose() {
		for (Particle particle : list) {
			particle.disposeFromMap();
		}
	}
	
}
