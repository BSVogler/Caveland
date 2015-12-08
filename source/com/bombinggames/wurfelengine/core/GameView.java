/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.wurfelengine.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Intersection;
import com.bombinggames.wurfelengine.core.Map.LoadMenu;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The GameView manages everything what should be drawn in an active game in game space.
 * @author Benedikt
 */
public class GameView implements GameManager {
	/**
	 * the cameras rendering the scene
	 */
    private final ArrayList<Camera> cameras = new ArrayList<>(6);//max 6 cameras
    
	/**
	 * true if current rendering is debug only
	 */
	private boolean inDebug;
	
    private ShaderProgram shader;
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
    private final SpriteBatch spriteBatch = new SpriteBatch(2000);
    
    private LoadMenu loadMenu;
    
        
    private boolean initalized;
	
	/**
	 * backup of cvar for keeping the time steady when changed in another view
	 */
	private float gameSpeed = 1f;
	
		/**
	 * @since v1.3.12
	 */
	private int orientation = 0;
    
    /**
     * Shoud be called before the object get initialized.
     * Initializes class fields.
     */
    public static void classInit(){
        //set up font
        //font = WurfelEngine.getInstance().manager.get("com/bombinggames/wurfelengine/EngineCore/arial.fnt"); //load font
        //font.scale(2);

        //font.scale(-0.5f);
        
        //load sprites
        RenderBlock.loadSheet();
    }
	private boolean useDefaultShader;
    
	/**
	 * Loades some files and set up everything. This should be done after
	 * creating and linking the view. After this has been inactive use {@link #onEnter() }
	 *
	 * @param controller The data sources used for the view. Can be null but
	 * should not.
	 * @param oldView The view used before. Can be null.
	 * @see #onEnter() 
	 */
	public void init(final Controller controller, final GameView oldView) {
		Gdx.app.debug("GameView", "Initializing");
		try {
			loadShaders();
		} catch (Exception ex) {
			Logger.getLogger(GameView.class.getName()).log(Level.SEVERE, null, ex);
		}

		this.controller = controller;

		//clear old stuff
		cameras.clear();

		//set up renderer
		libGDXcamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		shRenderer = new ShapeRenderer();

		//set up stage
		stage = new Stage(
			new StretchViewport(
				Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight()
			),
			spriteBatch
		);//spawn at fullscreen

		useDefaultShader();//set default shader

		initalized = true;
	}
	
	
	/**
	 * Get the loaded shader program of the view.
	 *
	 * @return
	 */
	public ShaderProgram getShader() {
		return shader;
	}
	
	/**
	 * enable debug rendering only
	 *
	 * @param debug
	 */
	void setDebugRendering(boolean debug) {
		this.inDebug = debug;
	}

	/**
	 *
	 * @return true if current rendering is debug only
	 */
	public boolean debugRendering() {
		return inDebug;
	}

	/**
	 * reloads the shaders
	 * @throws java.lang.Exception
	 */
	public void loadShaders() throws Exception {
		Gdx.app.debug("Shader", "loading");
		String vertexShader;
		String fragmentShader;
		//shaders are very fast to load and the asset loader does not support text files out of the box
		if (WE.getCvars().getValueB("LEnormalMapRendering")) {
			vertexShader = Gdx.files.internal("com/bombinggames/wurfelengine/core/vertexNM.vs").readString();
			fragmentShader = Gdx.files.internal("com/bombinggames/wurfelengine/core/fragmentNM.fs").readString();
		} else {
			vertexShader = Gdx.files.internal("com/bombinggames/wurfelengine/core/vertex.vs").readString();
			fragmentShader = Gdx.files.internal("com/bombinggames/wurfelengine/core/fragment.fs").readString();
		}
		//Setup shader
		ShaderProgram.pedantic = false;

		ShaderProgram newshader = new ShaderProgram(vertexShader, fragmentShader);
		if (newshader.isCompiled()) {
			shader = newshader;
		} else if (shader == null) {
			throw new Exception("Could not compile shader: " + newshader.getLog());
		}

		//print any warnings
		if (shader.getLog().length() != 0) {
			System.out.println(shader.getLog());
		}

		//setup default uniforms
		shader.begin();
		//our normal map
		shader.setUniformi("u_normals", 1); //GL_TEXTURE1
		shader.end();
	}

	/**
	 * Override to specify what should happen when the mangager becomes active. You can ignore the super call.
	 */
	@Override
	public void onEnter() {
		//no code here so missing super call has code executed in enter()
	}

