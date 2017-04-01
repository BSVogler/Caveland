/*
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
package com.bombinggames.wurfelengine.core.map;

import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import java.io.Serializable;
import java.util.LinkedList;

/**
 *A
 * @author Benedikt Vogler
 */
public interface Position extends Serializable {

    /**
     *square root of two
     */
    public static final float SQRT2 = 1.4142135623730950488016887242096980785696718753769480f;

    /**
     *half of square root of two
     */
    public static final float SQRT12 = 0.7071067811865475244008443621048490392848359376884740f;
	
	
	/**
     * Calculates it and creates new instance if not already in correct format then return a copy of itself.
     * @return the point representation. Copy safe.
     */
    public abstract Point toPoint();
    
	/**
	 * May not be copy safe.
	 *
	 * @return may not be copy safe
	 */
	public Point getPoint();

	/**
	 * May not be copy safe.
	 *
	 * @return may not be copy safe
	 */
	public Coordinate getCoord();

	/**
	 * Calculates it and creates new instance if not already in correct format
	 * then returns copy of itself.
	 *
	 * @return the coordinate representation. Copy safe
	 */
	public abstract Coordinate toCoord();

	/**
	 * Calculate position in view space.
	 *
	 * @return Returns the center of the projected (screen) x-position where the
	 * object is rendered without regarding the camera. It also adds the cell
	 * offset.
	 */
	public abstract int getViewSpcX();

	/**
	 * Calculate position in view space.
	 *
	 * @return Returns the center of the projected (view space) y-position where
	 * the object is rendered without regarding the camera.
	 */
	public abstract int getViewSpcY();
    
	/**
	 * maybe I mean screen space
	 * @param View
	 * @param camera
	 * @return 
	 */
	public abstract int getProjectionSpaceX(GameView View, Camera camera);
	
	/**
	 * maybe I mean screen space
	 * @param View
	 * @param camera
	 * @return 
	 */
	public abstract int getProjectionSpaceY(GameView View, Camera camera);
	
    /**
     * Get the block at the position.  Clamps positions over the map at topmost layer.
     * @return If the coordiante is not in memory will crash.
     */
    public abstract byte getBlockId();
    
    /**
     *
     * @return a copy of the object.
     */
    public abstract Position cpy(); 
    
    /**
	 * Checks if the coordiantes are accessable with the currently loaded chunks.
	 * Does not check for z axis (horizontal only). So the position can be udner or over the map.
	 *
	 * @return
	 */
    public abstract boolean isInMemoryAreaXY();
	
	/**
     * Checks if the position is on the chunks currently in memory. Checks all axis'.
     * @return <i>true</i> if inside a chunk. <i>false</i> if currently not loaded or outside range.
     */
    public abstract boolean isInMemoryAreaXYZ();
	
	/**
	 *
	 * @param object
	 * @return the distance from this point to the other object
	 */
	public abstract float distanceTo(AbstractGameObject object);
	
	/**
	 * The result is squared for fast comparison.
	 * @param object
	 * @return the distance from this point to the other object squared
	 * @see #distanceTo(AbstractGameObject) 
	 */
	public abstract float distanceToSquared(AbstractGameObject object);

	/**
	 *
	 * @param pos
	 * @return the distance from this pos to the other pos in game coordinates
	 */
	public abstract float distanceTo(Position pos);
	
	/**
	 * The result is squared for fast comparison.
	 * @param pos
	 * @return the distance from this point to the other object squared
	 * @see #distanceTo(Position) 
	 */
	public abstract float distanceToSquared(Position pos);

	/**
	 *  checks only x and y.
	 * @param object
	 * @return the distance from this point to the other point only regarding horizontal components.
	 */
	public abstract float distanceToHorizontal(AbstractGameObject object);

	/**
	 * checks only x and y.
	 * @param pos
	 * @return the distance from this pos to the other pos only regarding horizontal components.
	 */
	public abstract float distanceToHorizontal(Position pos);
	
	/**
	 *  checks only x and y.
	 * @param object
	 * @return the distance from this point to the other point squared only regarding horizontal components.
	 */
	public abstract float distanceToHorizontalSquared(AbstractGameObject object);

	/**
	 * checks only x and y.
	 * @param pos
	 * @return the distance from this pos to the other pos squared only regarding horizontal components.
	 */
	public abstract float distanceToHorizontalSquared(Position pos);
	
	/**
	 * Get entities in radius.
	 *
	 * @param <T> returns only object if type which is the filter
	 * @param radius in game dimension pixels
	 * @param type the type you want to filter
	 * @return every entitie in radius
	 */
	public <T> LinkedList<T> getEntitiesNearby(float radius, final Class<T> type);
	
	/**
	 * get entities in horizontal radius (like a pipe)
	 * @param <T>
	 * @param radius in game dimension pixels
	 * @param type whitelist
	 * @return every entitie in radius
	 */
	public <T> LinkedList<T> getEntitiesNearbyHorizontal(float radius, final Class<T> type);
	
	/**
	 * get entities in radius (horizontal only)
	 *
	 * @param radius in game dimension pixels
	 * @return every entitie in radius
	 */
	public LinkedList<AbstractEntity> getEntitiesNearbyHorizontal(float radius);
	
	
	/**
	 * 
	 * @return 
	 */
	public Chunk getChunk();

	/**
	 * The chunk coordinate.
	 * @return
	 */
	public abstract int getChunkX();

	/**
	 * The chunk coordinate.
	 * @return
	 */
	public abstract int getChunkY();

	/**
	 * Get the z in block grid coordinates of the coordinate. Faster than
	 * transforming to coordinate first.
	 *
	 * @return in grid coordinates.
	 */
	public abstract int getZGrid();

	/**
	 * Get the z in game world coordinates.
	 * @return 
	 */
	public abstract float getZPoint();
}
