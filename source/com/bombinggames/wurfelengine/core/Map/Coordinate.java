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
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import java.util.ArrayList;

/**
 * A coordinate is a reference to a specific cell in the map.
 * The coordinate uses a continously height value. The Z coordinate value can be
 * calculated.
 *
 * @author Benedikt Vogler
 */
public class Coordinate extends AbstractPosition {

	private static final long serialVersionUID = 3L;
	/**
	 * The x coordinate. Position from left to right.
	 */
	private int x;
	/**
	 * The y coordinate. From back to front .
	 */
	private int y;
	/**
	 * The z coordinate. Position from ground.
	 */
	private int z;

	/**
	 * Creates a coordiante refering to the given position on the map.
	 *
	 * @param x The x value as coordinate.
	 * @param y The y value as coordinate.
	 * @param z The z value as coordinate.
	 */
	public Coordinate(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new coordinate from an existing coordinate
	 *
	 * @param coord the Coordinate you want to copy
	 */
	public Coordinate(Coordinate coord) {
		this.x = coord.x;
		this.y = coord.y;
		this.z = coord.z;
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
		if (z >= Chunk.getBlocksZ()) {
			return Chunk.getBlocksZ() - 1;
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
	}

	/**
	 * Set the coordiantes Y component.
	 *
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Set the coordinates Z component.
	 *
	 * @param z
	 */
	public void setZ(int z) {
		this.z = z;
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
	public void setBlock(Block block) {
		if (block != null) {
			Controller.getMap().setBlock(this, block);
		} else {
			Controller.getMap().setBlock(this, null);
		}
	}
	
	/**
	 * Add a vector to the coordinates.
	 *
	 * @param vector integer coordinates stored as float
	 * @return the new coordinates which resulted of the addition
	 */
	public Coordinate addVector(int[] vector) {
		this.x += vector[0];
		this.y += vector[1];
		this.z += vector[2];
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
	public Coordinate addVector(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	@Override
	public Block getBlock() {
		if (z < 0) {
			return Controller.getMap().getNewGroundBlockInstance();
		} else if (z >= Chunk.getBlocksZ()){
			return null;
		} else {
			return Controller.getMap().getBlock(this);
		}
	}
	
	/**
	 * get the logic to a block.
	 * @return can return null if the block has no logic
	 */
	public AbstractBlockLogicExtension getLogic(){
		if (z < 0 || z >= Chunk.getBlocksZ()) {
			return null;
		} else {
			return Controller.getMap().getLogic(this);
		}
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
		return Controller.getMap().getData().stream().anyMatch((chunk) -> (chunk.hasCoord(this)));
	}

	/**
	 * Checks if the coordiantes are accessable with the currently loaded Chunks
	 * (x,y,z).
	 *
	 * @return
	 */
	@Override
	public boolean isInMemoryArea() {
		if (getZ() >= 0 && getZ() < Chunk.getBlocksZ()) {
			for (Chunk chunk : Controller.getMap().getData()) {
				if (chunk.hasCoord(this)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks wether the coordinate is in a cube spanning by both coordinates.
	 * @param x1 lower one
	 * @param x2 bigger one.
	 * @return 
	 */
	public boolean isInCube(Coordinate x1, Coordinate x2){
		return x >= x1.getX() && y >= x1.getY() && z >= x1.getZ()
			&& x <= x2.getX() && y <= x2.getY() && z <= x2.getZ();
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
			y += Block.GAME_DIAGLENGTH;
		}
		if (x < 0) {
			x += Block.GAME_DIAGLENGTH;
		}

		int result = 8;//standard result
		if (x + y <= Block.GAME_DIAGLENGTH2) {
			result = 7;
		}
		if (x - y >= Block.GAME_DIAGLENGTH2) {
			if (result == 7) {
				result = 0;
			} else {
				result = 1;
			}
		}
		if (x + y >= 3 * Block.GAME_DIAGLENGTH2) {
			if (result == 1) {
				result = 2;
			} else {
				result = 3;
			}
		}
		if (-x + y >= Block.GAME_DIAGLENGTH2) {
			switch (result) {
				case 3:
					result = 4;
					break;
				case 7:
					result = 6;
					break;
				default:
					result = 5;
					break;
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
	 * <br>
	 * Runtime: O(const)
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
	 * Copy safe.
	 * O(const)
	 * @return the coordiante's origin is the center
	 */
	@Override
	public Point toPoint() {
		return new Point(
			x * Block.GAME_DIAGLENGTH + (y % 2 != 0 ? Block.VIEW_WIDTH2 : 0),
			y * Block.GAME_DIAGLENGTH2,
			z * Block.GAME_EDGELENGTH
		);
	}

	@Override
	public Coordinate toCoord() {
		return this.cpy();
	}

	/**
	 * Get every entity on a coord. Loads the chunk if not in memory.
	 *
	 * @return a list with the entitys
	 */
	public ArrayList<AbstractEntity> getEntitiesInside() {
		if (!isInMemoryAreaHorizontal()) {
			Controller.getMap().loadChunk(toCoord().getChunkX(), toCoord().getChunkY());
		}
		return Controller.getMap().getEntitysOnCoord(this);
	}

	/**
	 * Get every entity on this coord of the wanted type. Loads the chunk if not in memory.
	 *
	 * @param <type> the class you want to filter.
	 * @param type the class you want to filter.
	 * @return a list with the entitys of the wanted type
	 */
	public <type extends AbstractEntity> ArrayList<type> getEntitiesInside(final Class<? extends AbstractEntity> type) {
		if (!isInMemoryAreaHorizontal()) {
			Controller.getMap().loadChunk(toCoord().getChunkX(), toCoord().getChunkY());
		}
		return Controller.getMap().getEntitysOnCoord(this, type);
	}

	@Override
	public int getViewSpcX(GameView view) {
		return x * Block.VIEW_WIDTH //x-coordinate multiplied by the projected size in x direction
			//+ AbstractGameObject.VIEW_WIDTH2 //add half tile for center
			+ (y % 2 != 0 ? Block.VIEW_WIDTH2 : 0); //offset by y
	}

	@Override
	public int getViewSpcY(GameView view) {
		return y * Block.VIEW_DEPTH2 *
			(
				view.getOrientation() == 0
					? -1
					: (view.getOrientation() == 2
						? 1
						: 0
					)
			)
			+ z * Block.VIEW_HEIGHT;
	}
	
	@Override
	public int getProjectionSpaceX(GameView view, Camera camera) {
		return (int) (getViewSpcX(view)-camera.getViewSpaceX() + camera.getWidthInProjSpc() / 2);
	}
	
	@Override
	public int getProjectionSpaceY(GameView view, Camera camera) {
		return (int) (getViewSpcY(view)-camera.getViewSpaceY() + camera.getHeightInProjSpc() / 2);
	}

	@Override
	public float distanceTo(AbstractGameObject object) {
		return toPoint().distanceTo(object);
	}

	@Override
	public float distanceTo(AbstractPosition point) {
		return toPoint().distanceTo(point);
	}

	@Override
	public float distanceToHorizontal(AbstractGameObject object) {
		return toPoint().distanceToHorizontal(object);
	}

	@Override
	public float distanceToHorizontal(AbstractPosition point) {
		return toPoint().distanceToHorizontal(point);
	}
	
	/**
	 * destroys the block at the current position, replacing by air.
	 */
	public void destroy() {
		setBlock((Block) null);
	}
		
	/**
	 * returns true if block got damaged
	 *
	 * @param amount value between 0 and 100
	 * @return
	 */
	public boolean damage(byte amount) {
		Block block = getBlock();
		if (block != null) {
			block.setHealth(this, (byte) (block.getHealth() - amount));
			if (block.getHealth() <= 0 && !block.isIndestructible()) {
				setBlock((Block) null);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public int getChunkX(){
		return Math.floorDiv(x, Chunk.getBlocksX());
	}
	
	@Override
	public int getChunkY(){
		return Math.floorDiv(y, Chunk.getBlocksY());
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
