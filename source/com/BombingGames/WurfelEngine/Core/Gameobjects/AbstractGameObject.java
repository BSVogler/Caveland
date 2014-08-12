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

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.LightEngine.PseudoGrey;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 *An object is something wich can be found in the game world.
 * @author Benedikt
 */
public abstract class AbstractGameObject {
    /**Screen SCREEN_DEPTH of a block/object sprite in pixels. This is the length from the top to the middle border of the block.
     * In game coordinates this is also the dimension from top to bottom.*/
    public static final int SCREEN_DEPTH = 80;
    /**The half (2) of SCREEN_DEPTH. The short form of: SCREEN_DEPTH/2*/
    public static final int SCREEN_DEPTH2 = SCREEN_DEPTH / 2;
    /**A quarter (4) of SCREEN_DEPTH. The short form of: SCREEN_DEPTH/4*/
    public static final int SCREEN_DEPTH4 = SCREEN_DEPTH / 4;
    
    /**
     * The width (x-axis) of the sprite size
     */
    public static final int SCREEN_WIDTH = 160;
    /**The half (2) of SCREEN_WIDTH. The short form of: SCREEN_WIDTH/2*/
    public static final int SCREEN_WIDTH2 = SCREEN_WIDTH / 2;
    /**A quarter (4) of SCREEN_WIDTH. The short form of: SCREEN_WIDTH/4*/
    public static final int SCREEN_WIDTH4 = SCREEN_WIDTH / 4;
    
    /**
     * The height (y-axis) of the sprite size
     */
    public static final int SCREEN_HEIGHT = 80;
    /**The half (2) of SCREEN_HEIGHT. The short form of: SCREEN_WIDTH/2*/
    public static final int SCREEN_HEIGHT2 = SCREEN_HEIGHT / 2;
    /**A quarter (4) of SCREEN_HEIGHT. The short form of: SCREEN_WIDTH/4*/
    public static final int SCREEN_HEIGHT4 = SCREEN_HEIGHT / 4;
    
    /**The real game world dimension in pixel (edge length). 1 game meter ^= 1 GAME_DIMENSION
       * Usually the use of SCREEN_DEPTH is enough because of the map format every coordinate center is straight.
        * The value is calculated by SCREEN_HEIGHT*sqrt(2) because of the axis shortening.
        */
    public static final int GAME_EDGELENGTH = (int) (SCREEN_HEIGHT * Math.sqrt(2));
    
    /**
     * The game size's aequivalent to SCREEN_DEPTH.
     * The value is GAMEDIMENSION * Math.sqrt(2) which is the same as SCREEN_HEIGHT * 2
     */
    public static final int GAME_DIAGLENGTH = SCREEN_HEIGHT * 2;
    
    /**Half of GAME_DIAGLENGTH<br />
     * This is in the normal case aqueivalent to SCREEN_HEIGHT.
     */
    public static final int GAME_DIAGLENGTH2 = SCREEN_HEIGHT;
    
    /**the max. amount of different object types*/
    public static final int OBJECTTYPESCOUNT = 99;
      /**the max. amount of different values*/
    public static final int VALUESCOUNT = 25;
    

        
    /**The sprite texture which contains every object texture*/
    private static TextureAtlas spritesheet;
    private static Pixmap pixmap;
    private static AtlasRegion[][][] sprites = new AtlasRegion['z'][OBJECTTYPESCOUNT][VALUESCOUNT];//{category}{id}{value}
    private static int drawCalls =0;
    
    private final int id; 
    private byte value;
    private boolean obstacle, transparent, clipped, hidden; 
    private float lightlevel = 0.5f;
    private int dimensionZ = GAME_EDGELENGTH;  

    
    /**
     * Creates an object. Use getInstance() to create blocks or entitys.
     * @param id the id of the object
     * @param value 
     * @see com.BombingGames.WurfelEngine.Core.Gameobjects.Block#getInstance(int) 
     */
    protected AbstractGameObject(int id, int value) {
        this.id = id;
        this.value = (byte)value;
    }
    
