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
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A Block is a wonderful piece of information and a geometrical object.
 * @author Benedikt Vogler
 */
public class Block extends AbstractGameObject {
    /**The id of the left side of a block.*/
    public static final byte LEFTSIDE=0;
    /**The id of the top side of a block.*/
    public static final byte TOPSIDE=1;
    /**The id of the right side of a block.*/
    public static final byte RIGHTSIDE=2;
    
    /**Containts the names of the objects. index=id*/
    public static final String[] NAMELIST = new String[OBJECTTYPESCOUNT];
    
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[OBJECTTYPESCOUNT][VALUESCOUNT][3];//{id}{value}{side}
        
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] colorlist = new Color[OBJECTTYPESCOUNT][VALUESCOUNT];
    
    private boolean liquid;
    private boolean hasSides = true;
    private boolean clippedRight = false;
    private boolean clippedTop = false;
    private boolean clippedLeft = false;
    
    static {
        NAMELIST[0] = "air";
        NAMELIST[1] = "grass";
        NAMELIST[2] = "dirt";
        NAMELIST[3] = "stone";
        NAMELIST[4] = "asphalt";
        NAMELIST[5] = "cobblestone";
        NAMELIST[6] = "pavement";
        NAMELIST[7] = "concrete";
        NAMELIST[8] = "sand";
        NAMELIST[9] = "water";
        NAMELIST[20] = "red brick wall";
        NAMELIST[30] = "fence";
        NAMELIST[32] = "sandbags";
        NAMELIST[33] = "crate";
        NAMELIST[34] = "flower";
        NAMELIST[35] = "round bush";
        NAMELIST[50] = "strewbed";
        NAMELIST[70] = "campfire";
        NAMELIST[71] = "explosive barrel";
        NAMELIST[72] = "animation test";
    }
    
    /**
     * Don't use this constructor to get a new block. Use the static <i>getInstance</i> methods instead.
     * @param id
     * @see com.BombingGames.WurfelEngine.Core.Gameobjects.Block#getInstance() 
     */
    protected Block(int id){
        super(id,0);
    } 
    
    /**
     * You can create a basic block if its id is not reserved. Else getInstace() is called.
     * @param id non-reserved id's=> id>39
     * @return 
     */
    public static Block createBasicInstance(final int id){
        Block block; 
        if (id>39) 
            block = new Block(id);
        else block = getInstance(id,0,null);
        return block;
    }
    
    /**
     *  Create a block. If the block needs to know it's position you have to use <i>getInstance(int id, int value,int x, int y, int z)</i>
     * @param id the block's id
     * @return the wanted block.
     */
    public static Block getInstance(final int id){
        return getInstance(id,0,null);
    }
    
    /**
     * Create a block. If the block needs to know it's position you have to use <i>getInstance(int id, int value,int x, int y, int z)</i>
     * @param id the block's id
     * @param value it's value
     * @return the wanted block.
     */
    public static Block getInstance(final int id, final int value){
        return getInstance(id,value,null);
    }
    
    /**
     * Create a block through this factory method. If the block needs to know it's position you have to use this method and give the coordinates.
     * @param id the id of the block
     * @param value the value of the block, which is like a sub-id
     * @param coords the coordinates where the block is going to be places. If the block does not need this information it can be null.
     * @return the Block
     */
    public static Block getInstance(final int id, final int value, final Coordinate coords){
        Block block;
        //define the default SideSprites
        switch (id){
            case 0: 
                    block = new Block(id);//air
                    block.setTransparent(true);
                    block.setHidden(true);
                    break;
            case 1: block = new Block(id); //grass
                    block.setObstacle(true);
                    break;
            case 2: block = new Block(id); //dirt
                    block.setObstacle(true);
                    break;
            case 3: block = new Block(id); 
                    block.setTransparent(false);
                    block.setObstacle(true);
                    break;
            case 4: block = new Block(id); 
                    block.setObstacle(true);
                    break;
            case 5: block = new Block(id); 
                    block.setObstacle(true);
                    break;
            case 6: block = new Block(id); 
                    block.setObstacle(true);
                    break;
            case 7: block = new Block(id); 
                    block.setObstacle(true);
                    break;
            case 8: block = new Block(id); //sand
                    block.setObstacle(true);
                    break;      
            case 9: if(Gdx.app.getType()==ApplicationType.Android)
                        block = new Block(id); //static water
                    else
                        block = new Sea(id, coords); //Sea
                    block.liquid = true;
                    block.setTransparent(true);
                    break;
            case 20: block = new Block(id);
                    block.setObstacle(true);
                    break;
            case 34: block = new Block(id); //flower
                    block.setTransparent(true);
                    block.hasSides = false;
                    break;
            case 35: block = new Block(id); //bush
                    block.setTransparent(true);
                    block.hasSides = false;
                    break;     
            default:
                if (id > 39) {
                    if (WE.getCurrentConfig().getBlockFactoy()!=null){
                        block = WE.getCurrentConfig().getBlockFactoy().produce(id, value, coords);
                    } else {
                        Gdx.app.error("Block", "Tried creating of custom block but there was no custom blockfactory found. Tried using a default block.");
                        block = new Block(id);
                    }
                } else {
                    Gdx.app.error("Block", "Engine reserved block "+id+" not defined.");
                    block = new Block(id);
                }
                break; 
        }
        block.setValue(value);
        return block;
    }  
    
    
     /**
     *  Returns a sprite sprite of a specific side of the block
     * @param id the id of the block
     * @param value the value of teh block
     * @param side Which side? (0 - 2)
     * @return an sprite of the side
     */
    public static AtlasRegion getBlockSprite(final int id, final int value, final int side) {
        if (getSpritesheet() == null) throw new NullPointerException("No spritesheet found.");
        
        if (blocksprites[id][value][side] == null){ //load if not already loaded
            AtlasRegion sprite = getSpritesheet().findRegion('b'+Integer.toString(id)+"-"+value+"-"+side);
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                
                Gdx.app.debug("debug", 'b'+Integer.toString(id)+"-"+value +"-"+ side +" not found");
                
                sprite = getSpritesheet().findRegion("b0-0-"+side);
                
                if (sprite == null) {//load generic error sprite if category sprite failed
                    sprite = getSpritesheet().findRegion("error");
                    if (sprite == null) throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
                }
            }
            blocksprites[id][value][side] = sprite;
            return sprite;
        } else {
            return blocksprites[id][value][side];
        }
    }
    

    
   /**
     * Returns a color representing the block. Picks from the sprite sprite.
     * @param id id of the Block
     * @param value the value of the block.
     * @return a color representing the block
     */
    public static Color getRepresentingColor(final int id, final int value){
        if (colorlist[id][value] == null){ //if not in list, add it to the list
            colorlist[id][value] = new Color();
            int colorInt;
            
            if (Block.getInstance(id,value, new Coordinate(0,0,0,false)).hasSides){    
                AtlasRegion texture = getBlockSprite(id, value, 1);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+SCREEN_DEPTH2, texture.getRegionY()-SCREEN_DEPTH4);
            } else {
                AtlasRegion texture = getSprite('b', id, value);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+SCREEN_DEPTH2, texture.getRegionY()-SCREEN_DEPTH2);
            }
            Color.rgba8888ToColor(colorlist[id][value], colorInt);
            return colorlist[id][value]; 
        } else return colorlist[id][value]; //return value when in list
    }

    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
    public boolean isLiquid() {
        return liquid;
    } 
    
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
     * @return 
     */
    public boolean hasSides() {
        return hasSides;
    } 
    
        /**
     * 
     * @param clipped When it is set to false, every side will also get clipped..
     */
    @Override
    public void setClipped(final boolean clipped) {
        super.setClipped(clipped);
        if (clipped) {
            clippedLeft = true;
            clippedTop = true;
            clippedRight = true;
        }
    }
    
    /**
     * Make a side (in)clipping. If one side is clipping, the whole block is clipping.
     * @param side 0 = left, 1 = top, 2 = right
     * @param clipping true when it should be clipped.
     */
    public void setSideClipping(final int side, final boolean clipping) {
        if (!clipping) this.setClipped(false);
        
        if (side==0)
            clippedLeft = clipping;
        else if (side==1)
            clippedTop = clipping;
                else if (side==2)
                    clippedRight = clipping;
    }
    
    @Override
    public void render(final View view, final Camera camera, final AbstractPosition coords) {
        if (!isClipped() && !isHidden()) {
            float scale =0;
            if (WE.getCurrentConfig().useScalePrototype())
                scale = (coords.getCoord().getZ()/(float) (Map.getBlocksZ()));
            if (hasSides) {
                if (!clippedTop)
                    renderSide(view, camera, coords, Block.TOPSIDE, scale);
                if (!clippedLeft)
                    renderSide(view, camera, coords, Block.LEFTSIDE, scale);
                if (!clippedRight)
                    renderSide(view, camera, coords, Block.RIGHTSIDE, scale);
            } else
                super.render(view, camera, coords, scale);
        }
    }
    
    /**
     * Render the whole block at a custom position and checks for clipping and hidden.
     * @param view the view using this render method
     * @param xPos rendering position (screen)
     * @param yPos rendering position (screen)
     */
    @Override
    public void render(final View view, final int xPos, final int yPos) {
        if (!isClipped() && !isHidden()) {
            if (hasSides) {
                if (!clippedTop)
                    renderSide(view, xPos, yPos, Block.TOPSIDE);
                if (!clippedLeft)
                    renderSide(view, xPos, yPos+SCREEN_WIDTH4, Block.LEFTSIDE);
                if (!clippedRight)
                    renderSide(view, xPos+SCREEN_WIDTH2, yPos+SCREEN_WIDTH4, Block.RIGHTSIDE);
                } else
                    super.render(view, xPos, yPos);
        }
    }

    @Override
    public void render(final View view, final int xPos, final int yPos, final Color color, float scale) {
        render(view, xPos, yPos, color, scale, Controller.getLightengine() == null);
    }
    
    /**
     * Renders the whole block at a custom position with a scale.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param color when the block has sides its sides gets shaded using this color.
     * @param staticShade i don't know what this does. This only makes it a bit brighter???
     * @param scale the scale factor of the image
     */
    public void render(final View view, final int xPos, final int yPos, Color color, final float scale, final boolean staticShade) {
        if (!isClipped() && !isHidden()) {
            if (hasSides) {
                if (!clippedTop)
                    renderSide(
                        view,
                        xPos-SCREEN_WIDTH2,
                        (int) (yPos-(SCREEN_HEIGHT2+SCREEN_DEPTH2)*(1+scale)),
                        Block.TOPSIDE,
                        color,
                        scale
                    );
                
                if (!clippedLeft) {
                    if (staticShade) {
                        color = color.add(Color.DARK_GRAY.cpy());
                        color.clamp();
                    }
                    renderSide(
                        view,
                        xPos-SCREEN_WIDTH2,
                        (int) (yPos-SCREEN_HEIGHT2*(1+scale)),
                        Block.LEFTSIDE,
                        color,
                        scale
                    );
                }

                if (!clippedRight) {
                    if (staticShade) {
                        color = color.sub(Color.DARK_GRAY.cpy());
                        color.clamp();
                    }
                    renderSide(
                        view,
                        xPos,
                        (int) (yPos-SCREEN_HEIGHT2*(1+scale)),
                        Block.RIGHTSIDE,
                        color,
                        scale
                    );
                }
            } else super.render(view, xPos, yPos-SCREEN_HEIGHT2, color, scale);
        }
    }
       
    /**
     * Render a side of a block at the position of the coordinates.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param coords the coordinates where the side is rendered 
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     * @param scale
     */
    public void renderSide(final View view, final Camera camera, final AbstractPosition coords, final int side, float scale){
        Color color;
        if (Controller.getLightengine() != null)
            color = Controller.getLightengine().getColor(side);
        else {
            color = Color.GRAY.cpy();
            
            if (WE.getCurrentConfig().shouldAutoShade()){
                if (side==0){
                    color = color.add(Color.DARK_GRAY.cpy());
                    color.clamp();
                }else if (side==2){
                    color = color.sub(Color.DARK_GRAY.cpy());
                    color.clamp();
                }
            }
        }
        
        //add fog
        if (WE.getCurrentConfig().useFog()){
            color.mul(
                (float) (0.5f+Math.exp(
                    (camera.getVisibleTopBorder()-coords.getCoord().getRelY())*0.05f+1
                ))
            );
        }
        
        renderSide(view, camera, coords, side, color,scale);
    }

    /**
     * Render a side of a block at the position of the coordinates.
     * @param view the view using this render method
     * @param camera The camera rendering the scene
     * @param coords the coordinates where to render 
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     * @param color a tint in which the sprite gets rendered
     * @param scale
     */
    public void renderSide(final View view, final Camera camera, final AbstractPosition coords, final int side, final Color color, final float scale){
        renderSide(
            view,
            coords.getProjectedPosX() - SCREEN_WIDTH2 + ( side == 2 ? (int) (SCREEN_WIDTH2*(1+scale)) : 0),//right side is  half a block more to the right,
            coords.getProjectedPosY() - SCREEN_HEIGHT - ( side == 1 ? (int) (SCREEN_DEPTH2*(1+scale)) : 0),//the top is drawn a quarter blocks higher,
            side,
            color,
            scale
        );
    }
    
    /**
     * Ignores lightlevel.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param sidenumb The number identifying the side. 0=left, 1=top, 2=right
     */
    public void renderSide(final View view, final int xPos, final int yPos, final int sidenumb){
        renderSide(view,
            xPos,
            yPos,
            sidenumb,
            Controller.getLightengine() != null ? Controller.getLightengine().getColor(sidenumb) : Color.GRAY.cpy(),
            0
        );
    }
    /**
     * Draws a side of a block at a custom position. Apllies color before rendering and takes the lightlevel into account.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param sidenumb The number identifying the side. 0=left, 1=top, 2=right
     * @param color a tint in which the sprite gets rendered
     * @param scale if you want to scale it up use scale > 0 else negative values scales down
     */
    public void renderSide(final View view, final int xPos, final int yPos, final int sidenumb, Color color, final float scale){
        Sprite sprite = new Sprite(getBlockSprite(getId(), getValue(), sidenumb));
        sprite.setPosition(xPos, yPos);
        if (scale != 0) {
            sprite.setOrigin(0, 0);
            sprite.scale(scale);
        }
        
        color.mul(getLightlevel()*2);
        
        prepareColor(view, color);
        
        sprite.getVertices()[SpriteBatch.C4] = color.toFloatBits();//top right
        
        //color.mul(getLightlevel()*2-((sidenumb == 2)?0.01f:0));
        //color.a = 1; 
        sprite.getVertices()[SpriteBatch.C1] = color.toFloatBits();//top left

        
//        if (sidenumb == 2)
//            color.mul(0.93f);
//        else if (sidenumb == 0)
//            color.mul(0.92f);
//        color.a = 1; 

        sprite.getVertices()[SpriteBatch.C2] = color.toFloatBits();//bottom left
        
//        if (sidenumb == 2)
//            color.mul(0.97f);
//        else if (sidenumb == 0) color.mul(1);
//        color.a = 1; 
        sprite.getVertices()[SpriteBatch.C3] = color.toFloatBits();//bottom right
 
        sprite.draw(view.getBatch());
        
        if (WE.getCurrentConfig().debugObjects()){
            ShapeRenderer sh = view.getIgShRender();
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.rect(xPos, yPos, sprite.getWidth(), sprite.getHeight());
            sh.end();
        }
        
        increaseDrawCalls();
    }

    @Override
    public void update(float delta) {
    }
    

    @Override
    public int getDepth(final AbstractPosition coords){
        return (int) (
            coords.getCoord().getRelY() *(Block.SCREEN_DEPTH+1)//Y
            + coords.getCoord().getCellOffset()[1]
            
            + coords.getHeight()/Math.sqrt(2)//Z
            + coords.getCoord().getCellOffset()[2]/Math.sqrt(2)
            + getDimensionZ()/Math.sqrt(2)
        );
    }

    /**
     *
     * @return
     */
    @Override
    public char getCategory() {
        return 'b';
    }

    @Override
    public String getName() {
        return NAMELIST[getId()];
    }

    /**
     *
     * @return
     */
    public static AtlasRegion[][][] getBlocksprites() {
        return blocksprites;
    }
    
    /**
     * Removes the flag that this block has sides. The default is true.
     */
    public void setNoSides(){
        hasSides=false;
    }
    
    /**
     *
     */
    public static void staticDispose(){
        blocksprites = new AtlasRegion[OBJECTTYPESCOUNT][VALUESCOUNT][3];//{id}{value}{side}
    }
}