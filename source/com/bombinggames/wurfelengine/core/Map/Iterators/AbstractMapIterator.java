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
import com.bombinggames.wurfelengine.core.Map.Map;
import java.util.Iterator;

/**
 * FOr iteratos which iterate over a map.
 * @author Benedikt Vogler
 */
public abstract class AbstractMapIterator implements Iterator<Block>{
	protected final Map map;
	/**
	 * Always points to a block. Iterates over a chunk.
	 */
	protected DataIterator<Block> blockIterator;
	private int topLimitZ;
	private int startingZ = 0;

	public AbstractMapIterator(Map map) {
		this.map = map;
	}	
	
	/**
	 * Check if the iterator has a next chunk
	 * @return 
	 */
	public abstract boolean hasNextChunk();
	
	/**
	 * set the top/last limit of the iteration (including).
	 * @param zLimit 
	 */
	public void setTopLimitZ(int zLimit) {
		this.topLimitZ = zLimit;
		if (blockIterator!=null) blockIterator.setTopLimitZ(zLimit);
	}

	/**
	 *
	 * @return
	 */
	public int getTopLimitZ() {
		return topLimitZ;
	}

	/**
	 * the z level where the iteration starts
	 * @return 
	 */
	public int getStartingZ() {
		return startingZ;
	}

	/**
	 * the z level where the iteration starts
	 * @param startingZ 
	 */
	protected void setStartingZ(int startingZ) {
		this.startingZ = startingZ;
	}
	
	@Override
	public abstract boolean hasNext();
	
	
	/**
	 * Should not be used because there should be no cases where you remove elements from the map.
	 */
	@Override
	public void remove() {
		//yIterator.remove();
	}
}
