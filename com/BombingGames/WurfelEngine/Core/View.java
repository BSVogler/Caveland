package com.BombingGames.WurfelEngine.Core;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WEMain;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * The View manages everything what should be drawn.
 * @author Benedikt
 */
public class View {
    /**
     * The virtual render width (resolution).
     * Every resolution smaller than this get's scaled down and every resolution bigger scaled up. 
     */
    public static final int RENDER_RESOLUTION_WIDTH = 1920;

    private static BitmapFont font;
    
    private SpriteBatch batch;    
    private ShapeRenderer shapeRenderer;
    
    private float equalizationScale;
    private Controller controller;
    
    private int drawmode;
    
    private OrthographicCamera hudCamera;
    
    
    /**
     *
     * @param controller
     */
    public void init(Controller controller){
        Gdx.app.debug("View", "Initializing");
        
        this.controller = controller;
        
        //font = WurfelEngine.getInstance().manager.get("com/BombingGames/WurfelEngine/EngineCore/arial.fnt"); //load font
        font = new BitmapFont(true);
        //font.scale(2);
        for (TextureRegion region : font.getRegions()) {
                region.flip(false, false);
            }
        
        font.setColor(Color.GREEN);
        //font.scale(-0.5f);
        
        //default rendering size is FullHD
        equalizationScale = Gdx.graphics.getWidth() / RENDER_RESOLUTION_WIDTH;
        Gdx.app.debug("View","Scale is:" + Float.toString(equalizationScale));
 
        hudCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        hudCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        
        Block.loadSheet();
    }
    
    /**
     *
     * @param delta
     */
    public void update(int delta){
    }
    
    /**
     * Main method which is called every time and renders everything.
     */
    public void render(){       
        //Gdx.gl10.glViewport(0, 0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        //Gdx.gl10.glClearColor(0, 0, 0, 1);
        //Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT); //clearing the screen is ~5-10% slower than without.
        
        //render every camera
        for (WECamera camera : controller.getCameras()) {
            camera.render(this, camera);
        }
        
        
        //render HUD
        // hudCamera.zoom = 1/equalizationScale;
        hudCamera.update();
        hudCamera.apply(Gdx.gl10);
         
        batch.setProjectionMatrix(hudCamera.combined);
        shapeRenderer.setProjectionMatrix(hudCamera.combined);
        
        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );
        
        drawString("FPS:"+ Gdx.graphics.getFramesPerSecond(), 10, 10);
        
        controller.getFpsdiag().render(this);
        
        //scale to fit
        //hudCamera.zoom = 1/equalizationScale;
        
        if (Controller.getLightengine() != null)
            Controller.getLightengine().render(this);
        
        if (controller.getMinimap() != null)
            controller.getMinimap().render(this); 
        
        GameplayScreen.msgSystem().render(this);
    }
       

    /**
     * The equalizationScale is a factor which the image is scaled by to have the same size on different resolutions.
     * @return the scale factor
     */
    public float getEqualizationScale() {
        return equalizationScale;
    }

    
   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic.
     * @param x the x position on the screen
     * @param camera the camera where the position is on
     * @return the relative game coordinate
     */
    public float ScreenXtoGame(int x, WECamera camera){
        return x / camera.getTotalScale()- camera.getViewportPosX()+ camera.getOutputPosX();
    }
    
   /**
     * Reverts the perspective and transforms it into a coordiante which can be used in the game logic.
     * @param y the y position on the screen
     * @param camera the camera where the position is on
     * @return the relative game coordinate
     */
    public float ScreenYtoGame(int y, WECamera camera){
        return (y / camera.getTotalScale() + camera.getOutputPosY())*2 - camera.getViewportPosY();
    }
    
    /**
     * Returns the coordinates belonging to a point on the screen.
     * @param x the x position on the screen
     * @param y the y position on the screen
     * @return the relative map coordinates
     */
    public Coordinate ScreenToGameCoords(int x, int y){
        //find camera
        WECamera camera;
        int i = 0;
         do {          
            camera = controller.getCameras().get(i);
            i++;
        } while (i < controller.getCameras().size()
            && !(x > camera.getViewportPosX() && x < camera.getViewportPosX()+camera.getViewportWidth()
                && y > camera.getViewportPosY() && y < camera.getViewportPosY()+camera.getViewportHeight()));
 

        
        return Controller.findCoordinate(
            new Point(
                ScreenXtoGame(x, camera),
                ScreenYtoGame(y, camera),
                Chunk.getGameHeight()-1, true),
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
     *
     * @param drawmode
     */
    public void setDrawmode(int drawmode) {
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
    public void drawString(String msg, int xPos, int yPos) {
        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        batch.begin();
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
    public void drawString(String msg, int xPos, int yPos, Color color) {
        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        font.setColor(color);
        batch.begin();
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
    public void drawText(String text, int xPos, int yPos, Color color){
        Gdx.gl10.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
        font.setColor(Color.BLACK);
        font.setScale(0.51f);
        batch.begin();
        font.drawMultiLine(batch, text, xPos, yPos);
        batch.end();
        
        font.setColor(Color.WHITE);
        font.setScale(0.5f);
        batch.begin();
        font.drawMultiLine(batch, text, xPos, yPos);
        batch.end();
    }
    
    /**
     *
     * @return
     */
    public OrthographicCamera getHudCamera() {
        return hudCamera;
    } 

    /**
     *
     * @return
     */
    public SpriteBatch getBatch() {
        return batch;
    }

    /**
     *
     * @return
     */
    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Controller getController() {
        return controller;
    }
}