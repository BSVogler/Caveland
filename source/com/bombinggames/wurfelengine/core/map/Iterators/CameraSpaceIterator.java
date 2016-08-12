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
package com.bombinggames.wurfelengine.core.map.Iterators;

import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import com.bombinggames.wurfelengine.core.map.rendering.RenderChunk;
import com.bombinggames.wurfelengine.core.map.rendering.RenderStorage;
import java.util.NoSuchElementException;

/**
 * A map iterator which loops only over the chunks covered by the camera (0-8).
 *
 * @author Benedikt Vogler
 */
public class CameraSpaceIterator {

	/**
	 * Always points to a block. Iterates over a chunk.
	 */
	private DataIterator<RenderCell> blockIterator;
	private final int centerChunkX;
	private final int centerChunkY;
	private RenderChunk currentChunk;

	private int topLevel;
	private final int startingZ;
	private int chunkNum = -1;
	private final RenderStorage renderStorage;

	/**
	 * Starts at z = -1.
	 *
	 * @param renderStorage
	 * @param centerCoordX the center chunk coordinate
	 * @param centerCoordY the center chunk coordinate
	 * @param startingZ to loop over ground level pass -1
	 * @param topLevel the top limit of the z axis, last level is included
	 */
	public CameraSpaceIterator(RenderStorage renderStorage, int centerCoordX, int centerCoordY, int startingZ, int topLevel) {
		this.renderStorage = renderStorage;
		this.topLevel = topLevel;
		this.startingZ = startingZ;
		centerChunkX = centerCoordX;
		centerChunkY = centerCoordY;
	}

	/**
	 * set the top/last limit of the iteration (including).
	 *
	 * @param zLimit
	 */
	public void setTopLimitZ(int zLimit) {
		this.topLevel = zLimit;
		if (blockIterator != null) {
			blockIterator.setTopLimitZ(zLimit);
		}
	}

	/**
	 * Loops over the map areas covered by the camera.
	 *
	 * @return Can return null.
	 */
	public RenderCell next() throws NoSuchElementException {
		//reached end of chunk?-> move to next chunk
		if (blockIterator == null || !blockIterator.hasNext()) {
			currentChunk = null;
			blockIterator = null;
			while (currentChunk == null && getNextChunk(chunkNum) != null) {//if has one move to next
				currentChunk = getNextChunk(chunkNum);
				chunkNum++;
			}
			//found chunk
			if (currentChunk != null) {
				blockIterator = currentChunk.getIterator(startingZ, topLevel);//reset chunkIterator
			} else {//can not find a new  block iterator
				return null;
			}
		}

		if (chunkNum < 9) {
			return blockIterator.next();
		} else {
			return null;
		}
	}

	/**
	 * get the indices position relative to a 3x3 chunk matrix.
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

	/**
	 * 
	 * @param current starting index: [0-8]
	 * @return 
	 */
	private RenderChunk getNextChunk(int current) {
		while (current < 8) { //if has one move to next
			current++;
			RenderChunk chunk = renderStorage.getChunk(
				centerChunkX - 1 + current % 3,
				centerChunkY - 1 + current / 3
			);
			if (chunk != null) {
				return chunk;
			}
		}
		return null;
	}

	/**
	 *
	 * @return
	 */
	public boolean hasNext() {
		return chunkNum < 9 && ((blockIterator != null && blockIterator.hasNext()) || getNextChunk(chunkNum) != null);
	}
}