	@Override
	public final void enter() {
		Gdx.app.debug("GameView", "Entering");
		WE.getEngineView().addInputProcessor(stage);//the input processor must be added every time because they are only 

		//enable cameras
		for (Camera camera : cameras) {
			camera.setActive(true);
		}

		if (WE.SOUND != null) {
			WE.SOUND.setView(this);
		}

		if ((boolean) WE.getCvars().get("DevMode").getValue()) {
			WE.getEngineView().setCursor(0);
		}

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		//restore gameSpeed
		WE.getCvars().get("timespeed").setValue(gameSpeed);
		onEnter();
	}

    /**
     *Updates every camera and everything else which must be updated.
     * @param dt time since last update in ms.
     */
    public void update(final float dt){
		gameSpeed = WE.getCvars().getValueF("timespeed");
		
        AbstractGameObject.resetDrawCalls();
        
        stage.act(dt);
		        
        //update cameras
		/**
		 * problem! Write acces in view. causes 1 frame hack without hacks. Workaround by post-update method.
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
    }
	    
    /**
     * Main method which is called every time and renders everything. You must manually render the devtools e.g. in an extended render method.
     */
    public void render(){       
        //Gdx.gl10.glViewport(0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        //clear screen if wished
        if ((boolean) WE.getCvars().get("clearBeforeRendering").getValue()){
            Gdx.gl20.glClearColor(0, 0, 0, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }

        //render every camera
        if (cameras.isEmpty()){
            Gdx.gl20.glClearColor(0.5f, 1, 0.5f, 1);
            Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
            drawString("No camera set up", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Color.BLACK.cpy());
        } else {
			setShader(getShader());
            for (Camera camera : cameras) {
                camera.render(this, camera);
            }
        }
               
        //render HUD and GUI
		useDefaultShader();
		spriteBatch.setProjectionMatrix(libGDXcamera.combined);
		shRenderer.setProjectionMatrix(libGDXcamera.combined);

		Gdx.gl20.glLineWidth(1);

		//set viewport of hud to cover whole window
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (controller.getDevTools() != null) {
			controller.getDevTools().render(this);
		}

		//render light engine based on first camera
		if (Controller.getLightEngine() != null && !getCameras().isEmpty()) {
			Controller.getLightEngine().render(this, getCameras().get(0).getCenter());
		}

		//render buttons
		stage.draw();
	}

    /**
     * The equalizationScale is a factor which scales the GUI/HUD to have the same relative size with different resolutions.
     * @return the scale factor
     */
    public float getEqualizationScale() {
		if (initalized)
			return libGDXcamera.viewportWidth / (int) WE.getCvars().get("renderResolutionWidth").getValue();
		else return 1;
    }

   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic. Should be verified if returning correct results.
     * @param screenX the x position on the screen
     * @param camera the camera where the position is on
     * @return view coordinate
     */
    public float screenXtoView(final int screenX, final Camera camera){
        return screenX / camera.getScreenSpaceScaling()
			- camera.getScreenPosX()
			+ camera.getViewSpaceX()
			- camera.getWidthInProjSpc()/2;//use left side
    }
    
   /**
     * Reverts the projection and transforms it into a coordinate which can be used in the game logic. Should be verified if returning correct results.
     * @param screenY the y position on the screen. y-up
     * @param camera the camera where the position is on
     * @return view coordinate
     */
    public float screenYtoView(final int screenY, final Camera camera){
        return camera.getViewSpaceY() //to view space
			+ camera.getHeightInProjSpc()/2//use top side, therefore /2
			- screenY / camera.getScreenSpaceScaling() //to view space and then revert scaling
			- camera.getScreenPosY(); //screen pos offset
    }
    
    /**
     * Returns deepest layer. Can be used in game space but then its on the floor layer.
     * @param x screen space
     * @param y screen space. y-up
     * @return the position on the map. deepest layer. If no camera returns map center.
     */
     public Point screenToGameBasic(final int x, final int y){
		 if (cameras.size() > 0) {
			 //identify clicked camera
			 Camera camera;
			 int i = 0;
			 do {
				 camera = cameras.get(i);
				 i++;
			} while (
				i < cameras.size()
				 && !(x > camera.getScreenPosX()
				 && x < camera.getScreenPosX() + camera.getWidthInScreenSpc()
				 && y > camera.getScreenPosY()
				&& y < camera.getScreenPosY()+camera.getHeightInScreenSpc())
			);

			 //find points
			 return new Point(
				 screenXtoView(x, camera),
				 screenYtoView(y, camera) * -2,
				 0
			 );
		 } else {
			 return Controller.getMap().getCenter();
		 }
    }
     
    /**
     * Returns the approximated game position belonging to a point on the screen. Does raytracing to find the intersection. Because information is lost if you do game to screen reverting this can only be done by approximating what happens in view -&gt; game. First does screen -&gt; view and then via raytracing view -&gt; game.
	 * 
     * @param x the x position on the screen from left
     * @param y the y position on the screen from bottom
     * @return the position on the map. can return null if no camera available
     */
    public Intersection screenToGame(final int x, final int y){
		if (cameras.size() > 0) {
			Point p = screenToGameBasic(x, y);
			//find point at top of map
			float deltaZ = Chunk.getGameHeight() - Block.GAME_EDGELENGTH - p.getZ();
			p.add(0, deltaZ * Point.SQRT2, deltaZ);//top of map

			return p.rayMarching(
				new Vector3(0, -1, -Block.ZAXISSHORTENING),//shoot in viewing direction, can not find correct vector: todo. Was -Point.SQRT12
				Float.POSITIVE_INFINITY,
				cameras.get(0),//assume it is in first camera
				null
			);
		} else {
			return null;
		}
    }
	
	/**
	 * Not a homomorphism, which means f(a*b) != f(a)*f(b)
	 * @param x view space
	 * @param camera
	 * @return screen space
	 */
	public int viewToScreenX(int x, Camera camera){
		return (int) (x-camera.getViewSpaceX()+camera.getWidthInScreenSpc()/2);
	}
	
	/**
	 * Not a homomorphism, which means f(a*b) != f(a)*f(b)
	 * @param y view space
	 * @param camera
	 * @return screen space 
	 */
	public int viewToScreenY(int y, Camera camera){
		return (int) (y-camera.getViewSpaceY()+camera.getHeightInScreenSpc()/2);
	}
    
	/**
     *Draw a string using the last active color.
     * @param msg
     * @param xPos screen space
     * @param yPos screen space
	 * @param openbatch true if begin/end shoould be called
     */
    public void drawString(final String msg, final int xPos, final int yPos, boolean openbatch) {
        if (openbatch) {
			spriteBatch.setProjectionMatrix(libGDXcamera.combined);
			spriteBatch.begin();
		}
			WE.getEngineView().getFont().draw(spriteBatch, msg, xPos, yPos);
        if (openbatch) spriteBatch.end();
    }
    
    /**
     *Draw a string in a color. Starts a new spriteBatch.
     * @param msg
     * @param xPos screen space
     * @param yPos screen space
     * @param color
     */
    public void drawString(final String msg, final int xPos, final int yPos, final Color color) {
        spriteBatch.setColor(Color.WHITE.cpy());
        spriteBatch.begin();
			WE.getEngineView().getFont().setColor(color);
            WE.getEngineView().getFont().draw(spriteBatch, msg, xPos, yPos);
        spriteBatch.end();
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
        //WE.getEngineView().getFont().setScale(1.01f);
        spriteBatch.begin();
        WE.getEngineView().getFont().draw(spriteBatch, text, xPos, yPos);
        spriteBatch.end();
        
//        WE.getEngineView().getFont().setColor(Color.WHITE);
//        WE.getEngineView().getFont().setScale(1f);
//        spriteBatch.begin();
//        WE.getEngineView().getFont().drawMultiLine(spriteBatch, text, xPos, yPos);
//        spriteBatch.end();
    }



    /**
     *
     * @return
     */
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

    /**
     * Add a camera to the game.
     * @param camera
     */
    protected void addCamera(final Camera camera) {
        this.cameras.add(camera);
		Controller.getMap().getOberservers().add(camera);
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
     * Game view dependent spriteBatch
     * @return 
     */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }
    
    /**
     *
     * @return
     */
    @Override
    public boolean isInitalized() {
        return initalized;
    }
	
	public void useDefaultShader(){
		spriteBatch.setShader(null);
		useDefaultShader = true;
	}

	public boolean isUsingDefaultShader() {
		return useDefaultShader;
	}
	
	/**
	 * 
	 * @param shader 
	 */
	public void setShader(ShaderProgram shader){
		spriteBatch.setShader(shader);
		useDefaultShader = false;
	}

 
	@Override
	public void exit(){
		//disable cameras
		for (Camera camera : cameras) {
			camera.setActive(false);
		}
	}

	@Override
	public void dispose() {
		shRenderer.dispose();
		spriteBatch.dispose();
		stage.dispose();
	}
	
}