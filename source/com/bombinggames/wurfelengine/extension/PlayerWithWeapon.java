/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.bombinggames.wurfelengine.extension;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Point;
import com.bombinggames.wurfelengine.extension.shooting.Weapon;

/**
 * The WeaponPlayer is a character who can walk and shoot.
 *
 * @author Benedikt
 */
public class PlayerWithWeapon extends MovableEntity {

	private static final long serialVersionUID = 1L;

	private transient Camera camera;
	private Weapon weapon;

	/**
	 * Creates a player. The parameters are for the lower half of the player.
	 *
	 * @param spritesPerDir
	 * @param height
	 */
	public PlayerWithWeapon(int spritesPerDir, int height) {
		super((byte) 30, spritesPerDir);
		Gdx.app.debug("Player", "Creating player");

		setObstacle(true);
		setFriction((float) WE.getCvars().get("playerfriction").getValue());
		setDimensionZ(height);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		weapon.spawn(point.cpy());
		return this;
	}

	/**
	 * Jumps the player with a sound
	 */
	@Override
	public void jump() {
		jump(5, true);
	}

	/**
	 * Getting aim relative to middle of view by reading mouse position. If no
	 * camera is configured dircetion of head.
	 *
	 * @return
	 */
	@Override
	public Vector3 getAiming() {
		Vector3 aim;
		if (camera != null) {
			aim = new Vector3(
				Gdx.input.getX() - camera.getWidthInScreenSpc() / 2,
				2 * (Gdx.input.getY() - camera.getHeightInScreenSpc() / 2),
				0
			);
		} else {
			aim = new Vector3(getOrientation(), 0);
		}
		return aim.nor();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (weapon != null) {
			weapon.getPosition().setValues(getPosition());
			weapon.update(dt);
		}
	}

	/**
	 * Set the camera which is renderin the player to calculate the aiming. If
	 * camera is null
	 *
	 * @param camera
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 *
	 * @return
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 *
	 * @return
	 */
	public Weapon getWeapon() {
		return weapon;
	}

	/**
	 * Gives the player a weapon.
	 *
	 * @param weapon
	 */
	public void equipWeapon(Weapon weapon) {
		if (this.weapon != null) {
			this.weapon.disposeFromMap();
		}
		this.weapon = weapon;
		weapon.reload();
	}

}
