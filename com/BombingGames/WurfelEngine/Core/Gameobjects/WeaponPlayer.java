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
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;


/**
 *The WeaponPlayer is a character who can walk and shoot.
 * @author Benedikt
 */
public class WeaponPlayer extends AbstractCharacter{
    private WECamera camera;
    /**
     * Creates a player. The parameters are for the lower half of the player. The constructor automatically creates a block on top of it.
     * @param point 
     * @see com.BombingGames.Game.Gameobjects.Block#getInstance(int) 
     */
    public WeaponPlayer(Point point) {
        super(30, 1, point);
        Gdx.app.debug("Player", "Creating player");
        setFallingSound(
            (com.badlogic.gdx.backends.openal.Ogg.Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/wind.ogg")
        );
        setRunningSound(
            (com.badlogic.gdx.backends.openal.Ogg.Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/victorcenusa_running.ogg")
        );
        setJumpingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/jump_man.wav")
        );
        setLandingSound((com.badlogic.gdx.backends.openal.Wav.Sound)
            WE.getAsset("com/BombingGames/WurfelEngine/Core/Sounds/landing.wav")
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
     * Getting aim relative to middle of view by reading mouse position. If no camera is configured it will return null.
     */
    public Vector3 getAiming(){
        Vector3 aim = null;
        if (camera != null){
            aim = new Vector3(
                Gdx.input.getX()- camera.getScreenWidth()/2,
                2*(Gdx.input.getY()- camera.getScreenHeight()/2),
                0
            );
            aim.nor();
            return aim;
        }else{
            return aim;
        }
    }

    /**
     *Set the camera which is renderin the player to calculate the aiming. If camera is null 
     * @param camera 
     */
    public void setCamera(WECamera camera) {
        this.camera = camera;
    }
}