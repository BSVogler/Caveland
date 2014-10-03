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

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

/**
 *A character is an entity wich can walk around. To control the character you should use {@link #walk(boolean, boolean, boolean, boolean, float) }".
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity {
   private static int soundlimit;//time to pass before new sound can be played
     
   private final int colissionRadius = GAME_DIAGLENGTH2/2;
   private final int spritesPerDir;
      
   /** Set value how fast the character brakes or slides. The higher the value, the more "slide". Can cause problems with running sound. Value >1. If =0 friciton is disabled**/
   private int friction = 0;
      
	/**
	 * direction of movement
	 */
	private Vector3 movement;
   /**The walking/running speed of the character. provides a factor for the horizontal part of the movement vector*/
	private float speed;
	private boolean coliding;
	/**
	 * affected by gractiy
	 */
	private boolean floating;
	
   private Sound fallingSound;
   
   private static Sound waterSound;
   private boolean fallingSoundPlaying;
   private Sound runningSound;
   private boolean runningSoundPlaying;
   private Sound jumpingSound;
   private Sound landingSound;
   private Sound[] damageSounds;


   private boolean inliquid;
   private int health = 1000;
   private int mana = 1000;
       
   private EntityShadow shadow;
   
   private int walkingAnimationCounter;

   /**
    * Constructor of AbstractCharacter.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
    */
   protected MovableEntity(final int id, final int spritesPerDir) {
        super(id);
        this.spritesPerDir = spritesPerDir;
		movement = new Vector3(0,0,0);
		speed = 0.5f;
        
		coliding = true;
		floating = false;
        waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
    }

	@Override
	public AbstractEntity spawn(Point point) {
		if (point != null)
			shadow = (EntityShadow) new EntityShadow().spawn(point.cpy());
		else
			shadow = null;
		return super.spawn(point);
	}
   
   /**
     * This method should define what happens when the object  jumps. It should call super.jump(int velo)
     * @see #jump(float)
     */
    public void jump(){
		//jump(0);
	};
   
	/**
     * Jump with a specific speed
     * @param velo the velocity in m/s
     */
    public void jump(float velo) {
        if (onGround()) {
			movement.z = velo;
            if (jumpingSound != null) jumpingSound.play();
        }
    }
	
    /**
     * Defines the direction of the gun - if no gun available - the direction of the head.
     * @return  If not overwritten returning movement.
     */
   public Vector3 getAiming(){
	   return movement;
   };

    
   /**
     * Updates the character.
     * @param delta time since last update in ms
     */
    @Override
    public void update(float delta) {
        //clamp health & mana
        if (mana > 1000) mana = 1000;
        if (health > 1000) health = 1000;
        if (mana < 0) mana = 0;
        if (health < 0) health = 0;
        
        /*Here comes the stuff where the character interacts with the environment*/
        if (getPosition()!= null && getPosition().onLoadedMapHorizontal()) {

            /*VERTICAL MOVEMENT*/
				float oldHeight = getPosition().getHeight();
				float t = delta/1000f; //t = time in s
				if (!floating)
					if (!onGround()) movement.z -= WE.getCurrentConfig().getGravity()*t; //in m/s
				getPosition().setHeight(getPosition().getHeight() + movement.z * GAME_EDGELENGTH * t); //in m


				//check new height for colission            
				//land if standing in or under 0-level or there is an obstacle
				if (movement.z < 0 && onGround()){
					if (landingSound != null)
						landingSound.play();//play landing sound
					if (fallingSound != null)
						fallingSound.stop();//stop falling sound
					movement.z = 0;

					//set on top of block
					getPosition().setHeight((int)(oldHeight/GAME_EDGELENGTH)*GAME_EDGELENGTH);
				}

				if (!inliquid && getPosition().getBlockClamp().isLiquid())//if enterin water
					waterSound.play();

				inliquid = getPosition().getBlockClamp().isLiquid();//save if in water


			/*HORIZONTAL MOVEMENT*/
				//normalize horizontal movement
				double vectorLenght = Math.sqrt(movement.x*movement.x + movement.y*movement.y);
				if (vectorLenght > 0){
					movement.x /= vectorLenght;
					movement.y /= vectorLenght;
				}

				//calculate new position
				float[] dMove = new float[]{
					delta * speed * movement.x,
					delta * speed * movement.y,
					0
				};

				//if movement allowed => move
				if (!coliding || ! horizontalColission(getPosition().cpy().addVector(dMove)) ) {                
						getPosition().addVector(dMove);
					}

            /* update sprite*/
			if (spritesPerDir>0) {
				if (movement.x < -Math.sin(Math.PI/3)){
					setValue(1);//west
				} else {
					if (movement.x < - 0.5){
						//y
						if (movement.y<0){
							setValue(2);//north-west
						} else {
							setValue(0);//south-east
						}
					} else {
						if (movement.x <  0.5){
							//y
							if (movement.y<0){
								setValue(3);//north
							}else{
								setValue(7);//south
							}
						}else {
							if (movement.x < Math.sin(Math.PI/3)) {
								//y
								if (movement.y < 0){
									setValue(4);//north-east
								} else{
									setValue(6);//sout-east
								}
							} else{
								setValue(5);//east
							}
						}
					}
				}
				if (spritesPerDir==3){
					//animation
					walkingAnimationCounter += delta*speed*4;
					if (walkingAnimationCounter > 1000) walkingAnimationCounter=0;    

					if (walkingAnimationCounter >750) setValue(getValue()+16);
					else if (walkingAnimationCounter >250 && walkingAnimationCounter <500) setValue(getValue()+8);
				}
			}

            //uncomment this line to see where to player stands:
            //Controller.getMapDataSafe(getRelCoords()[0], getRelCoords()[1], getRelCoords()[2]-1).setLightlevel(30);

            if (shadow != null)
				shadow.update(delta, this);

            //slow walking down
			if (friction>0) {
				if (speed > 0) speed -= delta/ friction;
				if (speed < 0) speed = 0;
			}
            
            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (speed < 0.5f) {
                    runningSound.stop();
                    runningSoundPlaying = false;
                } else {
                    if (!runningSoundPlaying){
                        runningSound.play();
                        runningSoundPlaying = true;
                    }
                }
            }

            //should the fallingsound be played?
            if (fallingSound != null) {
                if (movement.z < -1) {
                    if (!fallingSoundPlaying){
                        fallingSound.play();
                        fallingSoundPlaying = true;
                    }
                }else {
                    fallingSound.stop();
                    fallingSoundPlaying = false;
                }
            }
            if (soundlimit>0)soundlimit-=delta;
            
            if (health<=0)
                dispose();
        }
    }
    
    /**
     * check for horizontal colission
	 * @param pos the new position
     * @return 
     */
    private boolean horizontalColission(Point pos){
        boolean colission = false;
    
        //check for movement in y
        //top corner
        if (pos.cpy().addVector(0, - colissionRadius, 0).getCoord().getBlockClamp().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(0, colissionRadius, 0).getCoord().getBlockClamp().isObstacle())
            colission = true;
        
        //check X
        //left
        if (pos.cpy().addVector(-colissionRadius, 0, 0).getCoord().getBlockClamp().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(colissionRadius, 0, 0).getCoord().getBlockClamp().isObstacle())
            colission = true;
        
        return colission;
    }
    
    /**
     * Sets the sound to be played when falling.
     * @param fallingSound
     */
    public void setFallingSound(Sound fallingSound) {
        this.fallingSound = fallingSound;
    }

    /**
     * Set the sound to be played when running.
     * @param runningSound
     */
    public void setRunningSound(Sound runningSound) {
        this.runningSound = runningSound;
    }
    

    /**
     * Set the value of jumpingSound
     *
     * @param jumpingSound new value of jumpingSound
     */
    public void setJumpingSound(Sound jumpingSound) {
        this.jumpingSound = jumpingSound;
    }
    
        /**
     * Set sound played when the character lands on the feet.
     *
     * @param landingSound new value of landingSound
     */
    public void setLandingSound(Sound landingSound) {
        this.landingSound = landingSound;
    }
    
   /**
     * Set the value of waterSound
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(Sound waterSound) {
        MovableEntity.waterSound = waterSound;
    }
    
    /**
     *
     * @param sound
     */
    public void setDamageSounds(Sound[] sound){
        damageSounds = sound;
    }

	public Vector3 getMovement() {
		return movement;
	}

	public void setMovement(Vector3 movement) {
		this.movement = movement;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}
	

	public boolean isColiding() {
		return coliding;
	}

	public void setColiding(boolean coliding) {
		this.coliding = coliding;
	}
       
	/**
	 *  Is the object be affected by gravity?
	 * @return 
	 */
	public boolean isFloating() {
		return floating;
	}

	/**
	 * Should the object be affected by gravity?
	 * @param floating 
	 */
	public void setFloating(boolean floating) {
		this.floating = floating;
	}
	
    /**
     * Checks if standing on blocks.
     * @return 
     */
    @Override
    public boolean onGround() {
        if (getPosition().getHeight() > 0){
                getPosition().setHeight(getPosition().getHeight()-1);
                
                boolean colission = getPosition().getBlockClamp().isObstacle() || horizontalColission(getPosition());
                getPosition().setHeight(getPosition().getHeight()+1);
                
                //if standing on ground on own or neighbour block then true
                return (super.onGround() || colission);
        } return true;
    }

    /**
     * Is the character standing in a liquid?
     * @return 
     */
    public boolean isInLiquid() {
        return inliquid;
    }

    /**
     *
     * @param value
     */
    public void damage(int value) {
        if (health >0){
            if (damageSounds != null && soundlimit<=0) {
                damageSounds[(int) (Math.random()*(damageSounds.length-1))].play(0.7f);
                soundlimit = 100;
            }
            health -= value;
        } else
            health=0;
    }

    /**
     *
     * @return
     */
    public int getHealt() {
       return health;
    }

    /**
     *
     * @return
     */
    public int getMana() {
        return mana;
    }

    /**
     *
     * @param mana
     */
    public void setMana(int mana) {
        this.mana = mana;
    }

	/**
	 * autoamtically slows speed down.
	 * @param friction The higher the value, the more "slide". If =0 friciton is disabled.
	 */
	public void setFriction(int friction) {
		this.friction = friction;
	}
	/**
	 * 
	 * @param pos 
	 */
	@Override
	public void setPosition(AbstractPosition pos) {
		if (getPosition() == null)
			shadow = (EntityShadow) new EntityShadow().spawn((Point) pos.cpy());
		super.setPosition(pos);
	}
	
    /**
     *
     */
   @Override
    public void dispose(){
        super.dispose();
        shadow.dispose();
        if (fallingSound!= null) fallingSound.dispose();
        if (jumpingSound!= null) jumpingSound.dispose();
        if (waterSound!= null) waterSound.dispose();
        if (runningSound!= null) runningSound.dispose();
    }
}
