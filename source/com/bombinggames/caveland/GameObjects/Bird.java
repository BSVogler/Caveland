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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;

/**
 *
 * @author Benedikt Vogler
 */
public class Bird extends MovableEntity {
	private static final long serialVersionUID = 1L;
	private float timeSinceLastDirChange = 0;

	/**
	 *
	 */
	public Bird() {
		super((byte) 40, 3);//use vanya at the moment
		setFloating(true);
		setName("Bird");
		setJumpingSound("vanya_jump");
		setContinuousWalkingAnimation(2.5f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		timeSinceLastDirChange+=dt;
		if (timeSinceLastDirChange>2000) {
			timeSinceLastDirChange%=2000;//every two seconds
			addMovement(
				new Vector3(
					0,
					0,
					(float) (Math.random()-0.5f)*dt*0.1f
				)
			);
		}
		
		addMovement(
			new Vector2(
				(float) (Math.random()-0.5f)*dt*0.02f,
				(float) (Math.random()-0.5f)*dt*0.02f
			)
		);
	}
	
}
