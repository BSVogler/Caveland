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
package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import static com.bombinggames.wurfelengine.core.map.rendering.RenderCell.GAME_EDGELENGTH;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;

/**
 *A clas used mainly for characters or object which can walk around. To control the character you should use a {@link Controllable} or modify the movemnet via {@link #setMovement(com.badlogic.gdx.math.Vector3) }.
 * @author Benedikt
 */
public class MovableEntity extends AbstractEntity  {
	private static final long serialVersionUID = 4L;
	
	private transient static String waterSound = "splash";
     	
	   /**
     * Set the value of waterSound
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(String waterSound) {
        MovableEntity.waterSound = waterSound;
    }
	
	private final int spritesPerDir;
      
   /** Set value how fast the character brakes or slides. The higher the value, the more "slide". Can cause problems with running sound. Value &gt;1. If =0 friciton is disabled**/
	private float friction = 0;
      
	/**
	 * Direction and speed of movement.
	 */
	private final Vector3 movement;
	/**
	 * saves the viewing direction even if the player is not moving. Should never be len()==0
	 */
	private Vector2 orientation = new Vector2(1, 0);
	/**
	 * indicates whether this objects does collide with the blocks
	 */
	private boolean collider = true;
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
	private boolean inLiquid;
       
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
	
	private transient MoveToAi moveToAi;
	private transient AimBand particleBand;

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
		floating = false;
		friction = WE.getCVars().getValueF("friction");
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
		friction = entity.friction;
		collider = entity.collider;
		floating = entity.floating;
		
