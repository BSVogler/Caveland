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
package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import java.util.ArrayList;

/**
 * The View manages everything what should be drawn.
 * @author Benedikt
 */
public class View {
    private final ArrayList<Camera> cameras = new ArrayList<Camera>(6);//max 6 cameras
    
    private static BitmapFont font;
    
    private SpriteBatch batch;    
    private ShapeRenderer shapeRenderer;
    
    private Controller controller;
    
    private int drawmode;
    
    private OrthographicCamera hudCamera;
    private boolean keyF5isUp;
    
    private Stage stage;
    
    
    /**
     * Shoud be called before the object get initialized.
     * Initializes class fields.
     */
    public static void classInit(){
        //set up font
        //font = WurfelEngine.getInstance().manager.get("com/BombingGames/WurfelEngine/EngineCore/arial.fnt"); //load font
        font = new BitmapFont(false);
        //font.scale(2);

        
        font.setColor(Color.GREEN);
        //font.scale(-0.5f);
        
        //load sprites
        Block.loadSheet();
    }
    
    /**
     *Loades some files and set up everything.
     * @param controller
     */
    public void init(final Controller controller){
        Gdx.app.debug("View", "Initializing");
        
        this.controller = controller;
        
        //set up stage
        stage = new Stage();
        View.addInputProcessor(stage);
        
        //set up renderer
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        //set up cursor
        Pixmap cursor = new Pixmap(Gdx.files.internal("com/BombingGames/WurfelEngine/Core/images/cursor.png"));
        Gdx.input.setCursorImage(cursor, 8, 8);
    }
    
    /**
     * Add an inputProcessor to the views.
     * @param processor 
     */
    public static void addInputProcessor(final InputProcessor processor){
        if (Gdx.input.getInputProcessor() == null){
            Gdx.input.setInputProcessor(processor);
        }else{
            InputMultiplexer inputMultiplexer = new InputMultiplexer(Gdx.input.getInputProcessor());
            inputMultiplexer.addProcessor(processor);
            Gdx.input.setInputProcessor(inputMultiplexer);
        }
    }
        
    /**
     *Updates every camera and everything else which must be updated.
     * @param delta time since last update
     */
    public void update(float delta){
        //update cameras
        for (Camera camera : cameras) {
            if (camera.togglesChunkSwitch()) {
                //earth to right
                if (camera.getVisibleLeftBorder() <= 0)
                    Controller.getMap().setCenter(3);
                else
                    if (camera.getVisibleRightBorder() >= Map.getBlocksX()-1) 
                        Controller.getMap().setCenter(5); //earth to the left

                //scroll up, earth down            
                if (camera.getVisibleTopBorder() <= 0)
                    Controller.getMap().setCenter(1);
                else
                    if (camera.getVisibleBottomBorder() >= Map.getBlocksY()-1)
                        Controller.getMap().setCenter(7); //scroll down, earth up
            }
            camera.update();
        }
        
        //you can toggle the dev menu
        if (keyF5isUp && Gdx.input.isKeyPressed(Keys.F5)) {
            controller.getDevTools().setVisible(!controller.getDevTools().isVisible());
            keyF5isUp = false;
        }
        keyF5isUp = !Gdx.input.isKeyPressed(Keys.F5);
    }
    
    /**
     * Main method which is called every time and renders everything.
     */
    public void render(){       
        //Gdx.gl10.glViewport(0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        //clear screen if wished
        if (WE.getCurrentConfig().clearBeforeRendering()){
            Gdx.gl10.glClearColor(0, 0, 0, 1);
            Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
        }

        //render every camera
        if (cameras.isEmpty()){
            Gdx.gl10.glClearColor(0.5f, 1, 0.5f, 1);
            Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT);
            drawString("No camera set up", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, Color.BLACK.cpy());
        }else {
            for (Camera camera : cameras) {
                camera.render(this, camera);
            }
        }
        
