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
package com.BombingGames.WurfelEngine.MapEditor;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class EntityDrawable extends TextureRegionDrawable {
	private AbstractEntity instance = null;
	private float scaling = 0;

	public EntityDrawable(Class<? extends AbstractEntity> type) {
		try {
			instance = type.newInstance();
			if (instance.getId()>0) {
				//if bigger then default sprite size
				int spiteHeight = AbstractGameObject.getSprite('e', instance.getId(), instance.getValue()).packedHeight;
				int regularHeight = AbstractGameObject.VIEW_HEIGHT+AbstractGameObject.VIEW_DEPTH;
				if (
					spiteHeight
					> regularHeight
				)
					scaling =  -(1f-((float) regularHeight)/ ((float) spiteHeight));
				instance.setScaling(scaling);
			}
		} catch (InstantiationException | IllegalAccessException ex) {
			Logger.getLogger(EntityDrawable.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
    public void draw(Batch batch, float x, float y, float width, float height) {
		if (instance !=null)
			instance.render(WE.getEngineView(), (int) x, (int) y);
    }
	
	/**
	 *
	 * @return
	 */
	@Override
	public float getLeftWidth() {
		return 0;
	}
	
	/**
	 *
	 * @return
	 */
	@Override
	public float getRightWidth() {
		return AbstractGameObject.VIEW_WIDTH;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getTopHeight() {
		return AbstractGameObject.VIEW_HEIGHT2 + AbstractGameObject.VIEW_DEPTH2;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getBottomHeight() {
		return AbstractGameObject.VIEW_HEIGHT2 + AbstractGameObject.VIEW_DEPTH2;
	}
}
