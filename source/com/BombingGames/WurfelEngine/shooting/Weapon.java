/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
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

import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityAnimation;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.SimpleEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.backends.lwjgl.audio.Wav.Sound;
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
    
    private final byte id;
    private final String name;

    private MovableEntity parent;//the parent holding the weapon
    
    //sound
    private Sound fire;
    private Sound reload;
    
    //stats
    private final int delayBetweenShots;
    private final int shots;
    private final int relodingTime;
    private final int distance;
    private final int bps;//bullets per shot
    private final float spread;
    private final byte damage;
    private final byte bulletSprite;
    private final byte impactSprite;
    
    private int shotsLoaded;
    private int reloading;
    /** The current time delayBetweenShots between shots*/
    private float bulletDelay;
    private int explode;
    private AbstractEntity laserdot;

    
    /**
     *does nothing at the moment
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
     * @param parent
     */
    public Weapon(byte id, MovableEntity parent) {
        this.id = id;
        this.parent = parent;
        if (parent != null) {
            laserdot = new SimpleEntity((byte) 20).spawn(parent.getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH));
        }
        
        switch (id){
            case 0:
                name="katana";
                delayBetweenShots = 900;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.5f;
                damage = (byte) 1000;
                bulletSprite = -1;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/melee.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/wiz.wav"); 
            break;
                
            case 1:
                name="pistol";
                delayBetweenShots = 400;
                relodingTime =1000;
                shots = 7;
                distance = 10;
                bps = 1;
                spread = 0.1f;
                damage = (byte) 800;
                bulletSprite = 0;
                impactSprite=19;
                
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/shot.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 2:
                name="fist";
                delayBetweenShots = 500;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.4f;
                bulletSprite = -1;
                damage = (byte) 500;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/punch.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/melee.wav"); 
            break;
                
            case 3:
                name="shotgun";
                delayBetweenShots = 600;
                relodingTime =1300;
                shots = 2;
                distance = 5;
                bps = 20;
                spread = 0.2f;
                damage = (byte) 400;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/shotgun.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;    

            case 4:
                name="machine gun";
                delayBetweenShots = 20;
                relodingTime =1300;
                shots = 1000;
                distance = 10;
                bps = 1;
                spread = 0f;
                damage = (byte) 400;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/bust.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                                 
            case 5:
                name="poop";
                delayBetweenShots = 900;
                relodingTime =500;
                shots = 1;
                distance = 3;
                bps = 1;
                spread = 0.2f;
                damage = (byte) 400;
                bulletSprite = 3;
                explode = 1;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/poop.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 6:
                name="rocket launcher";
                delayBetweenShots = 0;
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
                delayBetweenShots = 40;
                relodingTime =1700;
                shots = 50;
                distance = 3;
                bps = 5;
                spread = 0.4f;
                damage = (byte) 200;
                bulletSprite = 1;
                impactSprite=18;
                
                //fire = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/fire.wav");
                //reload = WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break; 
                default:
                    name="pistol";
                    delayBetweenShots = 400;
                    relodingTime =1000;
                    shots = 7;
                    distance = 10;
                    bps = 1;
                    spread = 0.1f;
                    damage = (byte) 800;
                    bulletSprite = 0;
                    impactSprite=19;
        }
        shotsLoaded = shots; //fully loaded
    }
    
    /**
     * Renders a big version of the image
     * @param view
     * @param x
     * @param y 
     */
    public void renderBig(GameView view, int x, int y){
        Sprite sprite = new Sprite(spritesheetBig.findRegion(Integer.toString(id)));
        sprite.setX(x);
        sprite.setY(y);
        sprite.scale(scaling);
        sprite.draw(WE.getEngineView().getBatch());
    
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
     * @param dt t in ms
     */
    public void update(float dt){
        if (bulletDelay > 0)
            bulletDelay-=dt;
        if (reloading >= 0) {
            reloading-=dt;
            if (reloading<=0)//finished reloading
                shotsLoaded = shots;
        } else {
            //if not shooting or loading
            if (bulletDelay <= 0 && shotsLoaded <= 0)//autoreload
                reload();
        }
        
        Point raycast = parent.getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH).raycast(parent.getAiming(), 5000, null, false).getPoint();
        if (raycast!=null)
            laserdot.setPosition(raycast);
//        if (laser!=null && laser.shouldBeDisposed())
//            laser=null;
//        if (laser==null) {
//            laser = new Bullet(12, parent.getPosition().cpy());
//            laser.setValue(0);
//            laser.setHidden(true);
//
//            laser.setDirection(parent.getAiming());
//            laser.setSpeed(7);
//            laser.setMaxDistance(3000);
//            laser.setParent(parent);
//            laser.setDamage(0);
//            laser.setExplosive(0);
//            laser.setImpactSprite(20);
//            laser.spawn();
//        }
    }
    
    /**
     *shoots the weapon
     */
    public void shoot(){
        if (shotsLoaded>0 && bulletDelay <= 0 && reloading <= 0){
            if (fire != null) fire.play();

            bulletDelay = delayBetweenShots;
            shotsLoaded--;

            //muzzle flash
            if (bulletSprite <0)
                new SimpleEntity((byte) 60).spawn(parent.getPosition()).setAnimation(new EntityAnimation(new int[]{300}, true, false)
				);
            else
                new SimpleEntity((byte) 61).spawn(parent.getPosition()).setAnimation(new EntityAnimation(new int[]{300}, true, false)
				);

            //shot bullets
            for (int i = 0; i < bps; i++) {
                Bullet bullet;

                //pos.setHeight(pos.getHeight()+AbstractGameObject.GAME_EDGELENGTH);
                bullet = new Bullet();

                if (bulletSprite < 0){//if melee hide it
                    bullet.setValue((byte) 0);
                    bullet.setHidden(true);
                } else{
                    bullet.setValue(bulletSprite);
                }

                Vector3 aiming = parent.getAiming();
                aiming.x += Math.random() * (spread*2) -spread;
                aiming.y += Math.random() * (spread*2) -spread;
                bullet.setDirection(aiming);
                bullet.setSpeed(2f);
                bullet.setMaxDistance(distance*100+100);
                bullet.setParent(parent);
                bullet.setDamage(damage);
                bullet.setExplosive(explode);
                bullet.setImpactSprite(impactSprite);
                bullet.spawn(parent.getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH)); 
            }
        }
    }
    
    /**
     *reloads the weapon
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
    public String getName() {
        return name;
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
    
//    /**
//     *Get the distance to impact point.
//     * @return
//     */
//    public int getAimDistance(){
//        return laser.getDistance();
//    }


    /**
     * returns the position of the laserdot, the point where the aiming impacts
     * @return a copy
     */
    public Point getImpactPoint() {
        return laserdot.getPosition().cpy();
    }
}