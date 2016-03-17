/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
package com.bombinggames.wurfelengine.core.map.rendering;

import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Iterators.DataIterator;

/**
 *
 * @author Benedikt Vogler
 */
public class RenderChunk {

	private final RenderBlock data[][][];
	private Chunk chunk;
	private boolean cameraAccess;

	/**
	 * Without init
	 */
	public RenderChunk() {
		data = new RenderBlock[Chunk.getBlocksX()][Chunk.getBlocksY()][Chunk.getBlocksZ()];
	}

	/**
	 * With init
	 *
	 * @param rS
	 * @param chunk linked chunk
	 */
	public RenderChunk(RenderStorage rS, Chunk chunk) {
		data = new RenderBlock[Chunk.getBlocksX()][Chunk.getBlocksY()][Chunk.getBlocksZ()];
		init(rS, chunk);
	}

	/**
	 * update the content
	 *
	 * @param rS
	 * @param chunk
	 */
	public void init(RenderStorage rS, Chunk chunk) {
		this.chunk = chunk;
		init(rS);
	}

	/**
	 * fills every cell with the accoring data
	 *
	 * @param rS
	 */
	public void init(RenderStorage rS) {
		int tlX = chunk.getTopLeftCoordinateX();
		int tlY = chunk.getTopLeftCoordinateY();

		//fill every data cell
		int blocksZ = Chunk.getBlocksZ();
		int blocksX = Chunk.getBlocksX();
		int blocksY = Chunk.getBlocksY();
		for (int x = 0; x < blocksX; x++) {
			for (int y = 0; y < blocksY; y++) {
				for (int z = 0; z < blocksZ; z++) {
					Block block = chunk.getBlockViaIndex(x, y, z);
					//update only if cell changed
					if (data[x][y][z] == null || block != data[x][y][z].getBlockData()) {
						if (block != null) {
							data[x][y][z] = block.toRenderBlock();
						} else {
							data[x][y][z] = new RenderBlock();
						}
						data[x][y][z].setPosition(
							new Coordinate(
								tlX + x,
								tlY + y,
								z
							)
						);
					}
					data[x][y][z].setUnclipped();
					resetShadingCoord(x, y, z);
				}
			}
		}
	}

	/**
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return
	 */
	RenderBlock getBlock(int x, int y, int z) {
		if (z >= Chunk.getBlocksZ()) {
			return null;
		}
		return data[x - chunk.getTopLeftCoordinateX()][y - chunk.getTopLeftCoordinateY()][z];
	}

	RenderBlock[][][] getData() {
		return data;
	}

	/**
	 *
	 */
	protected void resetClipping() {
		int blocksZ = Chunk.getBlocksZ();
		int blocksX = Chunk.getBlocksX();
		int blocksY = Chunk.getBlocksY();
		for (int x = 0; x < blocksX; x++) {
			for (int y = 0; y < blocksY; y++) {
				for (int z = 0; z < blocksZ; z++) {
					if (data[x][y][z] != null) {
						data[x][y][z].setUnclipped();
					}
				}
			}
		}
	}

	/**
	 * Resets the shading for one block. Calculates drop shadow from blocks
	 * above.
	 *
	 * @param idexX
	 * @param idexY
	 * @param idexZ
	 */
	public void resetShadingCoord(int idexX, int idexY, int idexZ) {
		int blocksZ = Chunk.getBlocksZ();
		if (idexZ < Chunk.getBlocksZ() && idexZ >= 0) {
			RenderBlock block = getBlockViaIndex(idexX, idexY, idexZ);
			if (block != null) {
				data[idexX][idexY][idexZ].setLightlevel(1);

				if (idexZ < blocksZ - 2
					&& (data[idexX][idexY][idexZ + 1] == null
					|| data[idexX][idexY][idexZ + 1].isTransparent())) {
					//two block above is a block casting shadows
					if (data[idexX][idexY][idexZ + 2] != null
						&& !data[idexX][idexY][idexZ + 2].isTransparent()) {
						data[idexX][idexY][idexZ].setLightlevel(0.8f, Side.TOP, 0);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 1);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 2);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 3);//todo every vertex
					} else if (idexZ < blocksZ - 3
						&& (data[idexX][idexY][idexZ + 2] == null
						|| data[idexX][idexY][idexZ + 2].isTransparent())
						&& data[idexX][idexY][idexZ + 3] != null
						&& !data[idexX][idexY][idexZ + 3].isTransparent()) {
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 0);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 1);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 2);//todo every vertex
						data[idexX][idexY][idexZ].setLightlevel(0.9f, Side.TOP, 3);//todo every vertex
					}
				}
			}
		}
	}

	public int getTopLeftCoordinateX() {
		return chunk.getTopLeftCoordinateX();
	}
	
	public int getTopLeftCoordinateY() {
		return chunk.getTopLeftCoordinateY();
	}

	/**
	 * Returns an iterator which iterates over the data in this chunk.
	 *
	 * @param startingZ
	 * @param limitZ the last layer (including).
	 * @return
	 */
	public DataIterator<RenderBlock> getIterator(final int startingZ, final int limitZ) {
		return new DataIterator<>(
			data,
			startingZ,
			limitZ
		);
	}

	public int getChunkX() {
		return chunk.getChunkX();
	}

	public int getChunkY() {
		return chunk.getChunkY();
	}

	RenderBlock getBlockViaIndex(int x, int y, int z) {
		return data[x][y][z];
	}

	/**
	 *
	 * @return treu if a camera rendered this chunk this frame
	 */
	boolean cameraAccess() {
		return cameraAccess;
	}

	/**
	 * camera used this chunk
	 *
	 * @param b
	 */
	void setCameraAccess(boolean b) {
		cameraAccess = b;
	}

}
