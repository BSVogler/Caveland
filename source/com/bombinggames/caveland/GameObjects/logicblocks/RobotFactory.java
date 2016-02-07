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

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.Interactable;
import com.bombinggames.caveland.gameobjects.Quadrocopter;
import com.bombinggames.caveland.gameobjects.Robot;
import com.bombinggames.caveland.gameobjects.SpiderRobot;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class RobotFactory extends AbstractBlockLogicExtension implements Interactable, Telegraph  {
	private RobotFactoryLinker linkToRobot;
	
	/**
	 *
	 * @param block
	 * @param coord
	 */
	public RobotFactory(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (linkToRobot == null) {
			ArrayList<RobotFactoryLinker> links = getPosition().getEntitiesInside(RobotFactoryLinker.class);
			if (!links.isEmpty()) {
				linkToRobot = links.get(0);
			} else {
				linkToRobot = new RobotFactoryLinker();
			}
		}
		
		if (linkToRobot.shouldBeDisposed() || !linkToRobot.hasPosition()){//respawn if needed
			linkToRobot.spawn(getPosition().toPoint());
		} else {
			linkToRobot.setPosition(getPosition().toPoint());//force at position
		}
	}

	@Override
	public void dispose() {
		linkToRobot.dispose();
	}

	@Override
	@SuppressWarnings("null")
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			ActionBox ab;
			if (linkToRobot.getRobot() == null || linkToRobot.getRobot().shouldBeDisposed()) {
				ab = new ActionBox("What do you want to build?", ActionBox.BoxModes.SELECTION, "Drones can only be build at the surface.")
				.addSelection(
					new ActionBox.SelectionOption((byte) 0, "Fighter Robot"),
					new ActionBox.SelectionOption((byte) 1, "Robot"),
					new ActionBox.SelectionOption((byte) 2, "Drone")
				)
				.setConfirmAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
					if (linkToRobot.getRobot() == null ||
						linkToRobot.getRobot().shouldBeDisposed()
					) {
						Robot robot = null;
						switch (result.id) {
							case 0:
								robot = (Robot) new Robot().spawn(getPosition().toPoint());
								break;
							case 1:
								robot = (Robot) new SpiderRobot().spawn(getPosition().toPoint());
								break;
							case 2:
								robot = (Robot) new Quadrocopter().spawn(getPosition().toPoint());
								break;
							default:
								break;
						}
						robot.setTeamId(((Ejira) actor).getTeamId());//copy team id
						linkToRobot.setRobot(robot);
						WE.SOUND.play("construct");
					}
				});
			} else {
				ab = new ActionBox("Robot in use", ActionBox.BoxModes.BOOLEAN, "The robot is already in use. Destroy it?")
					.setConfirmAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
						MessageManager.getInstance().dispatchMessage(
							(Telegraph) this,
							linkToRobot.getRobot(),
							Events.damage.getId(),
							(byte) 100
						);
					});
			}
			ab.register(view, ((Ejira) actor).getPlayerNumber(), actor, getPosition());
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

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
	
}
