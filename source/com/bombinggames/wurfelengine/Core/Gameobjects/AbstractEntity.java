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
package com.bombinggames.wurfelengine.Core.Gameobjects;

import com.badlogic.gdx.Gdx;
import com.bombinggames.wurfelengine.Core.Controller;
import static com.bombinggames.wurfelengine.Core.Gameobjects.CoreData.GAME_EDGELENGTH;
import com.bombinggames.wurfelengine.Core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.Core.Map.Coordinate;
import com.bombinggames.wurfelengine.Core.Map.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 *An entity is a game object wich is self aware that means it knows it's position.
 * @author Benedikt
 */
public abstract class AbstractEntity extends AbstractGameObject implements HasID {
	private static final long serialVersionUID = 2L;
	private static java.util.HashMap<String, Class<? extends AbstractEntity>> entityMap = new java.util.HashMap<>(10);//map string to class

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
	
	private byte id = 0;
	private byte value = 0;
	private float lightlevel = 1f;
	private float health = 100f;
    private Point position;//the position in the map-grid
    private int dimensionZ = GAME_EDGELENGTH;  
    private boolean dispose;
	private boolean obstacle;
	private transient EntityAnimation animation;
	private transient EntityShadow shadow;
	private String name = "undefined";
	private boolean indestructible = false;
		/**
	 * time in ms to pass before new sound can be played
	 */
	private transient float soundTimeLimit;
	
	/**
	 * flags if should be saved
	 */
	private boolean saveToDisk = true;
	private transient String[] damageSounds;
	private ArrayList<AbstractEntity> children = new ArrayList<>(0);
   
    /**
     * Create an abstractEntity.
     * @param id objects with id -1 are to deleted. 0 are invisible objects
     */
    public AbstractEntity(byte id){
        super(id);
		this.id = id;
    }
	
	 /**
     * Create an abstractEntity.
     * @param id objects with id -1 are to deleted. 0 are invisible objects
	 * @param value
     */
    public AbstractEntity(byte id, byte value){
        super(id);
		this.id = id;
		this.value = value;
    }

	/**
     * Updates the logic of the object.
     * @param dt time since last update
     */
    public void update(float dt){
		if (animation!=null) animation.update(dt);
		
		 if (getHealth()<= 0 && !indestructible)
            dispose();
		 
		if (soundTimeLimit > 0)
			soundTimeLimit -= dt;
	}
		
    //AbstractGameObject implementation
    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public void setPosition(AbstractPosition pos) {
        this.position = pos.toPoint();
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
				position.toCoord().getX(),
				position.toCoord().getY(),
				z
			).getBlock();
			if (block == null)
				return false;
			return block.isObstacle();
        } else
            return false;//return false if over map
    }
    
    /**
     * Add this entity to the map-&gt; let it spawn
	 * @param point the point in the game world where the object is. If it was previously set this is ignored.
     * @return returns itself
     */
    public AbstractEntity spawn(Point point){
		if (position==null) {
			Controller.getMap().getEntitys().add(this);
			for (AbstractEntity child : children) {
				child.spawn(point);
			}
			position = point;
			dispose = false;
			if (shadow != null && !shadow.isSpawned())
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
		addChild(shadow);
		if (position!=null) shadow.spawn(position.cpy());
	}
	
	/**
	 *
	 */
	public void disableShadow(){
		if (shadow!=null) {
			getChildren().remove(shadow);
			shadow.dispose();
			shadow = null;
		}
	}
    
    /**
     *Is the object active on the map?
     * @return
     */
    public boolean isSpawned(){
        return position!=null;
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
     * Deletes the object from the map. The opposite to spawn();<br>
	 * Disposes all the children.
	 * @see #shouldBeDisposed() 
     */
    public void disposeFromMap(){
		for (AbstractEntity child : children) {
			child.disposeFromMap();
		}
		position = null;
    }
    
   /**
     * Deletes the object from the map and every other container. The opposite to spawn() but removes it completely.<br>
	 * Disposes all the children.
	 * @see #shouldBeDisposed() 
	 * @see #disposeFromMap() 
     */
    public void dispose(){
        dispose = true;
		for (AbstractEntity child : children) {
			child.dispose();
		}
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
	 * Is the oject saved on the map?
	 * @return true if savedin map file.
	 */
	public boolean isGettingSaved() {
		return saveToDisk;
	}

	/**
	 * Mark objects to not be saved in disk. Gets passed to the children. Temp objects should not be saved.
	 * @param saveToDisk new value of saveToDisk
	 */
	public void setSaveToDisk(boolean saveToDisk) {
		this.saveToDisk = saveToDisk;
		for (AbstractEntity child : children) {
			child.setSaveToDisk(saveToDisk);
		}
	}

	/**
	 * true if on chunk which is in memory
	 * @return 
	 */
	public boolean isInMemoryArea() {
		if (position==null)
			return false;
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
	
	/**
	 *
	 * @return
	 */
	public boolean isIndestructible() {
		return indestructible;
	}

	/**
	 *Also to all the children.
	 * @param indestructible
	 */
	public void setIndestructible(boolean indestructible) {
		this.indestructible = indestructible;
		for (AbstractEntity child : children) {
			child.setIndestructible(indestructible);
		}
	}

	@Override
	public boolean hasSides() {
		return false;
	}

	@Override
	public void setHidden(boolean hidden) {
		super.setHidden(hidden);
		for (AbstractEntity child : children) {
			child.setHidden(hidden);
		}
	}
	
	
	
	/**
     * called when gets damage
     * @param value
     */
    public void damage(byte value) {
		if (!indestructible) {
			if (getHealth() >0){
				if (damageSounds != null && soundTimeLimit<=0) {
					//play random sound
					Controller.getSoundEngine().play(damageSounds[(int) (Math.random()*(damageSounds.length-1))], getPosition());
					soundTimeLimit = 100;
				}
				setHealth((byte) (getHealth()-value));
			} else
				setHealth((byte) 0);
		}
    }
	
	/**
	 * heals the entity
	 * @param value 
	 */
	public void heal(byte value) {
		if (getHealth()<100)
			setHealth((byte) (getHealth()+value));
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
		enableShadow();
    }

	/**
	 *
	 * @param sound
	 */
	public void setDamageSounds(String[] sound) {
		damageSounds = sound;
	}
	
	public void addChild(AbstractEntity child){
		children.add(child);	
	}

	public ArrayList<AbstractEntity> getChildren() {
		return children;
	}
	
		@Override
    public byte getId() {
        return id;
    }
	
	@Override
    public byte getValue() {
        return value;
    }
	
	@Override
    public float getLightlevel() {
        return lightlevel;
    }

	@Override
	public void setLightlevel(float lightlevel) {
		this.lightlevel = lightlevel;
	}
	
	/**
	 * Set the value of the object.
	 * @param value
	 */
	@Override
	public void setValue(byte value) {
		this.value = value;
	}
	
	/**
     *
     * @return from maximum 100
     */
	public float getHealth() {
		return health;
	}
	
	/**
	 * clamps to [0..100]
	 * @param health 
	 */
	public void setHealth(float health) {
		if (health>100) health=100;
		if (health<0) health=0;
		this.health = health;
	}
}