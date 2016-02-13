/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;
import java.util.ArrayList;

/**
 * Waits for a minecart or player to interact with it.
 *
 * @author Benedikt Vogler
 */
public class LiftBasket extends MovableEntity {

	private static final long serialVersionUID = 1L;
	private transient SimpleEntity front;
	private transient SimpleEntity back;
	private int movementDir;
	private MovableEntity passenger;
	private transient MovableEntity leavingPassenger;

	public LiftBasket() {
		super((byte) 25, (byte) 0);
		setName("Lift Basket");
		checkComponents();
		setSaveToDisk(false);
		setFloating(true);
		setMass(150);
		enableShadow();
	}

	@Override
	public void update(float dt) {
		setMovement(Vector2.Zero);//can only move up or down
		super.update(dt);

		if (hasPosition()) {

			if (leavingPassenger != null && leavingPassenger.getPosition().distanceTo(this) > Block.GAME_EDGELENGTH) {
				leavingPassenger = null;
			}

			//reached top
			if (movementDir > 0) {
				setMovement(new Vector3(0, 0, 4));
			} else if (movementDir < 0) {
				setMovement(new Vector3(0, 0, -4));
			} else {
				stop();
			}

			if (isOnSurface()) {
				//stop upwards movement if in air
				if (movementDir > 0//moving up
					&& (getPosition().getBlock() == null || !getPosition().getBlock().isObstacle())//may be a problem if chunk not yet loaded, todo
					) {
					stop();
				} else if (movementDir < 0//moving down
					&& getPosition().getBlock() != null && !getPosition().getBlock().isObstacle()//may be a problem if chunk not yet loaded, todo
					) {
					ArrayList<Portal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(Block.GAME_EDGELENGTH * 2, Portal.class);
					if (!possibleExitPortals.isEmpty()) {
						possibleExitPortals.get(0).teleport(this);
					}
				}
			} else //underworld
			 if (getPosition().z > Block.GAME_EDGELENGTH * 9) {
					ArrayList<ExitPortal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(Block.GAME_EDGELENGTH * 2, ExitPortal.class);
					if (!possibleExitPortals.isEmpty()) {
						possibleExitPortals.get(0).teleport(this);
					} else {
						setMovementDir(-1);//come back
					}
				} else if (movementDir < 0 && isOnGround()) {
					stop();
				}

			//update passenger
			if (passenger != null) {
				//lock passenger if moving
				if (movementDir != 0) {
					passenger.getPosition().setValues(getPosition());
					passenger.setMovement(Vector2.Zero);
				}
			} else {
				//enter with minecart
				ArrayList<MineCart> possibleMineCart = getPosition().getEntitiesNearbyHorizontal(Block.GAME_EDGELENGTH2, MineCart.class);
				if (!possibleMineCart.isEmpty()) {
					setPassenger(possibleMineCart.get(0));
				}
			}

			//manage sprites
			checkComponents();
		}

//		if (getLiftLogic() == null) {
//			//has no lift
//			if (fahrstuhlkorb != null) {
//				fahrstuhlkorb.disposeFromMap();
//			}
//		} else //has lift
	}

	public void setPassenger(MovableEntity passenger) {
		if (leavingPassenger != passenger) {
			this.passenger = passenger;
			if (isOnSurface()) {
				setMovementDir(-1);
			} else {
				setMovementDir(1);
			}
		}
	}

	private void stop() {
		if (passenger != null) {
			passenger.setMovement(new Vector2(-1, 1).nor().scl(4f));//little bit of movement
			leavingPassenger = passenger;
			passenger = null;
		}
		setMovement(new Vector3());
		movementDir = 0;
	}

	/**
	 *
	 * @param movementDir 1 up, 0 stand, -1 down
	 */
	public void setMovementDir(int movementDir) {
		this.movementDir = movementDir;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		super.handleMessage(msg);
		if (passenger != null) {
			if (msg.message == Events.teleport.getId() && msg.receiver == this) {
				MessageManager.getInstance().dispatchMessage(
					this,
					passenger,
					Events.teleport.getId(),
					(Point) msg.extraInfo
				);
			}
		}
		return false;
	}

	private void checkComponents() {
		setHidden(true);
		if (back == null) {
			back = new SimpleEntity((byte) 25, (byte) 0);
			back.setDimensionZ(Block.GAME_EDGELENGTH * 2);
			back.setName("Lift Basket Back");
			back.setSaveToDisk(false);
		}

		//front
		if (front == null) {
			front = new SimpleEntity((byte) 25, (byte) 1);
			front.setName("Lift Basket front");
			front.setDimensionZ(Block.GAME_EDGELENGTH * 2);
			front.setSaveToDisk(false);
		}

		if (hasPosition()) {

			if (!back.hasPosition()) {
				back.spawn(getPosition().cpy());
			}

			back.getPosition().setValues(getPosition());
			back.getPosition().y -= Block.GAME_DIAGLENGTH2;

			if (!front.hasPosition()) {
				front.spawn(getPosition().cpy());
			}

			front.getPosition().setValues(getPosition());
			front.getPosition().y += 20;
		}
	}

	private boolean isOnSurface() {
		for (int z = 0; z < Chunk.getBlocksZ(); z++) {
			Coordinate checkpos = getPosition().toCoord();
			checkpos.setZ(z);//set height

			Block block = checkpos.getBlock();
			if (block != null && block.getId() == CavelandBlocks.CLBlocks.LIFT.getId()) {
				return true;
			}
		}
		return false;
	}

	public int getMovementDir() {
		return movementDir;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (front != null) {
			front.dispose();
		}
		if (back != null) {
			back.dispose();
		}
	}
}
