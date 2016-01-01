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
package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class SpiderRobot extends Robot{
	
	private static final long serialVersionUID = 2L;
	private transient long walkingSound;
	private transient Laserdot laserdot;
	private transient boolean moveUp;
	private transient boolean moveRight;
	private float scanHeight;
	private float laserRotate;

	/**
	 *
	 */
	public SpiderRobot() {
		setType(1);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (hasPosition()) {
			//look for resources
			ArrayList<Coordinate> nearbResources = nearbyResources();
			if (!nearbResources.isEmpty()) {
				MessageManager.getInstance().dispatchMessage(
					this,
					this,
					Events.moveTo.getId(),
					nearbResources.get(0).toPoint()
				);
			}


			//go to resources
			//gather resources
			//place them on storage
			
			if (moveUp) {
				scanHeight += dt / 300f;
			} else {
				scanHeight -= dt / 300f;
			}
			
			if (scanHeight >= 1) {
				moveUp = false;
				scanHeight = 1;
			}

			if (scanHeight <= -1) {
				moveUp = true;
				scanHeight = -1;
			}
			
			if (moveRight) {
				laserRotate += dt / 500f;
			} else {
				laserRotate -= dt / 500f;
			}
			
			if (laserRotate >= 1) {
				moveRight = false;
				laserRotate = 1;
			}

			if (laserRotate <= -1) {
				moveRight = true;
				laserRotate = -1;
			}
			
			if (laserdot == null){
				laserdot = (Laserdot) new Laserdot().spawn(getPosition().cpy());
				laserdot.setColor(COLORTEAM.cpy());
			}
			laserdot.update(
				getAiming(),
				getPosition()
			);
			
			//sound
			if (getMovementHor().len2() > 0 && isOnGround()) {
				if (walkingSound == 0l) {
					walkingSound = WE.SOUND.loop("robot2walk", getPosition());
				}
			} else {
				WE.SOUND.stop("robot2walk", walkingSound);
				walkingSound = 0;
			}
		}
	}
	
	protected ArrayList<Coordinate> nearbyResources(){
		ArrayList<Coordinate> coordList = new ArrayList<>(2);
		
		for (int x = -4; x < 4; x++) {
			for (int y = -4; y < 4; y++) {
				for (int z = -2; z < 2; z++) {
					Coordinate tmpCoord = getPosition().toCoord().addVector(x, y, z);
					Block block = tmpCoord.getBlock();
					if (block!=null) {
						byte id = tmpCoord.getBlock().getId();
						if ((id == CavelandBlocks.CLBlocks.COAL.getId()
							|| id == CavelandBlocks.CLBlocks.IRONORE.getId()
							|| id == CavelandBlocks.CLBlocks.CRYSTAL.getId()
							|| id == CavelandBlocks.CLBlocks.SULFUR.getId())
							&& getPosition().canSee(tmpCoord.toPoint(), 12)
						) {
							coordList.add(tmpCoord);
						}
					}
				}
			}
		}
		return coordList;
	}

	@Override
	public Vector3 getAiming() {
		return new Vector3(getOrientation(), scanHeight).rotate(Vector3.Z, laserRotate*90f).nor();
	}

	@Override
	public void disposeFromMap() {
		WE.SOUND.stop("robot2walk", walkingSound);
		walkingSound = 0;
		laserdot.dispose();
		super.disposeFromMap();
	}
}
