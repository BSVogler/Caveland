/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.BombingGames.EngineCore.Loading;

import com.BombingGames.WurfelEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;

/**
 *
 * @author Benedikt Vogler
 */
public class LoadingController {
    private float percent;
    private AssetManager manager;
    
    public void init(AssetManager manager){
        Gdx.app.log("LoadingController", "Initializing");
        this.manager = manager;
                
        // Tell the manager to load assets for the loading screen
        WurfelEngine.getInstance().manager.load(
            "com/BombingGames/EngineCore/Loading/loading.pack",
            TextureAtlas.class);
        // Wait until they are finished loading
        WurfelEngine.getInstance().manager.finishLoading();
        
        // Add everything to be loaded, for instance:
        //WurfelEngine.getInstance().manager.load("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);
        TextureParameter param = new TextureParameter();
        param.genMipMaps = true;
        manager.load("com/BombingGames/Game/Blockimages/Spritesheet.txt", TextureAtlas.class);
       // manager.load("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);
        manager.load("com/BombingGames/Game/Sounds/wind.ogg", Sound.class);
        manager.load("com/BombingGames/Game/Sounds/victorcenusa_running.ogg", Sound.class);
        manager.load("com/BombingGames/Game/Sounds/jump_man.wav", Sound.class);
        manager.load("com/BombingGames/Game/Sounds/landing.wav", Sound.class);
        manager.load("com/BombingGames/Game/Sounds/splash.ogg", Sound.class);
    }
    
    public void update(){
        if (manager.update()) { // Load some, will return true if done loading 
            Gdx.app.log("Loading", "finished");
            WurfelEngine.startGame();
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, manager.getProgress(), 0.1f);
    }

    public float getPercent() {
        return percent;
    }
}
