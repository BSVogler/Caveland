package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityAnimation;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;

/**
 *An enemy which can follow a character.
 * @author Benedikt Vogler
 */
public class Enemy extends MovableEntity{
	private static final long serialVersionUID = 1L;
	private static int killcounter = 0;
	private static String killSound = "robot1destroy";
	private static final String movementSound = "robot1Wobble";
	
    private transient MovableEntity target;
    private int runningagainstwallCounter = 0;
    private Point lastPos;
	private long movementSoundPlaying;
	private float mana = 1000;
    
	/**
	 *
	 */
	public void init(){
       killcounter=0; 
    }
    
    /**
     * Zombie constructor. Use AbstractEntitiy.getInstance to create an zombie.
     */
    public Enemy() {
        super((byte) 45,5);
		setName("Evil Robot");
        setObstacle(true);
		setFloating(false);
		setWalkingSpeedIndependentAnimation(1f);
		setWalkingAnimationCycling(true);
		setDamageSounds(new String[]{"robotHit"});
    }

	@Override
	public AbstractEntity spawn(final Point point) {
		movementSoundPlaying = Controller.getSoundEngine().loop(movementSound, point);
		return super.spawn(point);
	}

    @Override
    public void jump() {
        jump(5, true);
    }


    @Override
    public void update(float dt) {
		//update as usual
        super.update(dt);
        if (getPosition().toCoord().isInMemoryAreaHorizontal()) {
            //follow the target
            if (target != null) {
				Vector3 d = new Vector3();

				d.x = target.getPosition().getX()-getPosition().getX();
                d.y = target.getPosition().getY()-getPosition().getY();
				d.nor();//direction only
				d.scl(2f);
				d.z = getMovement().z;

				// update the movement vector
				setMovement(d);

				if (getPosition().distanceTo(target)<120) {
					//setSpeed(0);
					mana = ((int) (mana+dt));
                    if (mana>=1000){
                        mana = 0;//reset
                        new SimpleEntity((byte) 46).spawn(getPosition().cpy()).setAnimation(
							new EntityAnimation(new int[]{300}, true, false)
						);
                        target.damage((byte)50);
                    }
				}// else 
					//setSpeed(0.4f);
            }
			
			//find nearby target if there is none
			if (target==null){
				ArrayList<CustomPlayer> nearby = getPosition().getEntitiesNearbyHorizontal(Block.GAME_DIAGLENGTH*4, CustomPlayer.class);
				if (!nearby.isEmpty())
					target = nearby.get(0);
			}
			
            //if standing on same position as in last update
            if (getPosition().equals(lastPos) && getSpeed()>0)//not standing still
                runningagainstwallCounter += dt;
            else {
                runningagainstwallCounter=0;
                lastPos = getPosition().cpy();
            }

            //jump after some time
            if (runningagainstwallCounter > 500) {
                jump();
                mana =0;
                runningagainstwallCounter=0;
            }
        }
    }

    /**
     * Set the target which the zombie follows.
     * @param target an character
     */
    public void setTarget(MovableEntity target) {
        this.target = target;
    }

	@Override
	public void damage(byte value) {
		super.damage(value);
		if (getHealth()<=0){
			Controller.getSoundEngine().stop(movementSound, movementSoundPlaying);
			if (getHealth() <= 0 && killSound != null)
				Controller.getSoundEngine().play(killSound);
			killcounter++;
		}
	}

	/**
	 *
	 * @return
	 */
	public static int getKillcounter() {
        return killcounter;
    }

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}
}