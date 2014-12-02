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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import java.util.NoSuchElementException;

/**
 *A map iterator which loops only over area covered by the camera
 * @author Benedikt Vogler
 */
public class CameraSpaceIterator extends MapIterator {
	private int z=0;
	private int leftCoord;
	private int topCoord;
	private int bottomCoord;
	private int rightCoord;
	private Block current;

	/**
	 * Starts at z=-1.
	 * @param camera 
	 */
	public CameraSpaceIterator(Camera camera) {
		super();
		leftCoord = camera.getIndexedLeftBorder();
		topCoord = camera.getIndexedTopBorder();
		bottomCoord = camera.getIndexedBottomBorder();
		rightCoord = camera.getIndexedRightBorder();
		z=-1;
		//bring starting position to top left
		current = Controller.getMap().getData()[0].get(0).get(0);
		while (current.getPosition().getX() < leftCoord){
			nextX();
		}
		
		while (current.getPosition().getY() < topCoord){
			nextY();
		}
	}
	
	/**
	 * Reached end of covered area of y row?
	 * @return 
	 */
	@Override
	public boolean hasNextY() {
		return yIterator.hasNext() && current.getPosition().getY()<bottomCoord;
	}

	/**
	 * Reached end of x row?
	 * @return 
	 */
	@Override
	public boolean hasNextX() {
		return xIterator.hasNext() && current.getPosition().getX()<rightCoord;
	}

	/**
	 *Loops over the map areas covered by the camera.
	 * @return 
	 */
	@Override
	public Block next() throws NoSuchElementException {
		MapLayer[] mapdata = Controller.getMap().getData();
		current = yIterator.next();
		while (current.getPosition().getX() < leftCoord){
			nextX();
		}
		
		while (current.getPosition().getY() < topCoord){
			nextY();
		}
		
		if (!yIterator.hasNext()) {
			//if at end of y row
			if (xIterator.hasNext()){
				yIterator = xIterator.next().iterator();
			} else {//was at last block in layer
				if (z<Map.getBlocksZ()){
					MapLayer tmp = mapdata[z];
					xIterator = tmp.iterator();
					yIterator = tmp.get(0).iterator();
					z++;
				}
			}
		}
		if (z<0){
			//current pos -1 in z
			Block groundblock = Block.getInstance(2);
			groundblock.setPosition(
				new Coordinate(
					current.getPosition().getX(),
					current.getPosition().getY(),
					current.getPosition().getZ()-1
				)
			);
			current = groundblock;
		}
		return current;
	}
	
}
