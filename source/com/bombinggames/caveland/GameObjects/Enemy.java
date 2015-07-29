package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.DestructionParticle;
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
	private static final String killSound = "robot1destroy";
	private static final String movementSound = "robot1Wobble";
	
    private transient MovableEntity target;
    private int runningagainstwallCounter = 0;
    private Point lastPos;
	private long movementSoundPlaying;
	private float mana = 1000;
	private int attackInProgess = 0;
	/**
	 * in m/s
	 */
	private float movementSpeed = 2;
    
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
		
		if (attackInProgess > 0) {
			attackInProgess -= dt;
			movementSpeed = 0;
		}

		//clamp at 0
		if (attackInProgess < 0) {
			attackInProgess = 0;
			movementSpeed = 2;
			playMovementAnimation();
		}
			
        if (isSpawned() && getPosition().toCoord().isInMemoryAreaHorizontal()) {
            //follow the target
            if (target != null && target.isSpawned()) {
				if (getPosition().distanceTo(target) > Block.GAME_EDGELENGTH*1.5f) {
					//movement logic
					Vector3 d = new Vector3();

					d.x = target.getPosition().getX()-getPosition().getX();
					d.y = target.getPosition().getY()-getPosition().getY();
					d.nor();//direction only
					d.scl(movementSpeed);//speed at 2 m/s
					d.z = getMovement().z;

					setMovement(d);// update the movement vector

					mana = ((int) (mana + dt));
				}
				
				//attack
				if (attackInProgess == 0) {
					performAttack();
				}
            }
			
			
			//find nearby target if there is none
			if (target == null){
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
		if (getHealth() <= 0){
			new DestructionParticle((byte) 34).spawn(getPosition().toPoint().cpy());
			new DestructionParticle((byte) 35).spawn(getPosition().toPoint().cpy());
			new DestructionParticle((byte) 36).spawn(getPosition().toPoint().cpy());
			
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

	private void performAttack() {
		if (mana >= 1000 && getPosition().distanceTo(target) < Block.GAME_EDGELENGTH*2f ){
			mana = 0;//reset
			new SimpleEntity((byte) 33).spawn(target.getPosition().cpy()).setAnimation(
				new EntityAnimation(new int[]{300}, true, false)
			);
			target.damage((byte)50);
			pauseMovementAnimation();
			attackInProgess = 1500;//1500ms until the attack is done
		}
	}
}