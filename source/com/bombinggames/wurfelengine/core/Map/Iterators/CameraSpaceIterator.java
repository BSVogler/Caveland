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
package com.bombinggames.wurfelengine.core.Map.Iterators;

import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Map;
import java.util.NoSuchElementException;

/**
 * A map iterator which loops only over the chunks covered by the camera (0-8).
 *
 * @author Benedikt Vogler
 */
public class CameraSpaceIterator extends AbstractMapIterator {

	private final int centerChunkX;
	private final int centerChunkY;
	private Chunk current;

	private int chunkNum = -1;

	/**
	 * Starts at z = -1.
	 *
	 * @param map
	 * @param centerCoordX the center chunk coordinate
	 * @param centerCoordY the center chunk coordinate
	 * @param startingZ to loop over ground level pass -1
	 * @param topLevel the top limit of the z axis
	 */
	public CameraSpaceIterator(Map map, int centerCoordX, int centerCoordY, int startingZ, int topLevel) {
		super(map);
		setTopLimitZ(topLevel);
		setStartingZ(startingZ);
		centerChunkX = centerCoordX;
		centerChunkY = centerCoordY;
	}

	/**
	 * Loops over the map areas covered by the camera.
	 *
	 * @return
	 */
	@Override
	public Block next() throws NoSuchElementException {
		if (blockIterator == null || !blockIterator.hasNext()) {
			//reached end of chunk, move to next chunk
			current = null;
			while (hasNextChunk() && current == null) {//if has one move to next
				chunkNum++;
				current = map.getChunk(
					centerChunkX - 1 + chunkNum % 3,
					centerChunkY - 1 + chunkNum / 3
				);
			}
			if (current != null) {
				blockIterator = current.getIterator(getStartingZ(), getTopLimitZ());//reset chunkIterator
			}
		}

		if (chunkNum >= 9 || blockIterator == null) {
			return null;
		}
		return blockIterator.next();
	}

	/**
	 * get the indices position inside the chunk/data matrix
	 *
	 * @return copy safe
	 */
	public int[] getCurrentIndex() {
		int[] inChunk = blockIterator.getCurrentIndex();
		return new int[]{
			(chunkNum % 3) * Chunk.getBlocksX() + inChunk[0],
			(chunkNum / 3) * Chunk.getBlocksY() + inChunk[1],
			inChunk[2]
		};
	}

	@Override
	public boolean hasNextChunk() {
		return getNextChunk(chunkNum) != null;
	}

	private Chunk getNextChunk(int start) {
		int i = start;
		if (i < 0) {
			i = 0;
		}
		while (i < 8) { //if has one move to next
			Chunk chunk = map.getChunk(
				centerChunkX - 1 + i % 3,
				centerChunkY - 1 + i / 3
			);
			i++;
			if (chunk != null) {
				return chunk;
			}
		}
		return null;
	}

	@Override
	public boolean hasNext() {
		return chunkNum < 9 && ((blockIterator != null && blockIterator.hasNext()) || hasNextChunk());
	}

	public boolean hasAnyBlock() {
		return getNextChunk(0) != null;
	}
}
