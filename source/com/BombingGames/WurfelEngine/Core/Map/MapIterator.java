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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *Iterates over the blocks in camera space. The camera space is 3x3 chunks.
 * @author Benedikt Vogler
 */
public class MapIterator implements Iterator<Block>{
	private int z=0;
	/**
	 * use to iterate over chunks
	 */
	protected Iterator<Chunk> chunkIterator;
	/**
	 * always points to a block
	 */
	protected ChunkIterator blockIterator;
	private int topLimitZ;
	private int startingZ;

	public MapIterator() {
		ArrayList<Chunk> mapdata = Controller.getMap().getData();
		chunkIterator = mapdata.iterator();
		topLimitZ = Map.getBlocksZ()-1;
		startingZ = 0;
		blockIterator = mapdata.get(0).getIterator(0, topLimitZ);
	}
	
	

	@Override
	public boolean hasNext() {
		return blockIterator.hasNext() || hasNextChunk();
	}

	/**
	 *Loops over the complete map. Also loops over bottom layer
	 * @return 
	 */
	@Override
	public Block next() throws NoSuchElementException {
		Block block = blockIterator.next();
		if (!blockIterator.hasNext()){
			//end of chunk, move to next chunk
			blockIterator = chunkIterator.next().getIterator(startingZ, topLimitZ);
		}
		return block;
	}
	
	/**
	 * Should not be used because there should be no cases where you remove elements from the map.
	 */
	@Override
	public void remove() {
		//yIterator.remove();
	}

	
	/**
	 * set the top limit of the iteration
	 * @param zLimit 
	 */
	public void setTopLimitZ(int zLimit) {
		this.topLimitZ = zLimit;
	}

	public int getTopLimitZ() {
		return topLimitZ;
	}

	public int getStartingZ() {
		return startingZ;
	}

	/**
	 * resets the internal chunk iterator
	 * @param startingZ the new bottom layer
	 */
	public void setStartingZ(int startingZ) {
		this.startingZ = startingZ;
		blockIterator = Controller.getMap().getData().get(0).getIterator(startingZ, topLimitZ);
	}
	
	/**
	 * Reached end of y row?
	 * @return 
	 */
	public boolean hasNextChunk() {
		return chunkIterator.hasNext();
	}
	
}
