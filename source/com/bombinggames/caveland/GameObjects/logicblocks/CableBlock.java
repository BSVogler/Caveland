/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
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
package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CableBlock extends AbstractPowerBlock{
	private boolean initalized = false;
	private int type = 0;
	
	public CableBlock(Block block, Coordinate coord) {
		super(block, coord);
		if ((block.getValue()==0 || block.getValue()==1)) {
			type = 0;
		}
		if ((block.getValue()==2 || block.getValue()==3)) {
			type = 1;
		}
	}
	
	@Override
	public void update(float dt) {
		super.update(dt);
		if (initalized) {
			//turn off
			Block block = getPosition().getBlock();
			getPosition().setValue((byte) (block.getValue() - block.getValue() % 2));
			initalized = true;
		}
		
		Block lblock = getPosition().getBlock();
		getPosition().setValue(
			(byte) (lblock.getValue() - (lblock.getValue() % 2)
			+ (hasPower() ? 1 : 0))
		);
	}
	
	@Override
	public boolean outgoingConnection(int id) {
		if (type==0 && (id==1 ||id==5))
			return true;
		if (type==1 && (id==3 ||id==7))
			return true;
		return false;
	}
	
	
}
