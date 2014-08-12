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
package com.BombingGames.WurfelEngine.shooting;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AnimatedEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.Core.View;
import com.badlogic.gdx.backends.openal.Wav.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;

/**
 *
 * @author Benedikt Vogler
 */
public class Weapon {
    private static TextureAtlas spritesheetBig;
    private static final int scaling = 2;
    
    private final int id;
    private String name;

    private AbstractCharacter character;//the character holding the weapon
    
    //sound
    private Sound fire;
    private Sound reload;
    
    //stats
    private int delay;
    private int shots;
    private int relodingTime;
    private int distance;
    private int bps;//bullets per shot
    private float spread;
    private int damage;
    private int bulletSprite;
    private int impactSprite;
    
    private int shotsLoaded;
    private int reloading;
    private int shooting;
    private int explode;
    private Bullet laser;

    /**
     *
     */
    public static void init(){
        if (spritesheetBig == null) {
//            spritesheetBig = WEMain.getAsset("com/BombingGames/WeaponOfChoice/SpritesBig.txt");
//            for (TextureAtlas.AtlasRegion region : spritesheetBig.getRegions()) {
//                    region.flip(false, true);
//            }
//            for (Texture tex : spritesheetBig.getTextures()) {
//                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
//            }
        }
    }

    /**
     *
     * @param id
     * @param character
     */
    public Weapon(int id, AbstractCharacter character) {
        this.id = id;
        this.character = character;
        
        switch (id){
            case 0:
                name="katana";
                delay = 900;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.5f;
                damage = 1000;
                bulletSprite = -1;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/melee.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/wiz.wav"); 
            break;
                
            case 1:
                name="pistol";
                delay = 400;
                relodingTime =1000;
                shots = 7;
                distance = 10;
                bps = 1;
                spread = 0.1f;
                damage = 800;
                bulletSprite = 0;
                impactSprite=19;
                
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/shot.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 2:
                name="fist";
                delay = 500;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.4f;
                bulletSprite = -1;
                damage = 500;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/punch.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/melee.wav"); 
            break;
                
            case 3:
                name="shotgun";
                delay = 600;
                relodingTime =1300;
                shots = 2;
                distance = 5;
                bps = 20;
                spread = 0.2f;
                damage = 400;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/shotgun.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;    

            case 4:
                name="machine gun";
                delay = 20;
                relodingTime =1300;
                shots = 1000;
                distance = 10;
                bps = 1;
                spread = 0f;
                damage = 400;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/bust.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                                 
            case 5:
                name="poop";
                delay = 900;
                relodingTime =500;
                shots = 1;
                distance = 3;
                bps = 1;
                spread = 0.2f;
                damage = 400;
                bulletSprite = 3;
                explode = 1;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/poop.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 6:
                name="rocket launcher";
                delay = 0;
                relodingTime =1500;
                shots = 1;
                distance = 5;
                bps = 1;
                damage = 100;
                bulletSprite = 2;
                explode = 2;
                spread = 0.1f;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/thump.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 7:
                name="fire launcher";
                delay = 40;
                relodingTime =1700;
                shots = 50;
                distance = 3;
                bps = 5;
                spread = 0.4f;
                damage = 200;
                bulletSprite = 1;
                impactSprite=18;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/fire.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;     
                

           
        }
        shotsLoaded = shots; //fully loaded
    }
    
    /**
     * Renders a big version of the image
     * @param view
     * @param x
     * @param y 
     */
    public void renderBig(View view, int x, int y){
        Sprite sprite = new Sprite(spritesheetBig.findRegion(Integer.toString(id)));
        sprite.setX(x);
        sprite.setY(y);
        sprite.scale(scaling);
        sprite.draw(view.getBatch());
    
    }

    /**
     *
     * @return
     */
    public static TextureAtlas getSpritesheetBig() {
        return spritesheetBig;
    }

    /**
     *
     * @return the weapon's id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @return
     */
    public static int getScaling() {
        return scaling;
    }
    
    /**
     * Manages the weapon
     * @param trigger Is the trigger down?
     * @param delta
     */
    public void update(boolean trigger, float delta){
        if (shooting > 0){
            shooting-=delta;
        } else {
            if (reloading >= 0) {
                reloading-=delta;
                if (reloading<=0)//finished reloading
                    shotsLoaded = shots;
            } else {
                //if not shootring or loading
                if (shotsLoaded <= 0)//autoreload
                    reload();

                if (trigger && shotsLoaded>0)
                    shoot();  
            }
        }
        if (laser!=null && laser.shouldBeDisposed())
            laser=null;
        if (laser==null) {
            laser = new Bullet(12, character.getPos().cpy());
            laser.setValue(0);
            laser.setHidden(true);

            laser.setDirection(character.getAiming());
            laser.setSpeed(7);
            laser.setMaxDistance(3000);
            laser.setParent(character);
            laser.setDamage(0);
            laser.setExplosive(0);
            laser.setImpactSprite(20);
            laser.exist();
        }
    }
    
    
    private void shoot(){
        if (fire != null) fire.play();
                
        shooting = delay;
        shotsLoaded--;
        
        //muzzle flash
        if (bulletSprite <0)
            new AnimatedEntity(60, 0, character.getPos(), new int[]{300}, true, false).exist();
        else
            new AnimatedEntity(61, 0, character.getPos(), new int[]{300}, true, false).exist();
        
        //shot bullets
        for (int i = 0; i < bps; i++) {
            Bullet bullet;
            
            //pos.setHeight(pos.getHeight()+AbstractGameObject.GAME_EDGELENGTH);
            bullet = new Bullet(
                12,
                (Point) (character.getPos().cpy()).addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH)
            );
            
            if (bulletSprite < 0){//if melee hide it
                bullet.setValue(0);
                bullet.setHidden(true);
            } else{
                bullet.setValue(bulletSprite);
            }
            
            Vector3 aiming = character.getAiming();
            aiming.x += Math.random() * (spread*2) -spread;
            aiming.y += Math.random() * (spread*2) -spread;
            bullet.setDirection(aiming);
            bullet.setSpeed(0.5f);
            bullet.setMaxDistance(distance*100+100);
            bullet.setParent(character);
            bullet.setDamage(damage);
            bullet.setExplosive(explode);
            bullet.setImpactSprite(impactSprite);
            bullet.exist(); 
        }

    }
    
    /**
     *
     */
    public void reload(){
        reloading =relodingTime;
        if (reload != null) reload.play();
    }

    /**
     *
     * @return
     */
    public int getShotsLoaded() {
        return shotsLoaded;
    }

    /**
     *
     * @return
     */
    public int getShots() {
        return shots;
    }

    /**
     *
     * @return
     */
    public int getReloadingTime() {
        return reloading;
    }

    /**
     *
     * @return
     */
    public int getShootingTime() {
        return shooting;
    }

    /**
     *
     */
    public void trigger() {
         if (shooting <= 0 && reloading <= 0){
            //if not shootring or loading
            if (shotsLoaded <= 0)//autoreload
                reload();

            if (shotsLoaded>0)
                shoot();  
        }
    }

    /**
     *
     * @param spritesheetBig
     */
    public static void setSpritesheetBig(TextureAtlas spritesheetBig) {
        Weapon.spritesheetBig = spritesheetBig;
    }

    /**
     *
     * @param fire
     */
    public void setFire(Sound fire) {
        this.fire = fire;
    }

    /**
     *
     * @param reload
     */
    public void setReload(Sound reload) {
        this.reload = reload;
    }
    
    /**
     *Get the distance to impact point.
     * @return
     */
    public int getAimDistance(){
        return laser.getDistance();
    }
}