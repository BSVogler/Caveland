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

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 *
 * @author Benedikt Vogler
 */
public class BlockDrawable extends TextureRegionDrawable {
    private Block block;
    private float size = -0.5f;
    
    /**
     *
     * @param id
     */
    public BlockDrawable(int id) {
        this.block = Block.getInstance(id,0,new Coordinate(0, 0, 0, true));
    }



    @Override
    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        block.render(WE.getEngineView(), (int) x, (int) y, Color.GRAY.cpy(), size, true);
    }

	@Override
	public float getLeftWidth() {
		return Block.SCREEN_WIDTH2*(1f+size);
	}
	
	@Override
	public float getRightWidth() {
		return Block.SCREEN_WIDTH2*(1f+size);
	}

	@Override
	public float getTopHeight() {
		return (Block.SCREEN_HEIGHT2+Block.SCREEN_DEPTH2)*(1f+size);
	}

	@Override
	public float getBottomHeight() {
		return (Block.SCREEN_HEIGHT2+Block.SCREEN_DEPTH2)*(1f+size);
	}

	
	
	

    /**
     *
     * @return
     */
    @Override
    public float getMinHeight() {
        return (Block.SCREEN_HEIGHT+Block.SCREEN_DEPTH)*(1f+size);
    }

    /**
     *
     * @return
     */
    @Override
    public float getMinWidth() {
        return Block.SCREEN_WIDTH*(1f+size);
    }
}
