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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Component;
import com.bombinggames.wurfelengine.core.gameobjects.EntityShadow;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.LinkedList;

/**
 * Waits for a minecart or player to interact with it.
 *
 * @author Benedikt Vogler
 */
public class LiftBasket extends MovableEntity {

	private static final long serialVersionUID = 1L;
	private int movementDir;
	private MovableEntity passenger;
	private transient MovableEntity leavingPassenger;

	public LiftBasket() {
		super((byte) 25, (byte) 0);
		setName("Lift Basket");
		setHidden(true);
		setSavePersistent(false);
		setFloating(true);
		setMass(150);
		addComponent(new EntityShadow());
		addComponent(new BackSprite());
		addComponent(new FrontSprite());
	}

	@Override
	public void update(float dt) {
		setMovement(Vector2.Zero);//can only move up or down
		super.update(dt);

		if (hasPosition()) {

			if (leavingPassenger != null && leavingPassenger.getPosition().distanceTo(this) > RenderCell.GAME_EDGELENGTH) {
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
					&& (!getPosition().isObstacle())//may be a problem if chunk not yet loaded, todo
					) {
					stop();
				} else if (movementDir < 0//moving down
					&& !getPosition().isObstacle()//may be a problem if chunk not yet loaded, todo
					) {
					LinkedList<Portal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(RenderCell.GAME_EDGELENGTH * 2, Portal.class);
					if (!possibleExitPortals.isEmpty()) {
						possibleExitPortals.getFirst().teleport(this);
					}
				}
			} else //underworld
			 if (getPosition().z > RenderCell.GAME_EDGELENGTH * 9) {
					LinkedList<ExitPortal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(RenderCell.GAME_EDGELENGTH * 2, ExitPortal.class);
					if (!possibleExitPortals.isEmpty()) {
						possibleExitPortals.getFirst().teleport(this);
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
					passenger.getPosition().set(getPosition());
					passenger.setMovement(Vector2.Zero);
				}
			} else {
				//enter with minecart
				LinkedList<MineCart> possibleMineCart = getPosition().getEntitiesNearbyHorizontal(RenderCell.GAME_EDGELENGTH2, MineCart.class);
				if (!possibleMineCart.isEmpty()) {
					setPassenger(possibleMineCart.getFirst());
				}
			}

			setHidden(true);
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

	private boolean isOnSurface() {
		Coordinate checkpos = getPosition().toCoord();
		for (int z = 0; z < Chunk.getBlocksZ(); z++) {
			checkpos.setZ(z);//set height

			if (checkpos.getBlockId()== CavelandBlocks.CLBlocks.LIFT.getId()) {
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
	}

	class BackSprite extends SimpleEntity implements Component {

		private LiftBasket body;

		BackSprite() {
			super((byte) 25, (byte) 0);
			setDimensionZ(RenderCell.GAME_EDGELENGTH * 2);
			setName("Lift Basket Back");
			setSavePersistent(false);
		}

		@Override
		public void update(float dt) {
			if (body.hasPosition()) {
				if (!hasPosition()) {
					spawn(body.getPosition().cpy());
				}

				getPosition().set(body.getPosition());
				getPosition().y -= RenderCell.GAME_DIAGLENGTH2;
			}

		}

		@Override
		public void setParent(AbstractEntity body) {
			if (!(body instanceof LiftBasket)){
				Gdx.app.error("LiftBasket", "Can only be added to Lift Basket");
			}
			this.body = (LiftBasket) body;
		}
	}
	
	class FrontSprite extends SimpleEntity implements Component {

		private LiftBasket body;

		FrontSprite() {
			super((byte) 25, (byte) 1);
			setDimensionZ(RenderCell.GAME_EDGELENGTH * 2);
			setName("Lift Basket Front");
			setSavePersistent(false);
		}

		@Override
		public void update(float dt) {
			if (body.hasPosition()) {
				if (!hasPosition()) {
					spawn(body.getPosition().cpy());
				}

				getPosition().set(body.getPosition());
				getPosition().y += 20;
			}
		}

		@Override
		public void setParent(AbstractEntity body) {
			if (!(body instanceof LiftBasket)){
				Gdx.app.error("LiftBasket", "Can only be added to Lift Basket");
			}
			this.body = (LiftBasket) body;
		}

	}
}
