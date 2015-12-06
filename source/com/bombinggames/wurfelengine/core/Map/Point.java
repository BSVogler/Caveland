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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.Side;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 *A point is a single position in the game world not bound to the grid. Use this for entities.
 * @author Benedikt Vogler
 * @since WE1.1
 */
public class Point extends AbstractPosition {
	private static final long serialVersionUID = 2L;

    /**
     * Creates a point refering to a position in the game world.
     * @param posX The distance from the left border of the map (game space)
     * @param posY The distance from the top border of the map (game space)
     * @param height The distance from ground  (game space)
     */
    public Point(float posX, float posY, float height) {
		this.x = posX;
		this.y = posY;
        this.z = height;
    }
	
	/**
	 * 
	 * @param vec 
	 */
	public Point(Vector3 vec){
		this.x = vec.x;
		this.y = vec.y;
		this.z = vec.z;
	}
    
    /**
     * Copy-constructor. This constructor copies the values.
     * @param point the source of the copy
     */
    public Point(Point point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
    }
	

    /**
     *Returns copy of itself.
     * @return
     */
    @Override
    public Point toPoint() {
       return this.cpy();
    }
	
	    /**
     * Get the height (z-value) of the coordinate.
     * @return game dimension
     */
    public float getZ() {
        return z;
    }
	
	/**
     * Get the z in block grid coordinates of the coordinate. Faster the transofmring to coordinate first.
     * @return in grid coordinates.
     */
    public int getZGrid() {
        return (int) (z/Block.GAME_EDGELENGTH);
    }

    /**
     * 
     * @param height 
     */
    public void setZ(float height) {
        this.z = height;
    }
    
    /**
     * returns coordinate aquivalent. Removes floating of block.<br> Copy safe.<br>
	 * Looks complicated but has runtime O(const)
     * @return coordinate aquivalent
     */
    @Override
    public Coordinate toCoord() {
        //find out where the position is (basic)
        Coordinate coords = new Coordinate(
			Math.floorDiv((int) x, Block.GAME_DIAGLENGTH),
            Math.floorDiv((int) y, Block.GAME_DIAGLENGTH) *2+1, //maybe dangerous to optimize code here!
			Math.floorDiv((int) z, Block.GAME_EDGELENGTH)
		);
//		//clamp at top border
//		if (coords.getZ() >= Chunk.getBlocksZ())
//			coords.setZ(Chunk.getBlocksZ()-1);
       
		//return coords;
        //find the specific coordinate (detail)
        return coords.goToNeighbour(Coordinate.getNeighbourSide(getX() % Block.GAME_DIAGLENGTH,
                getY() % Block.GAME_DIAGLENGTH
            )
        );
    }
    
    /**
     *Get the game world position from left
     * @return
     */
    public float getX() {
        return x;
    }
    
    /**
     *Get the game world position from top.
     * @return
     */
    public float getY() {
        return y;
    }
	
	/**
	 * 
	 * @return  the offset to the coordiantes center.
	 */
	public float getRelToCoordX(){
		return x - toCoord().toPoint().x;
	}
	
	/**
	 * 
	 * @return  the offset to the coordiantes center.
	 */
	public float getRelToCoordY(){
		return y - toCoord().toPoint().y;
	}
	
	/**
	 * 
	 * @return the offset to the coordiantes center.
	 */
	public float getRelToCoordZ(){
		return getZ() - getZGrid()*Block.GAME_EDGELENGTH;
	}
	
	/**
	 * 
	 * @return the offset to the coordiantes center.
	 */
	public Vector3 getRelToCoord(){
		return new Vector3(
			getRelToCoordX(),
			getRelToCoordY(),
			getRelToCoordZ()
		);
	}
	
    @Override
    public Block getBlock() {
		if (z >= Chunk.getGameHeight())
			return null;
        return Controller.getMap().getBlock(toCoord());
    }
    
    @Override
    public Point cpy() {
        return new Point(this);
    }

    @Override
    public int getViewSpcX() {
        return (int) (getX()); //just the position as integer
    }

    @Override
    public int getViewSpcY() {
        return (int)( 
			-getY() / 2
            + (int) (getZ() * Block.ZAXISSHORTENING)
		);
    }

	@Override
	public int getProjectionSpaceX(GameView view, Camera camera) {
		return (int) (getViewSpcX() - camera.getViewSpaceX() + camera.getWidthInProjSpc() / 2);
	}
	
	@Override
	public int getProjectionSpaceY(GameView view, Camera camera) {
		return (int) (getViewSpcY()-camera.getViewSpaceY() + camera.getHeightInProjSpc() / 2);
	}
	
    
    @Override
    public boolean isInMemoryAreaHorizontal() {
		return toCoord().isInMemoryAreaHorizontal();
    }
	
