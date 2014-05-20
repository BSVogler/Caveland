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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;

/**
 *An example for a special block: barrel block which can explode
 * @author Benedikt
 */
public class ExplosiveBarrel extends Block implements IsSelfAware {
    /**Defines the radius of the explosion.*/
    public static final int RADIUS = 3;
    private Coordinate coords;
    private static Sound explosionsound;

    /**
     * Create a explosive barrel.
     * @param id the id of the explosive barrel
     * @param coords  The coordinates where this object get's placed.
     */
    public ExplosiveBarrel(int id, Coordinate coords){
        super(id);
        if (coords == null) throw new NullPointerException("No coordinates given to ExplosiveBarrel during creation."); 
        this.coords = coords;
        setObstacle(true);
        if (explosionsound == null)
            explosionsound = WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg");
    }
    
    /**
     * Explodes the barrel.
     */
    public void explode(){
        for (int x=-RADIUS; x<RADIUS; x++)
            for (int y=-RADIUS*2; y<RADIUS*2; y++)
                for (int z=-RADIUS; z<RADIUS; z++){
                    //place air
                     if (x*x + (y/2)*(y/2)+ z*z < RADIUS*RADIUS){
                        Controller.getMap().setDataSafe(
                            coords.cpy().addVector(new float[]{x, y, z}).getCoord() , Block.getInstance(0)
                        );
                     }
                }
        
         for (int x=-RADIUS; x<RADIUS; x++)
            for (int y=-RADIUS*2; y<RADIUS*2; y++)
                for (int z=-RADIUS; z<RADIUS; z++){
                    
                    //spawn effect
                    if (x*x + (y/2)*(y/2)+ z*z >= RADIUS*RADIUS-4 &&
                        x*x + (y/2)*(y/2)+ z*z <= RADIUS*RADIUS){
                        new AnimatedEntity(
                            31,
                            0,
                            coords.cpy().addVector(new float[]{x, y, z}),
                            new int[]{700,2000},
                            true,
                            false
                        ).exist();
                    }
                }
         explosionsound.play();
         Controller.requestRecalc();
    }

    @Override
    public Coordinate getPos() {
        return coords;
    }

    @Override
    public void setPos(AbstractPosition pos) {
        this.coords = pos.getCoord();
    }
}