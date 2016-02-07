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
package com.bombinggames.caveland.game;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.lightengine.GlobalLightSource;
import com.bombinggames.wurfelengine.core.lightengine.LightEngine;
import com.bombinggames.wurfelengine.core.map.Position;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLightEngine extends LightEngine {
	private final GlobalLightSource caveSun;

	/**
	 *
	 */
	public CustomLightEngine() {
		super();
		GlobalLightSource customSun = new GlobalLightSource(
			-WE.getCVars().getValueI("worldSpinAngle"),
			0,
			new Color(1.0f, 0.8f, 0.3f, 1),
			new Color(0.5f, 0.5f, 0.4f, 1),
			1.0f,
			60
		);
		setSun(customSun);
		
		//invisible moon
//		customMoon = new GlobalLightSource(40+180, 40+180, Color.BLACK.cpy(), Color.BLACK.cpy(), 90);
//		customMoon.setFixedPosition(true);
//		setMoon(customMoon);
		
		caveSun = new GlobalLightSource(
			40,
			40,
			new Color(0.1f, 0.3f, 0.3f, 1),
			new Color(0.1f, 0.4f, 0.4f, 1),
			1f,
			90
		);
		caveSun.setFixedPosition(true);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		caveSun.update(dt);
	}
	
	
	@Override
	public GlobalLightSource getSun(Position pos) {
		//in caves uses another light source
		if (pos.toCoord().getY() > ChunkGenerator.CAVESBORDER) {
			return caveSun;
		} else {
			return super.getSun(pos);
		}
	}
	
	@Override
	public GlobalLightSource getMoon(Position pos) {
		//in caves use another light source
		if (pos.toCoord().getY() > ChunkGenerator.CAVESBORDER)
			return null;
		else return super.getMoon(pos);
	}

	@Override
	public Color getAmbient(Position pos) {
		//in caves use anotehr lgiht source
		if (pos.toCoord().getY() > ChunkGenerator.CAVESBORDER)
			return caveSun.getAmbient();
		else return super.getAmbient(pos);
	}
}
