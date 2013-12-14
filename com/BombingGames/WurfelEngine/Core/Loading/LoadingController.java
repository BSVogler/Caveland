package com.BombingGames.WurfelEngine.Core.Loading;

import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
        WEMain.getInstance().manager.load(
            "com/BombingGames/WurfelEngine/Core/Loading/loading.pack",
            TextureAtlas.class);
        // Wait until they are finished loading
        WEMain.getInstance().manager.finishLoading();
        
        // Add everything to be loaded, for instance:
        //WurfelEngine.getInstance().manager.load("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);       
        manager.load("com/BombingGames/WurfelEngine/Game/Blockimages/Spritesheet.txt", TextureAtlas.class);
       // manager.load("com/BombingGames/WurfelEngine/Game/Blockimages/Spritesheet.png", Pixmap.class);
        manager.load("com/BombingGames/WurfelEngine/Game/Sounds/wind.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Game/Sounds/victorcenusa_running.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Game/Sounds/jump_man.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Game/Sounds/landing.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Game/Sounds/splash.ogg", Sound.class);
        //manager.load("com/BombingGames/WurfelEngine/Core/arial.fnt", BitmapFont.class);
    }
    
    public void update(){
        if (manager.update()) { // Load some, will return true if done loading 
            Gdx.app.log("Loading", "finished");
            WEMain.startGame();
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, manager.getProgress(), 0.1f);
    }

    public float getPercent() {
        return percent;
    }
}
