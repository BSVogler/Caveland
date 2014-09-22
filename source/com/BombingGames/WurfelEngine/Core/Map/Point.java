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

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.badlogic.gdx.math.Vector3;

/**
 *A point is a single position in the game world not bound to the grid. Use this for entities.
 * @author Benedikt Vogler
 * @since WE1.1
 */
public class Point extends AbstractPosition {
    private float x;
    private float y;

    /**
     * Creates a point refering to a position in the game world.
     * @param posX The distance from the left border of the map (game space)
     * @param posY The distance from the top border of the map (game space)
     * @param height The distance from ground  (game space)
     * @param relative  <b>true</b> if <b>relative</b> to currently loaded map, <b>false</b> if <b>absolute</b> (relative to map with chunk 0,0 in its center)
     */
    public Point(float posX, float posY, float height, boolean relative) {
        super();
         if (relative){
            this.x = posX;
            this.y = posY;
        } else {
            this.x = posX - getReferenceX() * Chunk.getBlocksX();
            this.y = posY - getReferenceY() * Chunk.getBlocksY();
        }
        setHeight(height);
    }
    
    /**
     * Copy-constructor. This constructor copies the values.
     * @param point the source of the copy
     */
    public Point(Point point) {
       super(point.getReferenceX(), point.getReferenceY());
       this.x = point.x;
       this.y = point.y;
       this.setHeight(point.getHeight());
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
     * returns coordinate aquivalent. Removes floating of block.
     * @return
     */
    @Override
    public Coordinate getCoord() {
        //find out where the position is (basic)
        Coordinate coords = new Coordinate(
            (int) (getRelX()) / AbstractGameObject.GAME_DIAGLENGTH,
            (int) (getRelY()) / AbstractGameObject.GAME_DIAGLENGTH*2+1,//maybe dangerous to optimize code here!
            getHeight(),
            true
        );
       
        //find the specific coordinate (detail)
        Coordinate specificCoords = coords.neighbourSidetoCoords(
            Coordinate.getNeighbourSide(
                getRelX() % AbstractGameObject.GAME_DIAGLENGTH,
                getRelY() % (AbstractGameObject.GAME_DIAGLENGTH)
            )
        );
        coords.setRelX(specificCoords.getRelX());
        coords.setRelY(specificCoords.getRelY());
        coords.setZ(coords.getZ());//remove floating
        return coords; 
    }
    
    /**
     *
     * @return
     */
    public float[] getRel(){
        return new float[]{getRelX(), getRelY(), getHeight()};
    }

    /**
     *Get the game world position from left
     * @return
     */
    public float getRelX() {
        return x + (getReferenceX()-Controller.getMap().getChunkCoords(0)[0]) * Chunk.getGameWidth();
    }
    
    /**
     *Get the game world position from top.
     * @return
     */
    public float getRelY() {
        return y + (getReferenceY()-Controller.getMap().getChunkCoords(0)[1]) * Chunk.getGameDepth();
    }
    
            /**
     *
     * @return
     */
    public float[] getAbs(){
        return new float[]{getAbsX(), getAbsY(), getHeight()};
    }

    /**
     *
     * @return
     */
    public float getAbsX() {
        return x + getReferenceX() *Chunk.getGameWidth();
    }
    
    /**
     *
     * @return
     */
    public float getAbsY() {
        return y + getReferenceY() *Chunk.getGameDepth();
    }
    
    @Override
    public Block getBlock() {
        return getCoord().getBlock();
    }
    
      /**
     *
     * @return
     */
    @Override
    public Block getBlockSafe(){
        if (onLoadedMap())
            return getCoord().getBlock();
        else return null;
    }
    

    /**
     *
     * @return
     */
    public Block getBlockClamp(){
        Coordinate coord = getCoord();
        if (coord.getZ() >= Chunk.getGameHeight())
            return Block.getInstance(0);
        else
            return Controller.getMap().getDataClamp(coord);
    }

    /**
     *
     * @return
     */
    @Override
    public Point cpy() {
        return new Point(this);
    }

    @Override
    public int getProjectedPosX() {
        return (int) (getRelX()); //just the position as integer
    }

    @Override
    public int getProjectedPosY() {
        return (int) ((Map.getGameDepth()-getRelY()) / 2) //add the objects position inside this coordinate
               + (int) (getHeight() * SQRT12) //take z-axis shortening into account
            +AbstractGameObject.SCREEN_HEIGHT2-AbstractGameObject.SCREEN_DEPTH2;//offset relative to coordinates
    }
    
    @Override
    public int getDepth(){
        return (int) (
            getPoint().getRelY()//Y
            
            + getHeight()*SQRT2//Z
        );
    }

    @Override
    public boolean onLoadedMap() {
        return (
            getRelX() >= 0 && getRelX() < Map.getGameWidth()//do some quick checks X
            && getRelY() >= 0 && getRelY() < Map.getGameDepth()//do some quick checks Y
            && getCoord().onLoadedMap()//do extended check
        );
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
        setHeight(getHeight()+ vector[2]);
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
        setHeight(getHeight()+ vector.z);
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
        setHeight(getHeight()+ z);
        return this;
    }
    
    /**
     * Call the callback with (x,y,z,value,normal) of all blocks along the line
 segment from point 'origin' in vector direction 'direction' of length
 'radius'. 'radius' may be infinite.

 'normal' is the normal vector of the normal of that block that was entered.
 It should not be used after the callback returns.
 
 If the callback returns a true value, the traversal will be stopped.
     * @param direction
     * @param radius
     * @return can return <i>null</i> if not hitting anything. The normal on the back sides may be wrong. The normals are in a turned coordiante system.
     * @since 1.2.29
     */
    public Intersection raycast(Vector3 direction, float radius) {
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
        float curZ = (float) Math.floor(getHeight());
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
        float tMaxZ = intbound(getHeight(), dz);
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
               (stepX > 0 ? curX < Map.getGameWidth() : curX >= 0) &&
               (stepY > 0 ? curY < Map.getGameDepth() : curY >= 0) &&
               (stepZ > 0 ? curZ < Map.getGameHeight() : curZ >= 0)) {

            // Invoke the callback, unless we are not *yet* within the bounds of the
            // world.
            if (!(curX < 0 || curY < 0 || curZ < 0 || curX >= Map.getGameWidth() || curY >= Map.getGameDepth()|| curZ >= Map.getGameHeight())){
                Point isectP = new Point(curX, curY, curZ, true);
                Block block = isectP.getBlockSafe();
                if (block == null) break;//check if outside of map
                if (block.getId() != 0){
                    
                    if (
                        (isectP.getRelX() -(isectP.getCoord().getRelY() % 2 == 0? Block.GAME_DIAGLENGTH2:0))
                        % Block.GAME_DIAGLENGTH
                        <
                        Block.GAME_DIAGLENGTH2
                    ) {
                        normal.y = 0;
                        normal.x = -1;
                    }
                    return new Intersection(isectP, normal, this.distanceTo(isectP));
                }
            }

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
            Point interectpoint = new Point(curX, curY, curZ, true);
            return new Intersection(interectpoint, normal, this.distanceTo(interectpoint));
        }
        else
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
    float dY = y-point.x;
    float dZ = getHeight()-point.getHeight();
    return (float) Math.sqrt(dX*dX+dY*dY+dZ*dZ);
}
    
    
}
