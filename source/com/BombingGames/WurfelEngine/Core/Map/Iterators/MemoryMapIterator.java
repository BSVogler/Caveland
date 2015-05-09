/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.BombingGames.WurfelEngine.Core.Map.Iterators;

import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Map.AbstractMap;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.ChunkMap;
import com.BombingGames.WurfelEngine.Core.Map.CompleteMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *Iterates over the blocks in memory.
 * @author Benedikt Vogler
 */
public class MemoryMapIterator extends AbstractMapIterator {
	/**
	 * use to iterate over chunks
	 */
	private Iterator<Chunk> chunkIterator;

	/**
	 *
	 * @param map
	 * @param startingZ
	 */
	public MemoryMapIterator(AbstractMap map, int startingZ) {
		super(map);
		setTopLimitZ(map.getBlocksZ()-1);
		setStartingZ(startingZ);
		
		if (useChunks) {
			ArrayList<Chunk> mapdata = ((ChunkMap) map).getData();
			chunkIterator = mapdata.iterator();
			blockIterator = mapdata.get(0).getIterator(startingZ, getTopLimitZ());
		} else {
			blockIterator = new DataIterator(
				((CompleteMap) map).getData(),
				startingZ,
				getTopLimitZ()
			);
		}
	}
	

	/**
	 *Loops over the complete map. Also loops over bottom layer
	 * @return 
	 */
	@Override
	public CoreData next() throws NoSuchElementException {
		CoreData block = (CoreData) blockIterator.next();
		if (useChunks && !blockIterator.hasNext()){
			//end of chunk, move to next chunk
			blockIterator = chunkIterator.next().getIterator(getStartingZ(), getTopLimitZ());
		}
		return block;
	}
	
	
	/**
	 * Reached end of y row?
	 * @return 
	 */
	@Override
	public boolean hasNextChunk() {
		return useChunks && chunkIterator.hasNext();
	}

	@Override
	public boolean hasNext() {
		return blockIterator.hasNext() || hasNextChunk();
	}

	int[] getCurrentIndex() {
		return blockIterator.getCurrentIndex();
	}
}
