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
package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.map.Point;
import java.io.Serializable;

/**
 *
 * @author Benedikt Vogler
 */
public class MoveToAi implements Telegraph, Serializable, Component {

	private static final long serialVersionUID = 1L;
	
	private MovableEntity body;
	/**
	 * where does it move to?
	 */
	private final Point movementGoal;
	private int runningagainstwallCounter = 0;
	/**
	 * used for detecting that is runnning against a wall
	 */
	private transient Point lastPos;

	/**
	 *
	 * @param goal
	 */
	public MoveToAi(Point goal) {
		this.movementGoal = goal;
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (movementGoal != null && body.getPosition() != null) {
			if (!atGoal()) {
				//movement logic
				Vector3 d = movementGoal.cpy().sub(body.getPosition());
				float movementSpeed;
				if (body.isFloating()) {
					movementSpeed = body.getSpeed();
				} else {
					d.z = 0;
					movementSpeed = body.getSpeedHor();
				}

				if (movementSpeed < 2) {
					movementSpeed = 2;
				}
				d.nor().scl(movementSpeed);//direction only
				//if walking keep momentum
				if (!body.isFloating()) {
					d.z = body.getMovement().z;
				}

				body.setMovement(d);// update the movement vector
			} else {
				body.setSpeedHorizontal(0);// update the movement vector
				dispose();
			}
			
			//Movement AI: if standing on same position as in last update
			if (!body.isFloating()) {
				if (body.getPosition().equals(lastPos) && body.getSpeed() > 0) {//not standing still
					runningagainstwallCounter += dt;
				} else {
					runningagainstwallCounter = 0;
					lastPos = body.getPosition().cpy();
				}

				//jump after some time
				if (runningagainstwallCounter > 500) {
					body.jump();
					runningagainstwallCounter = 0;
				}
			}
		}
	}
	
	/**
	 *
	 * @return
	 */
	public boolean atGoal() {
		if (movementGoal == null) {
			return true;
		}
		if (body.getPosition() == null) {
			return false;
		}
		if (body.isFloating()) {
			return body.getPosition().dst2(movementGoal) < 20; //sqrt(20)~=4,4
		} else {
			return new Vector2(body.getPosition().x, body.getPosition().y).dst2(new Vector2(movementGoal.x, movementGoal.y)) < 20; //sqrt(20)~=4,4
		}
			
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	/**
	 *
	 * @return
	 */
	public Point getGoal() {
		return movementGoal;
	}

	/**
	 *
	 * @param body
	 */
	@Override
	public void setParent(AbstractEntity body) {
		if (!(body instanceof MovableEntity)){
			Gdx.app.error("MoveToAi", "parent must be movable Entity");
		} else {
			this.body = (MovableEntity) body;
		}
	}

	@Override
	public void dispose() {
		if (body != null) {
			this.body.removeComponent(this);
		}
	}
}
