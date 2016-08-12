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

import com.badlogic.gdx.utils.Pool;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Iterators.DataIterator;

/**
 *
 * @author Benedikt Vogler
 */
public class RenderChunk {

	/**
	 * a pool containing chunkdata
	 */
	private static final Pool<RenderCell[][][]> DATAPOOL;
	
	static {
		DATAPOOL = new Pool<RenderCell[][][]>(3) {
			@Override
			protected RenderCell[][][] newObject() {
				return new RenderCell[Chunk.getBlocksX()][Chunk.getBlocksY()][Chunk.getBlocksZ()];
			}
		};
	}
	
	/**
	 *
	 */
	public static void clearPool(){
		DATAPOOL.clear();
	}
	
	private final RenderCell data[][][];
	private Chunk chunk;
	private boolean cameraAccess;

	/**
	 * With init
	 *
	 * @param rS
	 * @param chunk linked chunk
	 */
	public RenderChunk(RenderStorage rS, Chunk chunk) {
		data = DATAPOOL.obtain();
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
		initData(rS);
	}

	/**
	 * fills every render cell with the according data from the map
	 *
	 * @param rS
	 */
	public void initData(RenderStorage rS) {
		int tlX = chunk.getTopLeftCoordinateX();
		int tlY = chunk.getTopLeftCoordinateY();

		//fill every data cell
		int blocksZ = Chunk.getBlocksZ();
		int blocksX = Chunk.getBlocksX();
		int blocksY = Chunk.getBlocksY();
		for (int xInd = 0; xInd < blocksX; xInd++) {
			for (int yInd = 0; yInd < blocksY; yInd++) {
				for (int z = 0; z < blocksZ; z++) {
					//update only if cell changed
					int block = chunk.getCellByIndex(xInd, yInd, z);
					if (data[xInd][yInd][z] == null || (block & 255) != data[xInd][yInd][z].getId()) {
						data[xInd][yInd][z] = RenderCell.getRenderCell((byte) (block & 255), (byte) ((block >> 8) & 255));
					}
					
					data[xInd][yInd][z].getPosition().set(
						tlX + xInd,
						tlY + yInd,
						z
					);
					data[xInd][yInd][z].setUnclipped();
					resetShadingFor(xInd, yInd, z);
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
	RenderCell getCell(int x, int y, int z) {
		if (z >= Chunk.getBlocksZ()) {
			return null;
		}
		return data[x - chunk.getTopLeftCoordinateX()][y - chunk.getTopLeftCoordinateY()][z];
	}

	RenderCell[][][] getData() {
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
	 * @param idexX index pos
	 * @param idexY index pos
	 * @param idexZ index pos
	 */
	public void resetShadingFor(int idexX, int idexY, int idexZ) {
		int blocksZ = Chunk.getBlocksZ();
		if (idexZ < Chunk.getBlocksZ() && idexZ >= 0) {
			RenderCell block = data[idexX][idexY][idexZ];
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

	/**
	 *
	 * @return
	 */
	public int getTopLeftCoordinateX() {
		return chunk.getTopLeftCoordinateX();
	}

	/**
	 *
	 * @return
	 */
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
	public DataIterator<RenderCell> getIterator(final int startingZ, final int limitZ) {
		return new DataIterator<>(
			data,
			startingZ,
			limitZ
		);
	}

	/**
	 *
	 * @return
	 */
	public int getChunkX() {
		return chunk.getChunkX();
	}

	/**
	 *
	 * @return
	 */
	public int getChunkY() {
		return chunk.getChunkY();
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	protected RenderCell getCellByIndex(int x, int y, int z) {
		return data[x][y][z];
	}

	/**
	 * If not used can be removed.
	 * @return true if a camera rendered this chunk this frame. 
	 */
	protected boolean cameraAccess() {
		return cameraAccess;
	}

	/**
	 * Camera used this chunk this frame?
	 *
	 * @param b
	 */
	protected void setCameraAccess(boolean b) {
		cameraAccess = b;
	}

	/**
	 *
	 */
	protected void dispose() {
		DATAPOOL.free(data);
	}

}
