/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2014 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author Benedikt Vogler
 */
public class MapIterator implements Iterator<Block>{
	private int x=0;
	private int y=0;
	private int z=0;
	private ArrayList<ArrayList<ArrayList<Block>>> mapdata;
	private Iterator<Block> yIterator;
	private Iterator<ArrayList<Block>> xIterator;
	private Iterator<ArrayList<ArrayList<Block>>> zIterator;
	private int bottomLimitZ;
	private int topLimitZ;

	public MapIterator(Map map) {
		this.mapdata = map.getData();
		zIterator = mapdata.iterator();
		xIterator = mapdata.get(0).iterator();
		yIterator = mapdata.get(0).get(0).iterator();
	}
	
	

	@Override
	public boolean hasNext() {
		return ((zIterator.hasNext() || z <= topLimitZ) && xIterator.hasNext() && yIterator.hasNext());
	}

	/**
	 *Loops over the complete map.
	 * @return 
	 */
	@Override
	public Block next() throws NoSuchElementException {
		Block block = yIterator.next();
		if (!yIterator.hasNext()) {
			//if at end of y row
			if (xIterator.hasNext()){
				yIterator = xIterator.next().iterator();
			} else {//was at last block in layer
				if (zIterator.hasNext()){
					if (z >= 0) {
						ArrayList<ArrayList<Block>> tmp = zIterator.next();
						xIterator = tmp.iterator();
						yIterator = tmp.get(0).iterator();
					} else {
						//loop over ground layer twice
						zIterator = mapdata.iterator();
						xIterator = mapdata.get(0).iterator();
						yIterator = mapdata.get(0).get(0).iterator();
					}
					z++;
				}
			}
		}
		if (z<0){
			//current pos -1 in z
			Block groundblock = Block.getInstance(2);
			groundblock.setPosition(
				new Coordinate(
					block.getPosition().getX(),
					block.getPosition().getY(),
					block.getPosition().getZ()-1
				)
			);
			block = groundblock;
		}
		return block;
	}
	
	/**
	 * moves to next element;
	 * @return the next block
	 * @throws NoSuchElementException 
	 */
	public Block nextY() throws NoSuchElementException {
		return yIterator.next();
	}
	
	/**
	 * starts at next x row
	 * @throws NoSuchElementException 
	 */
	public void nextX() throws NoSuchElementException {
		yIterator = xIterator.next().iterator();
	}
	
	/**
	 * starts at next z layer
	 * @throws NoSuchElementException 
	 */
	public void nextZ() throws NoSuchElementException {
		ArrayList<ArrayList<Block>> tmp = zIterator.next();
		xIterator = tmp.iterator();
		yIterator = tmp.get(0).iterator();
		z++;
	}

	/**
	 * Should not be used because there should be no cases where you remove elements from the map.
	 */
	@Override
	public void remove() {
		yIterator.remove();
	}

	
	/**
	 * set the top limit of the iteration.
	 * @param zLimit 
	 */
	public void setBottomLimitZ(int zLimit) {
		this.bottomLimitZ = zLimit;
		
		//move z to bottomLimitZ if >0
		zIterator = mapdata.iterator();
		for (int i = 0; i < zLimit; i++) {
			nextZ();
		}

		z=bottomLimitZ;
	}
	
	/**
	 * set the top limit of the iteration
	 * @param zLimit 
	 */
	public void setTopLimitZ(int zLimit) {
		this.topLimitZ = zLimit;
	}
	
}
