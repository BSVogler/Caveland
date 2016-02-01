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
package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.PfNode;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class PathfindingTest extends SimpleEntity {

	private static final long serialVersionUID = 1L;
	
	private SimpleEntity end;
	private final ArrayList<AimBand> aimBandList = new ArrayList<>(10);
	private final AimBand directAimBand = new AimBand(this, end);
	
	/**
	 *
	 */
	public PathfindingTest() {
		super((byte) 22);
		setName("Start pathfinding test");
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
				end.setName("End Pathfinding test");
			}
			if (!end.hasPosition()){
				end.spawn(getPosition().toCoord().add(0, 1, 0).toPoint());
				directAimBand.setTarget(end);
			}
			
			directAimBand.update();
			
			//end.getPosition().setValues(getPosition()).ad
			
			//transform to aimbands
			Coordinate last = getPosition().toCoord();
			aimBandList.forEach(aimBand -> aimBand.dispose());
			aimBandList.clear();
			
			DefaultGraphPath<PfNode> path = Controller.getMap().findPath(
				getPosition().toCoord(), end.getPosition().toCoord()
			);
			for (PfNode coord : path) {
				aimBandList.add(new AimBand(last, coord));
			}
			for (AimBand aimBand : aimBandList) {
				aimBand.update();
			}
		}
	}
	
	
	
	
}
