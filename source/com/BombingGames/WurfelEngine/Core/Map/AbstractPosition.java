/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.badlogic.gdx.math.Vector3;

/**
 *A
 * @author Benedikt Vogler
 */
public abstract class AbstractPosition {
    private final int referenceX;//top left chunk x coordinate
    private final int referenceY;//top left chunk Y coordinate
    private float height;

    /**
     * With custom reference
     * @param topleftX the chunk's X coordinate of the chunk at the top left
     * @param topleftY the chunk's Y coordinate of the chunk at the top left 
     */
    public AbstractPosition(final int topleftX, final int topleftY) {
        this.referenceX = topleftX;
        this.referenceY = topleftY;
    }

    
    /**
     * With the currently loaded top left chunk.
     */
    public AbstractPosition() {
        referenceX = Controller.getMap().getChunkCoords(0)[0];
        referenceY = Controller.getMap().getChunkCoords(0)[1];
    }

    /**
     * Geht the height (z-value) of the coordinate (game dimension).
     * @return
     */
    public float getHeight() {
        return height;
    }

    /**
     * 
     * @param height 
     */
    public void setHeight(float height) {
        this.height = height;
    }
    
   /**
    * 
    * @return 
    */
    protected int getReferenceX() {
        return referenceX;
    }

    /**
     * 
     * @return 
     */
    protected int getReferenceY() {
        return referenceY;
    }
    
     /**
     *
     * @return Returns the center of the projected (screen) x-position where the object is rendered without regarding the camera. It also adds the cell offset.
     */
    public abstract int getProjectedPosX();
    
    /**
     *
     * @return Returns the center of the projected (screen) y-position where the object is rendered without regarding the camera. It also adds the cell offset.
     */
    public abstract int getProjectedPosY();
    
    /**
     *
     * @return
     */
    public abstract Point getPoint();
    
    /**
     *
     * @return
     */
    public abstract Coordinate getCoord();
    
    /**
     * 
     * @return Get the block at the position. If the coordiante is outside the map crash. Faster than "getBlockSafe()"
     * @see AbstractPosition#getBlockSafe() 
     */
    public abstract Block getBlock();
    
    /**
     * Get the block at the position. If the coordiante is outside the map return null. Slower than getBlock().
     * @return
     *  @see AbstractPosition#getBlock() 
     */
    public abstract Block getBlockSafe();

    /**
     *
     * @return
     */
    public abstract AbstractPosition cpy(); 
    
    /**
     * Checks if the position is on the chunks currently in memory.
     * @return 
     */
    public abstract boolean onLoadedMap();
    
    /**
     *
     * @param vector
     * @return returns itself
     */
    public abstract AbstractPosition addVector(float[] vector);
    
    /**
     *
     * @param vector
     * @return returns itself
     */
    public abstract AbstractPosition addVector(Vector3 vector);
    
    /**
     *
     * @param x
     * @param y
     * @param z
     * @return returns itself
     */
    public abstract AbstractPosition addVector(float x, float y, float z);
}
