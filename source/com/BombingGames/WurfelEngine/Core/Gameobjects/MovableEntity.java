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
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 *A character is an entity wich can walk around. To control the character you should use a {@link Controllable} or modify the movemnet via {@link #setMovement(com.badlogic.gdx.math.Vector3) }.
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity implements Cloneable  {
	private static final long serialVersionUID = 4L;
	
	/**
	 * time to pass before new sound can be played
	 */
	private static float soundlimit;
	private transient static Sound waterSound;
     	
	   /**
     * Set the value of waterSound
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(Sound waterSound) {
        MovableEntity.waterSound = waterSound;
    }
	
	private final int colissionRadius = GAME_DIAGLENGTH2/2;
	private final int spritesPerDir;
      
   /** Set value how fast the character brakes or slides. The higher the value, the more "slide". Can cause problems with running sound. Value >1. If =0 friciton is disabled**/
	private float friction = 0;
      
	/**
	 * Direction of movement.
	 */
	private Vector3 movement;
	/**
	 * saves the viewing direction even if the player is not moving. Should never be len()==0
	 */
	private Vector2 orientation;
	private boolean coliding;
	/**
	 * affected by gractiy
	 */
	private boolean floating;
	
	private transient Sound stepSound1Grass;
	private transient boolean stepSoundPlayedInCiclePhase;
	private transient Sound fallingSound;
	private transient long fallingSoundPlaying;
	private transient Sound runningSound;
	private transient boolean runningSoundPlaying;
	private transient Sound jumpingSound;
	private transient Sound landingSound;
	private transient Sound[] damageSounds;


	private boolean inliquid;
	private int mana = 1000;
	private boolean indestructible = false;
       
	/**
	 * somehow coutns when the new animation step must be displayed. Value: [0, 1000]
	 */
	private int walkingCycle;
	private boolean collectable;
	private boolean cycleAnimation;

   /**
    * Constructor of AbstractCharacter.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
    */
   protected MovableEntity(final int id, final int spritesPerDir) {
        super(id);
        this.spritesPerDir = spritesPerDir;
		movement = new Vector3(0,0,0);
        orientation = new Vector2(1, 0);
		coliding = true;
		floating = false;
		friction = CVar.get("friction").getValuef();
        if (waterSound!=null) waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
		enableShadow();
   }
   
	protected MovableEntity(MovableEntity entity) {
		super(entity.getId());
		this.spritesPerDir = entity.spritesPerDir;
		movement = entity.movement;
		orientation = new Vector2(1, 0);
		friction = entity.friction;
        
		coliding = entity.coliding;
		floating = entity.floating;
		collectable = entity.collectable;
		if (waterSound!=null) waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
		enableShadow();
	}
	
	/**
	 * Bounce back and forth (1,2,3,2,1,2 etc.) or cycle (1,2,3,1,2,3 etc.)
	 * @param cycle true if cycle, false if bounce
	 */
	public void setWalkingAnimationCycling(boolean cycle){
		cycleAnimation = cycle;
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
		addMovement(new Vector3(0, 0, velo));
		if (playSound && jumpingSound != null) jumpingSound.play();
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
     * @param dt time since last update in ms
     */
    @Override
    public void update(float dt) {
        //clamp health & mana
        if (mana > 1000) mana = 1000;
        if (mana < 0) mana = 0;

        
        /*Here comes the stuff where the character interacts with the environment*/
        if (getPosition()!= null && getPosition().isInMemoryHorizontal()) {
			float t = dt/1000f; //t = time in s
			/*HORIZONTAL MOVEMENT*/
			//calculate new position
			float[] dMove = new float[]{
				t * movement.x*GAME_EDGELENGTH,
				t * movement.y*GAME_EDGELENGTH,
				0
			};

			//if movement allowed => move
			if (coliding && horizontalColission(getPosition().cpy().addVector(dMove)) ) {                
				//stop
				setHorMovement(new Vector2());
				onCollide();
			}

			/*VERTICAL MOVEMENT*/
			float oldHeight = getPosition().getZ();
			if (!floating && !isOnGround())
				addMovement(
					new Vector3(0, 0, -CVar.get("gravity").getValuef()*t) //in m/s
				);

			//add movement
			getPosition().addVector(getMovement().scl(GAME_EDGELENGTH*t));
			
			//save orientation
			if (getMovementHor().len2() != 0)//only update if there is new information, else keep it
				orientation = getMovementHor().nor();
			
			//check new height for colission            
			//land if standing in or under 0-level or there is an obstacle
			if (movement.z < 0 && isOnGround()){
				onCollide();
				
				if (landingSound != null)
					landingSound.play();//play landing sound
				movement.z = 0;

				//set on top of block
				getPosition().setZ((int)(oldHeight/GAME_EDGELENGTH)*GAME_EDGELENGTH);
			}

			if (!inliquid && getPosition().getBlock().isLiquid())//if enterin water
				if (waterSound!=null) waterSound.play();

			inliquid = getPosition().getBlock().isLiquid();//save if in water


			//walking cycle
			if (floating || isOnGround()) {
				walkingCycle += dt*getSpeed()*CVar.get("walkingAnimationSpeedCorrection").getValuef();//multiply by factor to make the animation fit the movement speed
				if (walkingCycle > 1000) {
					walkingCycle=0;
					stepSoundPlayedInCiclePhase=false;//reset variable
				}

				//play sound twice a cicle
				if (walkingCycle<250){
					if (stepSound1Grass!=null && ! stepSoundPlayedInCiclePhase && isOnGround()) {
						step();
					}
				} else if (walkingCycle < 500){
					stepSoundPlayedInCiclePhase=false;
				} else if (walkingCycle > 500){
					if (stepSound1Grass!=null && ! stepSoundPlayedInCiclePhase && isOnGround()) {
						step();
					}
				}
			}
			
			//slow walking down
			//stop at a limit
			if (getMovementHor().len() > 0.1f)
				setHorMovement(getMovementHor().scl(1f/(dt*friction+1f)));//with this formula this fraction is always <1
			else {
				setHorMovement(new Vector2());
			}
				
				
			/* update sprite*/
			if (spritesPerDir>0) {
				if (orientation.x < -Math.sin(Math.PI/3)){
					setValue(1);//west
				} else {
					if (orientation.x < - 0.5){
						//y
						if (orientation.y<0){
							setValue(2);//north-west
						} else {
							setValue(0);//south-east
						}
					} else {
						if (orientation.x <  0.5){
							//y
							if (orientation.y<0){
								setValue(3);//north
							}else{
								setValue(7);//south
							}
						}else {
							if (orientation.x < Math.sin(Math.PI/3)) {
								//y
								if (orientation.y < 0){
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

				if (cycleAnimation){
						setValue(getValue()+walkingCycle/(1000/spritesPerDir)*8);
				} else {
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
			}

            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (getSpeed() < 0.5f) {
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
                if (getMovement().z < 0 && movement.len2() > 0.0f) {
					fallingSound.setVolume(fallingSoundPlaying, getSpeed()/10f);
                    if (fallingSoundPlaying == 0){
                        fallingSoundPlaying = fallingSound.loop();
                    }
                }else {
                    fallingSound.stop();
                    fallingSoundPlaying = 0;
                }
            }
			
            if (soundlimit>0)soundlimit-=dt;
            
            if (getHealth()<=0 && !indestructible)
                dispose();
        }
    }

	@Override
	public void render(GameView view, int xPos, int yPos, float scale) {
		if (view.debugRendering()){
			ShapeRenderer sh = view.getShapeRenderer();
			sh.begin(ShapeRenderer.ShapeType.Filled);
			sh.setColor(Color.GREEN);
			//life bar
			sh.rect(
				xPos-Block.VIEW_WIDTH2,
				yPos+Block.VIEW_HEIGHT,
				getHealth()*Block.VIEW_WIDTH/1000,
				5
			);
			//mana bar
			sh.setColor(Color.BLUE);
			sh.rect(
				xPos-Block.VIEW_WIDTH2,
				yPos+Block.VIEW_HEIGHT-6,
				getMana()*Block.VIEW_WIDTH/1000,
				5
			);

			sh.end();
		}
		super.render(view, xPos, yPos, scale);
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
        if (pos.cpy().addVector(0, - colissionRadius, 0).getBlock().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(0, colissionRadius, 0).getBlock().isObstacle())
            colission = true;
        
        //check X
        //left
        if (pos.cpy().addVector(-colissionRadius, 0, 0).getBlock().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(colissionRadius, 0, 0).getBlock().isObstacle())
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
	 * Loads the default sound included with the engine.
	 */
	public void loadEngineFallingSound() {
		fallingSound = (
            (Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/wind.ogg")
        );
	}
	
	/**
	 * Loads the default sound included with the engine.
	 */
	public void loadEngineLandingSound() {
		landingSound = ((Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/landing.wav")
        );
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
	 * Direction of movement. Normalized.
	 * @return unit vector for x and y component. 
	 */
	public Vector2 getOrientation() {
		return orientation.cpy();
	}
	
	/**
	 * Get the movement vector as the product of diretion and speed.
	 * @return in m/s. copy safe
	 */
	public Vector3 getMovement(){
		return movement.cpy();
	}
	
	/**
	 * Get the movement vector.
	 * @return in m/s. copy safe
	 */
	public Vector2 getMovementHor(){
		return new Vector2(movement.x, movement.y);
	}

	/**
	 * Sets speed and direction.
	 * @param movement containing direction and speed.
	 */
	public void setMovement(Vector3 movement){
		this.movement = movement;
	}
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s.
	 */
	public void addMovement(Vector2 movement){
		this.movement.add(movement.x, movement.y, 0);
	}
	
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s.
	 */
	public void addMovement(Vector3 movement){
		this.movement.add(movement);
	}
	
	/**
	 * Adds speed to horizontal moving directio.
	 * @param speed containing direction and speed in m/s.
	 */
	public void addToHor(float speed){
		addMovement(orientation.cpy().scl(speed));//add in move direction
	}
	
	/**
	 * Set the horizontal movement and ignore z
	 * @param movement 
	 */
	public void setHorMovement(Vector2 movement){
		Vector3 tmp = getMovement();
		tmp.x = movement.x;
		tmp.y = movement.y;
		setMovement(tmp);
	}
	
	public float getSpeed() {
		return movement.len();
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
			if (getPosition().getZ()> Map.getGameHeight()) return false;
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

	public float getFriction() {
		return friction;
	}

	/**
	 * automatically slows speed down.
	 * @param friction The higher the value, the more "slide". If =0 friciton is disabled.
	 */
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	/**
	 * called when in contact with floor or wall. Should be overriden.
	 */
	public void onCollide() {
	}
	
   @Override
    public void dispose(){
        super.dispose();
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

	public void step() {
		stepSound1Grass.play(1, (float) (1+Math.random()/5f), (float) (Math.random()-1/2f));
		stepSoundPlayedInCiclePhase = true;
	}
}
