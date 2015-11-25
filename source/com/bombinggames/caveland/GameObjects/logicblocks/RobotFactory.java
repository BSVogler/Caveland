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
package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.Events;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.Quadrocopter;
import com.bombinggames.caveland.GameObjects.Robot;
import com.bombinggames.caveland.GameObjects.SpiderRobot;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class RobotFactory extends AbstractBlockLogicExtension implements Interactable  {
	private Robot linkedRobot;
	
	public RobotFactory(Block block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		if (linkedRobot == null || linkedRobot.shouldBeDisposed()){
			getPosition().setValue((byte) 1);
		} else {
			getPosition().setValue((byte) 0);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	@SuppressWarnings("null")
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			ActionBox ab;
				if (linkedRobot == null || linkedRobot.shouldBeDisposed()) {
					ab = new ActionBox("What do you want to build?", ActionBox.BoxModes.SELECTION, "Drones can only be build at the surface.")
					.addSelectionNames("Fighter Robot","Robot", "Drone")
					.setConfirmAction((int result, AbstractEntity actor1) -> {
						if (linkedRobot == null || linkedRobot.shouldBeDisposed()) {
							switch (result) {
								case 0:
									linkedRobot = (Robot) new Robot().spawn(getPosition().toPoint());
									break;
								case 1:
									linkedRobot = (Robot) new SpiderRobot().spawn(getPosition().toPoint());
									break;
								case 2:
									linkedRobot = (Robot) new Quadrocopter().spawn(getPosition().toPoint());
									break;
								default:
									break;
							}
							linkedRobot.setTeamId(1);
							WE.SOUND.play("construct");
						}
					});
				} else {
				ab = new ActionBox("Robot in use", ActionBox.BoxModes.BOOLEAN, "The robot is already in use. Destroy it?")
					.setConfirmAction((int result, AbstractEntity actor1) -> {
						MessageManager.getInstance().dispatchMessage(
							(Telegraph) this,
							linkedRobot,
							Events.damage.getId(),
							0
						);
					});
				}
				ab.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
	
}
