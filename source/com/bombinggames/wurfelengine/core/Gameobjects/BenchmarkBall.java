/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH;
import com.bombinggames.wurfelengine.core.Map.Chunk;

/**
 *
 * @author Benedikt Vogler
 */
public class BenchmarkBall extends MovableEntity {

	private static float timer = 0;
	private static float timeTillBall = 1000;
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a Benchmark ball.
	 */
	public BenchmarkBall() {
		super((byte) 21, (byte) 0);
		setMovement(new Vector3((float) Math.random() - 0.5f, (float) Math.random() - 0.5f, -1));
		setFloating(false);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		timer += dt;
		if (timer > timeTillBall) {
			if (Gdx.graphics.getDeltaTime() < 0.013f) {//over 60 FPS
				new BenchmarkBall().spawn(
					Controller.getMap().getCenter(Chunk.getGameHeight() - 2 * GAME_EDGELENGTH)
				);
			}
			timer = 0;
		}
		timeTillBall -= dt / 5000000f;

		if (isOnGround()) {
			jump();
		}
	}

	@Override
	public void jump() {
		Vector3 tmp = new Vector3();
		tmp.x = (float) Math.random() - 0.5f;
		tmp.y = (float) Math.random() - 0.5f;
		super.jump(-tmp.z, false);
	}
}
