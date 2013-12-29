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
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Player;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.audio.Sound;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Bullet extends AbstractEntity {
    private static Sound explosionsound;
    private float[] dir = new float[3];//movement
    private float speed;
    private int damage;
    private int distance =0;//distance traveled
    private AbstractCharacter parent;//no self shooting
    private int maxDistance = 1000;//default maxDistance
    private int explosive = 0;
    private int impactSprite;
    
   public Bullet(int id){
       super(id);
   } 
   
    public static void init(){
        if (explosionsound == null)
            explosionsound = WEMain.getAsset("com/BombingGames/WurfelEngine/Game/Sounds/explosion2.ogg");
    }
   
    @Override
    public void update(float delta) {
        dir[2]=-delta/(float)maxDistance;
        
        float[] mov = new float[]{
            dir[0]*delta*speed,
            dir[1]*delta*speed,
            dir[2]
        };
            
        getPos().addVector(mov);
        
        //only exist specific distance then destroy self
        distance += Math.sqrt(Math.abs(mov[0])+Math.abs(mov[1])+Math.abs(mov[2]))*delta*speed;
        if (isHidden() && distance > 400)
            destroy();
                
        //block hit & spawn effect
        if (getPos().onLoadedMap() && getPos().getBlockClamp().isObstacle()){
            AbstractEntity.getInstance(impactSprite, 0, getPos().cpy()).exist();
            destroy();
        }
        
        //check character hit
         //get every character on this coordinate
        ArrayList<AbstractCharacter> entitylist = Controller.getMap().getAllEntitysOnCoord(getPos().getCoord(), AbstractCharacter.class);
        entitylist.remove(parent);
        if (!entitylist.isEmpty()) {
            entitylist.get(0).damage(damage);
            AbstractEntity.getInstance(16, 0, getPos().cpy()).exist();//spawn blood
            destroy();
        }
    }

    public void setDirection(float[] dir) {
        this.dir = dir;
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
    }

    public void setParent(AbstractCharacter parent) {
        this.parent = parent;
    }
    
    public void setMaxDistance(int maxDistance){
        this.maxDistance = maxDistance;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
    
    public void setExplosive(int ex){
        explosive = ex;
    }
    
      /**
     * Explodes the barrel.
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
                        AbstractEntity effect = AbstractEntity.getInstance(
                            41,
                            0,
                            getPos().getCoord().cpy().addVector(new float[]{x, y, z}).getPoint()
                        );
                        effect.exist();
                        ArrayList<AbstractCharacter> list = Controller.getMap().getAllEntitysOnCoord(effect.getPos().getCoord(), AbstractCharacter.class);
                        for (AbstractCharacter ent : list) {
                            if (!(ent instanceof Player))
                                ent.damage(1000);
                        }
                    }
                    
                }
         if (explosionsound != null) explosionsound.play();
         Controller.requestRecalc();
    }

    
    @Override
    public void destroy() {
        if (explosive>0) explode();
        super.destroy();
    }

    public void setImpactSprite(int hitSprite) {
        impactSprite = hitSprite;
    }
}
