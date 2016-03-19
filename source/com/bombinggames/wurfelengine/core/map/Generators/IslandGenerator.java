/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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

package com.bombinggames.wurfelengine.core.map.Generators;

import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Generator;

/**
 *Fenerates islands
 * @author Benedikt Vogler
 */
public class IslandGenerator implements Generator {
    private int mountainX;
    private int mountainY;

    /**
     *
     */
    public IslandGenerator() {
        //mountain
        mountainX = (int) (Math.random()*Chunk.getBlocksX()-1);
        mountainY = (int) (Math.random()*Chunk.getBlocksY()-1);
    }
    
    

    @Override
    public int generate(int x, int y, int z) {  
        if (z==0) return (byte)8;
        
        int height = Chunk.getBlocksZ()-1- Math.abs(mountainY-y)- Math.abs(mountainX-x);
        if (height>0 && z<height){//part of mountain?
            if (height-1 == z && z>2)
                return (byte)1;//grass on top
            
            if (z > 2)
                return (byte)2;
            else
                return (byte)8;//sand
        }

        //water
        if (z==1 || z==2) return (byte)9;
               
        return 0;
        
        //if (Math.random() < 0.15f && height < getBlocksZ()-1 && height > 2) data[x][y][height+1] = new Cell(34);
        //if (Math.random() < 0.15f && height < getBlocksZ()-1 && height > 2) data[x][y][height+1] = new Cell(35);
    }

	@Override
	public void spawnEntities(int x, int y, int z) {
	}
}
