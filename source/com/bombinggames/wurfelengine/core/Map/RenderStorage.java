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
package com.bombinggames.wurfelengine.core.Map;

import com.badlogic.gdx.Gdx;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Iterators.DataIterator;
import java.util.AbstractCollection;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class RenderStorage {

	/**
	 * Stores the data of the map.
	 */
	private final ArrayList<RenderChunk> data = new ArrayList<>(18);//amout of cameras *9
	private final AbstractCollection<Camera> cameraContainer;

	/**
	 *
	 * @param cameraContainer
	 */
	public RenderStorage(AbstractCollection<Camera> cameraContainer) {
		this.cameraContainer = cameraContainer;
	}

	
	public void update(float dt){
		checkNeededChunks();
	}
	
		/**
	 * checks which chunks must be loaded around the center
	 */
	private void checkNeededChunks() {
		//set every to false
		data.forEach(chunk -> chunk.setCameraAccess(false));
		
		//check if needed chunks are there and mark them
		for (Camera camera : cameraContainer) {
			for (int x = -1; x < 1; x++) {
				for (int y = -1; y < 1; y++) {
					checkChunk(camera.getCenterChunkX() + x, camera.getCenterChunkY() + y);
				}
			}
		}
		//remove chunks which are not used
		data.removeIf(chunk -> !chunk.cameraAccess());
	}
	
	/**
	 * Checks if chunk must be loaded or deleted.
	 *
	 * @param x
	 * @param y
	 */
	private void checkChunk(int x, int y) {
		RenderChunk rChunk = getChunk(x, y);
		if (rChunk == null) {//not in storage
			Chunk mapChunk = Controller.getMap().getChunk(x, y);
			if (mapChunk != null) {
				RenderChunk newrChunk = new RenderChunk(mapChunk);
				data.add(newrChunk);
				newrChunk.setCameraAccess(true);
			}
		} else {
			rChunk.setCameraAccess(true);
		}
	}
	
	/**
	 * get the chunk where the coordinates are on
	 *
	 * @param coord not altered
	 * @return can return null if not loaded
	 */
	public RenderChunk getChunk(final Coordinate coord) {
		//checks every chunk in memory
		for (RenderChunk chunk : data) {
			int left = chunk.getTopLeftCoordinate().getX();
			int top = chunk.getTopLeftCoordinate().getY();
			//check if coordinates are inside the chunk
			if (left <= coord.getX()
				&& coord.getX() < left + Chunk.getBlocksX()
				&& top <= coord.getY()
				&& coord.getY() < top + Chunk.getBlocksY()
			) {
				return chunk;
			}
		}
		return null;//not found
	}

	/**
	 * get the chunk with the given chunk coords. <br>Runtime: O(c)  c:
	 * amount of chunks -&gt; O(1)
	 *
	 * @param chunkX
	 * @param chunkY
	 * @return if not in memory return null
	 */
	public RenderChunk getChunk(int chunkX, int chunkY) {
		for (RenderChunk chunk : data) {
			if (chunkX == chunk.getChunkX()
				&& chunkY == chunk.getChunkY()) {
				return chunk;
			}
		}
		return null;//not found
	}

		/**
	 * Returns a block without checking the parameters first. Good for debugging
	 * and also faster. O(n)
	 *
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return the single block you wanted
	 */
	public RenderBlock getBlock(final int x, final int y, final int z) {
		return getBlock(new Coordinate(x, y, z));
	}

		/**
	 * If the block can not be found returns null pointer.
	 *
	 * @param coord
	 * @return
	 */
	public RenderBlock getBlock(final Coordinate coord) {
		if (coord.getZ() < 0) {
			return getNewGroundBlockInstance();
		}
		RenderChunk chunk = getChunk(coord);
		if (chunk == null) {
			return null;
		} else {
			return chunk.getBlock(coord.getX(), coord.getY(), coord.getZ());//find chunk in x coord
		}
	}
	
	/**
	 * performs a simple viewFrustum check by looking at the direct neighbours.
	 *
	 * @param camera the camera which is used for the limits. Gets stored
	 * globally so only one camera can be used. Calling this method more then
	 * once ith different cameras overwrites the result.
	 * @param chunkX chunk coordinate
	 * @param chunkY chunk coordinate
	 */
	public void hiddenSurfaceDetection(final Camera camera, final int chunkX, final int chunkY) {
		Gdx.app.debug("ChunkMap", "HSD for chunk " + chunkX + "," + chunkY);
		RenderChunk chunk = getChunk(chunkX, chunkY);
		if (chunk!=null) {
			RenderBlock[][][] chunkData = chunk.getData();

			chunk.resetClipping();

			//loop over floor for ground level
			//DataIterator floorIterator = chunk.getIterator(0, 0);
	//		while (floorIterator.hasNext()) {
	//			if (((Block) floorIterator.next()).hidingPastBlock())
	//				chunk.getBlock(
	//					floorIterator.getCurrentIndex()[0],
	//					floorIterator.getCurrentIndex()[1],
	//					chunkY)setClippedTop(
	//					floorIterator.getCurrentIndex()[0],
	//					floorIterator.getCurrentIndex()[1],
	//					-1
	//				);
	//		}
			//iterate over chunk
			DataIterator<RenderBlock> dataIter = new DataIterator<>(
				chunkData,
				0,
				camera.getZRenderingLimit() - 1
			);

			while (dataIter.hasNext()) {
				RenderBlock current = dataIter.next();//next is the current block

				if (current != null) {
					//calculate index position relative to camera border
					final int x = dataIter.getCurrentIndex()[0];
					final int y = dataIter.getCurrentIndex()[1];
					final int z = dataIter.getCurrentIndex()[2];

					RenderBlock neighbour;
					//left side
					//get neighbour block
					if (y % 2 == 0) {//next row is shifted right
						neighbour = getIndex(chunk, x - 1, y + 1, z);
					} else {
						neighbour = getIndex(chunk, x, y + 1, z);
					}

					if (neighbour != null
						&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))) {
						current.setClippedLeft();
					}

					//right side
					//get neighbour block
					if (y % 2 == 0)//next row is shifted right
					{
						neighbour = getIndex(chunk, x, y + 1, z);
					} else {
						neighbour = getIndex(chunk, x + 1, y + 1, z);
					}

					if (neighbour != null
						&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))) {
						current.setClippedRight();
					}

					//check top
					if (z < Chunk.getBlocksZ() - 1) {
						neighbour = getIndex(chunk, x, y + 2, z + 1);
						if ((chunkData[x][y][z + 1] != null
							&& (chunkData[x][y][z + 1].hidingPastBlock()
							|| chunkData[x][y][z + 1].isLiquid() && current.isLiquid()))
							|| (neighbour != null && neighbour.hidingPastBlock())) {
							current.setClippedTop();
						}
					}
				}
			}
		}
	}
	
	/**
	 * Helper function. Gets a block at an index. can be outside of this chunk
	 *
	 * @param chunk
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	private RenderBlock getIndex(RenderChunk chunk, int x, int y, int z) {
		if (x < 0 || y >= Chunk.getBlocksY() || x >= Chunk.getBlocksX()) {//index outside current chunk
			return getBlock(
				chunk.getTopLeftCoordinate().getX() + x,
				chunk.getTopLeftCoordinate().getY() + y,
				z
			);
		} else {
			return chunk.getBlockViaIndex(x, y, z);
		}
	}

	private RenderBlock getNewGroundBlockInstance() {
		return Block.getInstance((byte) WE.getCVars().getValueI("groundBlockID")).toRenderBlock(); //the representative of the bottom layer (ground) block
	}

}