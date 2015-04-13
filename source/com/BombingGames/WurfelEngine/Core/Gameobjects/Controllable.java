/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.math.Vector2;

/**
 *
 * @author Benedikt Vogler
 */
public class Controllable extends MovableEntity {
	private static final long serialVersionUID = 1L;

	/**
	 *
	 * @param id
	 * @param spritesPerDir
	 */
	public Controllable(byte id, int spritesPerDir) {
		super(id, spritesPerDir);
		setFriction(WE.CVARS.getValueF("friction"));
	}
	
   /**
     * Lets the player walk.
     * @param up move up?
     * @param down move down?
     * @param left move left?
     *  @param right move right?
     * @param walkingspeed the higher the speed the bigger the steps. Should be in m/s.
	 * @param dt
     */
    public void walk(boolean up, boolean down, boolean left, boolean right, float walkingspeed, float dt) {
        if (up || down || left || right){
			
			//update the direction vector
			Vector2 dir = new Vector2(0f,0f);

            if (up)    dir.y += -1;
            if (down)  dir.y += 1;
            if (left)  dir.x += -1;
            if (right) dir.x += 1;
			dir.nor().scl(walkingspeed);
			
			//set speed to 0 if at max allowed speed for accelaration and moving in movement direction
			//in order to find out, add movement dir and current movement dir together and if len(vector) > len(currentdir)*sqrt(2) then added speed=0
//			float accelaration =30;//in m/s^2
//			dir.scl(accelaration*dt/1000f);//in m/s
			
			//check if will reach max velocity
//			Vector3 res = getMovement().add(dir.cpy());
//			res.z=0;
//			if (res.len() > walkingspeed){
//				//scale that it will not exceed the walkingspeed
//				dir.nor().scl((walkingspeed-res.len()));
//			}
//			addMovement(dir);
			
			//repalce horizontal movement if walking
			setHorMovement(dir);
        }
   }
	@Override
	public void jump() {
		jump(3, true);
	}

}
