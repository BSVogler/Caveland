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

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;

/**
 *An entity is a game object wich is self aware that means it knows it's position.
 * @author Benedikt
 */
public abstract class AbstractEntity extends AbstractGameObject implements IsSelfAware {
    private Point position;//the position in the map-grid
    private int dimensionZ = GAME_EDGELENGTH;  
    private boolean dispose;
    private boolean spawned;
   
    /**
     * Create an abstractEntity.
     * @param id 
     */
    protected AbstractEntity(int id){
        super(id,0);
    }

    //IsSelfAware implementation
    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(AbstractPosition pos) {
        this.position = pos.getPoint();
    }

    /**
     * 
     * @param height 
     */
    public void setHeight(float height) {
        position.setHeight(height);
    }
    
  
    /**
     * Is the entity laying/standing on the ground?
     * @return true when on the ground
     */
    public boolean onGround(){
        if (getPosition().getHeight() <= 0) return true; //if entity is under the map
        
        if (getPosition().getHeight()>Map.getGameHeight()){
            //check if one pixel deeper is on ground.
            int z = (int) ((getPosition().getHeight()-1)/GAME_EDGELENGTH);
            if (z > Map.getBlocksZ()-1) z = Map.getBlocksZ()-1;

            return
                new Coordinate(
                    position.getCoord().getRelX(),
                    position.getCoord().getRelY(),
                    z,
                    true
                ).getBlock().isObstacle();
        } else
            return false;//return false if over map
    }
    
    /**
     * add this entity to the map-> let it spawn
	 * @param point
     * @return returns itself
     */
    public AbstractEntity spawn(Point point){
        Controller.getMap().getEntitys().add(this);
		position = point;
        spawned =true;
        return this;
    }
    
    /**
     *Is the object active on the map?
     * @return
     */
    public boolean spawned(){
        return spawned;
    }
  

    @Override
    public char getCategory() {
        return 'e';
    } 
    
    
    @Override
    public String getName() {
        return "feature not supported yet";
    }
    
     /**
     * Set the height of the object.
     * @param dimensionZ
     */
    public void setDimensionZ(int dimensionZ) {
        this.dimensionZ = dimensionZ;
    }

    /**
     * 
     * @return
     */
    public int getDimensionZ() {
        return dimensionZ;
    }
    
   /**
     * Deletes the object from the map. The opposite to spawn();
	 * @see #shouldBeDisposed() 
     */
    public void dispose(){
        dispose=true;
        spawned=false;
    }

    /**
     * 
     * @return true if disposing next tick
	 * @see #dispose() 
     */
    public boolean shouldBeDisposed() {
        return dispose;
    }
    
    /**
     * renders using it's saved postion
     * @param view
     * @param camera 
     */
    public void render(GameView view, Camera camera){
        super.render(view, camera, position);
    }
}