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
package com.bombinggames.caveland.gameobjects.logicblocks;

import com.badlogic.gdx.ai.msg.Telegram;
import com.bombinggames.caveland.gameobjects.Robot;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;

/**
 * Saves a link to a robot. Used for saving. Displayed as a LED.
 * Loads the chunk where the robot is.
 * @author Benedikt Vogler
 */
public class RobotFactoryLinker extends AbstractEntity{

	private static final long serialVersionUID = 1L;
	private Robot linkedRobot;
	
	/**
	 *
	 */
	public RobotFactoryLinker() {
		super((byte) 19);
		setName("Robot Factory Light Indicator");
		disableShadow();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (linkedRobot == null || linkedRobot.shouldBeDisposed()){
			setSpriteValue((byte) 1);
		} else {
			if (!linkedRobot.isInMemoryArea()){
				Controller.getMap().loadChunk(linkedRobot.getPosition().getChunkX(), linkedRobot.getPosition().getChunkY());
			}
			setSpriteValue((byte) 0);
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
	public Robot getRobot() {
		return linkedRobot;
	}

	void setRobot(Robot robot) {
		linkedRobot = robot;
	}
	
}
