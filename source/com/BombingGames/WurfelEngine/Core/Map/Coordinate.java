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
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.View;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * A coordinate is a reference to a specific cell in the map. The coordinate can transcode between relative and absolute coordinates.<br />
 * Relative coordinates are similar to the map array. Absolute coordinates are indipendent of the current map but to access them you must have the chunk where the coordiantes are pointing to in memory.<br />
 * The coordinate uses a continously height value. The Z coordinate value can be calculated. 
 * @author Benedikt Vogler
 */
public class Coordinate extends AbstractPosition {
	private static final long serialVersionUID = 1L;
	/**
	 * The x coordinate. Position from left
	 */
    private int x;
	/**
	 * The y coordinate. Position from behind.
	 */
    private int y;
	/**
	 * The z coordinate. Position from ground.
	 */
	private int z;
	/**
	 * gets calculated every time the coordinate is written to.
	 */
	private Point cachedPoint;
    
    /**
     * Creates a coordiante refering to the given position on the map.
     * @param x The x value as coordinate.
     * @param y The y value as coordinate.
     * @param z The z value as coordinate.
     */
    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
		this.z = z;
		refreshCachedPoint();
    }
    
    /**
     * Creates a new coordinate from an existing coordinate
     * @param coord the Coordinate you want to copy
     */
    public Coordinate(Coordinate coord) {
        this.x = coord.x;
        this.y = coord.y;
		this.z = coord.z;
		refreshCachedPoint();
    }
    
    /**
     *Gets the X coordinate relative to the map.
     * @return
     */
    public int getX(){
        return x;
    }
    /**
     *Gets the Y coordinate relative to the map.
     * @return
     */
    public int getY(){
        return y;
    }
	
	public int getZ(){
		return z;
	}
    
    
    /**
     *Checks if the calculated value is inside the map dimensions and if not clamps it to the map dimensions.
     * @return
     * @see #getZ() 
     */
    public int getZClamp(){
        if (z >= Map.getBlocksZ())
            return Map.getBlocksZ() -1;
        else if (z < 0)
                return 0;
             else
                return z;
    }
    
    
	@Override
    public Vector3 getVector(){
        return new Vector3(x, y, z);
    }
    
    /**
     *Set the coordiantes X component.
     * @param x
     */
    public void setX(int x){
        this.x = x;
		refreshCachedPoint();
    }
    
    /**
     *Set the coordiantes Y component.
     * @param y
     */
    public void setY(int y){
        this.y = y;
		refreshCachedPoint();
    }
    
    /**
     *Set the coordinates Z component. It will be transversed into a float value (height).
     * @param z
     */
    public void setZ(int z){
		this.z = z;
		refreshCachedPoint();
    }

    /**
     *Set a block in the map where the coordinate is pointing to.
     * @param block the block you want to set.
     */
    public void setBlock(Block block){
		block.setPosition(this);
        Controller.getMap().setData(block);
    }
    

    
    /**
     * Add a vector to the coordinates. If you just want the result and don't change the coordiantes use addVectorCpy.
     * @param vector
     * @return the new coordiantes which resulted of the addition
     */
    @Override
    public Coordinate addVector(float[] vector) {
        this.x += vector[0];
        this.y += vector[1];
		this.z += vector[2];
		refreshCachedPoint();
        return this;
    }
    
    /**
     *
     * @param vector
     * @return
     */
    @Override
    public Coordinate addVector(Vector3 vector) {
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
		refreshCachedPoint();
        return this;
    }
    
     /**
     * Add a vector to the coordinates. If you just want the result and don't change the coordiantes use addVectorCpy.
     * @param x
     * @param y
     * @param z
     * @return the new coordiantes which resulted of the addition
     */
    @Override
    public Coordinate addVector(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
    
    @Override
    public Block getBlock(){
		if (z<0)
			return Controller.getMap().getGroundBlock();
		else return Controller.getMap().getBlock(this);
    }
    
        /**
     *Checks of coordinates are valid before fetching the Block.
     * @return
     */
    @Override
    public Block getBlockSafe(){
        if (isInMemoryHorizontal())
            return Controller.getMap().getBlock(this);
        else return null;
    }
    
   /**
     * The block hides the past block when it has sides and is not transparent (like normal block)
	 * @param x offset in coords
	 * @param y offset in coords
	 * @param z offset in coords
     * @return true when hiding the past Block
     */
    public boolean hidingPastBlock(int x, int y, int z){
		Block block = Controller.getMap().getBlock(getX()+x, getY()+y, getZ()+z);
        return (block.hasSides() && ! block.isTransparent());
    }
    
    /** @return a copy of this coordinate */
    @Override
    public Coordinate cpy () {
        return new Coordinate(this);
    }
    
    /**
     * Checks if the coordiantes are accessable with the currently loaded Chunks (horizontal only).
     * @return 
     */
    @Override
    public boolean isInMemoryHorizontal(){
        boolean found = false;
		for (Chunk chunk : Controller.getMap().getData()){
			if (chunk.hasCoord(this))
				found = true;
		}
        return found;
    }
	
	 /**
     * Checks if the coordiantes are accessable with the currently loaded Chunks (x,y,z).
     * @return 
     */
    @Override
    public boolean isInMemory(){
		boolean found = false;
		if (getZ() >= 0 && getZ() < Map.getBlocksZ()){
			for (Chunk chunk : Controller.getMap().getData()){
				if (chunk.hasCoord(this))
					found = true;
			}
		}
        return found;
    }

    /**
     * Returns the field-id where the coordiantes are inside in relation to the current field. Field id count clockwise, starting with the top with 0.
     * If you want to get the neighbour you can use {@link #neighbourSidetoCoords(int)} with the parameter found by this function.
     * The numbering of the sides:<br>
     * 7 \ 0 / 1<br>
     * -------<br>
     * 6 | 8 | 2<br>
     * -------<br>
     * 5 / 4 \ 3<br>
	 * O(const)
     * @param x game-space-coordinates, value in pixels
     * @param y game-space-coordinates, value in pixels
     * @return Returns the fieldnumber of the coordinates. 8 is the field itself.
     * @see #neighbourSidetoCoords(int)
     */
    public static int getNeighbourSide(float x, float y) {       
        int result = 8;//standard result
        if (x + y <= Block.SCREEN_DEPTH) {
            result = 7;
        }
        if (x - y >= Block.SCREEN_DEPTH) {
            if (result == 7) {
                result = 0;
            } else {
                result = 1;
            }
        }
        if (x + y >= 3 * Block.SCREEN_DEPTH) {
            if (result == 1) {
                result = 2;
            } else {
                result = 3;
            }
        }
        if (-x + y >= Block.SCREEN_DEPTH) {
            if (result == 3) {
                result = 4;
            } else if (result == 7) {
                result = 6;
            } else {
                result = 5;
            }
        }
        return result;
    }

    /**
     * Get the neighbour coordinates of the neighbour of the coords you give.<br />
     * 7 \ 0 / 1<br />
     * -------<br />
     * 6 | 8 | 2<br />
     * -------<br />
     * 5 / 4 \ 3<br />
	 * O(const)
     * @param neighbourSide the side number of the given coordinates
     * @return The coordinates of the neighbour.
     */
    public Coordinate neighbourSidetoCoords(final int neighbourSide) {
        int[] result = new int[3];
        switch (neighbourSide) {
            case 0:
                result[0] = getX();
                result[1] = getY() - 2;
                break;
            case 1:
                result[0] = getX() + (getY() % 2 == 1 ? 1 : 0);
                result[1] = getY() - 1;
                break;
            case 2:
                result[0] = getX() + 1;
                result[1] = getY();
                break;
            case 3:
                result[0] = getX() + (getY() % 2 == 1 ? 1 : 0);
                result[1] = getY() + 1;
                break;
            case 4:
                result[0] = getX();
                result[1] = getY() + 2;
                break;
            case 5:
                result[0] = getX() - (getY() % 2 == 0 ? 1 : 0);
                result[1] = getY() + 1;
                break;
            case 6:
                result[0] = getX() - 1;
                result[1] = getY();
                break;
            case 7:
                result[0] = getX() - (getY() % 2 == 0 ? 1 : 0);
                result[1] = getY() - 1;
                break;
            default:
                result[0] = getX();
                result[1] = getY();
        }
        result[2] = getZ();
        return new Coordinate(result[0], result[1], result[2]);
    }
    
    /**
     *
     * @return the coordiante's origin is the center
     */
    @Override
    public Point getPoint() {
		return cachedPoint;
    }
	
	/**
	 * refresh the field cachedPoint
	 */
	private void refreshCachedPoint(){
		cachedPoint = new Point(
            x*Block.GAME_DIAGLENGTH + (y%2==1 ? Block.SCREEN_WIDTH2 : 0),
            y*Block.GAME_DIAGLENGTH2,
            z*Block.GAME_EDGELENGTH
        );
	}

    /**
     *
     * @return
     */
    @Override
    public Coordinate getCoord() {
        return this;
    }
	
	 /**
     * Get every entity on a coord.
     * @return a list with the entitys
     */
    public ArrayList<AbstractEntity> getEntitiesInside() {
       return Controller.getMap().getEntitysOnCoord(this);
    }
    
      /**
     * Get every entity on this coord of the wanted type
     * @param <type> the class you want to filter.
     * @param type the class you want to filter.
     * @return a list with the entitys of the wanted type
     */
    public <type> ArrayList<type> getEntitysInside(final Class<? extends AbstractEntity> type) {
        return Controller.getMap().getEntitysOnCoord(this, type);
    }
    
    @Override
    public int getViewSpcX(View view) {
		return getX() * AbstractGameObject.SCREEN_WIDTH //x-coordinate multiplied by the projected size in x direction
                //+ AbstractGameObject.SCREEN_WIDTH2 //add half tile for center
                + (Math.abs(getY() % 2)) * AbstractGameObject.SCREEN_WIDTH2; //offset by y
    }

    @Override
    public int getViewSpcY(View view) {
		return (int) (
			(
				view.getOrientation()==0
				?
					-getY() * AbstractGameObject.SCREEN_DEPTH2 //y-coordinate multiplied by half of the projected size in y direction
				:
					(
						view.getOrientation()==2
						?
							getY() * AbstractGameObject.SCREEN_DEPTH2
						:
							0
					)
			)
				
           // + AbstractGameObject.SCREEN_DEPTH2 //add half tile for center 
            + z*Block.GAME_EDGELENGTH *AbstractPosition.SQRT12 //subtract height and take axis shortening into account
        );
    }
    
	/**
	 * destroys the block at the current position, replacing by air. Calls onDestroy()
	 * @return true if destroyed, false if nothing destroyed
	 */
	public boolean destroy() {
		if (isInMemory() && getBlock().getId()!=0) {
			getBlock().onDestroy(this);//call destruction method
			setBlock(Block.getInstance(0));
			return true;
		}
		else return false;
	}
	
		/**
	 * returns true if block got damaged
	 * @param amount
	 * @return 
	 */
	public boolean damage(float amount) {
		if (isInMemory()) {
			Block block = getBlock();
			if (block.getId()!=0) {
				block.setHealth(block.getHealth()-amount);
				if (block.getHealth()<=0)
					destroy();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Coordinate other = (Coordinate)obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return z == other.z;
	}

	@Override
	public int hashCode() {
		//using generated source
		int hash = 7;
		hash = 53 * hash + this.x;
		hash = 53 * hash + this.y;
		hash = 53 * hash + this.z;
		return hash;
	}
	
	@Override
	public String toString() {
		return "{"+x +", "+ y +", " +z+"}";
	}
}