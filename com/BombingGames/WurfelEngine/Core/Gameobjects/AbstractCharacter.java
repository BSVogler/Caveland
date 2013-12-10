package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.audio.Sound;

/**
 *A character is an entity wich can walk around. To control the character you have to set the controls with "setControls(String controls)".
 * @author Benedikt
 */
public abstract class AbstractCharacter extends AbstractEntity {
   private final int COLISSIONRADIUS = GAME_DIAGSIZE/4;
   private final int SPRITESPERDIR;
      
   private final float[] dir = {1, 0, 0};
   private String controls = "NPC";

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


   private boolean inliquid;
       
   private final CharacterShadow shadow;
   
   private int walkingAnimationCounter;

   /**
    * Constructor of AbstractCharacter.
    * @param id
    * @param spritesPerDir The number of animation sprites per walking direction
    * @param point  
    */
   protected AbstractCharacter(final int id, final int spritesPerDir, Point point) {
        super(id);
        SPRITESPERDIR = spritesPerDir;
        shadow = (CharacterShadow) AbstractEntity.getInstance(42,0,point.cpy());
        shadow.exist();
        waterSound =  WEMain.getInstance().manager.get("com/BombingGames/WurfelEngine/Game/Sounds/splash.ogg");
    }
   
   /**
     * This method should define what happens when the object  jumps. It should call super.jump(int velo)
     * @see com.BombingGames.Game.Gameobjects.AbstractCharacter#jump(float)
     */
    public abstract void jump();
    
   /**
     * Jump with a specific speed
     * @param velo the velocity in m/s
     */
    public void jump(float velo) {
        if (onGround()) {
            dir[2] = velo;
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
            dir[0] = 0;
            dir[1] = 0;

            if (up)    dir[1] = -1;
            if (down)  dir[1] = 1;
            if (left)  dir[0] = -1;
            if (right) dir[0] = 1;
        }
   }
    
   /**
     * Updates the charackter.
     * @param delta time since last update
     */
    @Override
    public void update(float delta) {
        if (getPos().onLoadedMap()) {
            //scale that the velocity vector is always an unit vector (only x and y)
            double vectorLenght = Math.sqrt(dir[0]*dir[0] + dir[1]*dir[1]);
            if (vectorLenght > 0){
                dir[0] /= vectorLenght;
                dir[1] /= vectorLenght;
            }

            float oldHeight = getPos().getHeight();

            /*VERTICAL MOVEMENT*/
            float t = delta/1000f; //t = time in s
            if (!onGround()) dir[2] -= Map.GRAVITY*t; //in m/s
            getPos().setHeight(getPos().getHeight() + dir[2] * GAME_DIMENSION * t); //in m
            
            
            //check new height for colission            
            //land if standing in or under 0-level or there is an obstacle
            if (dir[2] < 0 && onGround()){
                if (landingSound != null)
                    landingSound.play();//play landing sound
                if (fallingSound != null)
                    fallingSound.stop();//stop falling sound
                dir[2] = 0;
                
                //set on top of block
                getPos().setHeight((int)(oldHeight/GAME_DIMENSION)*GAME_DIMENSION);
            }
            
            if (!inliquid && getPos().getBlockSafe().isLiquid())//if enterin water
                waterSound.play();
            
            inliquid = getPos().getBlockSafe().isLiquid();//save if in water


            /*HORIZONTAL MOVEMENT*/
            //calculate new position
            float[] dMove = new float[]{
                delta * speed * dir[0],
                delta * speed * dir[1],
                0
            };

            //if movement allowed => move player   
            if (! horizontalColission(getPos().cpy().addVector(dMove)) ) {                
                getPos().addVector(dMove);
            }

            //graphic
            if (dir[0] < -Math.sin(Math.PI/3)){
                setValue(1);//west
            } else {
                if (dir[0] < - 0.5){
                    //y
                    if (dir[1]<0){
                        setValue(2);//north-west
                    } else {
                        setValue(0);//south-east
                    }
                } else {
                    if (dir[0] <  0.5){
                        //y
                        if (dir[1]<0){
                            setValue(3);//north
                        }else{
                            setValue(7);//south
                        }
                    }else {
                        if (dir[0] < Math.sin(Math.PI/3)) {
                            //y
                            if (dir[1] < 0){
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
            if (SPRITESPERDIR==3){
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
                if (dir[2] < -1) {
                    if (!fallingSoundPlaying){
                        fallingSound.play();
                        fallingSoundPlaying = true;
                    }
                }else {
                    fallingSound.stop();
                    fallingSoundPlaying = false;
                }
            }
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
        if (pos.cpy().addVector(0, - COLISSIONRADIUS, 0).getCoord().getBlockSafe().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(0, COLISSIONRADIUS, 0).getCoord().getBlockSafe().isObstacle())
            colission = true;
        
        //check X
        //left
        if (pos.cpy().addVector(-COLISSIONRADIUS, 0, 0).getCoord().getBlockSafe().isObstacle())
            colission = true;
        //bottom corner
        if (pos.cpy().addVector(COLISSIONRADIUS, 0, 0).getCoord().getBlockSafe().isObstacle())
            colission = true;
        
        return colission;
    }
    
    /**
     * Returns a normalized vector wich contains the direction of the block.
     * @return 
     */
    public float[] getDirectionVector(){
        return dir;
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
     *
     * @param waterSound new value of waterSound
     */
    public static void setWaterSound(Sound waterSound) {
        AbstractCharacter.waterSound = waterSound;
    }
    
    
    
   /**
     * Set the controls.
     * @param controls either "arrows", "WASD" or "NPC"
     */
    public void setControls(String controls){
        if ("arrows".equals(controls) || "WASD".equals(controls) || "NPC".equals(controls))
            this.controls = controls;
    }
    
   /**
     * Returns the Controls
     * @return either "arrows", "WASD" or "NPC"
     */
    public String getControls(){
        return controls;
    }
    
    /**
     * Adds horizontal colission check to onGround().
     * @return 
     */
    @Override
    public boolean onGround() {
        if (getPos().getHeight() > 0){
            getPos().setHeight(getPos().getHeight()-1);

            boolean colission = getPos().getBlockSafe().isObstacle() || horizontalColission(getPos());
            getPos().setHeight(getPos().getHeight()+1);

            //if standing on ground on own or neighbour block then true
            return (super.onGround() || colission);
        } return true;
    }

    @Override
    public void exist() {
        super.exist();
       // shadow.exist();
    }

    @Override
    public void destroy() {
        super.destroy();
        //shadow.destroy();
    } 

    /**
     * Is the character standing in a liquid?
     * @return 
     */
    public boolean isInliquid() {
        return inliquid;
    }
    
   @Override
    public void dispose(){
        fallingSound.dispose();
        jumpingSound.dispose();
        waterSound.dispose();
        runningSound.dispose();
    }
}
