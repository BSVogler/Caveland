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
package com.bombinggames.wurfelengine.core;

/**
 *
 * @author Benedikt Vogler
 */
public enum Events {
	
	/**
	 *an entitiy which will receive this event will be damaged
	 *//**
	 *an entitiy which will receive this event will be damaged
	 */
	damage(11),

	/**
	 *
	 */
	collided(12),

	/**
	 *
	 */
	steppedOn(13),

	/**
	 *
	 */
	selectInEditor(14),

	/**
	 *
	 */
	deselectInEditor(15),

	/**
	 * extra info has coordinate
	 */
	blockDestroyed(16),
	
	/**
	 * extra info has coordinate
	 */
	blockDamaged(17),

	/**
	 *when an entity touches the ground
	 */
	landed(18),

	/**
	 *moves an object. extra information must contain {@link com.bombinggames.wurfelengine.core.map.Point}
	 */
	moveTo(19),

	/**
	 *
	 */
	standStill(20),
	
	/**
	 * extra information must contain {@link com.bombinggames.wurfelengine.core.map.Point}
	 */
	teleport(21),
	
	/**
	 *extra info contains changed chunk
	 */
	chunkChanged(22),
	
	/**
	 *
	 */
	mapChanged(23),
	
	/**
	 *
	 */
	mapReloaded(24),

	/**
	 * if a cell is changed
	 */
	cellChanged(25);
	
	private final int id;

	private Events(int messageId) {
		id = messageId;
	}

	/**
	 *
	 * @return
	 */
	public int getId() {
		return id;
	}
	
}
