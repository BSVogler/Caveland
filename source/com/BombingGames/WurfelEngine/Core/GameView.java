/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 * 
 * Copyright 2014 Benedikt Vogler.
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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Intersection;
import com.BombingGames.WurfelEngine.Core.Map.LoadMenu;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import java.util.ArrayList;

/**
 * The GameView manages everything what should be drawn in an active game.
 * @author Benedikt
 */
public class GameView extends View implements GameManager {
	/**
	 * the cameras rendering the scene
	 */
    private final ArrayList<Camera> cameras = new ArrayList<>(6);//max 6 cameras
    
    
    private ShapeRenderer shRenderer;
    
    private Controller controller;
    
	/**
	 * a camera rendering the gui and hud
	 */
    private OrthographicCamera libGDXcamera;
    private boolean keyF5isUp;
    
    /**
     * game related stage. e.g. holds hud and gui
     */
    private Stage stage;
    private SpriteBatch batch;
    
    private LoadMenu loadMenu;
    
        
    private boolean initalized;
	
	private Minimap minimap;
	
	private float gameSpeed = 1f;
    
    /**
     * Shoud be called before the object get initialized.
     * Initializes class fields.
     */
    public static void classInit(){
        //set up font
        //font = WurfelEngine.getInstance().manager.get("com/BombingGames/WurfelEngine/EngineCore/arial.fnt"); //load font
        //font.scale(2);

        //font.scale(-0.5f);
        
        //load sprites
        Block.loadSheet();
    }
    
