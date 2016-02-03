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

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.lightengine.AmbientOcclusionCalculator;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Iterators.DataIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * A RenderStorage is container which saves {@link RenderChunk}s used for rendering data only chunks. It manages which {@link Chunk}s must be transformed to {@link RenderChunk}s.
 * @author Benedikt Vogler
 */
public class RenderStorage implements Telegraph  {

	/**
	 * Stores the data of the map.
	 */
	private final ArrayList<RenderChunk> data;
	private final List<Camera> cameraContainer;
	private final ArrayList<Integer> lastCenterX;
	private final ArrayList<Integer> lastCenterY;
	/**
	 * a list of Blocks marked as dirty
	 */
	private final ArrayList<RenderBlock> dirtyFlags = new ArrayList<>(20);
	private int zRenderingLimit;

	/**
	 * Creates a new renderstorage.
	 */
	public RenderStorage() {
		this.cameraContainer = new ArrayList<>(1);
		data = new ArrayList<>(cameraContainer.size()*9);
		lastCenterX = new ArrayList<>(1);
		lastCenterY = new ArrayList<>(1);
		zRenderingLimit = Chunk.getBlocksZ();
		MessageManager.getInstance().addListener(this, Events.chunkChanged.getId());
	}

	public void update(float dt){
		checkNeededChunks();
		//update rendderblocks
		for (RenderChunk renderChunk : data) {
			for (RenderBlock[][] x : renderChunk.getData()) {
				for (RenderBlock[] y : x) {
					for (RenderBlock z : y) {
						if (z != null) {
							z.update(dt);
						}
					}
				}
			}
		}
	}
	
	public void preUpdate(float dt){
		resetShadingForDirty();
	}
	
	/**
	 * reset light to normal level for cordinates marked as dirty
	 */
	private void resetShadingForDirty() {
		for (RenderBlock rb : dirtyFlags) {
			Coordinate coord = rb.getPosition();
			RenderChunk chunk = getChunk(coord);
			//should be loaded but check nevertheless
			if (chunk != null) {
				chunk.resetShadingCoord(
					coord.getX() - chunk.getTopLeftCoordinate().getX(),
					coord.getY() - chunk.getTopLeftCoordinate().getY(),
					coord.getZ()
				);
			}
		}
		dirtyFlags.clear();
	}
	
		/**
	 * marks this block as "dirty".
	 * @param rB
	 */
	public void setLightFlag(RenderBlock rB) {
		if (!dirtyFlags.contains(rB))
			dirtyFlags.add(rB);
	}
	
	/**
	 * checks which chunks must be loaded around the center
	 */
	private void checkNeededChunks() {
		//set every to false
		data.forEach(chunk -> chunk.setCameraAccess(false));
		
		//check if needed chunks are there and mark them
		for (int i = 0; i < cameraContainer.size(); i++) {
			Camera camera = cameraContainer.get(i);
			if (camera.isEnabled()) {
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						checkChunk(camera.getCenterChunkX() + x, camera.getCenterChunkY() + y);//todo remove check if loaded
					}
				}
				//check if center changed
				if (lastCenterX.get(i)==null || lastCenterY==null || lastCenterX.get(i) != camera.getCenterChunkX() || lastCenterY.get(i) != camera.getCenterChunkY()) {
					//if (changesToCameraCache) {
					camera.fillCameraContentBlocks();
					lastCenterX.set(i, camera.getCenterChunkX());
					lastCenterY.set(i, camera.getCenterChunkY());
				}
			}
		}
		
		//remove chunks which are not used
		data.removeIf(chunk -> !chunk.cameraAccess());
	}
	
	public void refresh(){
		data.clear();
		checkNeededChunks();
	}
	
	/**
	 * Checks if chunk must be loaded or deleted.
	 *
	 * @param x
	 * @param y
	 * @return true if added a new renderchunk in the check
	 */
	private boolean checkChunk(int x, int y) {
		RenderChunk rChunk = getChunk(x, y);
		if (rChunk == null) {//not in storage
			Chunk mapChunk = Controller.getMap().getChunk(x, y);
			if (mapChunk != null) {
				RenderChunk newRChunk = new RenderChunk(this, mapChunk);
				data.add(newRChunk);
				newRChunk.setCameraAccess(true);
				AmbientOcclusionCalculator.calcAO(newRChunk);
				hiddenSurfaceDetection(newRChunk, zRenderingLimit - 1);

				//update neighbors
				RenderChunk neighbor = getChunk(x - 1, y);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor, zRenderingLimit - 1);
				}
				neighbor = getChunk(x + 1, y);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor, zRenderingLimit - 1);
				}
				neighbor = getChunk(x, y - 1);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor, zRenderingLimit - 1);
				}
				return true;
			}
		} else {
			rChunk.setCameraAccess(true);
		}
		return false;
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
				&& coord.getY() < top + Chunk.getBlocksY()) {
				return chunk;
			}
		}
		return null;//not found
	}

	/**
	 * get the chunk with the given chunk coords. <br>Runtime: O(c) c: amount of
	 * chunks -&gt; O(1)
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
	 * @param chunk
	 * @param toplimit
	 */
	public void hiddenSurfaceDetection(final RenderChunk chunk, final int toplimit) {
		if (chunk == null) {
			throw new IllegalArgumentException();
		}
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
			toplimit
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
	
	/**
	 * Helper function. Gets a block at an index. can be outside of this chunk
	 *
	 * @param chunk
	 * @param x index
	 * @param y index
	 * @param z index
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

	public ArrayList<RenderChunk> getData() {
		return data;
	}

	/**
	 * avoids duplicates
	 * @param camera 
	 */
	public void addCamera(Camera camera) {
		if (!cameraContainer.contains(camera)) {//avoid duplicates
			this.cameraContainer.add(camera);
			data.ensureCapacity(cameraContainer.size()*9);//redundant?
			lastCenterX.add(null);
			lastCenterY.add(null);
		}
	}
	
	/**
	 * get if a coordinate is clipped
	 *
	 * @param coords
	 * @return
	 */
	public boolean isClipped(Coordinate coords) {
		if (coords.getZ() >= zRenderingLimit) {
			return true;
		}
		
		if (coords.getZ() < -1)//filter below lowest level
			return true;
		
		RenderBlock block = getBlock(coords);
		if (block==null)
			return false;
		return block.isClipped();
	}

	/**
	 * 
	 * @return coordinate
	 */
	public int getZRenderingLimit() {
		return zRenderingLimit;
	}
	

	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message==Events.chunkChanged.getId()) {
			Chunk chunk = (Chunk) msg.extraInfo;
			data.remove(getChunk(chunk.getChunkX(), chunk.getChunkY()));//chunk is outdatet so remove it
			checkChunk(chunk.getChunkX(), chunk.getChunkY());
			return true;
		}
		return false;
	}

	public void dispose() {
		MessageManager.getInstance().removeListener(this, Events.chunkChanged.getId());
	}

}
