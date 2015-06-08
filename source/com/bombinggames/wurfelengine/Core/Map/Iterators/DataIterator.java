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
package com.bombinggames.wurfelengine.Core.Map.Iterators;

import com.bombinggames.wurfelengine.Core.Controller;
import com.bombinggames.wurfelengine.Core.Gameobjects.HasID;
import java.util.Iterator;

/**
 *An iterator iterating over a 3d array
 * @author Benedikt Vogler
 */
public class DataIterator implements Iterator<HasID>{
	/**
	 * current position
	 */
	private int[] pos;
	private HasID[][][] data;
	private int limitZ;
	private int left, right, back, front;

	/**
	 * 
	 * @param data
	 * @param startingZ the starting layer
	 * @param limitZ  the last layer 
	 */
	public DataIterator(
		HasID[][][] data,
		final int startingZ,
		final int limitZ
	) {
		pos = new int[]{ -1 , 0 , startingZ }; //start at -1 because the first call of next should return the first element
		this.limitZ=limitZ;
		this.data = data;
		
		left = 0;
		right = data.length-1;
		back = 0;
		front = data[0].length-1;
	}
	
	/**
	 * set the top/last limit of the iteration (including).
	 * @param zLimit 
	 */
	public void setTopLimitZ(int zLimit) {
		this.limitZ = zLimit;
	}

	@Override
	public boolean hasNext() {
		return (
			   pos[0] < right
			|| pos[1] < front
			|| pos[2] < limitZ
		);
	}

	@Override
	public HasID next() {
		if (pos[0] < right)
			pos[0]++;
		else if (pos[1] < front){
			pos[1]++;
			pos[0] = left;
		} else if (pos[2] < limitZ) {
			pos[2]++;
			pos[1] = back;
			pos[0] = left;
		}
		
		if (pos[2] < 0){
			//current pos -1 in z
			return Controller.getMap().getGroundBlock();
		} else {
			return data[pos[0]][pos[1]][pos[2]];
		}
	}

	@Override
	public void remove() {
	}
	
	/**
	 * get the reference to the indices position of the iterator
	 * @return 
	 */
	public int[] getCurrentIndex(){
		return pos;
	}

	/**
	 * sets index position borders during iterations. This reduces greatly the amount of blocks which are traversed.
	 * @param left
	 * @param right
	 * @param back
	 * @param front 
	 */
	public void setBorders(int left, int right, int back, int front) {
		if (left>0)
			this.left = left;
		if (right < data.length-1)
			this.right = right;
		if (back>0)
			this.back = back;
		if (front < data[0].length-1)
			this.front = front;
	}
	
}
