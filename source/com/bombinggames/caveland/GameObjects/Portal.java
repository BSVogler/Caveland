/*
 *
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
package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Portal extends AbstractEntity implements Interactable{
	private static final long serialVersionUID = 2L;
	private Coordinate target;
	private boolean spawner;
	private ArrayList<Enemy> spawnedList = new ArrayList<>(3);
	
	/**
	 * teleports to 0 0 0
	 */
	public Portal(){
		super((byte) 15);
		setName("Portal");
		this.target = new Coordinate(0, 0, Chunk.getBlocksZ()-1);
	}
	
	/**
	 * teleport to custom aim
	 * @param target 
	 */
	public Portal(Coordinate target) {
		this();
		this.target = target;
	}
	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		actor.setPosition(target.cpy());
	}

	public void setTarget(Coordinate target) {
		this.target = target;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (spawner){
			if (!getPosition().getEntitiesNearby(Block.GAME_EDGELENGTH*6, CustomPlayer.class).isEmpty()){//if a player is nearby
				if (spawnedList.size()<3) {
					Enemy e = (Enemy) new Enemy().spawn(
						getPosition().cpy().addVector(
							(float) ((Math.random()*4-2)*Block.GAME_EDGELENGTH),
							(float) ((Math.random()*4-2)*Block.GAME_EDGELENGTH),
							8
						)
					);
					spawnedList.add(e);
				}
				//remove killed enemys
				for (int i = 0; i < spawnedList.size(); i++) {
					Enemy e = spawnedList.get(i);
					if (e.shouldBeDisposed()) spawnedList.remove(e);
				}
			}
		
		}
	}
	
	
	
	/**
	 * creates monsters
	 */
	public void enableEnemySpawner(){
		spawner = true;
	}
}
