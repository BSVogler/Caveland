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
package com.BombingGames.WurfelEngine.Core.Loading;

import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

/**
 *The loadinbg controller manages the data during the loading process.
 * @author Benedikt Vogler
 */
public class LoadingController {
    private float percent;
    
    public LoadingController(){
        Gdx.app.log("LoadingController", "Initializing");
        AssetManager manager = WE.getAssetManager();
                
        // Tell the manager to load assets for the loading screen
        manager.load(
            "com/BombingGames/WurfelEngine/Core/Loading/loading.pack",
            TextureAtlas.class);
        // Wait until they are finished loading
        manager.finishLoading();
        
        // Add everything to be loaded, for instance:
        //WurfelEngine.getInstance().manager.load("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);       
        manager.load("com/BombingGames/WurfelEngine/Core/images/Spritesheet.txt", TextureAtlas.class);
       // manager.load("com/BombingGames/WurfelEngine/Game/Blockimages/Spritesheet.png", Pixmap.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/wind.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/victorcenusa_running.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/jump_man.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/landing.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg", Sound.class);
        //manager.load("com/BombingGames/WurfelEngine/Core/arial.fnt", BitmapFont.class);
    }
    
    public void update(){
        if (WE.getAssetManager().update()) { // Load some, will return true if done loading 
            Gdx.app.log("Loading", "finished");
            WE.startGame();
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, WE.getAssetManager().getProgress(), 0.1f);
    }

    public float getPercent() {
        return percent;
    }
}