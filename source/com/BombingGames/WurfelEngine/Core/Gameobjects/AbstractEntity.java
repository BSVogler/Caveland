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
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.badlogic.gdx.Gdx;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *An entity is a game object wich is self aware that means it knows it's position.
 * @author Benedikt
 */
public abstract class AbstractEntity extends AbstractGameObject {
	private static final long serialVersionUID = 2L;
	private static java.util.HashMap<String, Class<? extends AbstractEntity>> entityMap = new java.util.HashMap<>(10);//map string to class


    private Point position;//the position in the map-grid
    private int dimensionZ = GAME_EDGELENGTH;  
    private boolean dispose;
    private boolean onMap;
	private boolean obstacle;
	private transient EntityAnimation animation;
	private transient EntityShadow shadow;
	private String name = "undefined";
	
	/**
	 * flags if should be saved
	 */
	private boolean saveToDisk = true;
   
	/**
	 * Registers engine entities in a map.
	 */
	public static void registerEngineEntities() {
		entityMap.put("Explosion", Explosion.class);
		entityMap.put("Benchmarkball", BenchmarkBall.class);
	}
	
	/**
	 * Register a class of entities.
	 * @param name the name of the entitie. e.g. "Ball"
	 * @param entityClass the class you want to register
	 */
	public static void registerEntity(String name, Class<? extends AbstractEntity> entityClass){
		entityMap.put(name, entityClass);	
	}
	
	/**
	 * Get a map of the registered entities
	 * @return 
	 */
	public static java.util.HashMap<String, Class<? extends AbstractEntity>> getRegisteredEntities() {
		return entityMap;
	}
		
    /**
     * Create an abstractEntity.
     * @param id objects with id -1 are to deleted. 0 are invisible objects
     */
    protected AbstractEntity(byte id){
        super(id,(byte) 0);
    }

	/**
     * Updates the logic of the object.
     * @param dt time since last update
     */
    public void update(float dt){
		if (animation!=null) animation.update(dt);
	}
		
    //AbstractGameObject implementation
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
	 * @deprecated v1.5.4
     */
    public void setHeight(float height) {
        position.setZ(height);
    }
    
  
    /**
     * Is the entity laying/standing on the ground?
     * @return true when on the ground. False if in air or not in memory.
     */
    public boolean isOnGround(){
        if (getPosition().getZ() <= 0) return true; //if entity is under the map
        
        if (getPosition().getZ() < getPosition().getMap().getGameHeight()){
            //check if one pixel deeper is on ground.
            int z = (int) ((getPosition().getZ()-1)/GAME_EDGELENGTH);
            if (z > getPosition().getMap().getBlocksZ()-1) z = getPosition().getMap().getBlocksZ()-1;

			CoreData block = new Coordinate(
				getPosition().getMap(), 
				position.getCoord().getX(),
				position.getCoord().getY(),
				z
			).getBlock();
			if (block == null)
				return false;
			return block.isObstacle();
        } else
            return false;//return false if over map
    }
    
    /**
     * add this entity to the map-&gt; let it spawn
	 * @param point the point in the game world where the object is 
     * @return returns itself
     */
    public AbstractEntity spawn(Point point){
		if (!onMap) {
			Controller.getMap().getEntitys().add(this);
			position = point;
			onMap =true;
			dispose = false;
			if (shadow != null && !shadow.spawned())
				shadow.spawn(position.cpy());
		} else {
			Gdx.app.debug("AbstractEntity", "Already spawned.");
		}
        return this;
    }
	
	/**
	 *
	 */
	public void enableShadow(){
		shadow = new EntityShadow(this);
		if (position != null)
			if (onMap) shadow.spawn(position.cpy());
	}
	
	/**
	 *
	 */
	public void disableShadow(){
		shadow.dispose();
		shadow = null;
	}
    
    /**
     *Is the object active on the map?
     * @return
     */
    public boolean spawned(){
        return onMap;
    }

	/**
	 * Animation information.
	 * @return can be null if it has no animation
	 */
	public EntityAnimation getAnimation() {
		return animation;
	}

	/**
	 * Give the entity an animation.
	 * @param animation 
	 */
	public void setAnimation(EntityAnimation animation) {
		this.animation = animation;
		animation.setParent(this);
	}
	

    @Override
    public char getCategory() {
        return 'e';
    } 
    
    
    @Override
    public String getName() {
		if (name==null)
			return "undefined";
        return name;
    }
	
	/**
	 *
	 * @param name
	 */
	public void setName(String name){
		this.name = name;
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
	@Override
    public int getDimensionZ() {
        return dimensionZ;
    }
	
	/**
     * Deletes the object from the map. The opposite to spawn();
	 * @see #shouldBeDisposed() 
     */
    public void disposeFromMap(){
        onMap = false;
		if (shadow != null) shadow.dispose();
    }
    
   /**
     * Deletes the object from the map and every other container. The opposite to spawn() but removes it completely.;
	 * @see #shouldBeDisposed() 
	 * @see #disposeFromMap() 
     */
    public void dispose(){
        dispose = true;
        disposeFromMap();
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
     * 
     * @return true if disposing next tick from map only
	 * @see #disposeFromMap() 
	 * @see #shouldBeDisposed() 
     */
    public boolean shouldBeDisposedFromMap() {
        return !onMap;
    }
    
	
	/**
	 * Is the oject saved on the map?
	 * @return true if savedin map file.
	 */
	public boolean isGettingSaved() {
		return saveToDisk;
	}

	/**
	 * Temp objects should not be saved.
	 * @param saveToDisk new value of saveToDisk
	 */
	public void setSaveToDisk(boolean saveToDisk) {
		this.saveToDisk = saveToDisk;
	}

	/**
	 * true if on chunk which is in memory
	 * @return 
	 */
	public boolean isInMemoryArea() {
		return position.isInMemoryAreaHorizontal();
	}
	
	    /**
     * Make the object to an obstacle or passable.
     * @param obstacle true when obstacle. False when passable.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

	@Override
	public boolean isObstacle() {
		return obstacle;
	}
	

	@Override
	public boolean isTransparent() {
		return true;
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
		enableShadow();
    }
}