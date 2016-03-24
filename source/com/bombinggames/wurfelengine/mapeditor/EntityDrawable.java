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
package com.bombinggames.wurfelengine.mapeditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 *
 * @author Benedikt Vogler
 */
public class EntityDrawable extends TextureRegionDrawable {
	private AbstractEntity instance = null;
	private float scaling = 1;

	/**
	 *
	 * @param type
	 * @throws java.lang.InstantiationException
	 * @throws java.lang.IllegalAccessException
	 */
	public EntityDrawable(Class<? extends AbstractEntity> type) throws InstantiationException, IllegalAccessException {
		instance = type.newInstance();
		if (instance.getSpriteId() > 0) {
			//if bigger then default sprite size
			float spiteHeight = AbstractGameObject.getSprite('e', instance.getSpriteId(), instance.getSpriteValue()).packedHeight;
			float regularHeight = RenderCell.VIEW_HEIGHT+RenderCell.VIEW_DEPTH;
			if (spiteHeight > regularHeight) {
				scaling = (regularHeight / spiteHeight);
			}
		}
		instance.setScaling(scaling*0.5f);
	}

	@Override
    public void draw(Batch batch, float x, float y, float width, float height) {
		if (instance != null) {
			batch.end();
			
			//then use gameplay batch
			boolean wasDefault = false;
			if (WE.getGameplay().getView().isUsingDefaultShader()) {
				WE.getGameplay().getView().setShader(WE.getGameplay().getView().getShader());
				wasDefault = true;
			}
			WE.getGameplay().getView().getSpriteBatch().begin();
			instance.render(WE.getGameplay().getView(), (int) ((int) x+RenderCell.VIEW_WIDTH2*instance.getScaling()), (int) y);
			WE.getGameplay().getView().getSpriteBatch().end();
			if (wasDefault) {
				WE.getGameplay().getView().useDefaultShader();
			}
			batch.begin();
		}
    }
	
	@Override
	public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
		instance.setScaling(scaling*scaleY);
		draw(batch, x, y, width, height);
	}
	
	/**
	 *
	 * @return
	 */
	@Override
	public float getLeftWidth() {
		return RenderCell.VIEW_WIDTH2*(instance.getScaling());
	}
	
	/**
	 *
	 * @return
	 */
	@Override
	public float getRightWidth() {
		return RenderCell.VIEW_WIDTH2*(instance.getScaling());
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getTopHeight() {
		return (RenderCell.VIEW_HEIGHT2+RenderCell.VIEW_DEPTH2)*(instance.getScaling());
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getBottomHeight() {
		return (RenderCell.VIEW_HEIGHT2+RenderCell.VIEW_DEPTH2)*(instance.getScaling());
	}
	
    /**
     *
     * @return
     */
    @Override
    public float getMinHeight() {
        return (RenderCell.VIEW_HEIGHT+RenderCell.VIEW_DEPTH)*(instance.getScaling());
    }

    /**
     *
     * @return
     */
    @Override
    public float getMinWidth() {
		return RenderCell.VIEW_WIDTH*(instance.getScaling());
    }
}
