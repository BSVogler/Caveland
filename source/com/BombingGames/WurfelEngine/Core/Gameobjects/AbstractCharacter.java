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

import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

/**
 *A character is an entity wich can walk around. To control the character you have to set the controls with "setControls(String controls)".
 * @author Benedikt
 */
public abstract class AbstractCharacter extends AbstractEntity {
   private static int soundlimit;//time to pass before new sound can be played
     
   private final int colissionRadius = GAME_DIAGLENGTH2/2;
   private final int spritesPerDir;
      
   private final Vector3 dir = new Vector3(1, 0, 0);

   /** Set value how fast the character brakes or slides. 1 is "immediately". The higher the value, the more "slide". Can cause problems with running sound. Value >1**/
   private final int smoothBreaks = 200;
      
   /**The walking/running speed of the character. provides a factor for the movement vector*/
   private float speed;
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
       
   private final EntityShadow shadow;
   
   private int walkingAnimationCounter;

   /**
    * Constructor of AbstractCharacter.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction
    * @param point  
    */
   protected AbstractCharacter(final int id, final int spritesPerDir, Point point) {
        super(id, point);
        this.spritesPerDir = spritesPerDir;
        shadow = (EntityShadow) new EntityShadow(point.cpy()).exist();
        waterSound =  WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg");
    }
   
   /**
     * This method should define what happens when the object  jumps. It should call super.jump(int velo)
     * @see com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter#jump(float)
     */
    public abstract void jump();
   
    /**
     * Defines the direction of the gun - if no gun available - the direction of the head.
     * @return normalized vector with three values
     */
   public abstract Vector3 getAiming();
    
   /**
     * Jump with a specific speed
     * @param velo the velocity in m/s
     */
    public void jump(float velo) {
        if (onGround()) {
            dir.z = velo;
            if (jumpingSound != null) jumpingSound.play();
        }
    }

    
   /**
     * Lets the player walk.
     * @param up move up?
     * @param down move down?
     * @param left move left?
     *  @param right move right?
     * @param walkingspeed the higher the speed the bigger the steps. Should be in m/s.
     */
    public void walk(boolean up, boolean down, boolean left, boolean right, float walkingspeed) {
        if (up || down || left || right){
            speed = walkingspeed;

            //update the movement vector
            dir.x = 0;
            dir.y = 0;

            if (up)    dir.y = -1;
            if (down)  dir.y = 1;
            if (left)  dir.x = -1;
            if (right) dir.x = 1;
        }
   }
    
   /**
     * Updates the charackter.
     * @param delta time since last update
     */
    @Override
    public void update(float delta) {
        //clamp health & mana
        if (mana > 1000) mana = 1000;
        if (health > 1000) health = 1000;
        if (mana < 0) mana = 0;
        if (health < 0) health = 0;
        
        /*Here comes the stuff where the character interacts with the environment*/
        if (getPos().onLoadedMap()) {
            //normalyze only x and y
            double vectorLenght = Math.sqrt(dir.x*dir.x + dir.y*dir.y);
            if (vectorLenght > 0){
                dir.x /= vectorLenght;
                dir.y /= vectorLenght;
            }

            float oldHeight = getPos().getHeight();

            /*VERTICAL MOVEMENT*/
            float t = delta/1000f; //t = time in s
            if (!onGround()) dir.z -= WE.getCurrentConfig().getGravity()*t; //in m/s
            getPos().setHeight(getPos().getHeight() + dir.z * GAME_EDGELENGTH * t); //in m
            
            
            //check new height for colission            
            //land if standing in or under 0-level or there is an obstacle
            if (dir.z < 0 && onGround()){
                if (landingSound != null)
                    landingSound.play();//play landing sound
                if (fallingSound != null)
                    fallingSound.stop();//stop falling sound
                dir.z = 0;
                
                //set on top of block
                getPos().setHeight((int)(oldHeight/GAME_EDGELENGTH)*GAME_EDGELENGTH);
            }
            
            if (!inliquid && getPos().getBlockClamp().isLiquid())//if enterin water
                waterSound.play();
            
            inliquid = getPos().getBlockClamp().isLiquid();//save if in water


            /*HORIZONTAL MOVEMENT*/
            //calculate new position
            float[] dMove = new float[]{
                delta * speed * dir.x,
                delta * speed * dir.y,
                0
            };

                //if movement allowed => move player
            if (! horizontalColission(getPos().cpy().addVector(dMove)) ) {                
                    getPos().addVector(dMove);
                }

            //graphic
            if (dir.x < -Math.sin(Math.PI/3)){
                setValue(1);//west
            } else {
                if (dir.x < - 0.5){
                    //y
                    if (dir.y<0){
                        setValue(2);//north-west
                    } else {
                        setValue(0);//south-east
                    }
                } else {
                    if (dir.x <  0.5){
                        //y
                        if (dir.y<0){
                            setValue(3);//north
                        }else{
                            setValue(7);//south
                        }
                    }else {
                        if (dir.x < Math.sin(Math.PI/3)) {
                            //y
                            if (dir.y < 0){
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

            //uncomment this line to see where to player stands:
            //Controller.getMapDataSafe(getRelCoords()[0], getRelCoords()[1], getRelCoords()[2]-1).setLightlevel(30);

            shadow.update(delta, this);

            //slow walking down
            if (speed > 0) speed -= delta/ smoothBreaks;
            if (speed < 0) speed = 0;
            
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
                if (dir.z < -1) {
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
     * @param newx the new x position
     * @param newy the new y position
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
     * Returns a  vector wich contains the movement directions.
     * @return normalized vector
     */
    public Vector3 getDirectionVector(){
        return dir.nor();
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
        AbstractCharacter.waterSound = waterSound;
    }
    
    /**
     *
     * @param sound
     */
    public void setDamageSounds(Sound[] sound){
        damageSounds = sound;
    }
       
    /**
     * Adds horizontal colission check to onGround().
     * @return 
     */
    @Override
    public boolean onGround() {
        if (getPos().getHeight() > 0){
                getPos().setHeight(getPos().getHeight()-1);
                
                boolean colission = getPos().getBlockClamp().isObstacle() || horizontalColission(getPos());
                getPos().setHeight(getPos().getHeight()+1);
                
                //if standing on ground on own or neighbour block then true
                return (super.onGround() || colission);
        } return true;
    }

   @Override
   public AbstractCharacter exist() {
       super.exist();
       // shadow.exist();
       return this;
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
