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
package com.bombinggames.wurfelengine.extension.shooting;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.Particle;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleType;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.extension.AimBand;

/**
 *
 * @author Benedikt Vogler
 */
public class Weapon extends AbstractEntity implements Telegraph {
	private static final long serialVersionUID = 1L;
    
    private final byte weaponid;

    private AbstractGameObject parent;//the parent holding the weapon
    
    //sound
    private String fireSound;
    private String reload;
    
    //stats
	/**
	 * time in ms before new shot
	 */
    private final float delayBetweenShots;
    private final int shots;
    private final int relodingTime;
	/**
	 * distance in m
	 */
    private float distance;
    private final int bps;//bullets per shot
    private final float spread;
    private final byte damage;
    private final byte bulletSprite;
    private final byte impactSprite;
    
    private int shotsLoaded;
    private int reloading;
    /** The current time between shots. If reaches zero another bullet is spawned*/
    private float bulletDelay;
    private int explode;
    private transient Laserdot laserdot;

	private Vector3 aimDir = new Vector3(0, 0, 0);
	private byte ignoreId;
	/**
	 * true if just fired and weapon is still moving from the shot
	 */
	private boolean firing;
	private boolean fireSoundBust;
	private boolean bustSoundReady;
	private Point fixedPos = null;
	private transient AimBand particleBand;
	private transient PointLightSource lightSource;

