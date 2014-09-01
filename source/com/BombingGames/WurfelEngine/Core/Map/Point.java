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
     * returns coordinate aquivalent
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
        return (int) (getRelY() / 2) //add the objects position inside this coordinate
               - (int) (getHeight() / Math.sqrt(2)); //take z-axis shortening into account
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
    public AbstractPosition addVector(float x, float y, float z) {
        this.x += x;
        this.y += y;
        setHeight(getHeight()+ z);
        return this;
    }
    
        /**
     * Trace a ray down to find the deepest point.
     * @param visibilityCheck if this is true the depth check requires the blocks to be invisibble to pass through. If false only will go through air (=ignore rendering)
     * @return itself
     */
    public Point traceRay(final boolean visibilityCheck){
        float deltaZ = Chunk.getGameHeight()-Block.GAME_EDGELENGTH-getHeight();
        setHeight(getHeight()+deltaZ);
        y +=deltaZ/Math.sqrt(2)*2;
        
        //trace ray down to bottom.
        while (
            getHeight()>Chunk.getGameHeight()
            ||
            (
                getHeight()>0
                &&
                (
                    getBlock().getId() == 0
                    ||
                    (
                        visibilityCheck
                        &&
                        (
                            getBlock().isHidden()
                            ||
                            getHeight() >= Camera.getZRenderingLimit() * Block.GAME_EDGELENGTH
                        )
                    )
                )
            )
            ) {
               // Gdx.app.debug("Point", "y: "+y+" z: "+getHeight());
                // for each step 2 y and 1 z down
                y -= Block.GAME_DIAGLENGTH/4f;
                setHeight(getHeight()-Block.GAME_EDGELENGTH/4f);
        } 
        
        return this;
    }

    /**
     * if this point lays on the edge of a block
     * @return 0 - left, 1 - top, 2 - right
     */
    public int getNormal() {
        //get center of coord
        Point point = getCoord().getPoint();
        //vector from center to point
        Vector3 vec = new Vector3(x-point.x,y-point.y,getHeight()-point.getHeight());
        vec.nor();
        Vector3 centerEdge = new Vector3(0,Block.GAME_DIAGLENGTH2,Block.GAME_EDGELENGTH/2);
        centerEdge.nor();
        if (vec.z>centerEdge.z)
            return 1;
        if (vec.x<0)
            return 0;
        else return 1;
    }
    
    
}
