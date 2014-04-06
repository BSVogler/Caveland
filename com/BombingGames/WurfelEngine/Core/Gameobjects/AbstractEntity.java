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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;

/**
 *An entity is a game object wich is self aware that means it knows it's position.
 * @author Benedikt
 */
public abstract class AbstractEntity extends AbstractGameObject implements IsSelfAware {
       /**
     *
     */
    public static final char CATEGORY = 'e';
   
    /**Containts the names of the objects. index=id*/
    public static final String[] NAMELIST = new String[OBJECTTYPESCOUNT]; 
    
    /** A list containing the offset of the objects. */
    public static final int[][][] OFFSET = new int[OBJECTTYPESCOUNT][VALUESCOUNT][2];
    
    private Point point;//the position in the map-grid
   
    static {
        NAMELIST[30] = "player";
        OFFSET[30][0][0] = 54-80;
        OFFSET[30][0][1] = 37-40;
        OFFSET[30][1][0] = 55-80;
        OFFSET[30][1][1] = 38-40;
        OFFSET[30][2][0] = 53-80;
        OFFSET[30][2][1] = 35-40;
        OFFSET[30][3][0] = 46-80;
        OFFSET[30][3][1] = 33-40;
        OFFSET[30][4][0] = 53-80;
        OFFSET[30][4][1] = 35-40;
        OFFSET[30][5][0] = 64-80;
        OFFSET[30][5][1] = 33-40;
        OFFSET[30][6][0] = 53-80;
        OFFSET[30][6][1] = 33-40;
        OFFSET[30][7][0] = 46-80;
        OFFSET[30][7][1] = 33-40;
        NAMELIST[31] = "smoke test";
        NAMELIST[32] = "character shadow";
        OFFSET[32][0][0] = -80;
        OFFSET[32][0][1] = 40;
    }
    
    private boolean destroy;
   
    /**
     * Create an abstractEntity. You should use Block.getInstance(int) 
     * @param id 
     * @param point 
     * @see com.BombingGames.Game.Gameobjects.Block#getInstance(int) 
     */
    protected AbstractEntity(int id, Point point){
        super(id,0);
        setPos(point);
    }
    
    @Override
    public int getDepth(AbstractPosition pos){
        return (int) (
            pos.getPoint().getRelY()//Y
            
            + pos.getHeight()/Math.sqrt(2)//Z
            + (getDimensionZ() - 1) * GAME_EDGELENGTH/6/Math.sqrt(2)
        );
    }
    
    //IsSelfAware implementation
    @Override
    public Point getPos() {
        return point;
    }

    @Override
    public final void setPos(AbstractPosition pos) {
        this.point = pos.getPoint();
    }
    
    /**
     * 
     * @param height 
     */
    public void setHeight(float height) {
        point.setHeight(height);
    }
    
  
    /**
     * Is the entity laying/standing on the ground?
     * @return true when on the ground
     */
    public boolean onGround(){
        if (getPos().getHeight() <= 0) return true; //if entity is under the map
        
        //check if one pixel deeper is on ground.
        int z = (int) ((getPos().getHeight()-1)/GAME_EDGELENGTH);
        if (z > Map.getBlocksZ()-1) z = Map.getBlocksZ()-1;
        
        return
            new Coordinate(
                point.getCoord().getRelX(),
                point.getCoord().getRelY(),
                z,
                true
            ).getBlock().isObstacle();
    }
    
    /**
     * add this entity to the map-> let it exist
     * @return returns itself
     */
    public AbstractEntity exist(){
        Controller.getMap().getEntitys().add(this);
        return this;
    }
  
    /**
     *
     * @return
     */
    @Override
    public char getCategory() {
        return CATEGORY;
    } 
    
    @Override
    public String getName() {
        return NAMELIST[getId()];
    }
    
    /**
     *The offset is the offset of the sprite image.
     * @return
     */
    @Override
    public int getOffsetX() {
        return OFFSET[getId()][getValue()][0];
    }

    /**
     *The offset is the offset of the sprite image.
     * @return
     */
    @Override
    public int getOffsetY() {
        return OFFSET[getId()][getValue()][1];
    } 
    
   /**
     * Deletes the object from the map. The opposite to exist();
     */
    public void dispose(){
        destroy=true;
    }

    /**
     *
     * @return
     */
    public boolean shouldBeDestroyed() {
        return destroy;
    }
}