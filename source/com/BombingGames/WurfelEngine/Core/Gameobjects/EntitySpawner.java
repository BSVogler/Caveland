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
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *The entitty spawner spawns an entity when a character steps on it.
 * @author Benedikt Vogler
 */
public class EntitySpawner extends Block implements IsSelfAware {
    private Coordinate coords;//this field is needed because of it is selfAware
    private boolean up = true;

    /**
     *
     * @param id
     * @param coords
     */
    public EntitySpawner(int id, Coordinate coords){
        super(id);
        if (coords == null) throw new NullPointerException("No coordinates given to EntitySpawner during creation."); 
        this.coords = coords;
        setObstacle(true);
    }
    

    @Override
    public void update(float delta) {
        int[] coordsOnTop = coords.cpy().addVector(new float[]{0, 0, 1}).getCoord().getRel();
        
        //get every character
        ArrayList<AbstractCharacter> entitylist;
        entitylist = Controller.getMap().getAllEntitysOfType(AbstractCharacter.class);
        
        //check every character if standing on top
        int i = 0;
        while (i < entitylist.size() && !Arrays.equals( entitylist.get(i).getPos().getCoord().getRel(), coordsOnTop)){
            i++;
        }
        
        if (i < entitylist.size() && Arrays.equals(entitylist.get(i).getPos().getCoord().getRel(), coordsOnTop)) {
            if (up) trigger();
            up = false;
        } else {
            up = true;
        }
    }

    @Override
    public AbstractPosition getPos() {
        return coords;
    }


    private void trigger() {
        GameplayScreen.msgSystem().add("You are standing on: " + coords.getRelX() +"," + coords.getRelY() +","+ coords.getZ(), "System");
        new AnimatedEntity(
            31,
            0,
            coords.cpy().addVector(0, 2, 1),
            new int[]{700,2000},
            true,
            false
        ).exist();
    }

    @Override
    public void setPos(AbstractPosition pos) {
        coords = pos.getCoord();
    }
}
