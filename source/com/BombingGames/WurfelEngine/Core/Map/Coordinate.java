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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 * A coordinate is a reference to a specific cell in the map. The coordinate can
 * transcode between relative and absolute coordinates.<br>
 * Relative coordinates are similar to the map array. Absolute coordinates are
 * indipendent of the current map but to access them you must have the chunk
 * where the coordiantes are pointing to in memory.<br>
 * The coordinate uses a continously height value. The Z coordinate value can be
 * calculated.
 *
 * @author Benedikt Vogler
 */
public class Coordinate extends AbstractPosition {

	private static final long serialVersionUID = 2L;
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
	 *
	 * @param x The x value as coordinate.
	 * @param y The y value as coordinate.
	 * @param z The z value as coordinate.
	 * @param map
	 */
	public Coordinate(AbstractMap map, int x, int y, int z) {
		super(map);
		this.x = x;
		this.y = y;
		this.z = z;
		refreshCachedPoint();
	}

	/**
	 * Creates a new coordinate from an existing coordinate
	 *
	 * @param coord the Coordinate you want to copy
	 */
	public Coordinate(Coordinate coord) {
		super(coord.map);
		this.x = coord.x;
		this.y = coord.y;
		this.z = coord.z;
		refreshCachedPoint();
	}

	/**
	 * Gets the X coordinate
	 *
	 * @return
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the Y coordinate
	 *
	 * @return
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return
	 */
	public int getZ() {
		return z;
	}

	/**
	 * Checks if the calculated value is inside the map dimensions and if not
	 * clamps it to the map dimensions.
	 *
	 * @return
	 * @see #getZ()
	 */
	public int getZClamp() {
		if (z >= map.getBlocksZ()) {
			return map.getBlocksZ() - 1;
		} else if (z < 0) {
			return 0;
		} else {
			return z;
		}
	}

	@Override
	public Vector3 getVector() {
		return new Vector3(x, y, z);
	}

	/**
	 * Set the coordiantes X component.
	 *
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
		refreshCachedPoint();
	}

	/**
	 * Set the coordiantes Y component.
	 *
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
		refreshCachedPoint();
	}

	/**
	 * Set the coordinates Z component.
	 *
	 * @param z
	 */
	public void setZ(int z) {
		this.z = z;
		refreshCachedPoint();
	}

	/**
	 * Set a block in the map where the coordinate is pointing to.
	 *
	 * @param block the block you want to set.
	 */
	public void setBlock(RenderBlock block) {
		if (block!= null) {
			block.setPosition(this);
			Controller.getMap().setBlock(block);
		} else {
			Controller.getMap().setBlock(this, null);
		}
	}
	
	/**
	 * Set a block in the map where the coordinate is pointing to.
	 *
	 * @param block the block you want to set.
	 */
	public void setBlock(CoreData block) {
		if (block!= null) {
			Controller.getMap().setBlock(this, block);
		} else {
			Controller.getMap().setBlock(this, null);
		}
	}
	
	/**
	 * Add a vector to the coordinates. If you just want the result and don't
	 * change the coordiantes use addVectorCpy.
	 *
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
	 * Add a vector to the coordinates. If you just want the result and don't
	 * change the coordiantes use addVectorCpy.
	 *
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
		refreshCachedPoint();
		return this;
	}

	@Override
	public CoreData getBlock() {
		if (z < 0) {
			return Controller.getMap().getGroundBlock();
		} else {
			return Controller.getMap().getBlock(this);
		}
	}

	/**
	 * The block hides the past block when it has sides and is not transparent
	 * (like normal block)
	 *
	 * @param x offset in coords
	 * @param y offset in coords
	 * @param z offset in coords
	 * @return true when hiding the past RenderBlock
	 * @deprecated v1.5
	 */
	public boolean hidingPastBlocks(int x, int y, int z) {
		CoreData block = Controller.getMap().getBlock(
			this.x + x, this.y + y, this.z + z
		);
		return (block != null && block.hasSides() && !block.isTransparent());
	}

