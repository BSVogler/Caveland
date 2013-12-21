package com.BombingGames.WurfelEngine.shooting;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.backends.openal.Wav.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**
 *
 * @author Benedikt Vogler
 */
public class Weapon {
    private static TextureAtlas spritesheetBig;
    private static final int scaling = 2;
    
    private final int id;
    private String name;

    private AbstractCharacter character;//the char holding the weapon
    
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

    public static void init(){
        if (spritesheetBig == null) {
            spritesheetBig = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/SpritesBig.txt");
            for (TextureAtlas.AtlasRegion region : spritesheetBig.getRegions()) {
                    region.flip(false, true);
            }
            for (Texture tex : spritesheetBig.getTextures()) {
                tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            }
        }
    }
    private int explode;
    
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/melee.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/wiz.wav"); 
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
                
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/shot.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/punch.wav");
                //reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/melee.wav"); 
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/shotgun.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
            break;    

            case 4:
                name="machine gun";
                delay = 70;
                relodingTime =1300;
                shots = 25;
                distance = 10;
                bps = 1;
                spread = 0.08f;
                damage = 400;
                bulletSprite = 0;
                impactSprite=19;
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/bust.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/poop.wav");
                //reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/thump.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
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
                
                fire = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/fire.wav");
                reload = WEMain.getInstance().manager.get("com/BombingGames/WeaponOfChoice/Sounds/reload.wav"); 
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
        Sprite sprite = new Sprite(spritesheetBig.findRegion(""+id));
        sprite.setX(x);
        sprite.setY(y);
        sprite.scale(scaling);
        sprite.draw(view.getBatch());
    
    }

    public static TextureAtlas getSpritesheetBig() {
        return spritesheetBig;
    }

    public int getId() {
        return id;
    }

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
    }
    
    
    private void shoot(){
        fire.play();
                
        shooting = delay;
        shotsLoaded--;
        
        if (bulletSprite <0)
            AbstractEntity.getInstance(20, 0, character.getPos()).exist();
        else AbstractEntity.getInstance(21, 0, character.getPos()).exist();
        
        //shot bullets
        for (int i = 0; i < bps; i++) {
            Bullet bullet;
            
            Point pos = character.getPos().cpy();
            pos.setHeight(pos.getHeight()+AbstractGameObject.GAME_DIMENSION);
            
            if (bulletSprite <0){
                bullet = (Bullet) AbstractEntity.getInstance(12, 0, pos);
                bullet.setHidden(true);
            } else{
                bullet = (Bullet) AbstractEntity.getInstance(12, bulletSprite, pos);
            }
            
            float[] aiming = character.getAiming();
            aiming[0] += Math.random() * (spread*2) -spread;
            aiming[1] += Math.random() * (spread*2) -spread;
            bullet.setDirection(
                aiming
            );
            bullet.setSpeed(1.2f);
            bullet.setMaxDistance(distance*100+100);
            bullet.setParent(character);
            bullet.setDamage(damage);
            bullet.setExplosive(explode);
            bullet.setImpactSprite(impactSprite);
            bullet.exist(); 
        }

    }
    
    public void reload(){
        reloading =relodingTime;
        if (reload != null) reload.play();
    }

    public int getShotsLoaded() {
        return shotsLoaded;
    }

    public int getShots() {
        return shots;
    }

    public int getReloading() {
        return reloading;
    }

    public int getShooting() {
        return shooting;
    }

    void trigger() {
         if (shooting <= 0 && reloading <= 0){
            //if not shootring or loading
            if (shotsLoaded <= 0)//autoreload
                reload();

            if (shotsLoaded>0)
                shoot();  
        }
    }
}