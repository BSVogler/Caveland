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

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.GameObjects.Enemy;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.extension.shooting.Weapon;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Turret extends AbstractBlockLogicExtension {
	private Enemy target;
	private Weapon gun;
	
	public Turret(Block block, Coordinate coord) {
		super(block, coord);
	}
	
	@Override
	public void update(float dt) {
		///fill gun field
		if (gun==null) {
			//restore if possible
			ArrayList<Weapon> guns = getPosition().getEntitiesInside(Weapon.class);
			if (!guns.isEmpty()) {
				gun = guns.get(0);
			} else {
				gun = (Weapon) new Weapon((byte) 4, null).spawn(getPosition().toPoint().addVector(0, 0, Block.GAME_EDGELENGTH*1.2f));
				gun.ignoreBlock(CavelandBlocks.CLBlocks.TURRET.getId());
				gun.setFireSound("turret", true);
			}
		}
		
		//locate target
		if (target == null || !target.hasPosition()) {
			ArrayList<Enemy> nearby = getPosition().toPoint().getEntitiesNearbyHorizontal(Block.GAME_DIAGLENGTH * 4, Enemy.class);
			if (!nearby.isEmpty()) {
				target = nearby.get(0);
			}
		}

		if (target != null && target.hasPosition() && getPosition().distanceTo(target) < Block.GAME_DIAGLENGTH * 4) {
			gun.setAim(target.getPosition().getVector().sub(getPosition().toPoint().getVector()).nor());
			gun.shoot();
		}
		
	}

	@Override
	public void dispose() {
		gun.dispose();
	}
	
}
