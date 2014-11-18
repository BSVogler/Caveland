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

import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;

/**
 *A Sea Block which has a "waves" effect.
 * @author Benedikt Vogler
 */
public class Sea extends Block implements IsSelfAware{
	private static final long serialVersionUID = 1L;
    /**
     *
     */
    public static final int WAVE_AMPLITUDE = AbstractGameObject.GAME_EDGELENGTH/2-10;
    private static final float wavespeed = 1/700f; //the smaller the slower
    private static float currentX = 0;
    private static final int waveWidth = Map.getBlocksX()/7;
    
    private int startvalue;
    
    private Coordinate coords;
	private int offsetY;
        
    /**
     *
     * @param id
     */
    public Sea(final int id) {
        super(id);
        setTransparent(true);
                
        if (coords!=null)
			startvalue = (int) (offsetY + Math.random()*WAVE_AMPLITUDE - WAVE_AMPLITUDE);
       
    }

    @Override
    public Coordinate getPosition() {
        return coords;
    }

    @Override
    public void setPosition(AbstractPosition pos) {
        coords = pos.getCoord();
    }

    @Override
    public void update(float delta, int x, int y, int z) {
		if (coords!=null){
			offsetY =
				(int) (startvalue +
					Math.sin(
						(currentX-coords.getRelX()-coords.getRelY())
							* Math.PI/waveWidth
					)*WAVE_AMPLITUDE);
		}
    }
    
    /**
     *
     * @param dt
     */
    public static void staticUpdate(float dt){
        currentX += dt*wavespeed;
    }
	
	@Override
	public Block spawn(Coordinate coords) {
		this.coords = coords;
		return this;
	}
}