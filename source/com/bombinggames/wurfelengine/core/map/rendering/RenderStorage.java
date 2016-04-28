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
import com.bombinggames.wurfelengine.core.lightengine.AmbientOcclusionCalculator;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Iterators.DataIterator;
import com.bombinggames.wurfelengine.core.map.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * A RenderStorage is container which saves {@link RenderChunk}s used for rendering data only chunks. It manages which {@link Chunk}s must be transformed to {@link RenderChunk}s.
 * @author Benedikt Vogler
 */
public class RenderStorage implements Telegraph  {

	/**
	 * Stores the data of the map.
	 */
	private final LinkedList<RenderChunk> data = new LinkedList<>();
	private final List<Camera> cameraContainer;
	/**
	 * index means camera
	 */
	private final ArrayList<Integer> lastCenterX, lastCenterY;
	/**
	 * a list of Blocks marked as dirty. Dirty blocks are reshaded.
	 */
	private final LinkedList<RenderCell> dirtyFlags = new LinkedList<>();
	private float zRenderingLimit;

	/**
	 * Creates a new renderstorage.
	 */
	public RenderStorage() {
		this.cameraContainer = new ArrayList<>(1);
		lastCenterX = new ArrayList<>(1);
		lastCenterY = new ArrayList<>(1);
		zRenderingLimit = Chunk.getGameHeight();
	}
	
	public void preUpdate(float dt){
		resetShadingForDirty();
	}

	public void update(float dt){
		checkNeededChunks();
		//update rendderblocks
		for (RenderChunk renderChunk : data) {
			for (RenderCell[][] x : renderChunk.getData()) {
				for (RenderCell[] y : x) {
					for (RenderCell z : y) {
						if (z != null) {
							z.update(dt);
						}
					}
				}
			}
		}
	}
	
	/**
	 * checks which chunks must be loaded around the center
	 */
	private void checkNeededChunks() {
		//set every to false
		data.forEach(chunk -> chunk.setCameraAccess(false));
		
		//check if needed chunks are loaded
		for (int i = 0; i < cameraContainer.size(); i++) {
			Camera camera = cameraContainer.get(i);
			if (camera.isEnabled()) {
				//check 3x3 around center
				for (int x = -1; x <= 1; x++) {
					for (int y = -1; y <= 1; y++) {
						checkChunk(camera.getCenterChunkX() + x, camera.getCenterChunkY() + y);
					}
				}
				//check if center changed
				if (lastCenterX.get(i) == null || lastCenterX.get(i) != camera.getCenterChunkX() || lastCenterY.get(i) != camera.getCenterChunkY()) {
					//update the last center
					lastCenterX.set(i, camera.getCenterChunkX());
					lastCenterY.set(i, camera.getCenterChunkY());
					//rebuild
					RenderCell.rebuildCoverList();
				}
			}
		}
		
		//remove chunks which are not used
		data.forEach(chunk -> {
			if (!chunk.cameraAccess()) {
				chunk.dispose();
			}
		});
		data.removeIf(chunk -> !chunk.cameraAccess());
	}
	
