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

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import java.util.NoSuchElementException;

/**
 *A map iterator which loops only over area covered by the camera
 * @author Benedikt Vogler
 */
public class CameraSpaceIterator extends AbstractMapIterator {
	private int centerChunkX;
	private int centerChunkY;
	private Chunk current;
	
	/**
	 * Starts at z=-1. 
	 * @param centerCoordX the center chunk coordinate
	 * @param centerCoordY the center chunk coordinate
	 * @param startingZ to loop over ground level pass -1
	 */
	public CameraSpaceIterator(int centerCoordX, int centerCoordY, int startingZ, int topLevel) {
		setTopLimitZ(topLevel);
		setStartingZ(startingZ);
		centerChunkX = centerCoordX;
		centerChunkY = centerCoordY;
		//bring starting position to top left
		current = Controller.getMap().getChunk(centerChunkX-1, centerChunkY-1);
		blockIterator = current.getIterator(startingZ, topLevel);
	}

	/**
	 *Loops over the map areas covered by the camera.
	 * @return 
	 */
	@Override
	public Block next() throws NoSuchElementException {
		if (!blockIterator.hasNext()){
			//reached end of chunk, move to next chunk
			if (hasNextChunk()){//if has one move to next
				if (centerChunkX >= current.getChunkX()) {//current is left or middle column
					//continue one chunk to the right
					current = Controller.getMap().getChunk(
						current.getChunkX()+1,
						current.getChunkY()
					);
				} else {
					//move one row down
					current = Controller.getMap().getChunk(
						centerChunkX-1,
						current.getChunkY()+1
					);
				}
				
				blockIterator = current.getIterator(getStartingZ(), getTopLimitZ());//reset chunkIterator
			}
		}
			
		return blockIterator.next();
	}
	
	/**
	 * get the indices position inside the chunk
	 * @return 
	 */
	public int[] getCurrentIndex(){
		return blockIterator.getCurrentIndex();
	}
	
	/**
	 * 
	 * @return the chunk which the chunk iterator currently points to
	 */
	public Chunk getCurrentChunk(){
		return current;
	}

	@Override
	public boolean hasNextChunk() {
		return current.getChunkX() < centerChunkX+1//has next x
			|| current.getChunkY() < centerChunkY+1; //or has next Y
	}

}
