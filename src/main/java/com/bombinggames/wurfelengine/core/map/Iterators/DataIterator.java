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
import java.util.Iterator;

/**
 * An iterator iterating over a 3d array. Starts outside the array so first call before acces must be {@link #next()}.
 *
 * @author Benedikt Vogler
 * @param <T>
 */
public class DataIterator<T> implements Iterator<T> {

	/**
	 * current position
	 */
	private final int[] pos = new int[3];
	private final T[][][] data;
	private int limitZ;
	private final int startingZ;
	/**
	 * index positions
	 */
	private int left, right, back, front;

	/**
	 *
	 * @param data
	 * @param startingZ the starting layer
	 * @param limitZ the last layer (including).
	 */
	public DataIterator(
		T[][][] data,
		final int startingZ,
		final int limitZ
	) {
		this.startingZ  = startingZ;
		this.limitZ = limitZ;
		if (this.limitZ >= Chunk.getBlocksZ()) {
			this.limitZ = Chunk.getBlocksZ()-1;
		}
		if (this.limitZ <= startingZ) {
			this.limitZ = startingZ+1;
		}
		if (data==null) throw new IllegalArgumentException();
		this.data = data;

		left = 0;
		right = data.length - 1;
		back = 0;
		front = data[0].length - 1;
		restart();
	}

	/**
	 * set the top/last limit of the iteration (including).
	 *
	 * @param zLimit
	 */
	public void setTopLimitZ(int zLimit) {
		this.limitZ = zLimit;
	}

	@Override
	public boolean hasNext() {
		return (pos[0] < right
			|| pos[1] < front
			|| pos[2] < limitZ);
	}

	@Override
	public T next() {
		if (pos[2] < limitZ) {// go higher if it can and go to x=0, y=0
			pos[2]++;
		} else if (pos[0] < right) { // go right if it can
			pos[0]++;
			pos[2] = 0;
		} else if (pos[1] < front) {// go down if it can and start at x=0
			pos[1]++;
			pos[0] = left;
			pos[2] = 0;
		}
		return data[pos[0]][pos[1]][pos[2]];
	}

	@Override
	public void remove() {
		data[pos[0]][pos[1]][pos[2]] = null;
	}

	/**
	 * get the reference to the indices position of the iterator
	 *
	 * @return
	 */
	public int[] getCurrentIndex() {
		return pos;
	}

	/**
	 * sets index position borders during iterations. This reduces greatly the
	 * amount of blocks which are traversed.
	 *
	 * @param left index positions, no grid coordinates
	 * @param right index positions, no grid coordinates
	 * @param back index positions, no grid coordinates
	 * @param front index positions, no grid coordinates
	 */
	public void setBorders(int left, int right, int back, int front) {
		if (left > 0) {
			this.left = left;
		}
		if (right < data.length - 1) {
			this.right = right;
		}
		if (back > 0) {
			this.back = back;
		}
		if (front < data[0].length - 1) {
			this.front = front;
		}
		restart();
	}
	
	/**
	 *
	 */
	public void restart() {
		pos[0] = left;
		pos[1] = back;
		pos[2] = startingZ - 1; //start at -1 because the first call of next should return the first element
	}

}