    /**
     * Updates the logic of the object.
     * @param delta time since last update
     */
    public abstract void update(float delta);
    
    /**
     *
     * @return
     */
    public abstract char getCategory();
    
    /**
     * Place you static update methods here.
     * @param delta 
     */
    public static void updateStaticUpdates(float delta){
        Sea.staticUpdate(delta);
    }
        
    /**
     * Draws an object in the color of the light engine and with the lightlevel. Only draws if not hidden and not clipped.
     * @param pos the coordinates where the object should be rendered
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     */
    public void render(View view, Camera camera, AbstractPosition pos) {
        render(
            view,
            camera,
            pos,
            (Controller.getLightengine() != null ? Controller.getLightengine().getGlobalLight() : Color.GRAY.cpy()).mul(lightlevel)
        );
    }
    
    /**
     * Draws an object in the color of the light engine and with the lightlevel. Only draws if not hidden and not clipped.
     * @param pos the coordinates where the object should be rendered
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param scale
     */
    public void render(View view, Camera camera, AbstractPosition pos, float scale) {
        render(
            view,
            camera,
            pos,
            (Controller.getLightengine() != null ? Controller.getLightengine().getGlobalLight() : Color.GRAY.cpy()).mul(lightlevel),
            scale
        );
    }
    
     /**
     * Draws an object if it is not hidden and not clipped.
     * @param pos the coordinates where the object is rendered
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param color  custom blending color
     */
    public void render(View view, Camera camera, AbstractPosition pos, Color color) {
        render(
            view,
            camera,
            pos,
            color,
            (WE.getCurrentConfig().useScalePrototype()) ? pos.getPoint().getHeight()/(Map.getGameHeight()) : 0
        );
    }
    
         /**
     * Draws an object if it is not hidden and not clipped.
     * @param pos the posiiton where the object is rendered. The center of the object.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param color  custom blending color
     * @param scale
     */
    public void render(View view, Camera camera, AbstractPosition pos, Color color, float scale) {
        //draw the object except not clipped ones
        if (!hidden && !clipped) {             
            render(
                view,
                pos.getProjectedPosX(),
                pos.getProjectedPosY(),
                color,
                scale
            );
        }
    }
    
        /**
     * Renders at a custom position with the global light.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     */
    public void render(View view, int xPos, int yPos) {
        render(
            view,
            xPos,
            yPos,
            Controller.getLightengine() != null ? Controller.getLightengine().getGlobalLight() : Color.GRAY.cpy(),
            0
        );
    }
    
    /**
     * Renders at a custom position with the global light.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param scale
     */
    public void render(View view, int xPos, int yPos, float scale) {
        render(
            view,
            xPos,
            yPos,
            Controller.getLightengine() != null ? Controller.getLightengine().getGlobalLight() : Color.GRAY.cpy(),
            scale
        );
    }
    
