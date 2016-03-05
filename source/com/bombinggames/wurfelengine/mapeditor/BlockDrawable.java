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
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;

/**
 * a class what renders a block using the drawable interface.
 *
 * @author Benedikt Vogler
 */
public class BlockDrawable extends TextureRegionDrawable {

	private final RenderBlock block;

	/**
	 *
	 * @param id
	 */
	public BlockDrawable(byte id) {
		if (id >= Block.OBJECTTYPESNUM) {
			this.block = new RenderBlock((byte) 0, (byte) 0);//invalid id.
		} else {
			this.block = new RenderBlock(id, (byte) 0);
		}
	}

	/**
	 *
	 * @param id block id
	 * @param value block value
	 * @param size relative size
	 */
	public BlockDrawable(byte id, byte value, float size) {
		if (id >= Block.OBJECTTYPESNUM) {
			this.block = new RenderBlock((byte) 0, (byte) 0);//invalid id.
		} else {
			this.block = new RenderBlock(id, value);
		}
		block.setScaling(size);
	}
	
	void setValue(byte value) {
		this.block.setSpriteValue(value);
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		if (block != null && block.getSpriteId() != 0) {
			batch.end();//end current batch
			//then use gameplay batch
			boolean wasDefault = false;
			if (WE.getGameplay().getView().isUsingDefaultShader()) {
				WE.getGameplay().getView().setShader(WE.getGameplay().getView().getShader());
				wasDefault = true;
			}
			//batch.setColor(new Color(1, 1, 1, 1));
			batch.begin();

			//block.setColor(new Color(1, 1, 1, 1));
			block.resetLight();
			block.render(WE.getGameplay().getView(),
				(int) (x + Block.VIEW_WIDTH2 * block.getScaling()),//should be with -getLeftWidth() but then deos not align
				(int) y,
				null,
				true
			);

			batch.end();
			if (wasDefault) {
				WE.getGameplay().getView().useDefaultShader();
			}
			batch.begin();
		}
	}

	@Override
	public void draw(Batch batch, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation) {
		block.setScaling(scaleY);
		draw(batch, x, y, width, height);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getLeftWidth() {
		return Block.VIEW_WIDTH2 * block.getScaling();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getRightWidth() {
		return Block.VIEW_WIDTH2 * block.getScaling();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getTopHeight() {
		return 0;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getBottomHeight() {
		return 0;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getMinHeight() {
		return (Block.VIEW_HEIGHT + Block.VIEW_DEPTH) * block.getScaling();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getMinWidth() {
		return Block.VIEW_WIDTH * block.getScaling();
	}
}
