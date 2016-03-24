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

package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 *
 * @author Benedikt Vogler
 */
public enum Side {
    /**The id of the left side of a block.*/
    LEFT(0),

    /**
     *
     */
    TOP(1),

    /**
     *
     */
    RIGHT(2),
	
	/**
	 *
	 */
	BACKLEFT(3),
	
	/**
	 *
	 */
	BACKRIGHT(4),

	/**
	 *
	 */
	BOTTOM(5);
    private int code;

    private Side(int c) {
        code = c;
    }

    /**
     * The side as integer.
     * @return 
     */
    public int getCode() {
        return code;
    }
    
    /**
     *Get the side belonging to a vector
     * @param normal
     * @return
     */
    public static Side normalToSide(Vector3 normal){
        if (normal.z > 0) {
            return TOP;
		} else {
			if (normal.x < 0) {
				if (normal.y > 0) {
					return LEFT;
				} else {
					return BACKLEFT;
				}
			} else {
				if (normal.y > 0) {
					return RIGHT;
				} else {
					return BACKRIGHT;
				}
			}
		}
	}

	/**
	 * copy safe
	 * @return 
	 */
	public Vector3 toVector() {
		switch (this) {
			case TOP:
				return new Vector3(0, 0, 1);
			case RIGHT:
				return new Vector3(1, 1, 0).nor();
			case LEFT:
				return new Vector3(-1, 1, 0).nor();
			case BACKLEFT:
				return new Vector3(-1, -1, 0).nor();
			case BACKRIGHT:
				return new Vector3(1, -1, 0).nor();
			default:
				return new Vector3(0, 0, -1);
		}
	}
	
	/**
	 * 
	 * @param point
	 * @return 
	 */
	public static Side calculateNormal(Point point){
		Point coordPoint = point.toCoord().toPoint();
		if (point.getZ() <= coordPoint.getZ()) {
			return Side.BOTTOM;
		} else if (point.getZ() >= coordPoint.getZ() + RenderCell.GAME_EDGELENGTH - 2f) {//point is at top with a 2 error margin
			return Side.TOP;
		} else {
			Side normal;
			if (point.getX() > coordPoint.getX()) {
				normal = Side.RIGHT;
			} else {
				normal = Side.LEFT;
			}
			if (point.getY() < coordPoint.getY()) {
				if (normal == Side.RIGHT) {
					normal = Side.BACKRIGHT;
				} else {
					normal = Side.BACKLEFT;
				}
			}
			return normal;
		}
	}
}
