/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.wurfelengine.core.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.Side;

/**
 *
 * @author Benedikt Vogler
 */
public class Intersection {

	private Side normal;
	private Point point;
	private float distance;

	/**
	 *
	 * @param point intersection point
	 * @param normal the normal
	 * @param distance distance of the ray
	 */
	public Intersection(Point point, Side normal, float distance) {
		this.normal = normal;
		this.point = point;
		this.distance = distance;
	}

	/**
	 * Creates an empty intersection
	 */
	Intersection() {
		normal = null;
		point = null;
	}

	/**
	 *
	 * @return the normal
	 */
	public Side getNormal() {
		return normal;
	}

	/**
	 *
	 * @return intersection point
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 *
	 * @return distance of the ray
	 */
	public float getDistance() {
		return distance;
	}

	/**
	 * performs a line-box intersection.<br> The result are outside the coordiante grid field, so they are forced in it.
	 * 
	 * @param target target coordinate
	 * @param p starting point
	 * @param dir direction of ray
	 * @return null if not hitting
	 */
	public static Intersection intersect(final Coordinate target, final Point p, final Vector3 dir) {
		final Vector3 back = target.toPoint().add(0, -Block.GAME_DIAGLENGTH2, 0);
		final Vector3 front = target.toPoint().add(0, Block.GAME_DIAGLENGTH2, Block.GAME_EDGELENGTH);

		Intersection inter = new Intersection();

		float a = Float.NEGATIVE_INFINITY;
		float b = Float.NEGATIVE_INFINITY;
		if (dir.x != 0) {
			a = (back.x - p.getX()) / dir.x;
			b = (front.x - p.getX()) / dir.x;
		}

		float tmin = Math.min(a, b);
		float tmax = Math.max(a, b);

		a = Float.NEGATIVE_INFINITY;
		b = Float.NEGATIVE_INFINITY;
		if (dir.y != 0) {
			a = (back.y - p.getY()) / dir.y;
			b = (front.y - p.getY()) / dir.y;
		}

		tmin = Math.max(tmin, Math.min(a, b));
		tmax = Math.min(tmax, Math.max(a, b));

		//z
		a = Float.NEGATIVE_INFINITY;
		b = Float.NEGATIVE_INFINITY;
		if (dir.z != 0) {
			a = (back.z - p.getZ()) / dir.z;
			b = (front.z - p.getZ()) / dir.z;
		}
		tmin = Math.max(tmin, Math.min(a, b));
		tmax = Math.min(tmax, Math.max(a, b));

		//find t
		float t = tmin;
		if (t < 0) {
			t = tmax;
			if (t < 0) {
				return null;///not hitting
			}
		}

		//add dir because dir*t end's outside the target
		Vector3 i_outside3 = p.cpy().add(dir.cpy().scl(t));//regular i calculation
		Vector2 i_outside2 = new Vector2(i_outside3.x, i_outside3.y);
		//center as 2d vector
		Vector2 c = new Vector2(target.toPoint().x, target.toPoint().y);
		Vector2 d = i_outside2.sub(c);
		Vector2 i_inside2 = new Vector2(d.x, d.y).scl(0.5f);//move from center in direction of intersection point, empiracl factor 0.5 becaue it's bugged
		
		Point intersPoint;
		
		//lower a bit to prevent that is at next grid level
		if (i_outside3.z >= target.toPoint().getZ() + Block.GAME_EDGELENGTH) {
			intersPoint = new Point(
				i_outside3.x,
				i_outside3.y,
				i_outside3.z-1
			);
		}else {
			intersPoint = new Point(
				target.toPoint().getX() + i_inside2.x,
				target.toPoint().getY() + i_inside2.y,
				i_outside3.z
			);
		}

		inter.point = intersPoint;
		inter.normal = Side.calculateNormal(inter.point);
		
//		Vector3 stepBack = inter.normal.toVector();
//		stepBack.z = 0;
//		stepBack.nor();
//		stepBack.scl(-1);
//		inter.point.addVector(stepBack);//stay inside block

		inter.distance = Math.abs(t);
//		Particle dust = (Particle) new Particle(
//			(byte) 22,
//			200f
//		).spawn(inter.point.cpy());
//		dust.setMovement(inter.normal.toVector().scl(3f));
		return inter;
	}
	
	/**
	 * Calcualte the normal based of the position of the point.
	 * @param p 
	 */
	public void calcNormal(Point p){
		normal = Side.calculateNormal(p);
	}
}