	/**
	 * Checks if chunk must be loaded or deleted.
	 *
	 * @param x
	 * @param y
	 * @return true if created a new renderchunk in the check
	 */
	private void checkChunk(int x, int y) {
		RenderChunk rChunk = getChunk(x, y);
		//check if in storage
		if (rChunk == null) {
			Chunk mapChunk = Controller.getMap().getChunk(x, y);
			if (mapChunk != null) {
				//get chunk from pool if possible
				rChunk = new RenderChunk(this, mapChunk);
				data.add(rChunk);
				rChunk.setCameraAccess(true);
				AmbientOcclusionCalculator.calcAO(rChunk);
				hiddenSurfaceDetection(rChunk);

				//update neighbors
				RenderChunk neighbor = getChunk(x - 1, y);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor);
				}
				neighbor = getChunk(x + 1, y);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor);
				}
				neighbor = getChunk(x, y - 1);
				if (neighbor != null) {
					hiddenSurfaceDetection(neighbor);
				}
			}
		} else {
			rChunk.setCameraAccess(true);
		}
	}

	
	/**
	 * reset light to normal level for cordinates marked as dirty
	 */
	private void resetShadingForDirty() {
		for (RenderCell rb : dirtyFlags) {
			Coordinate coord = rb.getPosition();
			RenderChunk chunk = getChunk(coord);
			//should be loaded but check nevertheless
			if (chunk != null) {
				chunk.resetShadingFor(
					coord.getX() - chunk.getTopLeftCoordinateX(),
					coord.getY() - chunk.getTopLeftCoordinateY(),
					coord.getZ()
				);
			}
		}
		dirtyFlags.clear();
	}
	
	/**
	 * Marks this block as "dirty". O(n)
	 * @param rB
	 */
	public void setLightFlag(RenderCell rB) {
		if (!dirtyFlags.contains(rB))
			dirtyFlags.add(rB);
	}
	
	
	/**
	 * clears the used RenderChunks then resets
	 */
	public void reinitChunks() {
		RenderStorage rS = this;
		//loop over clone because may add new chunks to data
		@SuppressWarnings("unchecked")
		LinkedList<RenderChunk> dataclone = (LinkedList<RenderChunk>) data.clone();
		dataclone.forEach((RenderChunk rChunk) -> {
			rChunk.initData(rS);
		});
		dataclone.forEach((RenderChunk rChunk) -> {
			AmbientOcclusionCalculator.calcAO(rChunk);
			hiddenSurfaceDetection(rChunk);
		});			
	}
	
	/**
	 * get the chunk where the coordinates are on.
	 *
	 * @param coord not altered
	 * @return can return null if not loaded
	 */
	public RenderChunk getChunk(final Coordinate coord) {
		int left, top;
		//loop over storage
		for (RenderChunk chunk : data) {
			left = chunk.getTopLeftCoordinateX();
			top = chunk.getTopLeftCoordinateY();
			//check if coordinates are inside the chunk
			if (left <= coord.getX()
				&& coord.getX() < left + Chunk.getBlocksX()
				&& top <= coord.getY()
				&& coord.getY() < top + Chunk.getBlocksY()) {
				data.addFirst(data.removeLast());
				return chunk;
			}
		}
		return null;//not found
	}

	/**
	 * Get the chunk with the given chunk coords from the active pool. <br>Runtime: O(c) c: amount of
	 * chunks -&gt; O(1)
	 *
	 * @param chunkX
	 * @param chunkY
	 * @return if not in memory returns null
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
	public RenderCell getCell(final int x, final int y, final int z) {
		if (z < 0) {
			return getNewGroundCellInstance();
		}
		RenderChunk chunkWithBlock = null;
		int left, top;
		//loop over storage
		for (RenderChunk chunk : data) {
			left = chunk.getTopLeftCoordinateX();
			top = chunk.getTopLeftCoordinateY();
			//check if coordinates are inside the chunk
			if ( x >= left
				&& x < left + Chunk.getBlocksX()
				&& y >= top
				&& y < top + Chunk.getBlocksY()
			) {
				data.addFirst(data.removeLast());//move in front to speed up lookup
				chunkWithBlock = chunk;
				break;
			}
		}
		if (chunkWithBlock == null) {
			return null;
		} else {
			return chunkWithBlock.getCell(x, y, z);//find chunk in x coord
		}
	}

	/**
	 * If the cell can not be found returns null pointer.
	 *
	 * @param coord transform safe
	 * @return
	 */
	public RenderCell getCell(final Coordinate coord) {
		if (coord.getZ() < 0) {
			return getNewGroundCellInstance();
		}
		RenderChunk chunk = getChunk(coord);
		if (chunk == null) {
			return null;
		} else {
			return chunk.getCell(coord.getX(), coord.getY(), coord.getZ());//find chunk in x coord
		}
	}
	
		/**
	 * If the block can not be found returns null pointer.
	 *
	 * @param point transform safe
	 * @return
	 */
	public RenderCell getCell(final Point point) {
		if (point.getZ() < 0) {
			return getNewGroundCellInstance();
		}
		
		float x = point.x;
		float y = point.y;
		//bloated in-place code to avoid heap call with toCoord()
		int xCoord = Math.floorDiv((int) x, RenderCell.GAME_DIAGLENGTH);
		int yCoord = Math.floorDiv((int) y, RenderCell.GAME_DIAGLENGTH) * 2 + 1; //maybe dangerous to optimize code here!
		//find the specific coordinate (detail)
		switch (Coordinate.getNeighbourSide(x % RenderCell.GAME_DIAGLENGTH,
			y % RenderCell.GAME_DIAGLENGTH
		)) {
			case 0:
				yCoord -= 2;
				break;
			case 1:
				xCoord += yCoord % 2 == 0 ? 0 : 1;
				yCoord--;
				break;
			case 2:
				xCoord++;
				break;
			case 3:
				xCoord += yCoord % 2 == 0 ? 0 : 1;
				yCoord++;
				break;
			case 4:
				yCoord += 2;
				break;
			case 5:
				xCoord -= yCoord % 2 == 0 ? 1 : 0;
				yCoord++;
				break;
			case 6:
				xCoord--;
				break;
			case 7:
				xCoord -= yCoord % 2 == 0 ? 1 : 0;
				yCoord--;
				break;
		}

		return RenderStorage.this.getCell(xCoord, yCoord, Math.floorDiv((int) point.z, RenderCell.GAME_EDGELENGTH));
	}
	
	
	
	/**
	 * performs a simple viewFrustum check by looking at the direct neighbours.
	 *
	 * @param chunk
	 */
	public void hiddenSurfaceDetection(final RenderChunk chunk) {
		if (chunk == null) {
			throw new IllegalArgumentException();
		}
		RenderCell[][][] chunkData = chunk.getData();

		chunk.resetClipping();

		//loop over floor for ground level
		//DataIterator floorIterator = chunk.getIterator(0, 0);
//		while (floorIterator.hasNext()) {
//			if (((Block) floorIterator.next()).hidingPastBlock())
//				chunk.getCell(
//					floorIterator.getCurrentIndex()[0],
//					floorIterator.getCurrentIndex()[1],
//					chunkY)setClippedTop(
//					floorIterator.getCurrentIndex()[0],
//					floorIterator.getCurrentIndex()[1],
//					-1
//				);
//		}
		//iterate over chunk
		DataIterator<RenderCell> dataIter = new DataIterator<>(
			chunkData,
			0,
			(int) (zRenderingLimit/RenderCell.GAME_EDGELENGTH)
		);

		while (dataIter.hasNext()) {
			RenderCell current = dataIter.next();//next is the current block

			if (current != null) {
				//calculate index position relative to camera border
				final int x = dataIter.getCurrentIndex()[0];
				final int y = dataIter.getCurrentIndex()[1];
				final int z = dataIter.getCurrentIndex()[2];

				//left side
				//get neighbour block
				RenderCell neighbour = getCellByIndex(chunk, x - ((y % 2 == 0) ? 1 : 0), y + 1, z);//next row can be shifted right(?)

				if (neighbour != null
					&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))) {
					current.setClippedLeft();
				}

				//right side
				//get neighbour block
				neighbour = getCellByIndex(chunk, x + ((y % 2 == 0) ? 0 : 1), y + 1, z);//next row is shifted right

				if (neighbour != null
					&& (neighbour.hidingPastBlock() || (neighbour.isLiquid() && current.isLiquid()))) {
					current.setClippedRight();
				}

				//check top
				if (z < Chunk.getBlocksZ() - 1) {
					neighbour = getCellByIndex(chunk, x, y + 2, z + 1);
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
	 * Helper function. Gets a block at an index. index can be outside of this chunk. If it is outside will get the correct chunk.
	 *
	 * @param chunk the chunk where the index shoulde be found on
	 * @param x index
	 * @param y index
	 * @param z index
	 * @return
	 */
	private RenderCell getCellByIndex(RenderChunk chunk, int x, int y, int z) {
		if (x < 0 || y >= Chunk.getBlocksY() || x >= Chunk.getBlocksX()) {//index outside current chunk
			return getCell(
				chunk.getTopLeftCoordinateX() + x,
				chunk.getTopLeftCoordinateY() + y,
				z
			);
		} else {
			return chunk.getCellByIndex(x, y, z);
		}
	}

	private RenderCell getNewGroundCellInstance() {
		return RenderCell.getRenderCell((byte) WE.getCVars().getValueI("groundBlockID"), (byte) 0); //the representative of the bottom layer (ground) block
	}

	public LinkedList<RenderChunk> getData() {
		return data;
	}

	/**
	 * avoids duplicates
	 * @param camera 
	 */
	public void addCamera(Camera camera) {
		if (!cameraContainer.contains(camera)) {//avoid duplicates
			this.cameraContainer.add(camera);
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
		if (coords.getZ() >= zRenderingLimit*RenderCell.GAME_EDGELENGTH) {
			return true;
		}
		
		if (coords.getZ() < -1)//filter below lowest level
			return true;
		
		RenderCell block = getCell(coords);
		if (block==null)
			return false;
		return block.isClipped();
	}

	/**
	 * renders to this layer not including
	 *
	 * @return coordinate
	 */
	public float getZRenderingLimit() {
		return zRenderingLimit;
	}

	/**
	 * renders to this layer not including
	 *
	 * @param height game space
	 */
	public void setZRenderingLimit(float height) {
		zRenderingLimit = height;
		if (zRenderingLimit < 0) {
			zRenderingLimit = 0;
		}
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Events.mapChanged.getId()) {
			reinitChunks();
			RenderCell.rebuildCoverList();
			return true;
		}
		
		return false;
	}

	public void dispose() {
		RenderChunk.clearPool();
		MessageManager.getInstance().removeListener(this, Events.mapChanged.getId());
	}

}
