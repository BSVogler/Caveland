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

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Point;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class PathfindingTest extends SimpleEntity {

	private static final long serialVersionUID = 1L;
	
	SimpleEntity end;
	ArrayList<AimBand> aimBandList = new ArrayList<>(10);
	
	public PathfindingTest() {
		super((byte) 22);
		setName("pathfinding test");
	}

	@Override
	public AbstractEntity spawn(Point point) {
		return super.spawn(point);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (hasPosition()) {
			if (end == null) {
				end = new SimpleEntity((byte) 22);
				if (!end.hasPosition()){
					end.spawn(getPosition().cpy());
				}
			}
			DefaultGraphPath<Coordinate> path = Controller.getMap().findPath(getPosition().toCoord(), end.getPosition().toCoord());
			
			//transform to aimbands
			Coordinate last = getPosition().toCoord();
			aimBandList.forEach(aimBand -> aimBand.dispose());
			aimBandList.clear();
			for (Coordinate coord : path) {
				aimBandList.add(new AimBand(last, coord));
			}
		}
	}
	
	
	
	
}
