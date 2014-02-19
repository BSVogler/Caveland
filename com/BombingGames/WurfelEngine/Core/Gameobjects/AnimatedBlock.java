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

/**
 *A block who has an animation.
 * @author Benedikt
 */
public class AnimatedBlock extends Block implements Animatable{
    private final int[] animationsduration;
    private int counter = 0;
    private boolean running;
    private final boolean loop;
    
    /**
     * Create this Block with an array wich has the time of every animation step in ms in it.
     * @param id the id of the block.
     * @param animationsinformation  an array wich has the duraion of every animationstep inside
     * @param  autostart True when it should automatically start.
     * @param loop Set to true when it should loop, when false it stops after one time. 
     */
    public AnimatedBlock(int id, int[] animationsinformation, boolean autostart, boolean loop){
        super(id);
        this.animationsduration = animationsinformation;
        this.running = autostart;
        this.loop = loop;
    }
    
   /**
     * updates the block and the animation.
     * @param delta the time wich has passed since last update
     */
    @Override
    public void update(float delta) {
        if (running) {
            counter += delta;
            if (counter >= animationsduration[getValue()]){
                setValue(getValue()+1);
                counter=0;
                if (getValue() >= animationsduration.length)//if over animation array
                    if (loop)
                        setValue(0);
                    else{
                        running = false;
                        setValue(getValue()-1);
                    }
            }
        }
    }

    /**
     * Starts the animation.
     */
    @Override
    public void start() {
        running = true;
    }

    /**
     * Stops the animation.
     */
    @Override
    public void stop() {
        running = false;
    }
}