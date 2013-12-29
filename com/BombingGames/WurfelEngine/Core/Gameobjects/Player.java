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

import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;


/**
 *The Player is a character who can walk.
 * @author Benedikt
 */
public class Player extends AbstractCharacter{   
    /**
     * Creates a player. The parameters are for the lower half of the player. The constructor automatically creates a block on top of it.
     * @param id 
     * @param point 
     * @see com.BombingGames.Game.Gameobjects.Block#getInstance(int) 
     */
    public Player(int id, Point point) {
        super(id, 1, point);
        Gdx.app.debug("Player", "Creating player");
        setFallingSound(
            (com.badlogic.gdx.backends.openal.Ogg.Sound)
            WEMain.getAsset("com/BombingGames/WurfelEngine/Game/Sounds/wind.ogg")
        );
        setRunningSound(
            (com.badlogic.gdx.backends.openal.Ogg.Sound)
            WEMain.getAsset("com/BombingGames/WurfelEngine/Game/Sounds/victorcenusa_running.ogg")
        );
        setJumpingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WEMain.getAsset("com/BombingGames/WurfelEngine/Game/Sounds/jump_man.wav")
        );
        setLandingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WEMain.getAsset("com/BombingGames/WurfelEngine/Game/Sounds/landing.wav")
        );
        
        setTransparent(true);
        setObstacle(true);
        setDimensionZ(2);
    }   

    /**
     * Jumps the player
     */
    @Override
    public void jump() {
        super.jump(5);
    }
    
    @Override
    /**
     * Getting aim relative to player by reading mouse position.
     */
    public float[] getAiming(){
       float deltaX =Gdx.input.getX() - this.getPos().get2DPosX();
       float deltaY =Gdx.input.getY() - this.getPos().get2DPosY(); 
       float length = (float) Math.sqrt( Math.pow(deltaX,2)+ Math.pow(deltaY,2));
       return new float[]{
            deltaX/length,
            deltaY*2/length,
            0
        };
    }
}