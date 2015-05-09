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

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *A point is a single position in the game world not bound to the grid. Use this for entities.
 * @author Benedikt Vogler
 * @since WE1.1
 */
public class Point extends AbstractPosition {
	private static final long serialVersionUID = 1L;
    private float x;
    private float y;
	private float z;

    /**
     * Creates a point refering to a position in the game world.
	 * @param map
     * @param posX The distance from the left border of the map (game space)
     * @param posY The distance from the top border of the map (game space)
     * @param height The distance from ground  (game space)
     */
    public Point(AbstractMap map, float posX, float posY, float height) {
		super(map);
		this.x = posX;
		this.y = posY;
        this.z = height;
    }
    
    /**
     * Copy-constructor. This constructor copies the values.
     * @param point the source of the copy
     */
    public Point(Point point) {
		super(point.map);
		this.x = point.x;
		this.y = point.y;
		this.z = point.z;
    }

    /**
     *Returns itself.
     * @return
     */
    @Override
    public Point getPoint() {
       return this;
    }
	
	    /**
     * Get the height (z-value) of the coordinate.
     * @return game dimension
     */
    public float getZ() {
        return z;
    }
	
	/**
     * Get the z in grid coordinates of the coordinate. Faster then calculate the coordiante first.
     * @return in grid coordinates.
     */
    public float getZGrid() {
        return (int) (z/RenderBlock.GAME_EDGELENGTH);
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
	 * Looks complicated but is O(const)
     * @return coordinate aquivalent
     */
    @Override
    public Coordinate getCoord() {
        //find out where the position is (basic)
        Coordinate coords = new Coordinate(
			map,	
            (int) Math.floor(getX() / (float) AbstractGameObject.GAME_DIAGLENGTH),
            (int) Math.floor(getY() / (float) AbstractGameObject.GAME_DIAGLENGTH) *2+1, //maybe dangerous to optimize code here!
			(int) Math.floor(z/RenderBlock.GAME_EDGELENGTH)
		);
		//clamp at top border
		if (coords.getZ() >= Chunk.getBlocksZ())
			coords.setZ(Chunk.getBlocksZ()-1);
       
		//return coords;
        //find the specific coordinate (detail)
        return coords.goToNeighbour(
            Coordinate.getNeighbourSide(
                getX() % AbstractGameObject.GAME_DIAGLENGTH,
                getY() % AbstractGameObject.GAME_DIAGLENGTH
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
     *Get as array triple
     * @return
     */
	@Override
    public Vector3 getVector(){
        return new Vector3(x, y, z);
    }
    
	/**
	 * 
	 * @return  the offset to the coordiantes center.
	 */
	public float getRelToCoordX(){
		return x - getCoord().getPoint().x;
	}
	
	/**
	 * 
	 * @return  the offset to the coordiantes center.
	 */
	public float getRelToCoordY(){
		return y - getCoord().getPoint().y;
	}
	
	/**
	 * 
	 * @return the offset to the coordiantes center.
	 */
	public float getRelToCoordZ(){
		return getZ() - getZGrid()*RenderBlock.GAME_EDGELENGTH;
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
    public CoreData getBlock() {
        return Controller.getMap().getBlock(getCoord());
    }
    
    @Override
    public Point cpy() {
        return new Point(this);
    }

    @Override
    public int getViewSpcX(GameView view) {
        return (int) (getX()); //just the position as integer
    }

    @Override
    public int getViewSpcY(GameView view) {
        return (int)( 
			getY() / 2*
			(
				view.getOrientation()==0
				? -1
				: (
					view.getOrientation()==2
					? 1
					: 0
				  )
			)
			
            + (int) (getZ() * AbstractGameObject.ZAXISSHORTENING) //take z-axis shortening into account, witgh old block format SQRT12 worked btu now it's 8/9?
			);
    }
    
    @Override
    public boolean isInMemoryAreaHorizontal() {
		return getCoord().isInMemoryAreaHorizontal();
    }
	
	@Override
    public boolean isInMemoryArea() {
		if (getZ() < 0 && getZ() > map.getBlocksZ()){
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
    @Override
    public Point addVector(float[] vector) {
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
    public Point addVector(Vector2 vector) {
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
    public Point addVector(Vector3 vector) {
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
    public Point addVector(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }
	
	/**
	 * Relative to the current coordiante field set the offset.
	 * @param x offset from origin
	 * @param y offset from origin 
	 * @param z offset from origin 
	 */
	public void setPositionRelativeToCoord(float x, float y, float z) {
		Point origin = getCoord().getPoint(); 
		this.x = origin.x +x;
		this.y = origin.y +y;
		this.z = origin.z +z;
	}
    
    /**
     * Trace a ray through the map until ray hits non air block.
     * 
     * @param direction direction of the ray
     * @param radius the distane after which it should stop.
     * @param camera if set only intersect with blocks which are rendered (not clipped). ignoring clipping if <i>null</i>
     * @param onlySolid only intersect if true with block which are not transparent =solid
     * @return can return <i>null</i> if not hitting anything. The normal on the back sides may be wrong. The normals are in a turned coordiante system.
     * @since 1.2.29
     */
		public Intersection raycast(Vector3 direction, float radius, Camera camera, boolean onlySolid) {
      /*  Call the callback with (x,y,z,value,normal) of all blocks along the line
 segment from point 'origin' in vector direction 'direction' of length
 'radius'. 'radius' may be infinite.

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
        //                    origin + t * direction,
        // except that t is not actually stored; rather, at any given point in the
        // traversal, we keep track of the *greater* t values which we would have
        // if we took a step sufficient to cross a cube boundary along that axis
        // (i.e. change the integer part of the coordinate) in the variables
        // tMaxX, tMaxY, and tMaxZ.

        // Cube containing origin point.
        float curX = (float) Math.floor(x);
        float curY = (float) Math.floor(y);
        float curZ = (float) Math.floor(z);
        // Break out direction vector.
        float dx = direction.x;
        float dy = direction.y;
        float dz = direction.z;
        // Direction to increment x,y,z when stepping.
        float stepX = Math.signum(dx);
        float stepY = Math.signum(dy);
        float stepZ = Math.signum(dz);
        // See description above. The initial values depend on the fractional
        // part of the origin.
        float tMaxX = intbound(x, dx);
        float tMaxY = intbound(y, dy);
        float tMaxZ = intbound(z, dz);
        // The change in t when taking a step (always positive).
        float tDeltaX = stepX/dx;
        float tDeltaY = stepY/dy;
        float tDeltaZ = stepZ/dz;
        // Buffer for reporting faces to the callback.
        Vector3 normal = new Vector3();

        // Avoids an infinite loop.
        if (dx == 0 && dy == 0 && dz == 0)
          throw new Error("Raycast in zero direction!");

        // Rescale from units of 1 cube-edge to units of 'direction' so we can
        // compare with 't'.
        radius /= Math.sqrt(dx*dx+dy*dy+dz*dz);

        while (/* ray has not gone past bounds of world */
               stepZ > 0 ? curZ < map.getGameHeight() : curZ >= 0) {

				/** Point of intersection */
                Point isectP = new Point(map, curX, curY, curZ);
                if (!isectP.isInMemoryAreaHorizontal()) break;//check if outside of map
				CoreData block = isectP.getBlock();
                //intersect?
                if ((
					camera==null
					||
					(
						(
							curZ < camera.getZRenderingLimit()*RenderBlock.GAME_EDGELENGTH
							&& !camera.isClipped(isectP.getCoord())
						)
					)
				   )
					&& block != null
                    && (!onlySolid || (onlySolid && !block.isTransparent()))
                    
				){
                    //correct normal, should also be possible by comparing the point with the coordiante position and than the x value
                    if (
                        (RenderBlock.GAME_DIAGLENGTH+((isectP.getX() -(isectP.getCoord().getY() % 2 == 0? RenderBlock.GAME_DIAGLENGTH2 : 0))
                        % RenderBlock.GAME_DIAGLENGTH)) % RenderBlock.GAME_DIAGLENGTH
                        <
                        RenderBlock.GAME_DIAGLENGTH2
                    ) {
						normal.y = 0;
                        normal.x = -1;
                    }
					return new Intersection(isectP, normal, this.distanceTo(isectP));
				}
            //}

            /*tMaxX stores the t-value at which we cross a cube boundary along the
             X axis, and similarly for Y and Z. Therefore, choosing the least tMax
            chooses the closest cube boundary. Only the first case of the four
            has been commented in detail.*/
            if (tMaxX < tMaxY) {
                if (tMaxX < tMaxZ) {
                    if (tMaxX > radius) break;
                    // Update which cube we are now in.
                    curX += stepX;
                    // Adjust tMaxX to the next X-oriented boundary crossing.
                    tMaxX += tDeltaX;
                    // Record the normal vector of the cube normal we entered.
                    normal.x = -stepX;
                    normal.y = 0;
                    normal.z = 0;
                } else {
                    if (tMaxZ > radius) break;
                    curZ += stepZ;
                    tMaxZ += tDeltaZ;
                    normal.x = 0;
                    normal.y = 0;
                    normal.z = -stepZ;
                }
            } else {
                if (tMaxY < tMaxZ) {
                    if (tMaxY > radius) break;
                    curY += stepY;
                    tMaxY += tDeltaY;
                    normal.x = 0;
                    normal.y = -stepY;
                    normal.z = 0;
                } else {
                  // Identical to the second case, repeated for simplicity in
                  // the conditionals.
                  if (tMaxZ > radius) break;
                  curZ += stepZ;
                  tMaxZ += tDeltaZ;
                  normal.x = 0;
                  normal.y = 0;
                  normal.z = -stepZ;
                }
            }
        }
        //ground hit, must be 0,0,1
        if (curZ <= 0){
            Point intersectpoint = new Point(map, curX, curY, 0);
            return new Intersection(intersectpoint, normal, this.distanceTo(intersectpoint));
        } else
            return new Intersection();
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
	 * 
	 * @param point
	 * @return the distance from this point to the other point
	 */
	public float distanceTo(Point point) {
		float dX = x-point.x;
		float dY = y-point.y;
		float dZ = z-point.z;
		return (float) Math.sqrt(dX*dX+dY*dY+dZ*dZ);
	}

	/**
	 * 
	 * @param object
	 * @return the distance from this point to the other object
	 */
	public float distanceTo(AbstractGameObject object) {
		return distanceTo(object.getPosition().getPoint());
	}
	
	
		/**
	 * checks only x and y.
	 * @param point
	 * @return the distance from this point to the other point only regarding horizontal components.
	 */
	public float distanceToHorizontal(Point point) {
		float dX = x-point.x;
		float dY = y-point.y;
		return (float) Math.sqrt(dX*dX+dY*dY);
	}
	
	/**
	 *  checks only x and y.
	 * @param object
	 * @return the distance from this point to the other point only regarding horizontal components.
	 */
	public float distanceToHorizontal(AbstractGameObject object) {
		return distanceToHorizontal(object.getPosition().getPoint());
	}
	
	
	/**
	 * get entities in radius
	 * @param radius in game dimension pixels
	 * @return every entitie in radius
	 */
	public ArrayList<AbstractEntity> getEntitiesNearby(float radius){
		ArrayList<AbstractEntity> result = new ArrayList<>(5);//defautl size 5

        for (AbstractEntity entity : Controller.getMap().getEntitys()) {
            if (distanceTo(entity.getPosition().getPoint()) < radius){
                result.add(entity);
            } 
        }

        return result;
	}
	
	/**
	 * get entities in radius
	 * @param <type> returns only object if type which is the filter
	 * @param radius in game dimension pixels
	 * @param type the type you want to filter
	 * @return every entitie in radius
	 */
	@SuppressWarnings("unchecked")
	public <type> ArrayList<type> getEntitiesNearby(float radius, final Class<? extends AbstractEntity> type){
		ArrayList<type> result = new ArrayList<>(5);//defautl size 5

        for (AbstractEntity entity : Controller.getMap().getEntitys(type)) {
            if (distanceTo(entity.getPosition().getPoint()) < radius){
                result.add((type) entity);
            } 
        }

        return result;
	}
	
		/**
	 * get entities in radius (horizontal only)
	 * @param radius in game dimension pixels
	 * @return every entitie in radius
	 */
	public ArrayList<AbstractEntity> getEntitiesNearbyHorizontal(float radius){
		ArrayList<AbstractEntity> result = new ArrayList<>(5);//defautl size 5

        for (AbstractEntity entity : Controller.getMap().getEntitys()) {
            if (distanceToHorizontal(entity.getPosition().getPoint()) < radius){
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
	public <type> ArrayList<type> getEntitiesNearbyHorizontal(float radius, final Class<type> type){
		ArrayList<type> result = new ArrayList<>(5);//defautl size 5
		ArrayList<AbstractEntity> entityList = Controller.getMap().getEntitys();

        for (AbstractEntity entity : entityList) {//check every entity
            if (
				type.isInstance(entity) //if the entity is of the wanted type
				&&
				distanceToHorizontal(entity.getPosition().getPoint()) < radius
			) {
                result.add((type) entity);//add it to list
            }
        }

        return result;
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Point other = (Point)obj;
		if (x != other.x) return false;
		if (y != other.y) return false;
		return z == other.z;
	}
	
	@Override
	public String toString() {
		return "{"+x +", "+ y +", " +z+"}";
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Float.floatToIntBits(this.x);
		hash = 97 * hash + Float.floatToIntBits(this.y);
		hash = 97 * hash + Float.floatToIntBits(this.z);
		return hash;
	}

}
