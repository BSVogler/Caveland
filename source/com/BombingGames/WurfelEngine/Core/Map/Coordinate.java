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
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.badlogic.gdx.math.Vector3;

/**
 *A coordinate is a reference to a specific cell in the map. The coordinate can transfer between relative and absolute coordiantes.
 * Relative coordinates are similar to the currently loaded map array. Absolute coordinates  are indipendent of the current map but to acces them you must have the chunk thet the coordiantes are in the currently loaded chunks.
 * @author Benedikt Vogler
 */
public class Coordinate extends AbstractPosition {
    private int x; //saved as relative
    private int y; //saved as relative
    
    /**
     * Creates a coordiante refering to a position on the map.
     * @param x The x value as coordinate.
     * @param y The y value as coordinate.
     * @param z The z value as coordinate.
     * @param relative <b>True</b> when the coordiantes are relative to the currently loaded map. <b>False</b> when they are absolute.
     */
    public Coordinate(int x, int y, int z, final boolean relative) {
        super();
        
        this.x = x;
        this.y = y;
        if (!relative){ //if absolute then make it relative
            this.x -= getReferenceX() * Chunk.getBlocksX();
            this.y -= getReferenceY() * Chunk.getBlocksY();
        }
        
        setHeight(z*Block.GAME_EDGELENGTH);
    }
    
     /**
     * Creates a coordiante. You can specify wether the given values are absolute or relative to the map.
     * @param x The x value as coordinate.
     * @param y The y value as coordinate.
     * @param height The z value as height.
     * @param relative <b>True</b> when the coordiantes are relative to the currently loaded map. <b>False</b> when they are absolute.
     */
    public Coordinate(int x, int y, float height, final boolean relative) {
        super();
        
        this.x = x;
        this.y = y;
        if (!relative){ //if absolute then make it relative
            this.x -= getReferenceX() * Chunk.getBlocksX();
            this.y -= getReferenceY() * Chunk.getBlocksY();
        }
        
        setHeight(height);
    }
    
    /**
     * Creates a new coordinate from an existing coordinate
     * @param coord the Coordinate you want to copy
     */
    public Coordinate(Coordinate coord) {
        super(coord.getReferenceX(), coord.getReferenceY());
        
        this.x = coord.getRelX();
        this.y = coord.getRelY();
        setHeight(coord.getHeight());
    }
    
    /**
     *Gets the X coordinate relative to the map.
     * @return
     */
    public int getRelX(){
        return x + (getReferenceX()-Controller.getMap().getChunkCoords(0)[0]) * Chunk.getBlocksX();
    }
    /**
     *Gets the Y coordinate relative to the map.
     * @return
     */
    public int getRelY(){
        return y + (getReferenceY()-Controller.getMap().getChunkCoords(0)[1]) * Chunk.getBlocksY();
    }
    
    /**
     *Absolute coordinates are independent of the currently loaded chunks.
     * @return
     */
    public int getAbsX(){
        return x + getReferenceX() *Chunk.getBlocksX();
    }
    /**
     *Absolute coordinates are independent of the currently loaded chunks.
     * @return
     */
    public int getAbsY(){
         return y + getReferenceY() *Chunk.getBlocksY();
    }
    
    /**
     *The z value is absolute even when used as relative coordinate because there are no chunks in Z direction.
     * @return game coordinate
     */
    public int getZ(){
        return (int) (getHeight() / Block.GAME_EDGELENGTH);
    }
    
    /**
     *Checks if the calculated value is valid and clamps it to the map dimensions.
     * @return
     * @see #getZ() 
     */
    public int getZClamp(){
        int tmpZ = getZ();
        if (tmpZ >= Map.getBlocksZ())
            return Map.getBlocksZ() -1;
        else if (tmpZ < 0)
                return 0;
             else
                return tmpZ;
    }
    
    
   /**
     *
     * @return an array with the offset of the cell
     */
    public int[] getCellOffset(){
        return Controller.getMap().getCellOffset(this);
    }
 
    
    /**
     *Set the coordiantes X component.
     * @param x
     */
    public void setRelX(int x){
        this.x = x;
    }
    
    /**
     *Set the coordiantes Y component.
     * @param y
     */
    public void setRelY(int y){
        this.y = y;
    }
    
    /**
     *Set the coordinates Z component. It will be transversed into a float value (height).
     * @param z
     */
    public void setZ(int z){
        setHeight(z*Block.GAME_EDGELENGTH);
    }
    

    
    /**
     *Set the vertical offset in the cell, where the coordiante is pointing at.
     * @param height
     */
    public void setCellOffsetZ(int height){
        Controller.getMap().setCelloffset(this, 2, height);
    }
    
    /**
     *
     * @param block
     */
    public void setBlock(Block block){
        Controller.getMap().setData(this, block);
    }
    
    /**
     *
     * @return
     */
    public int[] getRel(){
        return new int[]{getRelX(), getRelY(), getZ()};
    }
    