	@Override
    public boolean isInMemoryArea() {
		if (z < 0 || z > Chunk.getGameWidth()){
			return false;
		} else {
			return getBlock() != null;
		}
    }

    /**
     *Add a vector to the position
     * @param vector all values in game world values
     * @return
     */
    public Point add(float[] vector) {
        this.x += vector[0];
        this.y += vector[1];
		this.z += vector[2];
        return this;
    }
    
	/**
     *Add a vector to the position
     * @param vector all values in game world values
     * @return
     */
    public Point add(Vector2 vector) {
        this.x += vector.x;
        this.y += vector.y;
        return this;
    }
	
     /**
     *Add a vector to the position
     * @param vector all values in game world values
     * @return
     */
    @Override
    public Point add(Vector3 vector) {
        this.x += vector.x;
        this.y += vector.y;
		this.z += vector.z;
        return this;
    }

    /**
     *
     * @param x x value to add
     * @param y y value to add
     * @param z height to add
     * @return
     */
	@Override
    public Point add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
	
	/**
	 * Relative to the current coordiante field set the offset.
	 *
	 * @param x offset from origin
	 * @param y offset from origin
	 * @param z offset from origin
	 */
	public void setPositionRelativeToCoord(float x, float y, float z) {
		Point origin = toCoord().toPoint();
		this.x = origin.x + x;
		this.y = origin.y + y;
		this.z = origin.z + z;
	}

	/**
	 * Relative to the current coordiante field set the offset.
	 *
	 * @param shift offset from origin
	 */
	public void setPositionRelativeToCoord(Vector3 shift) {
		Point origin = toCoord().toPoint();
		this.x = origin.x + shift.x;
		this.y = origin.y + shift.y;
		this.z = origin.z + shift.z;
	}
    
