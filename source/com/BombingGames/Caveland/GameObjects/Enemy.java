package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityAnimation;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.SimpleEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.backends.lwjgl.audio.Wav.Sound;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *An enemy which can follow a character.
 * @author Benedikt Vogler
 */
public class Enemy extends MovableEntity{
	private static final long serialVersionUID = 1L;
	private static int killcounter = 0;
	private static Sound killSound;
	
    private MovableEntity target;
    private int runningagainstwallCounter = 0;
    private Point lastPos;
    
    public void init(){
       killcounter=0; 
    }
    
    /**
     * Zombie constructor. Use AbstractEntitiy.getInstance to create an zombie.
     */
    public Enemy() {
        super(45,5);
        setTransparent(true);
        setObstacle(true);
		setFloating(false);
		setWalkingAnimationCycling(true);
		
		if (killSound==null)
			killSound = WE.getAsset("com/BombingGames/Caveland/Sounds/robot1destroy.wav");
//        setDamageSounds(new Sound[]{
//            (Sound) WE.getAsset("com/BombingGames/WeaponOfChoice/Sounds/impactFlesh.wav")
//        });
    }


    @Override
    public void jump() {
        jump(5, true);
    }

    @Override
    public void update(float dt) {
		//update as usual
        super.update(dt);
        if (getPosition().getCoord().isInMemoryHorizontal()) {
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
					setMana((int) (getMana()+dt));
                    if (getMana()>=1000){
                        setMana(0);//reset
                        new SimpleEntity(46).spawn(getPosition().cpy()).setAnimation(
							new EntityAnimation(new int[]{300}, true, false)
						);
                        target.damage(50);
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
                setMana(0);
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
    public void dispose() {
		if (getHealth() <= 0 && killSound != null)
			killSound.play();
        killcounter++;
        super.dispose();
    }

    public static int getKillcounter() {
        return killcounter;
    }

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}
}