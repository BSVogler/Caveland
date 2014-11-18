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

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
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
    private static final long serialVersionUID = 1L;
	 
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[OBJECTTYPESNUM][VALUESNUM][3];//{id}{value}{side}
        
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] colorlist = new Color[OBJECTTYPESNUM][VALUESNUM];
	private final static Block airblock;
	private static BlockFactory customBlockFactory;
    
    private boolean liquid;
    private boolean hasSides = true;
    
	static {
		airblock = new Block(0);//air
		airblock.setTransparent(true);
        airblock.setHidden(true);
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
	 * If you want to define custo id's >39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(BlockFactory customBlockFactory) {
		Block.customBlockFactory = customBlockFactory;
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
        else block = getInstance(id,0);
        return block;
    }
    
    /**
     *  Create a block. If the block needs to know it's position you have to use <i>getInstance(int id, int value,int x, int y, int z)</i>
     * @param id the block's id
     * @return the wanted block.
     */
    public static Block getInstance(final int id){
        return getInstance(id,0);
    }
    
    /**
     * Create a block through this factory method. If the block needs to know it's position you have to use this method and give the coordinates.
     * @param id the id of the block
     * @param value the value of the block, which is like a sub-id
     * @return the Block
     */
    public static Block getInstance(final int id, final int value){
        Block block;
        //define the default SideSprites
        switch (id){
            case 0: 
                    block = airblock;
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
                        block = new Sea(id); //Sea
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
			case 36:
					block = new Block(id); //tree
                    block.setTransparent(true);
                    block.hasSides = false;
					block.setObstacle(true);
				break;
            default:
                if (id > 39) {
                    if (customBlockFactory!=null){
                        block = customBlockFactory.produce(id, value);
                    } else {
                        Gdx.app.error("Block", "No custom blockFactory found for "+id+". Using a default block instead.");
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
    
	public static String getName(final int id, final int value){
		switch (id) {
			case 0:
				return "air";
			case 1:
				return "grass";
			case 2:
				return "dirt";
			case 3:
				return "???";
			case 4:
				return "???";
			case 5:
				return "???";
			case 6:
				return "???";
			case 7:
				return "???";
			case 8:
				return "sand";
			case 9:
				return "water";
			case 10:
				return "???";
			case 34:
				return "flower";
			case 35:
				return "bush";
			case 36:
				return "tree";
								
			default:
				if (id > 39) {
                    if (customBlockFactory!=null){
                        return customBlockFactory.getName(id, value);
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}
		/**
	 * places the object on the map. You can extend this to get the coordinate if {@link IsSelfAware}. Block may be placed without this method call.
	 * @param coord the position on the map
	 * @return itself
	 */
	public Block spawn(Coordinate coord){
		Controller.getMap().setData(coord, this);
		return this;
	};
    
     /**
     *  Returns a sprite sprite of a specific side of the block
     * @param id the id of the block
     * @param value the value of teh block
     * @param side Which side?
     * @return an sprite of the side
     */
    public static AtlasRegion getBlockSprite(final int id, final int value, final Sides side) {
        if (getSpritesheet() == null) throw new NullPointerException("No spritesheet found.");
        
        if (blocksprites[id][value][side.getCode()] == null){ //load if not already loaded
            AtlasRegion sprite = getSpritesheet().findRegion('b'+Integer.toString(id)+"-"+value+"-"+side.getCode());
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                
                Gdx.app.debug("debug", 'b'+Integer.toString(id)+"-"+value +"-"+ side.getCode() +" not found");
                
                sprite = getSpritesheet().findRegion("b0-0-"+side.getCode());
                
                if (sprite == null) {//load generic error sprite if category sprite failed
                    sprite = getSpritesheet().findRegion("error");
                    if (sprite == null) throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
                }
            }
            blocksprites[id][value][side.getCode()] = sprite;
            return sprite;
        } else {
            return blocksprites[id][value][side.getCode()];
        }
    }
    

    
   /**
     * Returns a color representing the block. Picks from the sprite sprite.
     * @param id id of the Block
     * @param value the value of the block.
     * @return copy of a color representing the block
     */
    public static Color getRepresentingColor(final int id, final int value){
        if (colorlist[id][value] == null){ //if not in list, add it to the list
            colorlist[id][value] = new Color();
            int colorInt;
            
            if (Block.getInstance(id,value).hasSides){//if has sides, take top block    
                AtlasRegion texture = getBlockSprite(id, value, Sides.TOP);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+SCREEN_DEPTH2, texture.getRegionY()+SCREEN_DEPTH4);
            } else {
                AtlasRegion texture = getSprite('b', id, value);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+SCREEN_DEPTH2, texture.getRegionY()+SCREEN_DEPTH2);
            }
            Color.rgba8888ToColor(colorlist[id][value], colorInt);
            return colorlist[id][value].cpy(); 
        } else return colorlist[id][value].cpy(); //return value when in list
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
    
    @Override
    public void render(final View view, final Camera camera, final AbstractPosition pos) {
        if (!isHidden()) {
            float scale =0;
			Coordinate coords = pos.getCoord();
            if (CVar.get("enableScalePrototype").getValueb())  //scale if the prototype is activated
                scale = (coords.getZ()/(float) (Map.getBlocksZ()));
            if (hasSides) {
                if (!camera.getClipping(coords)[1])
                    renderSide(view, camera, coords, Sides.TOP, scale);
                if (!camera.getClipping(coords)[0])
                    renderSide(view, camera, coords, Sides.LEFT, scale);
                if (!camera.getClipping(coords)[2])
                    renderSide(view, camera, coords, Sides.RIGHT, scale);
            } else
                super.render(view, camera, coords, scale);
        }
    }
    
    /**
     * Render the whole block at a custom position. Checks if hidden.
     * @param view the view using this render method
     * @param xPos rendering position (screen)
     * @param yPos rendering position (screen)
     */
    @Override
    public void render(final View view, final int xPos, final int yPos) {
        if (!isHidden()) {
            if (hasSides) {
				renderSide(view, xPos, yPos+(SCREEN_HEIGHT+SCREEN_DEPTH), Sides.TOP);
				renderSide(view, xPos, yPos, Sides.LEFT);
				renderSide(view, xPos+SCREEN_WIDTH2, yPos, Sides.RIGHT);
			} else {
				super.render(view, xPos, yPos);
			}
        }
    }

    @Override
    public void render(final View view, final int xPos, final int yPos, final Color color, float scale) {
        render(view, xPos, yPos, color, scale, Controller.getLightEngine() == null);
    }
    
    /**
     * Renders the whole block at a custom position with a scale.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param color when the block has sides its sides gets shaded using this color.
     * @param staticShade makes one side brighter, opposite side darker
     * @param scale the scale factor of the image
     */
    public void render(final View view, final int xPos, final int yPos, Color color, final float scale, final boolean staticShade) {
        if (!isHidden()) {
            if (hasSides) {
				renderSide(
					view,
					(int) (xPos-SCREEN_WIDTH2*(1+scale)),
					(int) (yPos+SCREEN_HEIGHT*(1+scale)),
					Sides.TOP,
					color,
					scale
				);

				if (staticShade) {
					color = color.cpy().add(Color.DARK_GRAY.cpy());
				}
				renderSide(
					view,
					(int) (xPos-SCREEN_WIDTH2*(1+scale)),
					yPos,
					Sides.LEFT,
					color,
					scale
				);

				if (staticShade) {
					color = color.cpy().sub(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, 0);
				}
				renderSide(
					view,
					xPos,
					yPos,
					Sides.RIGHT,
					color,
					scale
				);
            } else
                super.render(view, xPos, yPos-SCREEN_HEIGHT2, color, scale);
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
    public void renderSide(final View view, final Camera camera, final AbstractPosition coords, final Sides side, float scale){
        Color color = Color.GRAY.cpy();
        if (CVar.get("enableAutoShade").getValueb()){
            if (side==Sides.LEFT){
                color = color.add(Color.DARK_GRAY.cpy());
            }else if (side==Sides.RIGHT){
                color = color.sub(Color.DARK_GRAY.cpy());
            }
        } else if (Controller.getLightEngine() != null){
            color = Controller.getLightEngine().getColor(side);
        }
        
        //add fog
        if (CVar.get("enableFog").getValueb()){
            color.mul(
                (float) (0.5f+Math.exp(
                    (camera.getVisibleBackBorder()-coords.getCoord().getRelY())*0.05f+1
                ))
            );
        }
        
		color.a = 1;//prevent changes because of color operations
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
    public void renderSide(final View view, final Camera camera, final AbstractPosition coords, final Sides side, final Color color, final float scale){
        renderSide(
            view,
            coords.getProjectedPosX(view) - SCREEN_WIDTH2 + ( side == Sides.RIGHT ? (int) (SCREEN_WIDTH2*(1+scale)) : 0),//right side is  half a block more to the right,
            coords.getProjectedPosY(view) - SCREEN_HEIGHT + ( side == Sides.TOP ? (int) (SCREEN_HEIGHT*(1+scale)) : 0),//the top is drawn a quarter blocks higher,
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
    public void renderSide(final View view, final int xPos, final int yPos, final Sides sidenumb){
        renderSide(view,
            xPos,
            yPos,
            sidenumb,
            Controller.getLightEngine() != null ? Controller.getLightEngine().getColor(sidenumb) : Color.GRAY.cpy(),
            0
        );
    }
    /**
     * Draws a side of a block at a custom position. Apllies color before rendering and takes the lightlevel into account.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     * @param color a tint in which the sprite gets rendered
     * @param scale if you want to scale it up use scale > 0 else negative values scales down
     */
    public void renderSide(final View view, final int xPos, final int yPos, final Sides side, Color color, final float scale){
        Sprite sprite = new Sprite(getBlockSprite(getSpriteId(), getValue(), side));
        sprite.setPosition(xPos, yPos);
        if (scale != 0) {
            sprite.setOrigin(0, 0);
            sprite.scale(scale);
        }
        
        color.mul(getLightlevel(), getLightlevel(), getLightlevel(), 1);//darken

        sprite.getVertices()[SpriteBatch.C4] = color.toFloatBits();//top right
        
        //color.mul(getLightlevel()*2-((side == 2)?0.01f:0));
        //color.a = 1; 
        sprite.getVertices()[SpriteBatch.C1] = color.toFloatBits();//top left

        
//        if (side == 2)
//            color.mul(0.93f);
//        else if (side == 0)
//            color.mul(0.92f);
//        color.a = 1; 

        sprite.getVertices()[SpriteBatch.C2] = color.toFloatBits();//bottom left
        
//        if (side == 2)
//            color.mul(0.97f);
//        else if (side == 0) color.mul(1);
//        color.a = 1; 
        sprite.getVertices()[SpriteBatch.C3] = color.toFloatBits();//bottom right
 
        sprite.draw(view.getBatch());
        
        if (CVar.get("debugObjects").getValueb()){
            ShapeRenderer sh = view.getShapeRenderer();
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.rect(xPos, yPos, sprite.getWidth(), sprite.getHeight());
            sh.end();
        }
        
        increaseDrawCalls();
    }

	/**
	 * Update the block.
	 * @param dt time in ms since last update
	 * @param x relative pos
	 * @param y relative pos
	 * @param z relative pos
	 */
    public void update(float dt, int x, int y, int z) {
    }
    


    /**
     *
     * @return
     */
    @Override
    public char getCategory() {
        return 'b';
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
        blocksprites = new AtlasRegion[OBJECTTYPESNUM][VALUESNUM][3];//{id}{value}{side}
    }

    @Override
    public String getName() {
        return getName(getId(), getValue());
    }
	
	@Override
	public int getDimensionZ() {
		return 1;
	}

	/**
	 * Overwrite to define what should happen if the block is getting destroyed?
	 * @param pos the position of the block, can be null if not needed
	 */
	public void onDestroy(AbstractPosition pos) {
	}
}