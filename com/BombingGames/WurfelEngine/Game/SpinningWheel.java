package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.shooting.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class SpinningWheel extends ArrayList<Weapon> {
    private static final long serialVersionUID = 1L;
    
    private final GunTestController controller;
    private boolean visible;
    private int current = -1;
    private final int spintime = 5000;
    private int timer;
    private int currentRandom;
    private float wheelSpeed;
    private float wheelTimer;

    public SpinningWheel(GunTestController ctlr) {
        controller = ctlr;
    }
   
    
    /**
     * Returns a new selection
     */
    public void spin(){
        //Sound dudeldi = (Sound) WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/dudeldi.ogg");
        //dudeldi.play();
        //controller.getMusic().setVolume(0.2f);
        
        visible = true;
        timer = spintime;
        controller.setTimespeed(0.3f);
        wheelSpeed=1;
        wheelTimer=1;
    }
    
    public void update(float delta){
        if (visible) {
            timer -= delta;
        
            if (timer <= 0) {//reset
                visible = false;
                timer = spintime;
                current = currentRandom;
                controller.equipWeapon(current);
                controller.setTimespeed(1f);
                //controller.getMusic().setVolume(1f);
            }

            wheelSpeed *= 1+ delta/400f;//time to pass before new random item get's bigger
            
            if (wheelSpeed >1000)
                wheelSpeed=50000;//stop it
            
            wheelTimer -= delta;
            if (wheelTimer <= 0){
                wheelTimer = wheelSpeed;
                currentRandom = (int) (Math.random()*size());
            }
        }
    }
        
    public void render(View view){
        if (visible){
            Sprite sprite;
            sprite = new Sprite(Weapon.getSpritesheetBig().findRegion("canvas"));
            sprite.setX(Gdx.graphics.getWidth()/2 - sprite.getWidth()/2);
            sprite.setY(Gdx.graphics.getHeight()/2-30*Weapon.getScaling());
            sprite.scale(Weapon.getScaling());
            sprite.draw(view.getBatch());
            
            if (controller.getRound()==1){
                sprite = new Sprite(Weapon.getSpritesheetBig().findRegion("warmup"));
            } else {
                sprite = new Sprite(Weapon.getSpritesheetBig().findRegion("newround"));
            }
                sprite.setX(Gdx.graphics.getWidth()/2 - sprite.getWidth()/2);
                sprite.setY(Gdx.graphics.getHeight()/2-200);
                sprite.scale(Weapon.getScaling());
                sprite.draw(view.getBatch());
                
            get(currentRandom).renderBig(view,
                Gdx.graphics.getWidth()/2-10*Weapon.getScaling(),
                Gdx.graphics.getHeight()/2-25*Weapon.getScaling()
            );
        }
        if (current>-1)
            get(current).renderBig(view,
                Gdx.graphics.getWidth()-150,
                Gdx.graphics.getHeight()-150
            );
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public Object clone() {
        return super.clone();
    }
}
