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

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.gameobjects.Robot;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Intersection;
import com.bombinggames.wurfelengine.extension.shooting.Weapon;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Benedikt Vogler
 */
public class Turret extends AbstractPowerBlock {
	private Robot target;
	private Weapon gun;
	private float online;

	/**
	 *the radius where enemies can be seen
	 */
	public final float MAXDISTANCE = 20;
	private int teamId = 1;
	
	/**
	 *
	 * @param block
	 * @param coord
	 */
	public Turret(byte block, Coordinate coord) {
		super(block, coord);
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		
		///fill gun field
		if (gun == null) {
			//restore if possible
			ArrayList<Weapon> guns = getPosition().getEntitiesInside(Weapon.class);
			if (!guns.isEmpty()) {
				gun = guns.get(0);
			} else {
				gun = (Weapon) new Weapon((byte) 4, null).spawn(getPosition().toPoint());
				gun.ignoreBlock(CavelandBlocks.CLBlocks.TURRET.getId());
				gun.setFireSound("turret", true);
				gun.setSaveToDisk(false);
			}
		}
		
		if (hasPower()) {
			if (online < 1) {
				online += dt * 0.005;
			}
		} else {
			if (online > 0) {
				online -= dt * 0.005;
			}
		}
		
		//clamp
		if (online < 0) {
			online = 0;
		}
		if (online > 1) {
			online = 1;
		}
		
		if (!gun.hasPosition()) {
			gun.spawn(getPosition().toPoint());
		}
		
		if (gun.getFixedPos()==null){
			gun.setFixedPos(getPosition().toPoint());
		}
		
		gun.getFixedPos().setZ(
			getPosition().toPoint().getZ()+Block.GAME_EDGELENGTH*0.8f+online*Block.GAME_EDGELENGTH*0.6f
		);
		
		
		if (online >=1) {
			gun.setLaserHidden(false);
			
			//locate target
			target = null;
			ArrayList<Robot> nearby = getPosition().toPoint().getEntitiesNearbyHorizontal(Block.GAME_DIAGLENGTH * 4, Robot.class);
			if (!nearby.isEmpty()) {
				Iterator<Robot> it = nearby.iterator();
				while (target == null && it.hasNext()) {
					target = it.next();
					if (target.getTeamId() != getTeamId()) {
						Vector3 vecToTarget = target.getPosition().cpy().sub(gun.getFixedPos()).nor();
						//check if can see target
						Intersection intersect = gun.getFixedPos().rayMarching(
							vecToTarget,
							MAXDISTANCE,
							null,
							(Byte t) -> !Block.isTransparent(t,(byte) 0) && t != CavelandBlocks.CLBlocks.TURRET.getId()
						);

						if (
							intersect != null &&
							gun.getFixedPos().distanceTo(intersect.getPoint()) < gun.getPosition().distanceTo(target.getPosition())//check if point is before
						) {
							//can not see
							target = null;
						} else {
							//can see targetgg
							if (
								target != null
								&& target.hasPosition()
								&& getPosition().distanceTo(target) <= MAXDISTANCE * Block.GAME_EDGELENGTH
							) {
								//aim a bit higher
								vecToTarget = target.getPosition().cpy().add(0, 0, Block.GAME_EDGELENGTH2).sub(gun.getFixedPos()).nor();
								gun.setAimDir(vecToTarget);
								gun.shoot();
							}
						}
					}
				}

				
			}
		} else {
			gun.setLaserHidden(true);
		}
		
	}

	@Override
	public void dispose() {
		super.dispose();
		gun.dispose();
	}

	@Override
	public boolean outgoingConnection(int id) {
		return true;
	}

	private int getTeamId() {
		return teamId;
	}
	
}
