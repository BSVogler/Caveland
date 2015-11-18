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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.Events;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameView;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_DIAGLENGTH2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *A clas used mainly for characters or object which can walk around. To control the character you should use a {@link Controllable} or modify the movemnet via {@link #setMovement(com.badlogic.gdx.math.Vector3) }.
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity implements Cloneable  {
	private static final long serialVersionUID = 4L;
	
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
	 * Direction and speed of movement.
	 */
	private Vector3 movement;
	/**
	 * saves the viewing direction even if the player is not moving. Should never be len()==0
	 */
	private Vector2 orientation;
	/**
	 * indicates whether this objects does collide with the blocks
	 */
	private boolean coliding;
	/**
	 * affected by gravity
	 */
	private boolean floating;
	
	private transient String stepSound1Grass;
	private transient boolean stepSoundPlayedInCiclePhase;
	private transient String fallingSound = "wind";
	private transient long fallingSoundInstance;
	private transient String runningSound;
	private transient boolean runningSoundPlaying;
	private transient String jumpingSound;

	/**
	 * currently in a liquid?
	 */
	private boolean inliquid;
       
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
	private boolean walkingPaused = false;

	/**
	 * Simple MovableEntity with no animation.
	 * @param id 
	 */
	public MovableEntity(final byte id) {
		this(id, 0, true);
	}
	
  /**
    * Constructor of MovableEntity.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
    */
	public MovableEntity(final byte id, final int spritesPerDir) {
		this(id, spritesPerDir, true);
	}
	
   /**
    * Constructor of MovableEntity.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction. if 0 then it only uses the value 0
	 * @param shadow
    */
   public MovableEntity(final byte id, final int spritesPerDir, boolean shadow) {
        super(id);
        this.spritesPerDir = spritesPerDir;
		movement = new Vector3(0,0,0);
        orientation = new Vector2(1, 0);
		coliding = true;
		floating = false;
		friction = WE.CVARS.getValueF("friction");
		if (shadow) enableShadow();
   }
   
   /**
	* copy constructor
	* @param entity 
	*/
	public MovableEntity(MovableEntity entity) {
		super(entity.getSpriteId());
		this.spritesPerDir = entity.spritesPerDir;
		movement = entity.movement;
		orientation = new Vector2(1, 0);
		friction = entity.friction;
		coliding = entity.coliding;
		floating = entity.floating;
		
		enableShadow();
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		MessageManager.getInstance().addListener(this, Events.landed.getId());
		return this;
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
	public void setContinuousWalkingAnimation(float walkOnTheSpot) {
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
		final Vector2 horMov = getMovementHor();
		setMovement(new Vector3(horMov.x, horMov.y, velo));
		if (playSound && jumpingSound != null)
			WE.SOUND.play(jumpingSound, getPosition());
    }
	
    /**
     * Defines the direction of the gun - if no gun available - the direction of the head.
     * @return  If not overwriten returning orientation. copy save
     */
   public Vector3 getAiming(){
	   return new Vector3(getOrientation(),0);
   };

    
   /**
     * Updates the character. Applies gravitation.	
     * @param dt time since last update in ms
     */
	@Override
    public void update(float dt) {
		super.update(dt);
        
        /*Here comes the stuff where the character interacts with the environment*/
        if (hasPosition() && getPosition().isInMemoryAreaHorizontal()) {
			float t = dt*0.001f; //t = time in s
			/*HORIZONTAL MOVEMENT*/
			//calculate new position
			float[] dMove = new float[]{
				t * movement.x*GAME_EDGELENGTH,
				t * movement.y*GAME_EDGELENGTH,
				0
			};

			//if movement allowed => move
			if (coliding && collidesHorizontal(getPosition().cpy().addVector(dMove), colissionRadius) ) {                
				//stop
				setHorMovement(new Vector2());
				onCollide();
			}

			/*VERTICAL MOVEMENT*/
			float oldHeight = getPosition().getZ();
			//apply gravity
			if (!floating && !isOnGround()) {
				addMovement(
					new Vector3(0, 0, -WE.CVARS.getValueF("gravity") * t) //in m/s
				);
			}

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
					if (!hasPosition()) return;//object may be destroyed during colission
					if (!floating) {
						MessageManager.getInstance().dispatchMessage(this, Events.landed.getId());
						if (!hasPosition()) return;//object may be destroyed during colission
					}
					movement.z = 0;

					//set on top of block
					getPosition().setZ((int) (oldHeight / GAME_EDGELENGTH) * GAME_EDGELENGTH);
				}

				Block block = getPosition().getBlock();
				//if entering water
				if (!inliquid && block != null && block.isLiquid())
					if (waterSound != null) {
						WE.SOUND.play(waterSound);
					}

				if (block != null)
					inliquid = block.isLiquid();//save if in water
				else inliquid=false;

				if (!walkingPaused) {
					if(walkOnTheSpot > 0) {
						walkingCycle += dt*walkOnTheSpot;//multiply by factor to make the animation fit the movement speed
					} else {
						//walking cycle
						if (floating || isOnGround()) {
							walkingCycle += dt * getSpeed() * WE.CVARS.getValueF("walkingAnimationSpeedCorrection");//multiply by factor to make the animation fit the movement speed
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
					if (spritesPerDir > 0) {
						if (orientation.x < -Math.sin(Math.PI/3)){
							setSpriteValue((byte) 1);//west
						} else {
							if (orientation.x < - 0.5){
								//y
								if (orientation.y<0){
									setSpriteValue((byte) 2);//north-west
								} else {
									setSpriteValue((byte) 0);//south-east
								}
							} else {
								if (orientation.x <  0.5){
									//y
									if (orientation.y<0){
										setSpriteValue((byte) 3);//north
									}else{
										setSpriteValue((byte) 7);//south
									}
								}else {
									if (orientation.x < Math.sin(Math.PI/3)) {
										//y
										if (orientation.y < 0){
											setSpriteValue((byte) 4);//north-east
										} else{
											setSpriteValue((byte) 6);//sout-east
										}
									} else{
										setSpriteValue((byte)5);//east
									}
								}
							}
						}

						if (cycleAnimation){
							setSpriteValue((byte) (getSpriteValue()+(int) (walkingCycle/(1000/ (float) spritesPerDir))*8));
						} else {//bounce
							if (stepMode) { //some strange step order
								switch (spritesPerDir) {
									case 2:
										if (walkingCycle >500)
											setSpriteValue((byte) (getSpriteValue()+8));
										break;
									case 3:
										if (walkingCycle >750)
											setSpriteValue((byte) (getSpriteValue()+16));
										else
											if (walkingCycle >250 && walkingCycle <500)
												setSpriteValue((byte) (getSpriteValue()+8));
										break;
									case 4:
										if (walkingCycle >=166 && walkingCycle <333)
											setSpriteValue((byte) (getSpriteValue()+8));
										else {
											if ((walkingCycle >=500 && walkingCycle <666)
												||
												(walkingCycle >=833 && walkingCycle <1000)
												){
												setSpriteValue((byte) (getSpriteValue()+16));
											} else if (walkingCycle >=666 && walkingCycle < 833) {
												setSpriteValue((byte) (getSpriteValue()+24));
											}
										}	break;
									default:
								}
							} else {
								//regular bounce
								if (walkingCycle < 500) {//forth
									setSpriteValue((byte) (getSpriteValue() + (int) ((walkingCycle+500/(float) (spritesPerDir+spritesPerDir/2))*spritesPerDir / 1000f)*8));
								} else {//back
									setSpriteValue((byte) (getSpriteValue() + (int) (spritesPerDir-(walkingCycle-500+500/(float) (spritesPerDir+spritesPerDir/2))*spritesPerDir / 1000f)*8));

								}
							}
						}
					}
				}
			}

            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (getSpeed() < 0.5f) {
                    WE.SOUND.stop(runningSound);
                    runningSoundPlaying = false;
                } else {
                    if (!runningSoundPlaying){
                        WE.SOUND.play(runningSound);
                        runningSoundPlaying = true;
                    }
                }
            }

            //should the fallingsound be played?
            if (fallingSound != null) {
                if (!floating && getMovement().z < 0 && movement.len2() > 0.0f) {
                    if (fallingSoundInstance == 0) {
                        fallingSoundInstance = WE.SOUND.loop(fallingSound);
                    }
					WE.SOUND.setVolume(fallingSound,fallingSoundInstance, getSpeed()/10f);
                } else {
                    WE.SOUND.stop(fallingSound);
                    fallingSoundInstance = 0;
                }
            }
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
				xPos-Block.VIEW_WIDTH2,
				yPos+Block.VIEW_HEIGHT,
				getHealth()*Block.VIEW_WIDTH/1000,
				5
			);
			sh.end();
		}
		super.render(view, xPos, yPos);
	}
    
	
	
	/**
	 * check for horizontal colission (x and y)<br>
	 * O(1)
	 *
	 * @param pos the new position
	 * @param colissionRadius
	 * @return true if colliding horizontal
	 */
	public boolean collidesHorizontal(final Point pos, final float colissionRadius) {
		Point checkPoint = pos.cpy(); 
		//check for movement in y
		//top corner
		Block block = checkPoint.addVector(0, -colissionRadius, 0).getBlock();
		if (block != null && block.isObstacle()) {
			return true;
		}
		//bottom corner
		block = checkPoint.addVector(0, 2*colissionRadius, 0).getBlock();
		if (block != null && block.isObstacle()) {
			return true;
		}

        //check X
		//left
		block = checkPoint.addVector(-colissionRadius, -colissionRadius, 0).getBlock();
		if (block != null && block.isObstacle()) {
			return true;
		}
		//bottom corner
		block = checkPoint.addVector(2*colissionRadius, 0, 0).getBlock();
		return block != null && block.isObstacle();
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
	 * Get the movement vector as the product of direction and speed.
	 * @return in m/s. copy safe
	 */
	public Vector3 getMovement(){
		return movement.cpy();
	}
	
	/**
	 *Get the movement vector as the product of direction and speed.
	 * @return in m/s. copy safe
	 */
	public Vector2 getMovementHor(){
		return new Vector2(movement.x, movement.y);
	}

	/**
	 * Sets speed and direction combined in one vector.
	 * @param movement containing direction and speed (length) in m/s.
	 */
	public void setMovement(Vector2 movement){
		this.movement.x = movement.x;
		this.movement.y = movement.y;
		updateOrientation();
	}
	
	/**
	 * Sets speed and direction.
	 * @param movement containing direction and speed in m/s without the unit e.g. for 5m/s use just <i>5</i> and not <i>5*{@link Block#GAME_EDGELENGTH}</i>.
	 */
	public void setMovement(Vector3 movement){
		this.movement = movement;
		updateOrientation();
	}
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s without the unit e.g. for 5m/s use just <i>5</i> and not <i>5*{@link Block#GAME_EDGELENGTH}</i>.
	 */
	public void addMovement(Vector2 movement){
		this.movement.x += movement.x;
		this.movement.y += movement.y;
		updateOrientation();
	}
	
	
	/**
	 * Adds speed and direction.
	 * @param movement containing direction and speed in m/s without the unit e.g. for 5m/s use just <i>5</i> and not <i>5*{@link Block#GAME_EDGELENGTH}</i>.
	 */
	public void addMovement(Vector3 movement){
		this.movement.add(movement);
		updateOrientation();
	}
	
	/**
	 * Adds speed to horizontal moving directio.
	 *
	 * @param speed speed in m/s without the unit so no
	 * "5*{@link #GAME_EDGELENGTH}" for 5 m/s but just "5".
	 */
	public void addToHor(float speed) {
		this.movement.x += orientation.x * speed;
		this.movement.y += orientation.y * speed;
		updateOrientation();
	}

	/**
	 * Set the horizontal movement and keeps z
	 *
	 * @param movement
	 */
	public void setHorMovement(Vector2 movement) {
		this.movement.x = movement.x;
		this.movement.y = movement.y;
		updateOrientation();
	}
	
	/**
	 * Set the speed and only take x and y into account.
	 * @param speed in m/s
	 */
	public void setSpeedHorizontal(float speed) {
		this.movement.x = orientation.x * speed;
		this.movement.y = orientation.y * speed;
	}
	
	/**
	 * Set the speed. Uses x, y and z.
	 *
	 * @param speed in m/s
	 */
	public void setSpeedIncludingZ(float speed) {
		movement = movement.nor().scl(speed);
	}

	/**
	 * get the horizontal speed of the object in m/s.
	 *
	 * @return
	 */
	public float getSpeedHor() {
		return (float) Math.sqrt(movement.x * movement.x + movement.y * movement.y);
	}

	/**
	 * get the speed of the object in m/s.
	 *
	 * @return
	 */
	public float getSpeed() {
		return (float) Math.sqrt(movement.x * movement.x + movement.y * movement.y + movement.z * movement.z);
	}

	/**
	 * Turns an object in a different direction. Keeps the momentum.
	 *
	 * @param orientation the new orientation. Must be normalized.
	 */
	public void setOrientation(final Vector2 orientation) {
		this.orientation = orientation.cpy();
		float speedhor = getSpeedHor();
		this.movement.x = this.orientation.x * speedhor;
		this.movement.y = this.orientation.y * speedhor;
	}

	/**
	 * updates the orientation vector
	 */
	private void updateOrientation() {
		if (getMovementHor().len2() != 0) {//only update if there is new information, else keep it
			orientation = getMovementHor().nor();
		}
	}

	/**
	 *indicates whether this objects does collide with the blocks
	 * @return
	 */
	public boolean isColiding() {
		return coliding;
	}

	/**
	 * indicates whether this objects does collide with the blocks
	 * @param coliding true if collides with environment
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
		if (getPosition() == null) {
			return false;
		}
		if (getPosition().getZ() > 0) {
			if (getPosition().getZ() > Chunk.getGameHeight()) {
				return false;
			}
			getPosition().setZ(getPosition().getZ() - 1);//move one down for check

			Block block = getPosition().getBlock();
			boolean colission = (block != null && block.isObstacle()) || collidesHorizontal(getPosition(), colissionRadius);
			getPosition().setZ(getPosition().getZ() + 1);//reverse

			//if standing on ground on own or neighbour block then true
			return (super.isOnGround() || colission);
		}
		return true;
    }

    /**
     * Is the character standing in a liquid?
     * @return 
     */
    public boolean isInLiquid() {
        return inliquid;
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
	
	@Override
	public MovableEntity clone() throws CloneNotSupportedException{
		return new MovableEntity(this);
	}

	/**
	 * performs a step. Plays a sound.
	 */
	public void step() {
		WE.SOUND.play(
			stepSound1Grass,
			0.3f,
			(float) (0.9f+Math.random()/5f),
			(float) (Math.random()-1/2f)
		);
		stepSoundPlayedInCiclePhase = true;
	}
	
	/**
	 * Pauses the movement animation. A use case is when you want to play a different animation then walking while the object may still move.
	 * @see #playMovementAnimation() 
	 */
	public void pauseMovementAnimation(){
		walkingPaused = true;
	}
	
	/**
	 * Continues the movement animation when it was stopped before with {@link #pauseMovementAnimation() }.
	 */
	public void playMovementAnimation(){
		walkingPaused = false;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Events.landed.getId() && msg.sender == this) {
			WE.SOUND.play("landing", getPosition());//play landing sound
			step();
			return true;
		}
		
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		MessageManager.getInstance().removeListener(this, Events.deselectInEditor.getId());
		MessageManager.getInstance().removeListener(this, Events.selectInEditor.getId());
	}
	
	
}