		enableShadow();
	}

	@Override
	public AbstractEntity spawn(Point point) {
//		if (!hasPosition())
//			MessageManager.getInstance().addListener(this, Events.landed.getId());
		super.spawn(point);
		return this;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		MessageManager.getInstance().removeListener(this,
			Events.deselectInEditor.getId(),
			Events.selectInEditor.getId(),
			Events.landed.getId()
		);
	}

	/**
	 * <b>Bounce</b> back and forth (1,2,3,2,1,2 etc.) or <b>cycle</b>
	 * (1,2,3,1,2,3 etc.)
	 *
	 * @param cycle true if <b>cycle</b>, false if <b>bounce</b>
	 */
	public void setWalkingAnimationCycling(boolean cycle) {
		cycleAnimation = cycle;
	}
	
	/**
	 * Enable this to have a walking cycle even if not moving
	 *
	 * @param walkOnTheSpot the speed of the animation. Faktor without unit ~1. To disable pass 0.
	 */
	public void setContinuousWalkingAnimation(float walkOnTheSpot) {
		this.walkOnTheSpot = walkOnTheSpot;
	}

	/**
	 * Set step mode or disable step mode. if no step mode plays animation back
	 * and forth. If step mode then some strange pattern which looks god for
	 * walking animations.
	 *
	 * @param stepmode stepmode = true,
	 */
	public void setWalkingStepMode(boolean stepmode) {
		this.stepMode = stepmode;
	}
	
	
 /**
	 * This method should define what happens when the object jumps. It should
	 * call super.jump(int velo)
	 *
	 * @see #jump(float, boolean)
	 */
	public void jump() {
		//jump(0);
	}
   
	/**
	 * Jump with a specific speed. Can jump if in air.
	 *
	 * @param velo the velocity in m/s
	 * @param playSound plays jump sound if <i>true</i>
	 */
	public void jump(float velo, boolean playSound) {
		final Vector2 horMov = getMovementHor();
		setMovement(new Vector3(horMov.x, horMov.y, velo));
		if (playSound && jumpingSound != null) {
			WE.SOUND.play(jumpingSound, getPosition());
		}
	}

	/**
	 * Defines the direction of the gun - if no gun available - the direction of
	 * the head.
	 *
	 * @return If not overwriten returning orientation. copy save
	 */
	public Vector3 getAiming() {
		return new Vector3(getOrientation(), 0);
	}

   /**
	 * Updates the character. Applies gravitation.
	 *
	 * @param dt time since last update in ms
	 */
	@Override
	public void update(float dt) {
		super.update(dt);

		/*Here comes the stuff where the character interacts with the environment*/
		if (hasPosition()) {
			float t = dt * 0.001f; //t = time in s
			Vector3 movement = this.movement;
			
			if (moveToAi != null) {
				moveToAi.update(dt);
				if (moveToAi.atGoal()){
					moveToAi = null;
				}
			}
			
			if (particleBand != null) {
				particleBand.update();
			}
			
			/*HORIZONTAL MOVEMENT*/
			//calculate new position
			Vector3 dMove = new Vector3(
				t * movement.x * GAME_EDGELENGTH,
				t * movement.y * GAME_EDGELENGTH,
				0
			);

			Point newPos = getPosition().cpy().add(dMove);
			//check if movement to new position is okay
			if (collider && collidesWithWorld(newPos, colissionRadius)) {
				//stop
				setHorMovement(new Vector2());
				MessageManager.getInstance().dispatchMessage(this, Events.collided.getId());
			}

			/*VERTICAL MOVEMENT*/
			float oldHeight = getPosition().getZ();
			//apply gravity
			if (!floating && !isOnGround()) {
				addMovement(
					0, 0, -WE.getCVars().getValueF("gravity") * t //in m/s
				);
			}
			
			newPos = newPos.set(getPosition()).add(movement.cpy().scl(GAME_EDGELENGTH * t));
			
			if (collider && movement.z > 0 && isOnCeil(newPos)) {
				movement.z = 0;
				newPos = newPos.set(getPosition()).add(movement.cpy().scl(GAME_EDGELENGTH * t));
			}
			
			//check collision with other entities
			checkEntColl();
			
			//apply movement
			getPosition().set(newPos);

			//save orientation
			updateOrientation();

			//movement has applied, now maybe outside memory area 
			//check new height for colission            
			//land if standing in or under 0-level or there is an obstacle
			if (movement.z < 0 && isOnGround()) {

				//stop movement
				if (collider) {
					movement.z = 0;
				}

				//set on ground level of block
				//send event
				MessageManager.getInstance().dispatchMessage(this, Events.collided.getId());
				if (!hasPosition()) {
					return;//object may be destroyed during colission event
				}

				if (!floating) {
					MessageManager.getInstance().dispatchMessage(this, Events.landed.getId());
					if (!hasPosition()) {
						return;//object may be destroyed during colission
					}
				}
				if (collider) {
					getPosition().setZ((int) (oldHeight / GAME_EDGELENGTH) * GAME_EDGELENGTH);
				}
			}
			
			//if entering water
			if (!inLiquid && RenderCell.isLiquid(getPosition().getBlockId()) && getMass() > 1f) {
				if (waterSound != null) {
					WE.SOUND.play(waterSound, getPosition(), getMass() > 5 ? 1 : 0.5f);
				}
				inLiquid = RenderCell.isLiquid(getPosition().getBlockId());//save if in water
			} else {
				inLiquid = false;
			}

			if (!walkingPaused) {
				//walking cycle
				if (walkOnTheSpot > 0) {
					walkingCycle += dt * walkOnTheSpot;//multiply by factor to make the animation fit the movement speed
				} else if (floating || isOnGround()) {
					walkingCycle += dt * getSpeed() * WE.getCVars().getValueF("walkingAnimationSpeedCorrection");//multiply by factor to make the animation fit the movement speed
				}

				if (walkingCycle >= 1000) {
					walkingCycle %= 1000;
					stepSoundPlayedInCiclePhase = false;//reset variable
				}

				//make a step
				if (floating || isOnGround()) {
					//play sound twice a cicle
					if (walkingCycle < 250) {
						if (stepSound1Grass != null && !stepSoundPlayedInCiclePhase && isOnGround()) {
							step();
						}
					} else if (walkingCycle < 500) {
						stepSoundPlayedInCiclePhase = false;
					} else if (walkingCycle > 500) {
						if (stepSound1Grass != null && !stepSoundPlayedInCiclePhase && isOnGround()) {
							step();
						}
					}
				}

				//slow walking down
				if (isOnGround()) {
					//stop at a threshold
					if (movement.x * movement.x + movement.y * movement.y > 0.1f) {
						setHorMovement(getMovementHor().scl(1f / (dt * friction + 1f)));//with this formula this fraction is always <1
					} else {
						setHorMovement(new Vector2());
					}
				}

				updateSprite();
			}

            /* SOUNDS */
            //should the runningsound be played?
            if (runningSound != null) {
                if (getSpeed() < 0.5f) {
                    WE.SOUND.stop(runningSound);
                    runningSoundPlaying = false;
                } else {
                    if (!runningSoundPlaying){
                        WE.SOUND.play(runningSound, getPosition());
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
	
	/**
	 *
	 */
	public void updateSprite(){
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

	@Override
	public void render(GameView view, int xPos, int yPos) {
		if (view.debugRendering()) {
			ShapeRenderer sh = view.getShapeRenderer();
			sh.begin(ShapeRenderer.ShapeType.Filled);
			sh.setColor(Color.GREEN);
			//life bar
			sh.rect(xPos - RenderCell.VIEW_WIDTH2,
				yPos + RenderCell.VIEW_HEIGHT,
				getHealth() * RenderCell.VIEW_WIDTH / 1000,
				5
			);
			sh.end();
		}
		super.render(view, xPos, yPos);
	}

	/**
	 * O(1)
	 *
	 * @param pos not modified
	 * @return true if colliding
	 */
	private boolean checkCollisionCorners(final Point pos) {
		int colRad = colissionRadius;
		int block = (byte) pos.add(0, -colRad, 0).getBlock();
		//back corner top
		if (RenderCell.isObstacle(block)) {
			pos.add(0, colRad, 0);
			return true;
		}
		//front corner
		block = pos.add(0, 2 * colRad, 0).getBlock();
		if (RenderCell.isObstacle(block)) {
			pos.add(0, -colRad, 0);
			return true;
		}

		//check X
		//left
		block = pos.add(-colRad, -colRad, 0).getBlock();
		if (RenderCell.isObstacle(block)) {
			pos.add(colRad, 0, 0);
			return true;
		}
		//bottom corner
		block = pos.add(2 * colRad, 0, 0).getBlock();
		pos.add(-colRad, 0, 0);
		return RenderCell.isObstacle(block);
	}
	
	/**
	 * check for horizontal colission (x and y)<br>
	 * O(1)
	 *
	 * @param pos the new position, not modified
	 * @param colissionRadius
	 * @return true if colliding horizontal
	 */
	public boolean collidesWithWorld(final Point pos, final float colissionRadius) {
		if (checkCollisionCorners(pos)) {
			return true;
		}

		float heightBefore = pos.z;
		int dimZ = getDimensionZ();
		//chek in the middle if bigger then a block
		if (dimZ > RenderCell.GAME_EDGELENGTH) {
			pos.add(0, 0, dimZ / 2);
			if (checkCollisionCorners(pos)) {
				pos.add(0, 0, -dimZ / 2);
				return true;
			}
			pos.add(0, 0, dimZ / 2);
		} else {
			pos.add(0, 0, dimZ);
		}
		pos.z = heightBefore;
		//check top
		return checkCollisionCorners(pos);
	}
    
	/**
	 * Check if the top is coliding with a block. O(1)
	 *
	 * @param pos the position to check
	 * @return
	 */
	public boolean isOnCeil(final Point pos) {
		if (pos == null || pos.getZ() <= 0 || pos.getZ() > Chunk.getGameHeight()) {
			return false;
		}
		pos.z += getDimensionZ();
		boolean result = checkCollisionCorners(pos);
		pos.z -= getDimensionZ();
		return result;
	}
	
   /**
	 * Sets the sound to be played when falling.
	 *
	 * @param fallingSound
	 */
	public void setFallingSound(String fallingSound) {
		this.fallingSound = fallingSound;
	}

	/**
	 * Set the sound to be played when running.
	 *
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
	public final Vector2 getOrientation() {
		return orientation.cpy();
	}
	
	/**
	 * Get the movement vector as the product of direction and speed.
	 * @return in m/s. copy safe
	 */
	public final Vector3 getMovement(){
		return movement.cpy();
	}
	
	/**
	 *Get the movement vector as the product of direction and speed.
	 * @return in m/s. copy safe
	 */
	public final Vector2 getMovementHor(){
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
	 * Sets speed and direction values.
	 * @param movement containing direction and speed in m/s without the unit e.g. for 5m/s use just <i>5</i> and not <i>5*{@link Block#GAME_EDGELENGTH}</i>.
	 */
	public void setMovement(Vector3 movement){
		this.movement.set(movement);
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
	 * Adds speed and direction.
	 * @param x
	 * @param y
	 * @param z
	 */
	public void addMovement(float x, float y, float z){
		this.movement.add(x,y,z);
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
		movement.nor().scl(speed);
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
		return collider;
	}

	/**
	 * indicates whether this objects does collide with the blocks
	 * @param coliding true if collides with environment
	 */
	public void setColiding(boolean coliding) {
		this.collider = coliding;
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
	 * @param floating if true then not affacted
	 */
	public void setFloating(boolean floating) {
		this.floating = floating;
	}
	
    @Override
    public boolean isOnGround() {
		Point pos = getPosition();
		if (pos == null) {
			return false;
		} else {
			if (pos.getZ() > 0) {
				if (pos.getZ() > Chunk.getGameHeight()) {
					return false;
				}
				pos.setZ(pos.getZ() - 1);//move one down for check

				boolean colission = pos.isObstacle() || collidesWithWorld(pos, colissionRadius);
				pos.setZ(pos.getZ() + 1);//reverse

				return colission;
			} else {
				return true;
			}
		}
    }
	
    /**
     * Is the character standing in a liquid?
     * @return 
     */
    public boolean isInLiquid() {
        return inLiquid;
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

	/**
	 * Get the ai which moves the entity to a goal.
	 * @return can be null
	 */
	public MoveToAi getMovementAI() {
		return moveToAi;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Events.landed.getId() && msg.sender == this) {
			WE.SOUND.play("landing", getPosition());//play landing sound
			step();
			return true;
		}
		
		if (msg.message == Events.moveTo.getId() && msg.receiver == this) {
			moveToAi = new MoveToAi(this, (Point) msg.extraInfo);
			return true;
		}
		
		if (msg.message == Events.teleport.getId() && msg.receiver == this) {
			setPosition(((Point) msg.extraInfo).cpy());
			return true;
		}
		
		if (msg.message == Events.standStill.getId() && msg.receiver == this) {
			moveToAi = null;
			setSpeedHorizontal(0);
			return true;
		}
		
		if (msg.message == Events.deselectInEditor.getId()) {
			if (particleBand != null) {
				particleBand.dispose();
				particleBand = null;
			}
		} else if (msg.message == Events.selectInEditor.getId() && getMovementAI() != null) {
			if (particleBand == null) {
				particleBand = new AimBand(this, getMovementAI().getGoal());
			} else {
				particleBand.setTarget(getMovementAI().getGoal());
			}
		}
		
		return false;
	}

	/**
	 * checks the colissions with entities, O(n)
	 */
	private void checkEntColl() {
		ArrayList<MovableEntity> nearbyEnts = getCollidingEntities(MovableEntity.class);
		for (MovableEntity ent : nearbyEnts) {
			//if (this.collidesWith(ent))
			if (ent.isObstacle() && getMass() > 0.5f) {

				Vector3 colVec3 = getPosition().sub(ent.getPosition());
				Vector2 colVec2 = new Vector2(colVec3.x, colVec3.y);
				float d = colVec2.len();

				// minimum translation distance to push balls apart after intersecting
				Vector2 mtd = colVec2.scl(((colissionRadius + ent.colissionRadius) - d) / d);

				// impact speed
				float vn = getMovementHor().sub(ent.getMovementHor()).dot(mtd.nor());

				// sphere intersecting but moving away from each other already
				if (vn <= 0.0f) {
					// resolve intersection --
					// inverse mass quantities
					float im1 = 1 / getMass();
					float im2 = 1 / ent.getMass();
					// collision impulse
					Vector2 impulse = mtd.scl((-(1.0f + 90.1f) * vn) / (im1 + im2));

					//hack to prevent ultra fast speed
					if (impulse.len2() > 26){
						impulse.nor().scl(5);
					}

					// change in momentum
					addMovement(impulse.scl(im1));
					ent.addMovement(impulse.scl(-im2));

					MessageManager.getInstance().dispatchMessage(this, Events.collided.getId());
				}
			}
		}
	}
}