	/**
	 * Check if fron block is hiding because both a liquids.
	 *
	 * @param x offset in coords
	 * @param y offset in coords
	 * @param z offset in coords
	 * @return true when hiding the past RenderBlock
	 * @deprecated v1.5
	 */
	public boolean hidingPastLiquid(int x, int y, int z) {
		return getBlock().isLiquid() && cpy().addVector(x, y, z).getBlock().isLiquid();
	}

	/**
	 * Mixes both hidingPast methods for a single block
	 *
	 * @see #hidingPastBlocks(int, int, int)
	 * @see #hidingPastLiquid(int, int, int)
	 * @param x offset in coords
	 * @param y offset in coords
	 * @param z offset in coords
	 * @return true when hiding the past RenderBlock
	 * @deprecated v1.5
	 */
	public boolean hidingPastBlock(int x, int y, int z) {
		CoreData block = Controller.getMap().getBlock(
			this.x + x, this.y + y, this.z + z
		);
		return block != null
			&& (block.hasSides() && !block.isTransparent()
			||
			getBlock().isLiquid() && block.isLiquid());
	}

	/**
	 * @return a copy of this coordinate
	 */
	@Override
	public Coordinate cpy() {
		return new Coordinate(this);
	}

	/**
	 * Checks if the coordiantes are accessable with the currently loaded Chunks
	 * (horizontal only).
	 *
	 * @return
	 */
	@Override
	public boolean isInMemoryAreaHorizontal() {
		boolean found = false;
		if (WE.CVARS.getValueB("mapUseChunks")){
			for (Chunk chunk : ((ChunkMap) map).getData()) {
				if (chunk.hasCoord(this)) {
					found = true;
				}
			}
		} else {
			//to-do add method for completemap
		}
		return found;
	}

	/**
	 * Checks if the coordiantes are accessable with the currently loaded Chunks
	 * (x,y,z).
	 *
	 * @return
	 */
	@Override
	public boolean isInMemoryArea() {
		boolean found = false;
		if (getZ() >= 0 && getZ() < map.getBlocksZ()) {
			if (WE.CVARS.getValueB("mapUseChunks")){//to-do add method for completemap
				for (Chunk chunk : ((ChunkMap) map).getData()) {
					if (chunk.hasCoord(this)) {
						found = true;
					}
				}
			} else {
			
			}
		}
		return found;
	}

