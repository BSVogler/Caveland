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

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 *A character is an entity wich can walk around. To control the character you should use a {@link Controllable} or modify the movemnet via {@link #setMovement(com.badlogic.gdx.math.Vector3) }.
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity implements Cloneable  {
	private static final long serialVersionUID = 2L;
	
	/**
	 * time to pass before new sound can be played
	 */
	private static float soundlimit;
     
	private final int colissionRadius = GAME_DIAGLENGTH2/2;
	private final int spritesPerDir;
      
   /** Set value how fast the character brakes or slides. The higher the value, the more "slide". Can cause problems with running sound. Value >1. If =0 friciton is disabled**/
	private int friction = 0;
      
	/**
	 * direction of movement
	 */
	private Vector3 movementDir;
   /**The walking/running speed of the character. provides a factor for the horizontal part of the movement vector*/
	private float speedHorizontal;
	private boolean coliding;
	/**
	 * affected by gractiy
	 */
	private boolean floating;
	
	private transient static Sound waterSound;
	private transient Sound stepSound1Grass;
	private transient boolean stepSoundPlayedInCiclePhase;
	private transient Sound fallingSound;
	private transient boolean fallingSoundPlaying;
	private transient Sound runningSound;
	private transient boolean runningSoundPlaying;
	private transient Sound jumpingSound;
	private transient Sound landingSound;
	private transient Sound[] damageSounds;


	private boolean inliquid;
	private int mana = 1000;
	private boolean indestructible = false;
       
	private EntityShadow shadow;
   
	/**
	 * somehow coutns when the new animation step must be displayed. Value: [0, 1000]
	 */
	private int walkingCycle;
	private boolean collectable;
	/**
	 * A factor to make the animation fit the movement speed.
	 */
	private float animSpeedCorrection = 4;

	
	   /**
     * Set the value of waterSound
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(Sound waterSound) {
        MovableEntity.waterSound = waterSound;
    }

   /**
    * Constructor of AbstractCharacter.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
    */
   protected MovableEntity(final int id, final int spritesPerDir) {
        super(id);
        this.spritesPerDir = spritesPerDir;
		movementDir = new Vector3(0,0,0);
		speedHorizontal = 0.5f;
        
		coliding = true;
		floating = false;
        if (waterSound!=null) waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
    }
   
	protected MovableEntity(MovableEntity entity) {
		super(entity.getId());
		this.spritesPerDir = entity.spritesPerDir;
		movementDir = entity.movementDir;
		friction = entity.friction;
		speedHorizontal = entity.speedHorizontal;
        
		coliding = entity.coliding;
		floating = entity.floating;
		collectable = entity.collectable;
		if (waterSound!=null) waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
	}

	@Override
	public MovableEntity spawn(Point point) {
		if (point != null)
			shadow = (EntityShadow) new EntityShadow(this).spawn(point.cpy());
		else
			shadow = null;
		return (MovableEntity) super.spawn(point);
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
	 * @param playSound
     */
    public void jump(float velo, boolean playSound) {
		movementDir.z = velo;
		if (playSound && jumpingSound != null) jumpingSound.play();
    }
	
    /**
     * Defines the direction of the gun - if no gun available - the direction of the head.
     * @return  If not overwritten returning movement.
     */
   public Vector3 getAiming(){
	   return movementDir;
   };

    
   /**
     * Updates the character.
     * @param delta time since last update in ms
     */
    @Override
    public void update(float delta) {
        //clamp health & mana
        if (mana > 1000) mana = 1000;
        if (mana < 0) mana = 0;

        
        /*Here comes the stuff where the character interacts with the environment*/
        if (getPosition()!= null && getPosition().isInMemoryHorizontal()) {

            /*VERTICAL MOVEMENT*/
				float oldHeight = getPosition().getZ();
				float t = delta/1000f; //t = time in s
				if (!floating)
					if (!isOnGround())
						movementDir.z -= CVar.get("gravity").getValuef()*t; //in m/s
				getPosition().setZ(
					getPosition().getZ() + movementDir.z * GAME_EDGELENGTH * t
				); //in m


				//check new height for colission            
				//land if standing in or under 0-level or there is an obstacle
				if (movementDir.z < 0 && isOnGround()){
					onCollide();
					if (landingSound != null)
						landingSound.play();//play landing sound
					if (fallingSound != null)
						fallingSound.stop();//stop falling sound
					movementDir.z = 0;

					//set on top of block
					getPosition().setZ((int)(oldHeight/GAME_EDGELENGTH)*GAME_EDGELENGTH);
				}

				if (!inliquid && getPosition().getBlock().isLiquid())//if enterin water
					if (waterSound!=null) waterSound.play();

				inliquid = getPosition().getBlock().isLiquid();//save if in water


			/*HORIZONTAL MOVEMENT*/
				//calculate new position
				float[] dMove = new float[]{
					delta * speedHorizontal * movementDir.x,
					delta * speedHorizontal * movementDir.y,
					0
				};

				//if movement allowed => move
				if (!coliding || ! horizontalColission(getPosition().cpy().addVector(dMove)) ) {                
						getPosition().addVector(dMove);
				} else {
					onCollide();
				}


			//cycle
			walkingCycle += delta*speedHorizontal*animSpeedCorrection;//multiply by animSpeedCorrection to make the animation fit the movement speed
			if (walkingCycle > 1000) {
				walkingCycle=0;
				stepSoundPlayedInCiclePhase=false;//reset variable
			}
			
			//play sound twice a cicle
			if (walkingCycle<250){
				if (stepSound1Grass!=null && ! stepSoundPlayedInCiclePhase && isOnGround()) {
					stepSound1Grass.play(1, (float) (1+Math.random()/5), 0);
					stepSoundPlayedInCiclePhase = true;
				}
			} else if (walkingCycle < 500){
				stepSoundPlayedInCiclePhase=false;
			} else if (walkingCycle > 500){
				if (stepSound1Grass!=null && ! stepSoundPlayedInCiclePhase && isOnGround()) {
					stepSound1Grass.play(1, (float) (1+Math.random()/5f), (float) (Math.random()-1/2f));
					stepSoundPlayedInCiclePhase = true;
				}
			}
				
				
			/* update sprite*/
			if (spritesPerDir>0) {
				if (movementDir.x < -Math.sin(Math.PI/3)){
					setValue(1);//west
				} else {
					if (movementDir.x < - 0.5){
						//y
						if (movementDir.y<0){
							setValue(2);//north-west
						} else {
							setValue(0);//south-east
						}
					} else {
						if (movementDir.x <  0.5){
							//y
							if (movementDir.y<0){
								setValue(3);//north
							}else{
								setValue(7);//south
							}
						}else {
							if (movementDir.x < Math.sin(Math.PI/3)) {
								//y
								if (movementDir.y < 0){
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

				if (spritesPerDir==2){
					if (walkingCycle >500)
						setValue(getValue()+8);
				} else if (spritesPerDir==3){
					if (walkingCycle >750)
						setValue(getValue()+16);
					else
						if (walkingCycle >250 && walkingCycle <500)
							setValue(getValue()+8);
				} else if (spritesPerDir==4){
					if (walkingCycle >=166 && walkingCycle <333)
						setValue(getValue()+8);
					else {
						if ((walkingCycle >=500 && walkingCycle <666)
							||
							(walkingCycle >=833 && walkingCycle <1000)
						){
							setValue(getValue()+16);
						} else if (walkingCycle >=666 && walkingCycle < 833) {
							setValue(getValue()+24);
						}
					}
				}
			}

            //uncomment this line to see where to player stands:
            //Controller.getMapDataSafe(getRelCoords()[0], getRelCoords()[1], getRelCoords()[2]-1).setLightlevel(30);

            if (shadow != null)
				shadow.update(delta);

            //slow walking down
			if (friction>0) {
				if (speedHorizontal > 0) speedHorizontal -= delta/ friction;
				if (speedHorizontal < 0) speedHorizontal = 0;
			}
            
            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (speedHorizontal < 0.5f) {
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
                if (movementDir.z < -1) {
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
            
            if (getHealth()<=0 && !indestructible)
                dispose();
        }
    }

	@Override
	public void render(View view, int xPos, int yPos, Color color, float scale) {
		if (CVar.get("debugObjects").getValueb()){
			ShapeRenderer sh = view.getShapeRenderer();
			sh.begin(ShapeRenderer.ShapeType.Filled);
			sh.setColor(Color.GREEN);
			//life bar
			sh.rect(
				xPos-Block.SCREEN_WIDTH2,
				yPos+Block.SCREEN_HEIGHT,
				getHealth()*Block.SCREEN_WIDTH/1000,
				5
			);
			//mana bar
			sh.setColor(Color.BLUE);
			sh.rect(
				xPos-Block.SCREEN_WIDTH2,
				yPos+Block.SCREEN_HEIGHT-6,
				getMana()*Block.SCREEN_WIDTH/1000,
				5
			);

			sh.end();
		}
		super.render(view, xPos, yPos, color, scale);
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
        if (pos.cpy().addVector(0, - colissionRadius, 0).getCoord().getBlock().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(0, colissionRadius, 0).getCoord().getBlock().isObstacle())
            colission = true;
        
        //check X
        //left
        if (pos.cpy().addVector(-colissionRadius, 0, 0).getCoord().getBlock().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(colissionRadius, 0, 0).getCoord().getBlock().isObstacle())
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
     *
     * @param sound
     */
    public void setDamageSounds(Sound[] sound){
        damageSounds = sound;
    }
	
	public void setStepSound1Grass(Sound sound) {
		stepSound1Grass = sound;
	}

	/**
	 * Direction of movement.
	 * @return unit vector for x and y component. 
	 */
	public Vector3 getMovementDirection() {
		return movementDir;
	}
	
	/**
	 * Get the movement vector as the product of diretion and speed.
	 * @return 
	 */
	public Vector3 getMovement(){
		return movementDir.cpy().scl(speedHorizontal);
	}

	/**
	 * normalises x and y so ignores length.
	 * @param dir
	 */
	public void setMovementDir(Vector3 dir) {
	    double len = Math.sqrt(dir.x*dir.x+dir.y*dir.y);
		if (len != 0f && len != 1f) {
			dir.x /= len;
			dir.y /= len;
		}
		this.movementDir = dir;
	}
	
	/**
	 * Sets speed and direction.
	 * @param movement containing direction and speed.
	 */
	public void setMovement(Vector3 movement){
		double len = Math.sqrt(movement.x*movement.x+movement.y*movement.y);
		if (len != 0f && len != 1f) {
			movement.x /= len;
			movement.y /= len;
		}
		this.movementDir = movement;
		speedHorizontal = (float) len;
	}
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed.
	 */
	public void addMovement(Vector3 movement){
		setMovement(getMovement().add(movement));
	}
	

	public float getSpeed() {
		return speedHorizontal;
	}

	/**
	 * 
	 * @param speed
	 */
	public void setSpeed(float speed) {
		this.speedHorizontal = speed;
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
    public boolean isOnGround() {
        if (getPosition().getZ()> 0){
                getPosition().setZ(getPosition().getZ()-1);
                
                boolean colission = getPosition().getBlock().isObstacle() || horizontalColission(getPosition());
                getPosition().setZ(getPosition().getZ()+1);
                
                //if standing on ground on own or neighbour block then true
                return (super.isOnGround() || colission);
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
     * called when gets damage
     * @param value
     */
    public void damage(int value) {
		if (!indestructible) {
			if (getHealth() >0){
				if (damageSounds != null && soundlimit<=0) {
					damageSounds[(int) (Math.random()*(damageSounds.length-1))].play(0.7f);
					soundlimit = 100;
				}
				setHealth(getHealth()-value);
			} else
				setHealth(0);
		}
    }
	
	/**
	 * heals the entity
	 * @param value 
	 */
	public void heal(float value) {
		if (getHealth()<1000)
			setHealth(getHealth()+value);
	}

	public boolean isIndestructible() {
		return indestructible;
	}

	public void setIndestructible(boolean indestructible) {
		this.indestructible = indestructible;
	}
	
    /**
     *
     * @return from maximum 1000
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

	public int getFriction() {
		return friction;
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
		if (shadow != null)
			shadow.setPosition(pos.cpy());
		super.setPosition(pos);
	}
	
	/**
	 * called when in contact with floor or wall. Should be overriden.
	 */
	public void onCollide() {
	}
	
   @Override
    public void dispose(){
        super.dispose();
        shadow.dispose();
        if (fallingSound!= null) fallingSound.dispose();
        if (jumpingSound!= null) jumpingSound.dispose();
        if (runningSound!= null) runningSound.dispose();
    }

	@Override
	public MovableEntity clone() throws CloneNotSupportedException{
		return new MovableEntity(this);
	}

	public boolean isCollectable() {
		return collectable;
	}

	public void setCollectable(boolean collectable) {
		this.collectable = collectable;
	}
}
