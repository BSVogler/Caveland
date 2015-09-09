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
package com.bombinggames.wurfelengine.core.Map;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *A
 * @author Benedikt Vogler
 */
public abstract class AbstractPosition implements Serializable {
	private static final long serialVersionUID = 1L;

    /**
     *square root of two
     */
    public static final float SQRT2 = 1.4142135623730950488016887242096980785696718753769480f;

    /**
     *half of square root of two
     */
    public static final float SQRT12 = 0.7071067811865475244008443621048490392848359376884740f;
	
	
	/**
     * Calculates it and creates new instance if not already in correct format then return itself.
     * @return the point representation. Copy safe.
     */
    public abstract Point toPoint();
    
    /**
     * Calculates it and creates new instance if not already in correct format then return itself.
     * @return the coordinate representation. Copy safe
     */
    public abstract Coordinate toCoord();
	
     /**
     * Calculate position in view space.
	 * @param View
     * @return Returns the center of the projected (screen) x-position where the object is rendered without regarding the camera. It also adds the cell offset.
     */
    public abstract int getViewSpcX(GameView View);
    
    /**
     * Calculate position in view space.
	 * @param View
     * @return Returns the center of the projected (view space) y-position where the object is rendered without regarding the camera.
     */
    public abstract int getViewSpcY(GameView View);
    
	/**
     *Get as vector
     * @return copy safe
     */
    public abstract Vector3 getVector();
	
    /**
     * Get the block at the position.  Clamps positions over the map at topmost layer.
     * @return If the coordiante is not in memory will crash.
     */
    public abstract Block getBlock();
    
    /**
     *
     * @return a copy of the object.
     */
    public abstract AbstractPosition cpy(); 
    
    /**
     * Checks if the position is on the chunks currently in memory. Horizontal checks only. So the position can be udner or over the map.
     * @return 
     */
    public abstract boolean isInMemoryAreaHorizontal();
	
	/**
     * Checks if the position is on the chunks currently in memory. Checks all axis'.
     * @return <i>true</i> if inside a chunk. <i>false</i> if currently not loaded.
     */
    public abstract boolean isInMemoryArea();
	
	
    /**
     *
     * @param vector
     * @return returns itself
     */
    public abstract AbstractPosition addVector(Vector3 vector);
    
        /**
     * Returns the depth of the object. The depth is an int value wich is needed for producing the list of the renderorder. The higher the value the later it will be drawn.
	 * @param view
     * @return the depth in game size
     */
	public int getDepth(GameView view){
        return (int) (
			(
				view.getOrientation()==0
				?
					 toPoint().getY()//Y
				:
					(
						view.getOrientation()==2
						?
							Controller.getMap().getGameDepth()-toPoint().getY()//Y
						:
							0
					)
			)            
            + toPoint().getZ()*SQRT12//Z
        );
    }

	/**
	 *
	 * @param object
	 * @return the distance from this point to the other object
	 */
	public abstract float distanceTo(AbstractGameObject object);

	/**
	 *
	 * @param pos
	 * @return the distance from this pos to the other pos in game coordinates
	 */
	public abstract float distanceTo(AbstractPosition pos);

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
	public abstract float distanceToHorizontal(AbstractPosition pos);
	
	/**
	 * Get entities in radius.
	 *
	 * @param <type> returns only object if type which is the filter
	 * @param radius in game dimension pixels
	 * @param type the type you want to filter
	 * @return every entitie in radius
	 */
	@SuppressWarnings("unchecked")
	public <type> ArrayList<type> getEntitiesNearby(float radius, final Class<? extends AbstractEntity> type) {
		ArrayList<type> result = new ArrayList<>(5);//default size 5

		for (AbstractEntity entity : Controller.getMap().getEntitys(type)) {
			if (distanceTo(entity.getPosition()) < radius) {
				result.add((type) entity);
			}
		}

		return result;
	}
}
