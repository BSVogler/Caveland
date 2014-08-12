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

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AnimatedEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.PlayerWithWeapon;
import com.BombingGames.WurfelEngine.Core.Gameobjects.SimpleEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Bullet extends AbstractEntity {
    private static Sound explosionsound;
    private Vector3 dir;//movement
    private float speed;
    private int damage;
    private int distance =0;//distance traveled
    private AbstractCharacter parent;//no self shooting
    private int maxDistance = 1000;//default maxDistance
    private int explosive = 0;
    private int impactSprite;
    
    /**
     * 
     * @param id
     * @param point 
     */
    public Bullet(int id, Point point){
        super(id, point);
    }

    /**
     *
     */
    public static void init(){
        if (explosionsound == null)
            explosionsound = WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg");
    }
   
    @Override
    public void update(float delta) {
        //dir.z=-delta/(float)maxDistance;//fall down
        Vector3 dMov = dir.cpy().scl(delta*speed);
        //dMov.z /= 1.414213562f;//mixed screen and game space together?
        getPos().addVector(dMov);
        
        //only exist specific distance then destroy self
        distance += dMov.len();
        if (distance > maxDistance)
            dispose();
                
        //block hit -> spawn effect
        if (getPos().onLoadedMap() && getPos().getBlockClamp().isObstacle()){
            if (impactSprite!= 0)
                new AnimatedEntity(impactSprite, 0, getPos().cpy(), new int[]{1000} , true, false).exist();
            dispose();
        }
        
        //check character hit
         //get every character on this coordinate
        ArrayList<AbstractCharacter> entitylist;
        entitylist = Controller.getMap().getAllEntitysOnCoord(getPos().getCoord(), AbstractCharacter.class);
        entitylist.remove(parent);//remove self from list to prevent self shooting
        if (!entitylist.isEmpty()) {
            entitylist.get(0).damage(damage);//damage only the first unit on the list
            new SimpleEntity(16, getPos().cpy()).exist();//spawn blood
            dispose();
        }
    }

    /**
     *
     * @param dir
     */
    public void setDirection(Vector3 dir) {
        this.dir = dir;
    }
    
    /**
     *
     * @param speed
     */
    public void setSpeed(float speed){
        this.speed = speed;
    }

    /**
     *
     * @param parent
     */
    public void setParent(AbstractCharacter parent) {
        this.parent = parent;
    }
    
    /**
     *
     * @param maxDistance
     */
    public void setMaxDistance(int maxDistance){
        this.maxDistance = maxDistance;
    }

    /**
     *
     * @param damage
     */
    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    /**
     *
     * @param ex
     */
    public void setExplosive(int ex){
        explosive = ex;
    }
    
      /**
     * Spawns explosion.
     */
    private void explode(){
        for (int x=-explosive; x<explosive; x++)
            for (int y=-explosive*2; y<explosive*2; y++)
                for (int z=-explosive; z<explosive; z++){
                    //place air
                     if (x*x + (y/2)*(y/2)+ z*z < explosive*explosive){
                        Controller.getMap().setDataSafe(
                            getPos().getCoord().cpy().addVector(new float[]{x, y, z}).getCoord() , Block.getInstance(0)
                        );
                     }
                }
        
         for (int x=-explosive; x<explosive; x++)
            for (int y=-explosive*2; y<explosive*2; y++)
                for (int z=-explosive; z<explosive; z++){
                    
                    //spawn explosion effect
                    if (x*x + (y/2)*(y/2)+ z*z >= explosive*explosive-4 &&
                        x*x + (y/2)*(y/2)+ z*z <= explosive*explosive){
                        AbstractEntity effect = new AnimatedEntity(
                            31,
                            0,
                            getPos().getCoord().cpy().addVector(new float[]{x, y, z}).getPoint(),
                            new int[]{700,2000},
                            true,
                            false
                        ).exist();
                        ArrayList<AbstractCharacter> list;
                        list = Controller.getMap().getAllEntitysOnCoord(effect.getPos().getCoord(), AbstractCharacter.class);
                        for (AbstractCharacter ent : list) {
                            if (!(ent instanceof PlayerWithWeapon))
                                ent.damage(1000);
                        }
                    }
                    
                }
         if (explosionsound != null) explosionsound.play();
         Controller.requestRecalc();
    }

    
    @Override
    public void dispose() {
        if (explosive>0) explode();
        super.dispose();
    }

    /**
     * Set the sprite which get spawned when the bullet hits.
     * @param id  if you don't want an impact sprite set id to0.
     */
    public void setImpactSprite(int id) {
        impactSprite = id;
    }

    /**
     *
     * @return the distance traveled.
     */
    public int getDistance() {
        return distance;
    }
    
    
}