    /**
     * Renders at a custom position with a custom light.
     * @param view
     * @param xPos rendering position, center of sprite (screen space?)
     * @param yPos rendering position, center of sprite (screen space?)
     * @param color custom blending color
     * @param scale relative value
     */
    public void render(View view, int xPos, int yPos, Color color, float scale) {
        AtlasRegion texture = getSprite(getCategory(), id, value);
        Sprite sprite = new Sprite(texture);
        sprite.setPosition(
            xPos+texture.offsetX-texture.originalWidth/2,
            yPos-SCREEN_HEIGHT-SCREEN_DEPTH2+texture.offsetY+
                (SCREEN_HEIGHT+SCREEN_DEPTH-texture.originalHeight)
        );
        
        sprite.scale(scale);
        prepareColor(view, color);

        sprite.setColor(color);
        sprite.draw(view.getBatch());
        
        
        if (WE.getCurrentConfig().debugObjects()){
            ShapeRenderer sh = view.getIgShRender();
            sh.begin(ShapeRenderer.ShapeType.Line);
            //sprite outline
            sh.rect(
                sprite.getX(),
                sprite.getY(),
                sprite.getWidth(),
                sprite.getHeight()
            );
            //crossing lines
            sh.line(
                xPos-SCREEN_WIDTH2,
                yPos-SCREEN_DEPTH2,
                xPos+SCREEN_WIDTH2,
                yPos+SCREEN_DEPTH2
            );
            sh.line(
                xPos-SCREEN_WIDTH2,
                yPos+SCREEN_DEPTH2,
                xPos+SCREEN_WIDTH2,
                yPos-SCREEN_DEPTH2
            );
            //bounding box
            sh.line(xPos-SCREEN_WIDTH2, yPos, xPos, yPos-SCREEN_DEPTH2);
            sh.line(xPos-SCREEN_WIDTH2, yPos, xPos, yPos+SCREEN_DEPTH2);
            sh.line(xPos, yPos-SCREEN_DEPTH2, xPos+SCREEN_WIDTH2, yPos);
            sh.line(xPos, yPos+SCREEN_DEPTH2, xPos+SCREEN_WIDTH2, yPos);
            sh.end();
        }

        drawCalls++;
    }
    
    /**
     * Transform the color that it works with the blending mode which is also set in this method. Spritebatch must be began first.
     * @param view
     * @param color a tint in which the sprite should be rendered
     */
    public void prepareColor(View view, Color color){
        float brightness = PseudoGrey.toFloat(color);
        //float brightness = (color.r+color.g+color.b)/3;
        
        if (brightness > 0.5f){
            view.setDrawmode(GL10.GL_ADD);
            color.r -= .5f;
            color.g -= .5f;
            color.b -= .5f;
        } else {
            view.setDrawmode(GL10.GL_MODULATE);
            color.mul(2);
        }
        color.clamp();
        color.a = 1;
    }
    
    /**
     * Load the spritesheet from memory.
     */
    public static void loadSheet()  {
        //spritesheet = new TextureAtlas(Gdx.files.internal("com/BombingGames/Game/Blockimages/Spritesheet.txt"), true);
        Gdx.app.log("AGameObject", "getting spritesheet");
        if (spritesheet == null) {
            spritesheet = WE.getAsset(WE.getCurrentConfig().getSpritesheetPath()+".txt");
            for (AtlasRegion region : spritesheet.getRegions()) {
                region.flip(false, true);
            }
        }
        
        //load again for pixmap, allows access to image color data;
        if (pixmap == null) {
            //pixmap = WurfelEngine.getInstance().manager.get("com/BombingGames/Game/Blockimages/Spritesheet.png", Pixmap.class);
            pixmap = new Pixmap(
                Gdx.files.internal(WE.getCurrentConfig().getSpritesheetPath()+".png")
            );
        }
    }

