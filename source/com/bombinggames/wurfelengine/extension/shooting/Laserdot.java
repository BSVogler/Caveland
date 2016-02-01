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
package com.bombinggames.wurfelengine.extension.shooting;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Intersection;
import com.bombinggames.wurfelengine.core.map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class Laserdot extends SimpleEntity {

	private static final long serialVersionUID = 1L;

	private byte ignoreId;

	public Laserdot() {
		super((byte) 22);
		setColor(new Color(1, 0, 0, 1));
		setScaling(-0.95f);
		setSaveToDisk(false);
		setName("Laser dot");
		disableShadow();
	}

	
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	public void update(Vector3 aimDir, Point origin) {
		if (hasPosition() && !aimDir.isZero()) {
			
			Intersection raycast = origin.rayMarching(
				aimDir,
				12,
				null,
				(Block t) -> !t.isTransparent() && t.getId() != ignoreId
			);
			setHidden(raycast == null);
			if (raycast != null && raycast.getPoint() != null) {
				setPosition(raycast.getPoint());
			} else {
				getPosition().setValues(getPosition());
			}
		}
	}

	public void ignoreBlock(byte ignoreId) {
		this.ignoreId = ignoreId;
	}
	
	
	
}