    /**
     *Loades some files and set up everything. This should be done after creating and linking the view.
     * @param controller
     */
    public void init(final Controller controller){
        super.init();
        Gdx.app.debug("View", "Initializing");
        
        this.controller = controller;
        
        //clear old stuff
        cameras.clear();
        
        //set up renderer
        libGDXcamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        shRenderer = new ShapeRenderer();
        
        //set up stage
        stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), WE.getEngineView().getBatch());//spawn at fullscreen
        
        
        batch = new SpriteBatch(10);
        batch.setShader(getShader());
        //load cursor

        initalized = true;
		
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		if (CVar.get("enableMinimap").getValueb()) {
			minimap = new Minimap(
				200,
				Gdx.graphics.getHeight()-800
			);
		}
    }
    
    /**
     *Updates every camera and everything else which must be updated.
     * @param dt time since last update in ms.
     */
    public void update(final float dt){
        AbstractGameObject.resetDrawCalls();
        
        stage.act(dt);
        
        //update cameras
		/**
		 * problem! Write acces in view. causes 1 frame hack without hacks.
		 */
        for (Camera camera : cameras) {
            camera.update(dt);
        }
        
        // toggle the dev menu?
        if (keyF5isUp && Gdx.input.isKeyPressed(Keys.F5)) {
            controller.getDevTools().setVisible(!controller.getDevTools().isVisible());
            keyF5isUp = false;
        }
        keyF5isUp = !Gdx.input.isKeyPressed(Keys.F5);
		
		if (CVar.get("enableMinimap").getValueb()) {
			if (minimap != null && minimap.isNeedingRebuild()) minimap.buildTexture(this);
		}
    }
	    
    /**
     * Main method which is called every time and renders everything.
     */
    public void render(){       
        //Gdx.gl10.glViewport(0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        //clear screen if wished
        if (CVar.get("clearBeforeRendering").getValueb()){
            Gdx.gl20.glClearColor(0, 0, 0, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        //render every camera
        if (cameras.isEmpty()){
            Gdx.gl20.glClearColor(0.5f, 1, 0.5f, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
            drawString("No camera set up", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Color.BLACK.cpy());
        } else {
            for (Camera camera : cameras) {
                camera.render(this, camera);
            }
        }
               
        //render HUD and GUI
        {
//            libGDXcamera.zoom = 1/getEqualizationScale();
//            libGDXcamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.setProjectionMatrix(libGDXcamera.combined);
            shRenderer.setProjectionMatrix(libGDXcamera.combined);

            //WE.getEngineView().getShapeRenderer().setProjectionMatrix(libGDXcamera.combined);
            Gdx.gl20.glLineWidth(1);

            //set viewport of hud to cover whole window
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

            //end of setup
            
            controller.getDevTools().render(this);

			if (CVar.get("enableMinimap").getValueb())
				if (minimap != null)
					minimap.render(this); 
             
            if (Controller.getLightEngine() != null)
                Controller.getLightEngine().render(this);
            
            //render buttons
            stage.draw();
        }
    }
       
    /**
     * The equalizationScale is a factor which scales the GUI/HUD to have the same relative size with different resolutions.
     * @return the scale factor
     */
    public float getEqualizationScale() {
        return libGDXcamera.viewportWidth / CVar.get("renderResolutionWidth").getValuei();
    }

    
   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic.
     * @param screenX the x position on the screen
     * @param camera the camera where the position is on
     * @return the relative (to current loaded map) game coordinate
     */
    public float screenXtoGame(final int screenX, final Camera camera){
        return screenX / camera.getScreenSpaceScaling()- camera.getScreenPosX()+ camera.getViewSpaceX()-camera.getWidthInViewSpc()/2;
    }
    
   /**
     * Reverts the projection and transforms it into a coordiante which can be used in the game logic.
     * @param screenY the y position on the screen. y down
     * @param camera the camera where the position is on
     * @return the relative game coordinate
     */
    public float screenYtoGame(final int screenY, final Camera camera){
        return camera.getViewSpaceY()*-2 //to game space
			+ screenY*2 / camera.getScreenSpaceScaling() //to game space and then revert scaling
			- camera.getScreenPosY() //screen pos offset
			- camera.getHeightInViewSpc()/2;//use left side
    }
    
    /**
     * Returns deepest layer.
     * @param x
     * @param y
     * @return if no camera returns map center
     */
     public Point screenToGameFlat(final int x, final int y){
		if (cameras.size()>0){
			//identify clicked camera
			Camera camera;
			int i = 0;
			do {          
				camera = cameras.get(i);
				i++;
			} while (
					i < cameras.size()
					&& !(x > camera.getScreenPosX() && x < camera.getScreenPosX()+camera.getWidthInScreenSpc()
					&& y > camera.getScreenPosY() && y < camera.getScreenPosY()+camera.getHeightInScreenSpc())
			);

			//find points
			return new Point(
				screenXtoGame(x, camera),
				screenYtoGame(y, camera),
				0
			);
		} else return Map.getCenter();
    }
     
    /**
     * Returns the position belonging to a point on the screen. Does raytracing to find the intersection.
     * @param x the x position on the screen from left
     * @param y the y position on the screen from top
     * @return the position on the map. Deepest layer.
     */
    public Intersection screenToGameRaytracing(final int x, final int y){
		if (cameras.size()>0) {
			Point p = screenToGameFlat(x,y);
			//find point at top of map
			float deltaZ = Chunk.getGameHeight() - Block.GAME_EDGELENGTH - p.getZ();
			p.addVector(0, deltaZ/Point.SQRT2*2, deltaZ);//top of map

			return p.raycast(new Vector3(0,-1, -Point.SQRT12), 5000, cameras.get(0), false);//todo identify camera
		} else return new Intersection(null, Vector3.Zero, 0);
    }
    
	    /**
     *Draw a string using the last active color.
     * @param msg
     * @param xPos
     * @param yPos
	 * @param openbatch treu if begin/end shoould be called
     */
    public void drawString(final String msg, final int xPos, final int yPos, boolean openbatch) {
        if (openbatch) {
			batch.setProjectionMatrix(libGDXcamera.combined);
			batch.begin();
		}
			WE.getEngineView().getFont().draw(batch, msg, xPos, yPos);
        if (openbatch) batch.end();
    }
    
    /**
     *Draw a string in a color.
     * @param msg
     * @param xPos
     * @param yPos
     * @param color
     */
    public void drawString(final String msg, final int xPos, final int yPos, final Color color) {
        batch.setColor(color);
        batch.begin();
            WE.getEngineView().getFont().draw( batch, msg, xPos, yPos);
        batch.end();
    }
    
    /**
     *Draw multi-lines with this method
     * @param text
     * @param xPos space from left
     * @param yPos space from top
     * @param color the colro of the text.
     */
    public void drawText(final String text, final int xPos, final int yPos, final Color color){
        WE.getEngineView().getFont().setColor(Color.BLACK);
        WE.getEngineView().getFont().setScale(1.01f);
        batch.begin();
        WE.getEngineView().getFont().drawMultiLine(batch, text, xPos, yPos);
        batch.end();
        
        WE.getEngineView().getFont().setColor(Color.WHITE);
        WE.getEngineView().getFont().setScale(1f);
        batch.begin();
        WE.getEngineView().getFont().drawMultiLine(batch, text, xPos, yPos);
        batch.end();
    }



    /**
     *
     * @return
     */
    @Override
    public ShapeRenderer getShapeRenderer() {
        return shRenderer;
    }
    

    /**
     *
     * @return
     */
    public Controller getController() {
        return controller;
    }
    
     /**
     * Returns a camera.
     * @return The virtual cameras rendering the scene
     */
    public ArrayList<Camera> getCameras() {
        return cameras;
    }

    /**
     *Get a menu which can be used for loading maps.
     * @return
     */
    public LoadMenu getLoadMenu() {
        if (loadMenu==null) loadMenu = new LoadMenu();//lazy init
        return loadMenu;
    }

	public Minimap getMinimap() {
		return minimap;
	}

	public void setMinimap(Minimap minimap) {
		this.minimap = minimap;
	}

    /**
     * Add a camera.
     * @param camera
     */
    protected void addCamera(final Camera camera) {
        this.cameras.add(camera);
		Controller.getMap().addLinkedObject(camera);
		if (CVar.get("enableMinimap").getValueb()) {
			minimap.setCamera(cameras.get(0));
		}
    }
    
     /**
     * should be called when the window get resized
     * @param width
     * @param height 
     */
    public void resize(final int width, final int height) {
        for (Camera camera : cameras) {
            camera.resize(width, height);//resizes cameras to fullscreen?
        }
        //stage.setViewport(new StretchViewport(width, height));
        //EngineView.getStage().setViewport(new StretchViewport(width, height));
        libGDXcamera.setToOrtho(false, width, height);
		libGDXcamera.zoom = 1/getEqualizationScale();
        libGDXcamera.update();
    }

    /**
     * The libGDX scene2d stage
     * @return 
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Game view dependent batch
     * @return 
     */
    @Override
    public SpriteBatch getBatch() {
        return batch;
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean isInitalized() {
        return initalized;
    }

 
   /**
     *override to specify what should happen when the mangager becomes active
     */
    @Override
    public void onEnter(){
    }
    
    @Override
    public final void enter() {
        WE.getEngineView().addInputProcessor(stage);//the input processor must be added every time because they are only 
        Gdx.input.setCursorImage(WE.getEngineView().getCursor(), 8, 8);
		for (Camera camera : cameras) {
			camera.hiddenSurfaceDetection();
		}
		
		WE.getEngineView().setMusicLoudness(CVar.get("music").getValuef());//reload music loudness
		Controller.getMap().setGameSpeed(gameSpeed);
        onEnter();
    }

	@Override
	public void dispose() {
		shRenderer.dispose();
		batch.dispose();
		stage.dispose();
	}
	
}