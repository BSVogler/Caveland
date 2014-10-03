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

package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Intersection;
import com.BombingGames.WurfelEngine.Core.Map.Point;

/**
 *
 * @author Benedikt Vogler
 */
public class Selection extends AbstractEntity {
    private AnimatedEntity normal;
    private Sides normalSide;
    
    /**
     *
     */
    public Selection() {
        super(13);
        setLightlevel(10);
        
        normal = new AnimatedEntity(14, 0, new int[]{200,200}, true, true);
        normal.ignoreGameSpeed(true);
        normal.setLightlevel(10);
    }

	@Override
	public AbstractEntity spawn(Point point) {
		normal.spawn(point);
		return super.spawn(point);
	}
	
	

    @Override
    public void update(float delta) {
        
//        if (normal.getPosition().getNormal()==0)
//            normal.setPosition(normal.getPosition().cpy().addVector(-Block.GAME_DIAGLENGTH2, Block.GAME_DIAGLENGTH2, 0));
//        else if (normal.getPosition().getNormal()==1)
//            normal.setPosition(normal.getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH));
//            else if (normal.getPosition().getNormal()==2)
//                normal.setPosition(normal.getPosition().cpy().addVector(Block.GAME_DIAGLENGTH2, Block.GAME_DIAGLENGTH2, 0));
    }

    @Override
    public void setPosition(AbstractPosition pos) {
        super.setPosition( pos.getCoord());
        setHidden(getPosition().getHeight()<0);
        normal.setPosition(pos.cpy().addVector(0, 1, 0));
    }
        
    /**
     *
     * @param side
     */
    public void setNormal(Sides side){
        normalSide = side;
        if (side == Sides.LEFT)
            normal.setRotation(120);
        else if (side == Sides.TOP)
            normal.setRotation(0);
        if (side == Sides.RIGHT)
            normal.setRotation(-120);
    }

    /**
     *
     * @return
     */
    public Sides getNormalSides() {
        return normalSide;
    }
    
    /**
     * Updates thhe selection using the screen position of the cursor.
     * @param view
     * @param screenX cursor position
     * @param screenY cursor position
     */
    public void update(GameView view, int screenX, int screenY){
        Intersection intersect = view.screenToGameRaytracing(screenX, screenY);
                
            if (intersect.getPoint() != null){
               setPosition( intersect.getPoint() );
               setNormal( Sides.normalToSide( intersect.getNormal() ) );
            }
    }
}