	/**
	 * Returns the field-id where the coordiantes are inside in relation to the
	 * current field. Field id count clockwise, starting with the top with 0. If
	 * you want to get the neighbour you can use {@link #goToNeighbour(int)}
	 * with the parameter found by this function. The numbering of the
	 * sides:<br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | 8 | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 * Run time: O(1)
	 *
	 * @param x game-space-coordinates, value in pixels
	 * @param y game-space-coordinates, value in pixels
	 * @return Returns the fieldnumber of the coordinates. 8 is the field
	 * itself.
	 * @see #goToNeighbour(int)
	 */
	public static int getNeighbourSide(float x, float y) {
		//modulo
		if (y < 0) {
			y += AbstractGameObject.GAME_DIAGLENGTH;
		}
		if (x < 0) {
			x += AbstractGameObject.GAME_DIAGLENGTH;
		}

		int result = 8;//standard result
		if (x + y <= AbstractGameObject.GAME_DIAGLENGTH2) {
			result = 7;
		}
		if (x - y >= AbstractGameObject.GAME_DIAGLENGTH2) {
			if (result == 7) {
				result = 0;
			} else {
				result = 1;
			}
		}
		if (x + y >= 3 * AbstractGameObject.GAME_DIAGLENGTH2) {
			if (result == 1) {
				result = 2;
			} else {
				result = 3;
			}
		}
		if (-x + y >= AbstractGameObject.GAME_DIAGLENGTH2) {
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
	 * Goes to the the neighbour with the specific side.<br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | 8 | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 * O(const)
	 *
	 * @param neighbourSide the side number of the given coordinates
	 * @return itself for chaining
	 */
	public Coordinate goToNeighbour(final int neighbourSide) {
		switch (neighbourSide) {
			case 0:
				addVector(0, -2, 0);
				break;
			case 1:
				addVector(getY() % 2 != 0 ? 1 : 0, -1, 0);
				break;
			case 2:
				addVector(1, 0, 0);
				break;
			case 3:
				addVector(getY() % 2 != 0 ? 1 : 0, 1, 0);
				break;
			case 4:
				addVector(0, 2, 0);
				break;
			case 5:
				addVector(getY() % 2 == 0 ? -1 : 0, 1, 0);
				break;
			case 6:
				addVector(-1, 0, 0);
				break;
			case 7:
				addVector(getY() % 2 == 0 ? -1 : 0, -1, 0);
				break;
		}
		return this;
	}

	/**
	 *
	 * @return the coordiante's origin is the center
	 * @see #refreshCachedPoint()
	 */
	@Override
	public Point getPoint() {
		return cachedPoint;
	}

	/**
	 * refresh the field cachedPoint. Does a coord -> point transform.
	 */
	private void refreshCachedPoint() {
		cachedPoint = new Point(
			map,
			x * AbstractGameObject.GAME_DIAGLENGTH + (y % 2 != 0 ? AbstractGameObject.VIEW_WIDTH2 : 0),
			y * AbstractGameObject.GAME_DIAGLENGTH2,
			z * AbstractGameObject.GAME_EDGELENGTH
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
	 *
	 * @return a list with the entitys
	 */
	public ArrayList<AbstractEntity> getEntitiesInside() {
		return Controller.getMap().getEntitysOnCoord(this);
	}

	/**
	 * Get every entity on this coord of the wanted type
	 *
	 * @param <type> the class you want to filter.
	 * @param type the class you want to filter.
	 * @return a list with the entitys of the wanted type
	 */
	public <type> ArrayList<type> getEntitysInside(final Class<? extends AbstractEntity> type) {
		return Controller.getMap().getEntitysOnCoord(this, type);
	}

	@Override
	public int getViewSpcX(GameView view) {
		return x * AbstractGameObject.VIEW_WIDTH //x-coordinate multiplied by the projected size in x direction
			//+ AbstractGameObject.VIEW_WIDTH2 //add half tile for center
			+ (y % 2 != 0 ? AbstractGameObject.VIEW_WIDTH2 : 0); //offset by y
	}

	@Override
	public int getViewSpcY(GameView view) {
		return y * AbstractGameObject.VIEW_DEPTH2 *
			(
				view.getOrientation() == 0
					? -1
					: (view.getOrientation() == 2
						? 1
						: 0
					)
			)
			+ z * AbstractGameObject.VIEW_HEIGHT;
	}

	/**
	 * destroys the block at the current position, replacing by air.
	 */
	public void destroy() {
		setBlock((CoreData) null);
	}
		
	/**
	 * returns true if block got damaged
	 *
	 * @param amount value between 0 and 100
	 * @return
	 */
	public boolean damage(byte amount) {
		CoreData block = getBlock();
		if (block != null) {
			block.setHealth(this, (byte) (block.getHealth() - amount));
			if (block.getHealth() <= 0) {
				setBlock((CoreData) null);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		//point on same object
		if (this == obj) {
			return true;
		}
		//not null
		if (obj == null) {
			return false;
		}
		//not same class
		if (getClass() != obj.getClass()) {
			return false;
		}
		//check fields
		Coordinate other = (Coordinate) obj;
		if (x != other.x) {
			return false;
		}
		if (y != other.y) {
			return false;
		}
		return z == other.z;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + this.x;
		hash = 17 * hash + this.y;
		hash = 13 * hash + this.z;
		return hash;
	}

	@Override
	public String toString() {
		return "{" + x + ", " + y + ", " + z + "}";
	}
}