    /**
     *
     * @param weaponid
     * @param parent the object which holds the weapon
     */
    public Weapon(byte weaponid,  AbstractGameObject parent) {
		super((byte) 18);
		this.parent = parent;
        
		this.weaponid = weaponid;
        switch (weaponid){
            case 0:
                setName("Katana");
                delayBetweenShots = 900;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.5f;
                damage = (byte) 5;
                bulletSprite = -1;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/melee.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/wiz.wav"); 
            break;
                
            case 1:
                setName("Pistol");
                delayBetweenShots = 400;
                relodingTime =1000;
                shots = 7;
                distance = 10;
                bps = 1;
                spread = 0.1f;
                damage = (byte) 5;
                bulletSprite = 0;
                impactSprite=19;
                
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/shot.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 2:
                setName("Fist");
                delayBetweenShots = 500;
                relodingTime =0;
                shots = 1;
                distance = 0;
                bps = 10;
                spread = 0.4f;
                bulletSprite = -1;
                damage = (byte) 100;
                impactSprite=15;
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/punch.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/melee.wav"); 
            break;
                
            case 3:
                setName("Shotgun");
                delayBetweenShots = 600;
                relodingTime =1300;
                shots = 2;
                distance = 5;
                bps = 20;
                spread = 0.2f;
                damage = (byte) 400;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/shotgun.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break;    

            case 4:
                setName("Machine Gun");
                delayBetweenShots = 75;
                relodingTime =1300;
                shots = 14;
                distance = 40;
                bps = 1;
                spread = 0.005f;
                damage = (byte) 50;
                bulletSprite = 0;
                impactSprite=19;
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/bust.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                                 
            case 5:
				setName("Poop");
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
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/poop.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 6:
				setName("Rocket Launcher");
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
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/thump.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break;
                
            case 7:
				setName("FireLauncher");
                delayBetweenShots = 40;
                relodingTime =1700;
                shots = 50;
                distance = 3;
                bps = 5;
                spread = 0.4f;
                damage = (byte) 200;
                bulletSprite = 1;
                impactSprite=18;
                
                //fire = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/fire.wav");
                //reload = WEMain.getAsset("com/bombinggames/WeaponOfChoice/Sounds/reload.wav"); 
            break; 
                default:
					setName("Pistol");
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
	 * The point where the weapon returns after shooting.
	 * @param fixedPos 
	 */
	public void setFixedPos(Point fixedPos) {
		this.fixedPos = fixedPos;
	}

	
	/**
	 * 
	 * @param maxDistance 
	 */
	public void setMaxDistance(float maxDistance){
		this.distance = maxDistance;
	}
	/**
	 *
	 * @return can be null
	 */
	public Point getFixedPos() {
		return fixedPos;
	}
	
    /**
     *
     * @return the weapon's id
     */
    public int getWeaponId() {
        return weaponid;
    }

    /**
     * Manages the weapon
     * @param dt t in ms
     */
	@Override
    public void update(float dt){
		super.update(dt);
        if (bulletDelay > 0) {
			bulletDelay -= dt;
		}
		
		if (bulletDelay <= 0){
			firing = false;
		}
		
		if (particleBand != null){
			particleBand.update();
		}
		
		//move back
		if (hasPosition() && fixedPos != null) {
			if (lightSource==null && fixedPos != null){
				lightSource= (PointLightSource) new PointLightSource(
					Color.WHITE.cpy(),
					3,
					30f,
					WE.getGameplay().getView()
				).spawn(fixedPos.cpy());
				lightSource.setSaveToDisk(false);
			}
			if (firing){
				lightSource.enable();
				float t;
				if (bulletDelay > delayBetweenShots/2){
					t = (bulletDelay-(delayBetweenShots/2f))/(delayBetweenShots/2f);
				} else {
					t = bulletDelay/(delayBetweenShots/2f);
				}
				this.getPosition().lerp(
					fixedPos.cpy().add(aimDir.cpy().scl(-Block.GAME_EDGELENGTH2)),
					t
				);
			} else {
				lightSource.disable();
				this.setPosition(fixedPos.cpy());
			}
		}
		
		
       if (reloading >= 0) {
			reloading -= dt;
			if (reloading <= 0) {//finished reloading
				shotsLoaded = shots;
			}
		} else { //if not shooting or loading
			if (bulletDelay <= 0 && shotsLoaded <= 0) {//autoreload
				reload();
			}
		}
	   
		
		if (laserdot == null){
			laserdot = (Laserdot) new Laserdot().spawn(getPosition().cpy());
		}
		laserdot.ignoreBlock(ignoreId);
		laserdot.update(aimDir, getPosition());
	}
	
	/**
	 * 
	 * @param dir 
	 */
	public void setAimDir(Vector3 dir){
		this.aimDir = dir.nor();
		laserdot.update(aimDir, getPosition());
	}
    
    /**
     *shoots the weapon. like holding the trigger down
     */
    public void shoot(){
       if (shotsLoaded > 0 && bulletDelay <= 0 && reloading <= 0 && hasPosition()) {
			if (fireSound != null) {
				if (bustSoundReady) {
					WE.SOUND.play(fireSound, getPosition());
				}
				if (fireSoundBust) {
					bustSoundReady = false;
				}
			}
			
			bulletDelay += delayBetweenShots;
			shotsLoaded--;
			firing = true;

			//muzzle flash
			Particle flash = new Particle();
			flash.setTTL(400);
			flash.setColor(Color.YELLOW.cpy());
			flash.setType(ParticleType.FIRE);
			flash.spawn(getPosition().toPoint());
			flash.setMovement(aimDir.cpy().scl(4f));
		
            //shot bullets
            for (int i = 0; i < bps; i++) {

                //pos.setHeight(pos.getHeight()+AbstractGameObject.GAME_EDGELENGTH);
				Bullet bullet = new Bullet();
				//bullet.setGun(this);

                if (bulletSprite < 0){//if melee hide it
                    bullet.setSpriteValue((byte) 0);
                    bullet.setHidden(true);
                } else{
                    bullet.setSpriteValue(bulletSprite);
                }

                Vector3 aiming = aimDir.cpy();
                aiming.x += Math.random() * (spread*2) -spread;
                aiming.y += Math.random() * (spread*2) -spread;
				bullet.setMovement(aiming.scl(40f));
				bullet.setScaling(-0.8f);
                bullet.setMaxDistance(distance*Block.GAME_EDGELENGTH);
                bullet.setDamage(damage);
                bullet.setExplosive(explode);
                bullet.setImpactSprite(impactSprite);
				bullet.ignoreBlock(ignoreId);
				//bullet.ignoreCoord(getPosition().toCoord());
                bullet.spawn(getPosition().toPoint()); 
            }
        }
    }
	
	/**
	 * 
	 * @param id 
	 */
	public void ignoreBlock(byte id){
		this.ignoreId = id;
	}
    
    /**
     *reloads the weapon
     */
    public void reload(){
		bustSoundReady = true;
        reloading = relodingTime;
        if (reload != null) {
			WE.SOUND.play("reload", getPosition());
		}
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
     * @param fire
	 * @param bustSound true if one sound per bust
     */
    public void setFireSound(String fire, boolean bustSound) {
        this.fireSound = fire;
		this.fireSoundBust = bustSound;
    }

    /**
     *
     * @param reload
     */
    public void setReload(String reload) {
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

	@Override
	public void dispose() {
		super.dispose();
		if (laserdot != null)
			laserdot.dispose();
	}
	
		
	@Override
	public boolean handleMessage(Telegram msg) {
		 if (msg.message == Events.deselectInEditor.getId()){
			if (particleBand != null) {
				particleBand.dispose();
				particleBand = null;
			}
		} else if (msg.message == Events.selectInEditor.getId()){
			if (particleBand == null) {
				particleBand = new AimBand(this, laserdot);
			} else {
				particleBand.setTarget(laserdot);
			}
		}
		return true;
	}

	/**
	 *
	 * @param hidden
	 */
	public void setLaserHidden(boolean hidden) {
		if (laserdot!=null)
			laserdot.setHidden(hidden);
	}
	
}