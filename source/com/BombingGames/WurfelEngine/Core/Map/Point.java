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
 *A point is a single positionin the game workd not bound to the grid. Use this for entities.
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
        return toCoord(this, false, false);
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
     * Game position to game coordinate
     * @param pos the position on the map
     * @param depthCheck when true the coordiantes are checked with depth, use this for "screen to coords". This is only possible if the position are on the map.
     * @param visibilityCheck if this is true the depth check requires the blocks to be invisibble to pass through. If false only will go through air (=ignore rendering)
     * @return 
     */
    public static Coordinate toCoord(final Point pos, final boolean depthCheck, final boolean visibilityCheck){
        //find out where the position is (basic)
        Coordinate coords = new Coordinate(
            (int) (pos.getRelX()) / AbstractGameObject.GAME_DIAGLENGTH,
            (int) (pos.getRelY()) / AbstractGameObject.GAME_DIAGLENGTH*2+1,//maybe dangerous to optimize code here!
            pos.getHeight(),
            true
        );
       
        //find the specific coordinate (detail)
        Coordinate specificCoords = coords.neighbourSidetoCoords(
            Coordinate.getNeighbourSide(
                pos.getRelX() % AbstractGameObject.GAME_DIAGLENGTH,
                pos.getRelY() % (AbstractGameObject.GAME_DIAGLENGTH)
            )
        );
        coords.setRelX(specificCoords.getRelX());
        coords.setRelY(specificCoords.getRelY());
        
        //trace ray down if wanted
        if (depthCheck && pos.onLoadedMap()) {
            coords.setRelY(coords.getRelY() + (depthCheck? coords.getZ()*2 : 0));
            //if selection is not found by that specify it
            //trace ray down to bottom. for each step 2 y and 1 z down
            while (
                coords.getZ()>0
                &&
                (
                    coords.getBlock().getId() == 0
                    ||
                    (
                        visibilityCheck
                        &&
                        (
                            coords.getBlock().isHidden()
                            ||
                            coords.getZ() >= Camera.getZRenderingLimit()
                        )
                    )
                )
                ) {
                    coords.setRelY(coords.getRelY()-2);
                    coords.setZ(coords.getZ()-1);
            } 
        }
        
        return coords;
    }
}
