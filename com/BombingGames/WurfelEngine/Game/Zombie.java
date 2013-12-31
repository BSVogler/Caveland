package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.badlogic.gdx.graphics.Color;

/**
 *A zombie which can follow a character.
 * @author Benedikt Vogler
 */
public class Zombie extends AbstractCharacter{
    private AbstractCharacter target;
    private int runningagainstwallCounter = 0;
    private Point lastPos;
    
    /**
     * Zombie constructor. Use AbstractEntitiy.getInstance to create an zombie.
     * @param id
     * @param pos
     */
    public Zombie(int id, Point pos) {
        super(id, 3, pos);
        setTransparent(true);
        setObstacle(true);
        setDimensionZ(2);
    }

    @Override
    public void jump() {
        super.jump(5);
    }

    @Override
    public void render(View view, WECamera camera, AbstractPosition pos) {
        getSprites()[CATEGORY][43][getValue()] = getSprites()[CATEGORY][40][getValue()];//reference player sprite
        Color color = Color.GRAY.cpy();
        if (Controller.getLightengine() != null){
            color = Controller.getLightengine().getGlobalLight();
        }
        render(view, camera, pos, color.mul(Color.GREEN));
    }

    @Override
    public void update(float delta) {
        //follow the target
        walk(
            (target.getPos().getAbsY()<getPos().getAbsY()),
            (target.getPos().getAbsY()>getPos().getAbsY()),
            (target.getPos().getAbsX()<getPos().getAbsX()),
            (target.getPos().getAbsX()>getPos().getAbsX()),
            0.35f
        );
        
        //update as usual
        super.update(delta);
        
        //if standing on same position as in last update
        if (getPos().equals(lastPos))
            runningagainstwallCounter += delta;
        else {
            runningagainstwallCounter=0;
            lastPos = getPos().cpy();
        }
        
        //jump after one second
        if (runningagainstwallCounter > 50) {
            jump();
            runningagainstwallCounter=0;
        }
    }

    /**
     * Set the target which the zombie follows.
     * @param target an character
     */
    public void setTarget(AbstractCharacter target) {
        this.target = target;
    }

    @Override
    public float[] getAiming() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}