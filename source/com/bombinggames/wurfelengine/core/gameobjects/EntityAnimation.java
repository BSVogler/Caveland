/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.bombinggames.wurfelengine.core.gameobjects;

/**
 * An animation interface for entitys.
 * @author Benedikt
 */
public class EntityAnimation implements Animatable, Component {
	private static final long serialVersionUID = 1L;
    private final int[] animationsduration;
    private float counter = 0;
    private boolean running;
    private final boolean loop;
	private AbstractEntity parent;
    
   /**
     * Create an entity with an animation with an array wich has the time of every animation step in ms in it.
     * @param animationsinformation  the time in ms for each animation step
     * @param autostart True when it should automatically start.
     * @param loop Set to true when it should loop, when false it stops after one time.
     */
    public EntityAnimation(int[] animationsinformation, boolean autostart, boolean loop){
        this.animationsduration = animationsinformation;
        this.running = autostart;
        this.loop = loop;
    }
    
   /**
     * updates the entity and the animation.
     * @param dt the time wich has passed since last update
     */
	@Override
    public void update(float dt) {
        if (running && parent != null) {
			counter += dt;
			int value = parent.getSpriteValue();
			if (value < 0) {
				parent.dispose();
				running = false;
				return;
			}
			if (value >= animationsduration.length) { //stop the animation if value is suddenly too big
				running = false;
			} else if (counter >= animationsduration[value]) {
				parent.setSpriteValue((byte) (parent.getSpriteValue() + 1));
				counter = 0;
				if (parent.getSpriteValue() >= animationsduration.length) {//if over animation array
					if (loop) {
						parent.setSpriteValue((byte) 0);
					} else {//delete
						parent.dispose();
					}
				}
			}
        }
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

	/**
	 *
	 * @param parent
	 */
	@Override
	public void setParent(AbstractEntity parent) {
		this.parent = parent;
	}

	/**
	 * set an offset in time.
	 * @param time in ms
	 */
	public void setOffset(float time) {
		counter = time;
	}

	@Override
	public void dispose() {
		parent.removeComponent(this);
	}
    
}