        //render load screen
        if (controller.getLoadMenu().isOpen()){
            controller.getLoadMenu().render(this);
        }
        
        
        //render HUD
        {
            // hudCamera.zoom = 1/equalizationScale;
            hudCamera.update();
            hudCamera.apply(Gdx.gl10);

            batch.setProjectionMatrix(hudCamera.combined);
            shapeRenderer.setProjectionMatrix(hudCamera.combined);

            //set vieport of hud to cover whole window
            Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

            controller.getDevTools().render(this);

            //render buttons
            stage.draw();

            //scale to fit
            //hudCamera.zoom = 1/equalizationScale;

            if (Controller.getLightengine() != null)
                Controller.getLightengine().render(this);

            if (controller.getMinimap() != null)
                controller.getMinimap().render(this); 

            GameplayScreen.msgSystem().render(this);
        }
    }
       

    /**
     * The equalizationScale is a factor which scales the GUI/HUD to have the same relative size with different resolutions.
     * @return the scale factor
     */
    public float getEqualizationScale() {
        return Gdx.graphics.getWidth() / WE.getCurrentConfig().getRenderResolutionWidth();
    }

    
   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic.
     * @param x the x position on the screen
     * @param camera the camera where the position is on
     * @return the relative game coordinate
     */
    public float screenXtoGame(final int x, final Camera camera){
        return x / camera.getScaling()- camera.getScreenPosX()+ camera.getViewportPosX();
    }
    
   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic.
     * @param y the y position on the screen
     * @param camera the camera where the position is on
     * @return the relative game coordinate
     */
    public float screenYtoGame(final int y, final Camera camera){
        return (y / camera.getScaling() + camera.getViewportPosY())*2 - camera.getScreenPosY();
    }
    
    /**
     * Returns the coordinates belonging to a point on the screen.
     * @param x the x position on the screen
     * @param y the y position on the screen
     * @return the map coordinates
     */
    public Coordinate screenToGameCoords(final int x, final int y){
        //identify clicked camera
        Camera camera;
        int i = 0;
        do {          
            camera = cameras.get(i);
            i++;
        } while (
                i < cameras.size()
                && !(x > camera.getScreenPosX() && x < camera.getScreenPosX()+camera.getScreenWidth()
                && y > camera.getScreenPosY() && y < camera.getScreenPosY()+camera.getScreenHeight())
        );
 
        //find coordinate
        return Point.toCoord(
            new Point(
                screenXtoGame(x, camera),
                screenYtoGame(y, camera),
                Chunk.getGameHeight()-1,
                true
            ),
            true,
            true
        );
    }
    

    /**
     * 
     * @return
     */
    public BitmapFont getFont() {
        return font;
    }

    /**
     *
     * @return
     */
    public int getDrawmode() {
        return drawmode;
    }

    /**
     *The batch must be began before claling this method.
     * @param drawmode
     */
    public void setDrawmode(final int drawmode) {
        if (drawmode != this.drawmode){
            this.drawmode = drawmode;
            batch.end();
            //GameObject.getSpritesheet().getFullImage().endUse();
            Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, drawmode);
            //GameObject.getSpritesheet().getFullImage().startUse();
            batch.begin();
        }
    }

    /**
     *Draw a string using the last active color.
     * @param msg
     * @param xPos
     * @param yPos
     */
    public void drawString(final String msg, final int xPos, final int yPos) {
        batch.begin();
        setDrawmode(GL10.GL_MODULATE);
        font.draw(batch, msg, xPos, yPos);
        batch.end();
    }
    
    /**
     *Draw a string in a color.
     * @param msg
     * @param xPos
     * @param yPos
     * @param color
     */
    public void drawString(final String msg, final int xPos, final int yPos, final Color color) {
        font.setColor(color);
        batch.begin();
        setDrawmode(GL10.GL_MODULATE);
        font.draw(batch, msg, xPos, yPos);
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
        font.setColor(Color.BLACK);
        font.setScale(0.51f);
        batch.begin();
        setDrawmode(GL10.GL_MODULATE);
        font.drawMultiLine(batch, text, xPos, yPos);
        batch.end();
        
        font.setColor(Color.WHITE);
        font.setScale(0.5f);
        batch.begin();
        font.drawMultiLine(batch, text, xPos, yPos);
        batch.end();
        font.setScale(1f);
    }
    
    /**
     *
     * @return
     */
    public OrthographicCamera getHudCamera() {
        return hudCamera;
    } 

    /**
     *y-down
     * @return
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     * Y-down
     * @return
     */
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
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
     * Add a camera.
     * @param camera
     */
    protected void addCamera(final Camera camera) {
        this.cameras.add(camera);
    }
    
     /**
     * should be called when the window get resized
     * @param width
     * @param height 
     */
    public void resize(final int width, final int height) {
        for (Camera camera : cameras) {
            camera.resize(width, height);
        }
    }

    public Stage getStage() {
        return stage;
    }
}