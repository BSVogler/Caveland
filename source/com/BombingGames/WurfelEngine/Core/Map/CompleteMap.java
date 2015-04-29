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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import java.io.File;
import java.io.IOException;

/**
 *A map where every available data is stored in one big array. No streaming.
 * @author Benedikt Vogler
 */
public class CompleteMap extends AbstractMap {
	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;
	private final CoreData[][][] data;

	public CompleteMap(final File name, int saveSlot) throws IOException {
		this(name, getDefaultGenerator(), saveSlot);
	}

	
	public CompleteMap(final File name, Generator generator, int saveSlot) throws IOException {
		super(name, generator, saveSlot);
		blocksX = 100;
        blocksY = 200;
        blocksZ =  10;
		data = new CoreData[blocksX][blocksY][blocksZ];
	}

	
	@Override
	public CoreData getBlock(Coordinate coord) {
		if (coord.getZ() < 0)
			return getGroundBlock();
		else
			return data[coord.getX()][coord.getY()][coord.getZ()];
	}

	@Override
	public CoreData getBlock(int x, int y, int z) {
		return data[blocksX/2+x][blocksY/2+y][z];
	}

	@Override
	public boolean save(int saveSlot) {
		return false;
	}

	@Override
	public void setBlock(RenderBlock block) {
		data[block.getPosition().getX()][block.getPosition().getX()][block.getPosition().getZ()] = block.getCoreData();
	}

	@Override
	public void setBlock(Coordinate coord, CoreData block) {
		data[coord.getX()][coord.getX()][coord.getZ()] = block;
	}
	
	
	
	 /**
     * Get the data of the map
     * @return
     */
	public CoreData[][][] getData() {
		return data;
	}

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 * @return
	 */
	@Override
	public int getBlocksX() {
		return blocksX;
	}

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 * @return
	 */
	@Override
	public int getBlocksY() {
		return blocksY;
	}

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 * @return
	 */
	@Override
	public int getBlocksZ() {
		return blocksZ;
	}

	@Override
	public void print() {
	}

	@Override
	public void postUpdate(float dt) {
	}

	@Override
	public AbstractMap clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