    /**
     * Returns a sprite texture. You may use your own method like in <i>Block</i>.
     * @param category the category of the sprite e.g. "b" for blocks
     * @param id the id of the object
     * @param value the value of the object
     * @return 
     */
    public static AtlasRegion getSprite(final char category, final int id, final int value) {
        if (spritesheet == null) return null;
        if (sprites[category][id][value] == null){ //load if not already loaded
            AtlasRegion sprite = spritesheet.findRegion(category+Integer.toString(id)+"-"+value);
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                Gdx.app.debug("Spritesheet", category+Integer.toString(id)+"-"+value + " not found");
                sprite = getSpritesheet().findRegion(category+"0-0");
                if (sprite == null) {//load generic error sprite if category sprite failed
                    sprite = getSpritesheet().findRegion("error");
                    if (sprite == null) throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
                }
            }
            sprites[category][id][value] = sprite;
            return sprite;
        } else {
            return sprites[category][id][value];
        }
    }


    //getter & setter
    
     /**
     * Returns the spritesheet used for rendering.
     * @return the spritesheet used by the objects
     */
    public static TextureAtlas getSpritesheet() {
        return spritesheet;
    }

    /**
     *
     * @return
     */
    public static Pixmap getPixmap() {
        return pixmap;
    }
    
    /**
     * returns the id of a object
     * @return getId
     */
    public int getId() {
        return this.id;
    }

    /**
     * How bright is the object?
     * The lightlevel is a number between 0 and 1. 1 is full bright. 0 is black. Default is .5.
     * @return
     */
    public float getLightlevel() {
        return lightlevel;
    }

    /**
     * Returns the depth of the object. The depth is an int value wich is needed for producing the list of the renderorder. The higher the value the later it will be drawn.
     * @param pos 
     * @return the depth in game size
     */
    public abstract int getDepth(AbstractPosition pos);
    
    /**
     * Returns the name of the object
     * @return the name of the object
     */
    public abstract String getName();
    
    /**
     * Get the value. It is like a sub-id and can identify the status.
     * @return
     */
    public int getValue() {
        return value;
    }
    
        /**
     * 
     * @return
     */
    public int getDimensionZ() {
        return dimensionZ;
    }

    /**
     * Returns true, when set as hidden. Hidden objects are not rendered even when they are clipped ("clipped" by the meaning of the raytracing).
     * @return if the object is invisible
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Is this object an obstacle or can you pass through?
     * @return
     */
    public boolean isObstacle() {
        return obstacle;
    }

    /**
     * Can light travel through object?
     * @return
     */
    public boolean isTransparent() {
        return transparent;
    }

    /**
     * Is the object clipped?
     * @return true when clipped
     */
    public boolean isClipped() {
        return clipped;
    }


    /**
     * Set the brightness of the object.
     * The lightlevel is a number between 0 and 1. 1 is full bright. 0 is black.
     * @param lightlevel
     */
    public void setLightlevel(float lightlevel) {
        this.lightlevel = lightlevel;
    }

    /**
     * Make the object to an obstacle or passable.
     * @param obstacle true when obstacle. False when passable.
     */
    public void setObstacle(boolean obstacle) {
        this.obstacle = obstacle;
    }

    /**
     * Has the object transparent areas?
     * @param transparent
     */
    public void setTransparent(boolean transparent) {
        this.transparent = transparent;
    }

    /**
     * Set the value of the object.
     * @param value
     */
    public void setValue(int value) {
        this.value = (byte)value;
    }

    /**
     * Hide this object and prevent it from beeing rendered. Don't use this to hide objects as "invisible". This method is only for the rendering process  and view specific not for gameworld information. This should be just used for clipping during the rendering process.
     * @param clipped Sets the visibility.
     * @see com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject#setHidden(boolean) 
     */
    public void setClipped(boolean clipped) {
        this.clipped = clipped;
    }

    /**
     * Hide an object. It won't be rendered even if it is clipped.
     * @param hidden
     */
    public void setHidden(boolean hidden){
        this.hidden = hidden;
    }

    /**
     * Set the height of the object.
     * @param dimensionZ
     */
    public void setDimensionZ(int dimensionZ) {
        this.dimensionZ = dimensionZ;
    }

    /**
     *
     * @return
     */
    public static AtlasRegion[][][] getSprites() {
        return sprites;
    }

    /**
     * Reset couner for this frame
     */
    public static void resetDrawCalls() {
        AbstractGameObject.drawCalls = 0;
    }

    /**
     * Maybe not quite correct. A single block has only one drawcall even it should consist of three.
     * @return 
     */
    public static int getDrawCalls() {
        return drawCalls;
    }
    
    /**
     * When calling sprite.draw this hsould also be called for statistics.
     */
    protected void increaseDrawCalls(){
        drawCalls++;
    }
    
    
    /**
     *
     */
    public static void staticDispose(){
        spritesheet.dispose();//is this line needed?
        WE.getAssetManager().unload(WE.getCurrentConfig().getSpritesheetPath()+".txt");
        spritesheet = null;
        sprites = new AtlasRegion['z'][OBJECTTYPESCOUNT][VALUESCOUNT];
        //pixmap.dispose();
        pixmap = null;
    }
}