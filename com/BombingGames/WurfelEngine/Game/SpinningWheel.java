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