    /**
     * Trace a ray through the map until ray hits non air block.<br>
     * Does not work properly with the staggered map.
     * @param dir dir of the ray
     * @param maxDistance the distane after which it should stop. (in game meters)
     * @param camera if set only intersect with blocks which are rendered (not clipped). ignores clipping if set to <i>null</i>
     * @param hitFullOpaque if true only intersects with blocks which are not transparent =full opaque
     * @return can return <i>null</i> if not hitting anything. The normal on the back sides may be wrong. The normals are in a turned coordiante system.
     * @since 1.2.29
     */
		public Intersection raycast(final Vector3 dir, float maxDistance, final Camera camera, final boolean hitFullOpaque) {
		/*  Call the callback with (x,y,z,value,normal) of all blocks along the line
		segment from point 'origin' in vector dir 'dir' of length
		'maxDistance'. 'maxDistance' may be infinite.
		'normal' is the normal vector of the normal of that block that was entered.
		It should not be used after the callback returns.
		If the callback returns a true value, the traversal will be stopped.
		 */
		// From "A Fast Voxel Traversal Algorithm for Ray Tracing"
		// by John Amanatides and Andrew Woo, 1987
		// <http://www.cse.yorku.ca/~amana/research/grid.pdf>
		// <http://citeseer.ist.psu.edu/viewdoc/summary?doi=10.1.1.42.3443>
		// Extensions to the described algorithm:
		//   • Imposed a distance limit.
		//   • The normal passed through to reach the current cube is provided to
		//     the callback.
		// The foundation of this algorithm is a parameterized representation of
		// the provided ray,
		//                    origin + t * dir,
		// except that t is not actually stored; rather, at any given point in the
		// traversal, we keep track of the *greater* t values which we would have
		// if we took a step sufficient to cross a cube boundary along that axis
		// (i.e. change the integer part of the coordinate) in the variables
		// tMaxX, tMaxY, and tMaxZ.
		// Cube containing origin point.
		
		// Avoids an infinite loop.
        if (dir.isZero()) {
			throw new Error("Raycast in zero direction!");
		}
		dir.nor();
		
		Coordinate isectC = toCoord();
        int curX = isectC.getX();
        int curY = isectC.getY();
        int curZ = isectC.getZ();
		
		// Direction to increment x,y,z when stepping.
		int stepX = (int) Math.signum(dir.x);
		int stepY = (int) Math.signum(dir.y);
		int stepZ = (int) Math.signum(dir.z);
        // See description above. The initial values depend on the fractional
		// part of the origin.
		float tMaxX = intbound(x, dir.x);
		float tMaxY = intbound(y, dir.y);
		float tMaxZ = intbound(z, dir.z);
        // The change in t when taking a step (always positive).
        float tDeltaX = stepX / dir.x;
		float tDeltaY = stepY / dir.y;
		float tDeltaZ = stepZ / dir.z;

		/* ray has not gone past bounds of world */
		while (
			(stepZ > 0 ? curZ < Chunk.getBlocksZ(): curZ >= 0)
			&& isectC.isInMemoryAreaHorizontal()
		) {

			isectC = new Coordinate(curX, curY, curZ);
			Block block = isectC.getBlock();
			//intersect?
			if ((
				camera == null
				||
				(curZ < camera.getZRenderingLimit() && !camera.isClipped(isectC))
			   )
				&& block != null
				&& !(hitFullOpaque && block.isTransparent())
			){
				//found intersection point
				if (distanceTo(isectC.toPoint()) <= maxDistance*Block.GAME_EDGELENGTH)
					return Intersection.intersect(isectC, this, dir);
				else return null;
			}

            /*tMaxX stores the t-value at which we cross a cube boundary along the
             X axis, and similarly for Y and Z. Therefore, choosing the least tMax
            chooses the closest cube boundary. Only the first case of the four
            has been commented in detail.*/
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    if (tMaxX > maxDistance) break;
                    // Update which cube we are now in.
                    curX += stepX;
                    // Adjust tMaxX to the next X-oriented boundary crossing.
                    tMaxX += tDeltaX;
                } else {
                    if (tMaxZ > maxDistance) break;
                    curZ += stepZ;
                    tMaxZ += tDeltaZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    if (tMaxY > maxDistance) break;
                    curY += stepY;
                    tMaxY += tDeltaY;
                } else {
                  // Identical to the second case, repeated for simplicity in
                  // the conditionals.
                  if (tMaxZ > maxDistance) break;
                  curZ += stepZ;
                  tMaxZ += tDeltaZ;
                }
            }
        }
        //ground hit, must be 0,0,0
        if (curZ <= 0) {
			Point intersectpoint = new Coordinate(curX, curY, 0).toPoint();
			float distance = this.distanceTo(intersectpoint);
			if (distance <= Block.GAME_EDGELENGTH * maxDistance) {
				return new Intersection(intersectpoint, Side.TOP, distance);
			} else {
				return null;
			}
		} else {
			return null;
		}
    }

    /**
     * Find the smallest positive t such that s+t*ds is an integer.
     * @param s
     * @param ds
     * @return 
     * @since 1.2.29
     */
	private int intbound(float s, float ds) {
		if (ds < 0) {
			return intbound(-s, -ds);
		} else {
			s = mod(s, 1);
			// problem is now s+t*ds = 1
			return (int) ((1-s)/ds);
		}
	}

	private float mod(float value, int modulus) {
		return (value % modulus + modulus) % modulus;
	}

	/**
	 * Sends a ray by moving a coordiante though the map. Slow but it works.
	 * @param dir
	 * @param maxDistance game space in meters
	 * @param camera
	 * @param hitCondition can be null
	 * @return 
	 * @see #raycast(com.badlogic.gdx.math.Vector3, float, com.bombinggames.wurfelengine.core.Camera, boolean) 
	 */
	public Intersection rayMarching(
		final Vector3 dir,
		float maxDistance,
		final Camera camera,
		final Predicate<Block> hitCondition
	){
		if (dir == null) {
			throw new NullPointerException("Direction of raycasting not defined");
		}
		
		if (dir.isZero()) {
			throw new Error("Raycast in zero direction!");
		}
		
		Point traverseP = cpy();
		dir.nor();
		Coordinate isectC = traverseP.toCoord();
		while (
			isectC.isInMemoryAreaHorizontal()
			&& traverseP.getZ() >= 0
			&& distanceTo(traverseP) < maxDistance*Block.GAME_EDGELENGTH
		){
			//move
			traverseP.add(dir);

			isectC = traverseP.toCoord();
			Block block = isectC.getBlock();
			if ((
				camera == null
				||
				(isectC.getZ() < camera.getZRenderingLimit() && !camera.isClipped(isectC))
			   )
				&& block != null
				&& (hitCondition == null || hitCondition.test(block))
			){
				Intersection interse = new Intersection(traverseP, null, distanceTo(traverseP));
				interse.calcNormal(traverseP);
				return interse;
			}
		}
		//check for ground hit
		if (traverseP.getZ() <= 0) {
			traverseP.setZ(0);//clamp at 0
			float distance = this.distanceTo(traverseP);
			if (distance <= Block.GAME_EDGELENGTH * maxDistance) {
				return new Intersection(traverseP, Side.TOP, distance);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
		
	/**
	 * 
	 * @param point
	 * @return the distance from this point to the other point in game world coordinates
	 */
	@Override
	public float distanceTo(AbstractPosition point) {
		return distanceTo(point.toPoint());
	}
	
	/**
	 * 
	 * @param point
	 * @return the distance from this point to the other point in game coordinates
	 */
	public float distanceTo(Point point) {
		float dX = x - point.x;
		float dY = y - point.y;
		float dZ = z - point.z;
		return (float) Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}

	/**
	 *
	 * @param object
	 * @return the distance from this point to the other object
	 */
	@Override
	public float distanceTo(AbstractGameObject object) {
		return distanceTo(object.getPosition().toPoint());
	}

	@Override
	public float distanceToHorizontal(AbstractPosition pos) {
		return distanceToHorizontal(pos.toPoint());
	}
	
	/**
	 * checks only x and y.
	 * @param point
	 * @return the distance from this point to the other point only regarding
	 * horizontal components.
	 */
	public float distanceToHorizontal(Point point) {
		float dX = x - point.x;
		float dY = y - point.y;
		return (float) Math.sqrt(dX * dX + dY * dY);
	}

	/**
	 * checks only x and y.
	 *
	 * @param object
	 * @return the distance from this point to the other point only regarding
	 * horizontal components.
	 */
	@Override
	public float distanceToHorizontal(AbstractGameObject object) {
		return distanceToHorizontal(object.getPosition().toPoint());
	}


	/**
	 * get entities in radius
	 * @param radius in game dimension pixels
	 * @return every entitie in radius
	 */
	public ArrayList<AbstractEntity> getEntitiesNearby(float radius) {
		ArrayList<AbstractEntity> result = new ArrayList<>(5);//defautl size 5

		for (AbstractEntity entity : Controller.getMap().getEntitys()) {
			if (entity.hasPosition() && distanceTo(entity.getPosition().toPoint()) < radius) {
				result.add(entity);
			}
		}

		return result;
	}
	
	/**
	 * get entities in radius (horizontal only)
	 *
	 * @param radius in game dimension pixels
	 * @return every entitie in radius
	 */
	public ArrayList<AbstractEntity> getEntitiesNearbyHorizontal(float radius) {
		ArrayList<AbstractEntity> result = new ArrayList<>(5);//defautl size 5
		ArrayList<AbstractEntity> entityList = Controller.getMap().getEntitys();
		for (AbstractEntity entity : entityList) {
			if (distanceToHorizontal(entity.getPosition().toPoint()) < radius) {
				result.add(entity);
			}
		}

		return result;
	}

	/**
	 * get entities in horizontal radius (like a pipe)
	 * @param <type>
	 * @param radius in game dimension pixels
	 * @param type
	 * @return every entitie in radius
	 */
	@SuppressWarnings("unchecked")
	public <type> ArrayList<type> getEntitiesNearbyHorizontal(float radius, final Class<type> type) {
		ArrayList<type> result = new ArrayList<>(5);//default size 5
		ArrayList<AbstractEntity> entityList = Controller.getMap().getEntitys();

        for (AbstractEntity entity : entityList) {//check every entity
            if (
				entity.hasPosition()
				&& type.isInstance(entity) //if the entity is of the wanted type
				&& distanceToHorizontal(entity.getPosition().toPoint()) < radius
			) {
                result.add((type) entity);//add it to list
            }
        }

        return result;
	}
	
	@Override
	public int getChunkX() {
		return Math.floorDiv((int) x, Chunk.getGameWidth());
	}

	@Override
	public int getChunkY() {
		return Math.floorDiv((int) y, Chunk.getGameDepth());
	}
	
	
	/**
	 * Overwrites the data.
	 * @param target
	 * @param t
	 * @return
	 */
	@Override
	public Point lerp(final Vector3 target, float t) {
		super.lerp(target, t);
		return this;
	}
	
	/**
	 * 
	 * @param p
	 * @param maxdistance game space in meters
	 * @return 
	 */
	public boolean canSee(Point p, float maxdistance) {
		Vector3 vecToTarget = cpy().sub(p).nor();
		//check if can see target
		Intersection intersect = p.rayMarching(
			vecToTarget,
			maxdistance,
			null,
			(Block t) -> !t.isTransparent()
		);
		return !(intersect != null
			&& distanceTo(intersect.getPoint()) < distanceTo(p)//check if point is before
);
	}

	/**
	 * overwrites the coordinates with values from another point. Faster then creating a new object-
	 * @param point is not modified
	 * @return itself for chaining
	 */
	public Point setValues(final Point point) {
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
		return this;
	}
}
