package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AnimatedEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.badlogic.gdx.math.Vector3;

/**
 *An enemy which can follow a character.
 * @author Benedikt Vogler
 */
public class Enemy extends MovableEntity{
	private static final long serialVersionUID = 1L;
    private MovableEntity target;
    private int runningagainstwallCounter = 0;
    private Point lastPos;
    private static int killcounter = 0;
    
    public void init(){
       killcounter=0; 
    }
    
    /**
     * Zombie constructor. Use AbstractEntitiy.getInstance to create an zombie.
     */
    public Enemy() {
        super(45,1);
        setTransparent(true);
        setObstacle(true);
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
        if (getPosition().getCoord().isInMemoryHorizontal()) {
            //follow the target
            if (target != null) {
				Vector3 d = new Vector3();

				d.x = target.getPosition().getX()-getPosition().getX();
                d.y = target.getPosition().getY()-getPosition().getY();
				d.z = getMovementDirection().z;
				d.scl(0.4f);
				// update the movement vector
				setMovementDir(getMovementDirection().cpy().scl(getSpeed()).add(d));

				if (getPosition().distanceTo(target)<120) {
					setSpeed(0);
					setMana((int) (getMana()+dt));
                    if (getMana()>=1000){
                        setMana(0);//reset
                        new AnimatedEntity(46, 0, new int[]{300}, true, false).spawn(getPosition().cpy());//spawn blood
                        target.damage(50);
                    }
				} else 
					setSpeed(0.4f);
            }
            //update as usual
            super.update(dt);
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
        killcounter++;
        super.dispose();
    }

    public static int getKillcounter() {
        return killcounter;
    }
}