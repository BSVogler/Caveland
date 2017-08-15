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
package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Component;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.MoveToAi;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.io.Serializable;
import java.util.Random;

/**
 * an AI which moves a movable entity around.
 * @author Benedikt Vogler
 */
public class IdleAI implements Telegraph, Serializable, Component {

	private static final long serialVersionUID = 1L;
	
	private final static Random RANDOMGENERATOR = new java.util.Random(1);
	private MovableEntity body;
	/**
	 * position where the ai will return
	 */
	private Point home;
	private float timeTillMove;
	private final float idleRaidus = RenderCell.GAME_EDGELENGTH * 2;

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (body.hasPosition()) {
			if (home == null) {
				home = body.getPosition().cpy();
			}

			if (timeTillMove > 0) {
				timeTillMove -= dt;
			}

			//generate new movement goal
			if (timeTillMove <= 0 && body.getComponent(MoveToAi.class) == null) {
				timeTillMove = 1500;

				Point target;
				//only 100 trials
				int i = 0;
				//find target in radius
				do {	
					i++;
					target = home.cpy();
					if (body.isFloating())
						target.add(
							new Vector3(
								RANDOMGENERATOR.nextFloat() - 0.5f,
								RANDOMGENERATOR.nextFloat() - 0.5f,
								RANDOMGENERATOR.nextFloat() - 0.5f
							).nor().scl(idleRaidus)
						);
					else {
						target.add(
							new Vector2(
								RANDOMGENERATOR.nextFloat() - 0.5f,
								RANDOMGENERATOR.nextFloat() - 0.5f
							).nor().scl(idleRaidus)
						);
					}
				} while (i < 100 && target.isObstacle());
				
				if (i < 100){
					body.setSpeedHorizontal(2);
					MessageManager.getInstance().dispatchMessage(
						0,
						this,
						body,
						Events.moveTo.getId(),
						target
					);
				}
			}
		}
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	@Override
	public void setParent(AbstractEntity body) {
		this.body = (MovableEntity) body;
	}

	@Override
	public void dispose() {
		if (body != null){
			body.removeComponent(this);
		}
	}
}
