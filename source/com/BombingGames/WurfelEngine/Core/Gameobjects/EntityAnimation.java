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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.badlogic.gdx.Gdx;

/**
 * An animation interface for entitys.
 * @author Benedikt
 */
public class EntityAnimation implements Animatable {
	private static final long serialVersionUID = 1L;
    private final int[] animationsduration;
    private float counter = 0;
    private boolean running;
    private final boolean loop;
    /**
     * ignores game time speed.
     */
    private boolean updateIgnoringGameTime;
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
    public void update(float dt) {
        if (running && parent != null) {
            if (updateIgnoringGameTime)
                counter += Gdx.graphics.getDeltaTime()*1000f;
            else 
                counter += dt;
			int value = parent.getValue();
			if (value >= animationsduration.length) //stop the animation if value is suddenly too big
				running=false;
			else if (counter >= animationsduration[ value ]){
					parent.setValue((byte) (parent.getValue()+0b1));
					counter=0;
					if (parent.getValue() >= animationsduration.length)//if over animation array
						if (loop)
							parent.setValue((byte)0);
						else{//delete
							parent.setHidden(true);
							parent.setValue((byte) (parent.getValue()-1));
							parent.dispose();
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
     * ignores the delta time of the game world. use this if you want to have an animation independent of game speed (e.g. slow motion.)
     * @param ignore true ignores game time
     */
    public void ignoreGameSpeed(boolean ignore) {
        this.updateIgnoringGameTime = ignore;
    }

	void setParent(AbstractEntity parent) {
		this.parent = parent;
	}
    
    
}