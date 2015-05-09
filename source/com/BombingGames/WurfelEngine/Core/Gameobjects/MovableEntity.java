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
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 *A clas used mainly for characters or object which can walk around. To control the character you should use a {@link Controllable} or modify the movemnet via {@link #setMovement(com.badlogic.gdx.math.Vector3) }.
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity implements Cloneable  {
	private static final long serialVersionUID = 4L;
	
	/**
	 * time in ms to pass before new sound can be played
	 */
	private transient static float soundTimeLimit;
	private transient static String waterSound = "splash";
     	
	   /**
     * Set the value of waterSound
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(String waterSound) {
        MovableEntity.waterSound = waterSound;
    }
	
	private final int colissionRadius = GAME_DIAGLENGTH2/2;
	private final int spritesPerDir;
      
   /** Set value how fast the character brakes or slides. The higher the value, the more "slide". Can cause problems with running sound. Value &gt;1. If =0 friciton is disabled**/
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
	
	private transient String stepSound1Grass;
	private transient boolean stepSoundPlayedInCiclePhase;
	private transient String fallingSound = "wind";
	private transient long fallingSoundInstance;
	private transient String runningSound;
	private transient boolean runningSoundPlaying;
	private transient String jumpingSound;
	private transient String landingSound = "landing";
	private transient String[] damageSounds;


	private boolean inliquid;
	private boolean indestructible = false;
       
	/**
	 * somehow coutns when the new animation step must be displayed. Value: [0, 1000]
	 */
	private int walkingCycle;
	private boolean cycleAnimation;
	/**
	 * factor which gets multiplied with the walking animation
	 */
	private float walkOnTheSpot = 0;
	private boolean stepMode = true;

	/**
	 * Simple MovableEntity with no animation.
	 * @param id 
	 */
	public MovableEntity(final byte id) {
		this(id, 0);
	}
	
   /**
    * Constructor of MovableEntity.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
    */
   public MovableEntity(final byte id, final int spritesPerDir) {
        super(id);
        this.spritesPerDir = spritesPerDir;
		movement = new Vector3(0,0,0);
        orientation = new Vector2(1, 0);
		coliding = true;
		floating = false;
		friction = WE.CVARS.getValueF("friction");
		enableShadow();
   }
   
   /**
	* copy constructor
	* @param entity 
	*/
	public MovableEntity(MovableEntity entity) {
		super(entity.getId());
		this.spritesPerDir = entity.spritesPerDir;
		movement = entity.movement;
		orientation = new Vector2(1, 0);
		friction = entity.friction;
        
		coliding = entity.coliding;
		floating = entity.floating;
		enableShadow();
	}
	
	/**
	 * <b>Bounce</b> back and forth (1,2,3,2,1,2 etc.) or <b>cycle</b> (1,2,3,1,2,3 etc.)
	 * @param cycle true if <b>cycle</b>, false if <b>bounce</b>
	 */
	public void setWalkingAnimationCycling(boolean cycle){
		cycleAnimation = cycle;
	}
	
	/**
	 * Enable this to have a walking cycle even if not moving
	 * @param walkOnTheSpot the speed of the animation: ~1. To disable pass 0.
	 */
	public void setWalkingSpeedIndependentAnimation(float walkOnTheSpot) {
		this.walkOnTheSpot = walkOnTheSpot;
	}

	/**
	 * Set step mode or disable step mode. if no step mode plays animation back and forth. If step mode then some strange pattern which looks god for walking animations.
	 * @param stepmode  stepmode = true, 
	 */
	public void setWalkingStepMode(boolean stepmode) {
		this.stepMode = stepmode;
	}
	
	
   /**
     * This method should define what happens when the object  jumps. It should call super.jump(int velo)
     * @see #jump(float, boolean)
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
		if (playSound && jumpingSound != null)
			Controller.getSoundEngine().play(jumpingSound, getPosition());
    }
	
    /**
     * Defines the direction of the gun - if no gun available - the direction of the head.
     * @return  If not overwriten returning orientation. copy save
     */
   public Vector3 getAiming(){
	   return new Vector3(getOrientation(),0);
   };

    
   /**
     * Updates the character.
     * @param dt time since last update in ms
     */
    @Override
    public void update(float dt) {
		super.update(dt);
        
        /*Here comes the stuff where the character interacts with the environment*/
        if (spawned() && getPosition()!= null && getPosition().isInMemoryAreaHorizontal()) {
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
					new Vector3(0, 0, -WE.CVARS.getValueF("gravity")*t) //in m/s
				);

			//add movement
			getPosition().addVector(getMovement().scl(GAME_EDGELENGTH*t));
			
			//save orientation
			updateOrientation();
			
			//movement has applied maybe outside memory area 
			if (getPosition().isInMemoryAreaHorizontal()) {
				//check new height for colission            
				//land if standing in or under 0-level or there is an obstacle
				if (movement.z < 0 && isOnGround()){
					onCollide();
					onLand();

					if (landingSound != null && !floating)
						Controller.getSoundEngine().play(landingSound, getPosition());//play landing sound

					movement.z = 0;

					//set on top of block
					getPosition().setZ((int)(oldHeight/GAME_EDGELENGTH)*GAME_EDGELENGTH);
				}

				CoreData block = getPosition().getBlock();
				if (!inliquid && block != null && block.isLiquid())//if enterin water
					if (waterSound!=null) Controller.getSoundEngine().play(waterSound);

				if (block != null)
					inliquid = block.isLiquid();//save if in water
				else inliquid=false;

				if(walkOnTheSpot > 0) {
					walkingCycle += dt*walkOnTheSpot;//multiply by factor to make the animation fit the movement speed
				} else { 
					//walking cycle
					if (floating || isOnGround()) {
						walkingCycle += dt*getSpeed()*WE.CVARS.getValueF("walkingAnimationSpeedCorrection");//multiply by factor to make the animation fit the movement speed
					}
				}

				if (walkingCycle >= 1000) {
					walkingCycle %= 1000;
					stepSoundPlayedInCiclePhase = false;//reset variable
				}

				//make a step
				if (floating || isOnGround()) {
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
				if (isOnGround()) {
					//stop at a threshold
					if (getMovementHor().len() > 0.1f)
						setHorMovement(getMovementHor().scl(1f/(dt*friction+1f)));//with this formula this fraction is always <1
					else {
						setHorMovement(new Vector2());
					}
				}


				/* update sprite*/
				if (spritesPerDir>0) {
					if (orientation.x < -Math.sin(Math.PI/3)){
						setValue((byte) 1);//west
					} else {
						if (orientation.x < - 0.5){
							//y
							if (orientation.y<0){
								setValue((byte) 2);//north-west
							} else {
								setValue((byte) 0);//south-east
							}
						} else {
							if (orientation.x <  0.5){
								//y
								if (orientation.y<0){
									setValue((byte) 3);//north
								}else{
									setValue((byte) 7);//south
								}
							}else {
								if (orientation.x < Math.sin(Math.PI/3)) {
									//y
									if (orientation.y < 0){
										setValue((byte) 4);//north-east
									} else{
										setValue((byte) 6);//sout-east
									}
								} else{
									setValue((byte)5);//east
								}
							}
						}
					}

					if (cycleAnimation){
						setValue((byte) (getValue()+(int) (walkingCycle/(1000/ (float) spritesPerDir))*8));
					} else {//bounce
						if (stepMode) {//some strange step order
							if (spritesPerDir==2){
								if (walkingCycle >500)
									setValue((byte) (getValue()+8));
							} else if (spritesPerDir==3){
								if (walkingCycle >750)
									setValue((byte) (getValue()+16));
								else
									if (walkingCycle >250 && walkingCycle <500)
										setValue((byte) (getValue()+8));
							} else if (spritesPerDir==4){
								if (walkingCycle >=166 && walkingCycle <333)
									setValue((byte) (getValue()+8));
								else {
									if ((walkingCycle >=500 && walkingCycle <666)
										||
										(walkingCycle >=833 && walkingCycle <1000)
									){
										setValue((byte) (getValue()+16));
									} else if (walkingCycle >=666 && walkingCycle < 833) {
										setValue((byte) (getValue()+24));
									}
								}
							}
						} else {
							//regular bounce
							if (walkingCycle < 500) {//forht
								setValue((byte) (getValue() + (int) ((walkingCycle+500/(float) (spritesPerDir+spritesPerDir/2))*spritesPerDir / 1000f)*8));
							} else {//back
								setValue((byte) (getValue() + (int) (spritesPerDir-(walkingCycle-500+500/(float) (spritesPerDir+spritesPerDir/2))*spritesPerDir / 1000f)*8));

							}
						}
					}
				}
			}

            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (getSpeed() < 0.5f) {
                    Controller.getSoundEngine().stop(runningSound);
                    runningSoundPlaying = false;
                } else {
                    if (!runningSoundPlaying){
                        Controller.getSoundEngine().play(runningSound);
                        runningSoundPlaying = true;
                    }
                }
            }

            //should the fallingsound be played?
            if (fallingSound != null) {
                if (!floating && getMovement().z < 0 && movement.len2() > 0.0f) {
                    if (fallingSoundInstance == 0) {
                        fallingSoundInstance = Controller.getSoundEngine().loop(fallingSound);
                    }
					Controller.getSoundEngine().setVolume(fallingSound,fallingSoundInstance, getSpeed()/10f);
                } else {
                    Controller.getSoundEngine().stop(fallingSound);
                    fallingSoundInstance = 0;
                }
            }
			
            if (soundTimeLimit > 0)
				soundTimeLimit -= dt;
            
            if (getHealth()<= 0 && !indestructible)
                dispose();
        }
    }

	@Override
	public void render(GameView view, int xPos, int yPos) {
		if (view.debugRendering()){
			ShapeRenderer sh = view.getShapeRenderer();
			sh.begin(ShapeRenderer.ShapeType.Filled);
			sh.setColor(Color.GREEN);
			//life bar
			sh.rect(
				xPos-RenderBlock.VIEW_WIDTH2,
				yPos+RenderBlock.VIEW_HEIGHT,
				getHealth()*RenderBlock.VIEW_WIDTH/1000,
				5
			);
			sh.end();
		}
		super.render(view, xPos, yPos);
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
		CoreData block = pos.cpy().addVector(0, - colissionRadius, 0).getBlock();
        if (block!=null && block.isObstacle())
            colission = true;
        //bottom corner
		block = pos.cpy().addVector(0, colissionRadius, 0).getBlock();
        if (block!=null && block.isObstacle())
            colission = true;
        
        //check X
        //left
		block = pos.cpy().addVector(-colissionRadius, 0, 0).getBlock();
        if (block!=null && block.isObstacle())
            colission = true;
        //bottom corner
		block = pos.cpy().addVector(colissionRadius, 0, 0).getBlock();
        if (block!=null && block.isObstacle())
            colission = true;
        
        return colission;
    }
    
    /**
     * Sets the sound to be played when falling.
     * @param fallingSound
     */
    public void setFallingSound(String fallingSound) {
        this.fallingSound = fallingSound;
    }
	
    /**
     * Set the sound to be played when running.
     * @param runningSound
     */
    public void setRunningSound(String runningSound) {
        this.runningSound = runningSound;
    }
    

    /**
     * Set the value of jumpingSound
     *
     * @param jumpingSound new value of jumpingSound
     */
    public void setJumpingSound(String jumpingSound) {
        this.jumpingSound = jumpingSound;
    }
    
        /**
     * Set sound played when the character lands on the feet.
     *
     * @param landingSound new value of landingSound
     */
    public void setLandingSound(String landingSound) {
        this.landingSound = landingSound;
    }

	/**
	 * 
	 * @return 
	 */
	public String getLandingSound() {
		return landingSound;
	}
	
    /**
     *
     * @param sound
     */
    public void setDamageSounds(String[] sound){
        damageSounds = sound;
    }
	
	/**
	 *
	 * @param sound
	 */
	public void setStepSound1Grass(String sound) {
		stepSound1Grass = sound;
	}

	/**
	 * Direction of movement. Normalized.
	 * @return unit vector for x and y component. copy safe
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
	 * Sets speed and direction combined in one vector.
	 * @param movement containing direction and speed (length).
	 */
	public void setMovement(Vector2 movement){
		this.movement = new Vector3(movement, this.movement.z);
		updateOrientation();
	}
	
	/**
	 * Sets speed and direction.
	 * @param movement containing direction and speed in m/s without the unit so no "5*{@link #GAME_EDGELENGTH}" for 5 m/s but just "5".
	 */
	public void setMovement(Vector3 movement){
		this.movement = movement;
		updateOrientation();
	}
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s without the unit so no "5*{@link #GAME_EDGELENGTH}" for 5 m/s but just "5".
	 */
	public void addMovement(Vector2 movement){
		this.movement.add(movement.x, movement.y, 0);
		updateOrientation();
	}
	
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s without the unit so no "5*{@link #GAME_EDGELENGTH}" for 5 m/s but just "5".
	 */
	public void addMovement(Vector3 movement){
		this.movement.add(movement);
		updateOrientation();
	}
	
	/**
	 * Adds speed to horizontal moving directio.
	 * @param speed speed in m/s without the unit so no "5*{@link #GAME_EDGELENGTH}" for 5 m/s but just "5".
	 */
	public void addToHor(float speed){
		addMovement(orientation.cpy().scl(speed));//add in move direction
	}
	
	/**
	 * Set the horizontal movement and keeps z
	 * @param movement 
	 */
	public void setHorMovement(Vector2 movement){
		Vector3 tmp = getMovement();
		tmp.x = movement.x;
		tmp.y = movement.y;
		setMovement(tmp);
	}
	
	/**
	 * Set the speed and only take x and y into account.
	 * @param speed
	 */
	public void setSpeedHorizontal(float speed) {
		setHorMovement(getOrientation().scl(speed));
	}
	
	/**
	 * Set the speed. Uses x, y and z.
	 * @param speed
	 */
	public void setSpeedIncludingZ(float speed) {
		movement = getMovement().nor().scl(speed);
	}
	
	/**
	 * get the gorizontalspeed of the object in m/s.
	 * @return 
	 */
	public float getSpeedHor() {
		return (float)Math.sqrt(movement.x * movement.x + movement.y * movement.y);
	}
	
	/**
	 * get the speed of the object in m/s.
	 * @return 
	 */
	public float getSpeed() {
		return movement.len();
	}
	
	/**
	 * Turns an object in a different direction. Keeps the momentum.
	 * @param orientation the new orientation
	 */
	public void setOrientation(Vector2 orientation){
		this.orientation = orientation;
		setMovement(orientation.cpy().scl(getSpeedHor()));
	}
	
	/**
	 * updates the orientation vector
	 */
	private void updateOrientation() {
		if (getMovementHor().len2() != 0)//only update if there is new information, else keep it
			orientation = getMovementHor().nor();
	}

	/**
	 *
	 * @return
	 */
	public boolean isColiding() {
		return coliding;
	}

	/**
	 *
	 * @param coliding
	 */
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
			if (getPosition().getZ() > getPosition().getMap().getGameHeight()) return false;
                getPosition().setZ(getPosition().getZ()-1);//move one down for check
                
				CoreData block = getPosition().getBlock();
                boolean colission =  (block != null && block.isObstacle()) || horizontalColission(getPosition());
                getPosition().setZ(getPosition().getZ()+1);//reverse
                
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

	/**
	 *
	 * @return
	 */
	public boolean isIndestructible() {
		return indestructible;
	}

	/**
	 *
	 * @param indestructible
	 */
	public void setIndestructible(boolean indestructible) {
		this.indestructible = indestructible;
	}
	
	/**
	 * The factor which slows donw movement.
	 * @return 
	 */
	public float getFriction() {
		return friction;
	}

	/**
	 * Automatically slows speed down.
	 * @param friction The higher the value, the less "slide". If =0 friciton is disabled. Value should be ~0.01f
	 */
	public void setFriction(float friction) {
		this.friction = friction;
	}
	
	/**
	 * called when in contact with floor or wall. Should be overriden.
	 */
	public void onCollide() {
	}
	
	/**
	 * called when objects lands
	 */
	public void onLand() {
	}
	
	@Override
	public MovableEntity clone() throws CloneNotSupportedException{
		return new MovableEntity(this);
	}

	/**
	 * performs a step. Plays a sound.
	 */
	public void step() {
		Controller.getSoundEngine().play(stepSound1Grass, 0.5f,(float) (0.9f+Math.random()/5f), (float) (Math.random()-1/2f));
		stepSoundPlayedInCiclePhase = true;
	}
}
