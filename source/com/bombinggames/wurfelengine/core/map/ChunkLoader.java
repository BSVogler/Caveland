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
package com.bombinggames.wurfelengine.core.map;

import java.io.File;

/**
 *
 * @author Benedikt Vogler
 */
public class ChunkLoader implements Runnable{
	private Chunk chunk;
	private final Generator generator;
	private final File path;
	private final int coordY;
	private final int coordX;
	private final Map map;

	/**
	 *
	 * @param map
	 * @param path
	 * @param coordX
	 * @param coordY
	 * @param generator
	 */
	public ChunkLoader(final Map map, final File path, final int coordX, final int coordY, final Generator generator) {
		this.map = map;
		this.coordX = coordX;
		this.coordY = coordY;
		this.path = path;
		this.generator = generator;
	}
	
	
	@Override
	public void run() {
		chunk = new Chunk(map, path, coordX, coordY, generator);
	}

	/**
	 * has a chunk if it has finished loading
	 * @return 
	 */
	public Chunk getChunk() {
		return chunk;
	}
	
	/**
	 *
	 * @return
	 */
	public int getCoordX() {
		return coordX;
	}

	/**
	 *
	 * @return
	 */
	public int getCoordY() {
		return coordY;
	}
	
}
