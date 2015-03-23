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
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.View;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * A Block is a wonderful piece of information and a geometrical object.
 * @author Benedikt Vogler
 */
public class Block extends AbstractGameObject {
    private static final long serialVersionUID = 1L;
	/**
	 * {id}{value}{side}
	 */
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[OBJECTTYPESNUM][VALUESNUM][3];
	
	private static String destructionSound;
        
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] colorlist = new Color[OBJECTTYPESNUM][VALUESNUM];
	private static BlockFactory customBlockFactory;
		
	private static BlockDestructionAction destructionAction;
	
	/**
	 * Set the action performed if a block gets destroyed.
	 * @param DestructionAction an instance whose {@link BlockDestructionAction#action(AbstractPosition) }  method then gets executet.
	 * @see BlockDestructionAction
	 * @since v1.4.20
	 */
	public static void setDestructionAction(BlockDestructionAction DestructionAction) {
		Block.destructionAction = DestructionAction;
	}
	
	/**
	 * Implements the command pattern.
	 * @see #setDestructionAction(BlockDestructionAction) 
	 * @since v1.4.20
	 */
	public static interface BlockDestructionAction {
		/**
		 * the method performed if a block is destroyed
		 * @param block
		 * @since v1.4.20
		 */
		public void action(Block block);
	}
    
    private boolean liquid;
    private boolean hasSides = true;
	private Coordinate coord;
    
    /**
     * Don't use this constructor to get a new block. Use the static <i>getInstance</i> methods instead. You can extend this to implement own blocks.
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
                    block = new Block(id); //air
					block.setTransparent(true);
                    block.setObstacle(false);
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
		setPosition(coord);
		Controller.getMap().setData(this);
		return this;
	};
    
     /**
     *  Returns a sprite sprite of a specific side of the block
     * @param id the id of the block
     * @param value the value of teh block
     * @param side Which side?
     * @return an sprite of the side
     */
    public static AtlasRegion getBlockSprite(final int id, final int value, final Side side) {
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
                AtlasRegion texture = getBlockSprite(id, value, Side.TOP);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+VIEW_DEPTH2, texture.getRegionY()+VIEW_DEPTH4);
            } else {
                AtlasRegion texture = getSprite('b', id, value);
                if (texture == null) return new Color();
                colorInt = getPixmap().getPixel(
                    texture.getRegionX()+VIEW_DEPTH2, texture.getRegionY()+VIEW_DEPTH2);
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
    public void render(final GameView view, final Camera camera) {
        if (!isHidden()) {
			Coordinate coords = getPosition();
            if (hasSides) {
				boolean staticShade = CVar.get("enableAutoShade").getValueb();
                if (!camera.getClipping(coords)[1])
                    renderSide(view, camera, coords, Side.TOP, staticShade);
                if (!camera.getClipping(coords)[0])
                    renderSide(view, camera, coords, Side.LEFT, staticShade);
                if (!camera.getClipping(coords)[2])
                    renderSide(view, camera, coords, Side.RIGHT, staticShade);
            } else
                super.render(view, camera);
        }
    }
    
    /**
     * Render the whole block at a custom position. Checks if hidden.
     * @param view the view using this render method
     * @param xPos rendering position (screen)
     * @param yPos rendering position (screen)
     */
    @Override
    public void render(final GameView view, final int xPos, final int yPos) {
        if (!isHidden()) {
            if (hasSides) {
				renderSide(view, xPos, yPos+(VIEW_HEIGHT+VIEW_DEPTH), Side.TOP);
				renderSide(view, xPos, yPos, Side.LEFT);
				renderSide(view, xPos+VIEW_WIDTH2, yPos, Side.RIGHT);
			} else {
				super.render(view, xPos, yPos);
			}
        }
    }

    /**
     * Renders the whole block at a custom position.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param color when the block has sides its sides gets shaded using this color.
     * @param staticShade makes one side brighter, opposite side darker
     */
    public void render(final GameView view, final int xPos, final int yPos, Color color, final boolean staticShade) {
        if (!isHidden()) {
            if (hasSides) {
				renderSide(
					view,
					(int) (xPos-VIEW_WIDTH2*(1+getScaling())),
					(int) (yPos+VIEW_HEIGHT*(1+getScaling())),
					Side.TOP,
					color
				);

				if (staticShade) {
					if (color==null)
						color = new Color(0.75f, 0.75f, 0.75f, 1);
					else
						color = color.cpy().add(0.25f, 0.25f, 0.25f, 0);
				}
				renderSide(
					view,
					(int) (xPos-VIEW_WIDTH2*(1+getScaling())),
					yPos,
					Side.LEFT,
					color
				);

				if (staticShade) {
					color = color.cpy().sub(0.25f, 0.25f, 0.25f, 0);
				}
				renderSide(
					view,
					xPos,
					yPos,
					Side.RIGHT,
					color
				);
            } else
                super.render(view, xPos, yPos, color);
        }
    }
       
	/**
     * Render a side of a block at the position of the coordinates.
     * @param view the view using this render method
	 * @param camera
     * @param coords the coordinates where to render 
     * @param side The number identifying the side. 0=left, 1=top, 2=right
	 * @param staticShade
     */
    public void renderSide(
		final GameView view,
		final Camera camera,
		final AbstractPosition coords,
		final Side side,
		final boolean staticShade
	){
		Color color;
		if (CVar.get("enableFog").getValueb()) {
			//can use CVars for dynamic change. using harcored values for performance reasons
			float factor = (float) (Math.exp((camera.getVisibleBackBorder()-getPosition().getCoord().getY())*0.17+2));
			color = new Color(0.5f, 0.5f, 0.5f, 1).add(
				0.3f*factor,
				0.4f*factor,
				1f*factor,
				0f
			);
		} else
			color = Color.GRAY.cpy();
		
        renderSide(view,
            coords.getViewSpcX(view) - VIEW_WIDTH2 + ( side == Side.RIGHT ? (int) (VIEW_WIDTH2*(1+getScaling())) : 0),//right side is  half a block more to the right,
            coords.getViewSpcY(view) - VIEW_HEIGHT2 + ( side == Side.TOP ? (int) (VIEW_HEIGHT*(1+getScaling())) : 0),//the top is drawn a quarter blocks higher,
            side,
            staticShade ?
				side == Side.RIGHT
				? color.sub(0.25f, 0.25f, 0.25f, 0)
				: (
					side == Side.LEFT
						? color.add(0.25f, 0.25f, 0.25f, 0)
						: color
					)
				: color
        );
    }
	
	
    /**
     * Ignores lightlevel.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     */
    public void renderSide(final View view, final int xPos, final int yPos, final Side side){
		Color color;
		if (Controller.getLightEngine() != null && !Controller.getLightEngine().isShadingPixelBased()) {
			//color = Controller.getLightEngine().getColor(side);//todo reimplement vertex shading
			color = Color.GRAY.cpy();
        } else
			color = Color.GRAY.cpy();
		 
        renderSide(
			view,
            xPos,
            yPos,
            side,
            color
        );
    }
    /**
     * Draws a side of a block at a custom position. Apllies color before rendering and takes the lightlevel into account.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     * @param color a tint in which the sprite gets rendered. If null lightlevel gets ignored
     */
    public void renderSide(final View view, final int xPos, final int yPos, final Side side, Color color){
        Sprite sprite = new Sprite(getBlockSprite(getSpriteId(), getValue(), side));
        sprite.setPosition(xPos, yPos);
        if (getScaling() != 0) {
            sprite.setOrigin(0, 0);
            sprite.scale(getScaling());
        }
		//System.out.println("rend:"+xPos+","+yPos);
		if (color!=null) {
			color.r *= getLightlevel();
			color.g *= getLightlevel();
			color.b *= getLightlevel();

			sprite.setColor(color);
		}
 
        if (view.debugRendering()){
            ShapeRenderer sh = view.getShapeRenderer();
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.rect(xPos, yPos, sprite.getWidth(), sprite.getHeight());
            sh.end();
        } else {
			sprite.draw(view.getBatch());
			increaseDrawCalls();
		}
    }

	/**
	 * Update the block.
	 * @param dt time in ms since last update
	 */
    public void update(float dt) {
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
	
	public void setLiquid(boolean liquid){
		this.liquid = liquid;
	}

	/**
	 * set the sound to be played if a block gets destroyed.
	 * @param destructionSound 
	 */
	public static void setDestructionSound(String destructionSound) {
		Block.destructionSound = destructionSound;
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
	 * Overwrite to define what should happen if the block is getting destroyed? Sets the value to -1. So be carefull when to call super.onDestroy().
	 * @since v1.4
	 */
	public void onDestroy() {
		setValue(-1);
		if (destructionSound != null) Controller.getSoundEngine().play(destructionSound);
		if (destructionAction != null) destructionAction.action(this);
	}

	@Override
	public Coordinate getPosition() {
		return coord;
	}

	@Override
	public void setPosition(AbstractPosition pos) {
		coord = pos.getCoord();
	}
}