    /**
     *
     * @return
     */
    public int[] getAbs(){
        return new int[]{getAbsX(), getAbsY(), getZ()};
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
        setHeight(getHeight()+ vector[2]*Block.GAME_EDGELENGTH);
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
        setHeight(getHeight()+ vector.z*Block.GAME_EDGELENGTH);
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
        setHeight(getHeight()+ z*Block.GAME_EDGELENGTH);
        return this;
    }
    
    @Override
    public Block getBlock(){
        return Controller.getMap().getBlock(this);
    }
    
        /**
     *Checks of coordinates are valid before fetching the Block.
     * @return
     */
    @Override
    public Block getBlockSafe(){
        if (onLoadedMap())
            return Controller.getMap().getBlock(this);
        else return null;
    }
    
    /**
     *
     * @return
     */
    public Block getBlockClamp(){
        return Controller.getMap().getDataClamp(this);
    }
    

    
    /**
     * Has the object an offset (pos vector)?
     * @return when it has offset true, else false
     */
    public boolean hasOffset() {
        return getCellOffset()[0] != 0 || getCellOffset()[1] != 0 || getCellOffset()[2] != 0;
    }
    
   /**
     * The block hides the past block when it has sides and is not transparent (like normal block)
     * @return true when hiding the past Block
     */
    public boolean hidingPastBlock(){
        return (getBlock().hasSides() && ! getBlock().isTransparent() && ! hasOffset());
    }
    
    /** @return a copy of this coordinate */
    @Override
    public Coordinate cpy () {
        return new Coordinate(this);
    }
    
    /**
     * Checks if the coordiantes are accessable with the currently loaded Chunks.
     * @return 
     */
    @Override
    public boolean onLoadedMap(){
        return (
            getRelX() >= 0
            && getRelX() < Map.getBlocksX()
            && getRelY() >= 0
            && getRelY() < Map.getBlocksY()
        );
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
     * Get the neighbour coordinates of the neighbour of the coords you give.
     * @param neighbourSide the side number of the given coordinates
     * @return The coordinates of the neighbour.
     */
    public Coordinate neighbourSidetoCoords(final int neighbourSide) {
        int[] result = new int[3];
        switch (neighbourSide) {
            case 0:
                result[0] = getRelX();
                result[1] = getRelY() - 2;
                break;
            case 1:
                result[0] = getRelX() + (getRelY() % 2 == 1 ? 1 : 0);
                result[1] = getRelY() - 1;
                break;
            case 2:
                result[0] = getRelX() + 1;
                result[1] = getRelY();
                break;
            case 3:
                result[0] = getRelX() + (getRelY() % 2 == 1 ? 1 : 0);
                result[1] = getRelY() + 1;
                break;
            case 4:
                result[0] = getRelX();
                result[1] = getRelY() + 2;
                break;
            case 5:
                result[0] = getRelX() - (getRelY() % 2 == 0 ? 1 : 0);
                result[1] = getRelY() + 1;
                break;
            case 6:
                result[0] = getRelX() - 1;
                result[1] = getRelY();
                break;
            case 7:
                result[0] = getRelX() - (getRelY() % 2 == 0 ? 1 : 0);
                result[1] = getRelY() - 1;
                break;
            default:
                result[0] = getRelX();
                result[1] = getRelY();
        }
        result[2] = getZ();
        return new Coordinate(result[0], result[1], result[2], true);
    }
    
    /**
     *
     * @return
     */
    protected int getX() {
        return x;
    }

    /**
     *
     * @return
     */
    protected int getY() {
        return y;
    }

    /**
     *
     * @return the coordiante's origin is the center
     */
    @Override
    public Point getPoint() {
        return new Point(
            x*Block.GAME_DIAGLENGTH + (y%2==1 ? Block.SCREEN_WIDTH2 : 0),
            y*Block.GAME_DIAGLENGTH2,
            getHeight(),
            true
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
    
    @Override
    public int getProjectedPosX() {
        return getRelX() * AbstractGameObject.SCREEN_WIDTH //x-coordinate multiplied by the projected size in x direction
                //+ AbstractGameObject.SCREEN_WIDTH2 //add half tile for center
                + (getRelY() % 2) * AbstractGameObject.SCREEN_WIDTH2 //offset by y
                + (getZ()>=0 ?   //read cell offset if inside map
                    getCellOffset()[0]
                : 0);
    }

    @Override
    public int getProjectedPosY() {
        return (int) (getRelY() * AbstractGameObject.SCREEN_DEPTH2 //x-coordinate multiplied by half of the projected size in y direction
           // + AbstractGameObject.SCREEN_DEPTH2 //add half tile for center 
            - getHeight() *0.7071067811865475f //subtract height and take axis shortening into account
            + (getZ()>=0 ?  //read cell offset if inside map
                + getCellOffset()[1] / 2 //add the objects y position inside this coordinate
                - getCellOffset()[2] *0.7071067811865475f //subtract the objects z position inside this coordinate;
            :0)
        );
    }

    /**
     * Clamps x and y coordiantes if outside of map.
     */
    public Coordinate clampToMap() {
        if (x>=Map.getBlocksX())
            x=Map.getBlocksX()-1;
        else
            if (x<0) x=0;
        if (y>=Map.getBlocksY())
            y=Map.getBlocksY()-1;
        else if (y<0)
            y=0;
        return this;
    }
}