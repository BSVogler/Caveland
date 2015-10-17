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

package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Intersection;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 *The seletion indicator in the level editor.
 * @author Benedikt Vogler
 */
public class Selection extends AbstractEntity {
	private static final long serialVersionUID = 1L;
    private SimpleEntity normal;
    private Side normalSide;
    
    /**
     *
     */
    public Selection() {
        super((byte) 13);
		setSaveToDisk(false);
		setName("selectionEntity");
        
        normal = new SimpleEntity((byte) 14);
		EntityAnimation anim = new EntityAnimation(new int[]{200,200}, true, true);
		normal.setUseRawDelta(true);
		normal.enableShadow();
		normal.setAnimation(anim);
        normal.setLightlevel(10);
		normal.setSaveToDisk(false);
		normal.setName("normal");
    }

	@Override
	public AbstractEntity spawn(Point point) {
		normal.spawn(point);
		return super.spawn(point);
	}
	
	@Override
	public void setPosition(AbstractPosition pos) {
		setPosition(pos.toPoint());
	}
	
    @Override
    public void setPosition(Point pos) {
        super.setPosition( pos.toCoord());
        setHidden(getPosition().getZ() < 0);//hide if is under map
        normal.setPosition(pos);
    }
        
    /**
     *
     * @param side
     */
    public void setNormal(Side side){
        normalSide = side;
        if (side == Side.LEFT)
            normal.setRotation(120);
        else if (side == Side.TOP)
            normal.setRotation(0);
        if (side == Side.RIGHT)
            normal.setRotation(-120);
    }

    /**
     *
     * @return
     */
    public Side getNormalSide() {
        return normalSide;
    }
	
	/**
	 * if at ground does not move up
	 * @return the neighbour coordinate where the normal points to
	 */
	public Coordinate getCoordInNormalDirection(){
		Coordinate coords = getPosition().toCoord();
		if (normal.getPosition().getZ() > 0 && normalSide != null) {
			switch (normalSide) {
				case LEFT:
					coords = coords.goToNeighbour(5);
					break;
				case TOP:
					coords.addVector(0, 0, 1);
					break;
				case RIGHT:
					coords = coords.goToNeighbour(3);
					break;
			}
		}
		return coords;
	}
	
	    /**
     *
     * @return
     */
    public SimpleEntity getNormal() {
        return normal;
    }
    
    /**
     * Updates the selection using the screen position of the cursor.
     * @param view
     * @param screenX cursor position from left
     * @param screenY cursor position from top
     */
    public void update(GameView view, int screenX, int screenY){
       Intersection intersect = view.screenToGame(screenX, screenY);

		if (intersect != null && intersect.getPoint() != null) {
			setPosition(intersect.getPoint());
			setNormal(intersect.getNormal());
		}
    }
}
