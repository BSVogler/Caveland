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

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;

/**
 * Attach two objects together by comparing their offset and moving the smaller object to keep it.
 * @author Benedikt Vogler
 */
public class SuperGlue extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	private final AbstractEntity main;
	private final AbstractEntity smaller;
	private Vector3 offset = new Vector3();
	
	/**
	 * glue some object to another object. If you move
	 * @param main the object at which you glue something
	 * @param smaller the object you want to glue at some other obejct
	 */
	public SuperGlue(AbstractEntity main, AbstractEntity smaller){
		super((byte) 1);//use any id but 0
		setName("SuperGlue: "+ main.getName() +"<-"+ smaller.getName() );
		setHidden(true);
		this.main = main;
		this.smaller = smaller;
		//if one of both is not getting saved then also don't get saved
		if (!main.isGettingSaved() || !smaller.isGettingSaved())
			setSaveToDisk(false);
	}

	public void setOffset(Vector3 offset) {
		this.offset = offset;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (main.hasPosition()) {
			smaller.setPosition(main.getPosition().cpy().addVector(offset));
		}
		//dispose glue if one component should be disposed.
		if (main.shouldBeDisposed() || smaller.shouldBeDisposed())
			dispose();
	}
	
	
}
