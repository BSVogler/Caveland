/**
 * This class is public domain.
 */

package com.BombingGames.WurfelEngine.Core.Loading;

import com.BombingGames.WurfelEngine.Core.Configuration;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Class under public domain. Modified for own needs. This class renders a loading screen.
 * @author Mats Svensson, Benedikt Vogler
 */
public class LoadingScreen implements Screen {

    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;

    private Actor loadingBar;
    private float percent;
    
    private final Configuration config;

    /**
     *
     * @param config
     */
    public LoadingScreen(final Configuration config) {
        Gdx.app.log("LoadingScreen", "Initializing");
        AssetManager manager = WE.getAssetManager();
                
        // Tell the manager to load assets for the loading screen
        manager.load("com/BombingGames/WurfelEngine/Core/Loading/loading.pack",TextureAtlas.class);
        // Wait until they are finished loading
        manager.finishLoading();
        
        // Add everything to be loaded, for instance:
        //WurfelEngine.getInstance().manager.load("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);
        manager.load(config.getSpritesheetPath()+".txt", TextureAtlas.class);
        manager.load("com/BombingGames/WurfelEngine/Core/skin/gui.txt", TextureAtlas.class);
        
       // manager.load("com/BombingGames/WurfelEngine/Game/Blockimages/Spritesheet.png", Pixmap.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/wind.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/victorcenusa_running.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/jump_man.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/landing.wav", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/splash.ogg", Sound.class);
        manager.load("com/BombingGames/WurfelEngine/Core/Sounds/explosion2.ogg", Sound.class);
        //manager.load("com/BombingGames/WurfelEngine/Core/arial.fnt", BitmapFont.class);
        
        //load files from config
        config.initLoadingQueque(manager);
        this.config = config;
    }

    
    
    @Override
    public void show() {       
        // Initialize the stage where we will place everything
        stage = new Stage();

        // Get our textureatlas from the manager
        TextureAtlas atlas = WE.getAssetManager().get("com/BombingGames/WurfelEngine/Core/Loading/loading.pack");

        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim") );
        anim.setPlayMode(Animation.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);

        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));

        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
    }

    @Override
    public void resize(int width, int height) {
        // Set our screen to always be XXX x 480 in size
        width = 480 * width / height;
        height = 480;
        stage.setViewport(width , height, false);

        // Make the background fill the screen
        screenBg.setSize(width, height);

        // Place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 100);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void render(float delta) {
        if (WE.getAssetManager().update()) { // Load some, will return true if done loading 
            Gdx.app.log("Loading", "finished");
            WE.startGame();
        }

        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, WE.getAssetManager().getProgress(), 0.1f);
        
        // Clear the screen
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
        WE.getAssetManager().unload("com/BombingGames/WurfelEngine/Core/Loading/loading.pack");
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        WE.getAssetManager().unload(config.getSpritesheetPath()+".txt");
    }
}
