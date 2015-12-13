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
package com.bombinggames.caveland.Game;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.wurfelengine.core.LightEngine.GlobalLightSource;
import com.bombinggames.wurfelengine.core.LightEngine.LightEngine;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomLightEngine extends LightEngine {
	private final GlobalLightSource customSun;
	private final GlobalLightSource customMoon;

	/**
	 *
	 */
	public CustomLightEngine() {
		super();
		customSun = new GlobalLightSource(40, 40, new Color(0.1f, 0.3f, 0.3f, 1), new Color(0.1f, 0.4f, 0.4f, 1), 90);
		customSun.setFixedPosition(true);
		//invisible moon
		customMoon = new GlobalLightSource(40+180, 40+180, Color.BLACK.cpy(), Color.BLACK.cpy(), 90);
		customMoon.setFixedPosition(true);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		customMoon.update(dt);
		customSun.update(dt);
	}
	
	
	
	
	@Override
	public GlobalLightSource getSun(AbstractPosition pos) {
		//in caves uses another light source
		if (pos.toCoord().getY()>ChunkGenerator.CAVESBORDER)
			return customSun;
		else return super.getSun(pos);
	}
	
	@Override
	public GlobalLightSource getMoon(AbstractPosition pos) {
		//in caves use another light source
		if (pos.toCoord().getY() > ChunkGenerator.CAVESBORDER)
			return customMoon;
		else return super.getMoon(pos);
	}

	@Override
	public Color getAmbient(AbstractPosition pos) {
		//in caves use anotehr lgiht source
		if (pos.toCoord().getY() > ChunkGenerator.CAVESBORDER)
			return customSun.getAmbient();
		else return super.getAmbient(pos);
	}
}
