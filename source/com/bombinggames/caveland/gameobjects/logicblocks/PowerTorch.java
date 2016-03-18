/*
 *
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
package com.bombinggames.caveland.gameobjects.logicblocks;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class PowerTorch extends AbstractPowerBlock {

	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public final static float POINTRADIUS = 3 * RenderBlock.GAME_EDGELENGTH;
	private PointLightSource lightsource;

	/**
	 *
	 * @param block
	 * @param coord
	 */
	public PowerTorch(byte block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		if (lightsource == null) {
			lightsource = new PointLightSource(Color.YELLOW, 3, 15, WE.getGameplay().getView());
		}

		if (lightsource.getPosition() == null) {
			lightsource.setPosition(getPosition().toPoint().add(0, 0, RenderBlock.GAME_EDGELENGTH2));
		}
		
		if (hasPower()) {
			lightsource.enable();
		} else {
			lightsource.disable();
		}

		lightsource.update(dt);
	}

	@Override
	public void dispose() {
		super.dispose();
		lightsource.dispose();
	}

	
	@Override
	public boolean outgoingConnection(int id) {
		return true;
	}
}
