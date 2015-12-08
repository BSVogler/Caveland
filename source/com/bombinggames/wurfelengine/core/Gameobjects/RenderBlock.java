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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_DEPTH;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_DEPTH2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_DEPTH4;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_HEIGHT;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_HEIGHT2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_WIDTH2;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 * It is something which can be rendered and therefore render information saved. A RenderBlock should not be shared across cameras nor should use the event system. The class extends (wraps) the plain data of the {@link Block} with a position and {@link AbstractGameObject} class methods. The {@link Block} is shared, so changing this {@link RenderBlock} changes the data in the map.<br>
 * The internal block can have different id then used for rendering. The rendering sprite id's are set in the constructor or later manualy.<br>
 * @see Block
 * @author Benedikt Vogler
 */
public class RenderBlock extends AbstractGameObject{
    private static final long serialVersionUID = 1L;
	/**
	 * {id}{value}{side}
	 */
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[Block.OBJECTTYPESNUM][Block.VALUESNUM][3];
	
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] COLORLIST = new Color[Block.OBJECTTYPESNUM][Block.VALUESNUM];
	private static boolean fogEnabled;
	private static boolean staticShade;
	/**
	 * the brightness of the ao
	 */
	private static float ambientOcclusion;

	/**
	 * Indicate whether the blocks should get shaded independent of the light engine by default.
	 * @param shade 
	 */
	public static void setStaticShade(boolean shade){
		staticShade = shade;
	}
	
	/**
	 * Returns a sprite sprite of a specific side of the block
	 *
	 * @param id the id of the block
	 * @param value the value of teh block
	 * @param side Which side?
	 * @return an sprite of the side
	 */
	public static AtlasRegion getBlockSprite(final byte id, final byte value, final Side side) {
		if (getSpritesheet() == null) {
			throw new NullPointerException("No spritesheet found.");
		}

		if (blocksprites[id][value][side.getCode()] == null) { //load if not already loaded
			AtlasRegion sprite = getSpritesheet().findRegion('b' + Byte.toString(id) + "-" + value + "-" + side.getCode());
			if (sprite == null) { //if there is no sprite show the default "sprite not found sprite" for this category
				Gdx.app.debug("debug", 'b' + Byte.toString(id) + "-" + value + "-" + side.getCode() + " not found");

				sprite = getSpritesheet().findRegion("b0-0-" + side.getCode());

				if (sprite == null) {//load generic error sprite if category sprite failed
					sprite = getSpritesheet().findRegion("error");
					if (sprite == null) {
						throw new NullPointerException("Sprite and category error not found and even the generic error sprite could not be found. Something with the sprites is fucked up.");
					}
				}
			}
			blocksprites[id][value][side.getCode()] = sprite;
			return sprite;
		} else {
			return blocksprites[id][value][side.getCode()];
		}
	}
	
	/**
	 * checks if a sprite is defined. if not the error sprite will be rendered
	 * @param block
	 * @return 
	 */
	public static boolean isSpriteDefined(final RenderBlock block){
		if (block == null) return false;
		if (getSpritesheet() == null) return false;
		AtlasRegion sprite;
		if (block.hasSides())
			sprite = getSpritesheet().findRegion('b'+Byte.toString(block.getSpriteId())+"-"+block.getSpriteValue()+"-0");
		else
			sprite = getSpritesheet().findRegion('b'+Byte.toString(block.getSpriteId())+"-"+block.getSpriteValue());
		return 	sprite != null;
	}

    
   /**
     * Returns a color representing the block. Picks from the sprite sprite.
     * @param id id of the RenderBlock
     * @param value the value of the block.
     * @return copy of a color representing the block
     */
    public static Color getRepresentingColor(final byte id, final byte value){
        if (COLORLIST[id][value] == null){ //if not in list, add it to the list
            COLORLIST[id][value] = new Color();
            int colorInt;
            
            if (Block.getInstance(id, value).hasSides()){//if has sides, take top block    
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
            Color.rgba8888ToColor(COLORLIST[id][value], colorInt);
            return COLORLIST[id][value].cpy(); 
        } else return COLORLIST[id][value].cpy(); //return value when in list
    }
	
	/**
     *
     * @return
     */
    public static AtlasRegion[][][] getBlocksprites() {
        return blocksprites;
    }
	
    /**
     *dipsose the static fields
     */
    public static void staticDispose(){
        blocksprites = new AtlasRegion[Block.OBJECTTYPESNUM][Block.VALUESNUM][3];//{id}{value}{side}
    }
	
	
	private final Block blockData;
	private Coordinate coord;
	
	/**
	 * 
	 * @param id 
	 */
    public RenderBlock(byte id){
        super(id);
		blockData = Block.getInstance(id);
		fogEnabled = WE.getCvars().getValueB("enableFog");//refresh cache
		ambientOcclusion = WE.getCvars().getValueF("ambientOcclusion");
    }
	
	/**
	 * 
	 * @param id
	 * @param value 
	 */
	public RenderBlock(byte id, byte value){
		super(id, value);
		blockData = Block.getInstance(id, value);
		fogEnabled = WE.getCvars().getValueB("enableFog");//refresh cache
		ambientOcclusion = WE.getCvars().getValueF("ambientOcclusion");
	}
	
	/**
	 * Create a new render block referencing to an existing coreData object.
	 * @param data 
	 */
	public RenderBlock(Block data){
		super(data.getId(), data.getValue());//copy id's from data for rendering
		blockData = data;
		fogEnabled = WE.getCvars().getValueB("enableFog");//refresh cache
		ambientOcclusion = WE.getCvars().getValueF("ambientOcclusion");
	}
	
	
	@Override
	public boolean isObstacle() {
		return blockData.isObstacle();
	}

    @Override
    public String getName() {
        return  blockData.getName();
    }
	
	/**
	 * places the object on the map. You can extend this to get the coordinate. RenderBlock may be placed without this method call. A regular renderblock is not spawned expect explicitely called.
	 * @param coord the position on the map
	 * @return itself
	 * @see #setPosition(com.bombinggames.wurfelengine.core.Map.AbstractPosition) 
	 */
	public RenderBlock spawn(Coordinate coord){
		setPosition(coord);
		Controller.getMap().setBlock(this);
		return this;
	};
    
    @Override
    public void render(final GameView view, final Camera camera) {
        if (!isHidden()) {
            if (hasSides()) {
				Coordinate coords = getPosition();
				byte clipping = blockData.getClipping();
                if ((clipping & (1 << 1)) == 0)
                    renderSide(view, camera, coords, Side.TOP, staticShade);
				if ((clipping & 1) == 0)
                    renderSide(view, camera, coords, Side.LEFT, staticShade);
                if ((clipping & (1 << 2)) == 0)
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
            if (hasSides()) {
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
     * @param xPos rendering position of the center
     * @param yPos rendering position of the center
     * @param color when the block has sides its sides gets shaded using this color.
     * @param staticShade makes one side brighter, opposite side darker
     */
    public void render(final GameView view, final int xPos, final int yPos, Color color, final boolean staticShade) {
        if (!isHidden()) {
            if (hasSides()) {
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
                super.render(view, xPos, yPos+VIEW_DEPTH4, color);
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
		if (fogEnabled) {
			//can use CVars for dynamic change. using harcored values for performance reasons
			float factor = (float) (Math.exp(0.025f * (camera.getVisibleFrontBorderHigh() - coords.toCoord().getY() - 18.0)) - 1);
			//float factor = (float) (Math.exp( 0.0005f*(coords.getDepth(view)-500) )-1 );
			color = new Color(0.5f + 0.3f * factor, 0.5f + 0.4f * factor, 0.5f + 1f * factor, 1);
		} else {
			color = Color.GRAY.cpy();
		}

		//if vertex shaded then use different shading for each side
		if (Controller.getLightEngine() != null && !Controller.getLightEngine().isShadingPixelBased()) {
			color = Controller.getLightEngine().getColor(side).mul(color.r + 0.5f, color.g + 0.5f, color.b + 0.5f, color.a + 0.5f);
		}
		
        renderSide(
			view,
            coords.getViewSpcX() - VIEW_WIDTH2 + ( side == Side.RIGHT ? (int) (VIEW_WIDTH2*(1+getScaling())) : 0),//right side is  half a block more to the right,
            coords.getViewSpcY() - VIEW_HEIGHT2 + ( side == Side.TOP ? (int) (VIEW_HEIGHT*(1+getScaling())) : 0),//the top is drawn a quarter blocks higher,
            side,
            staticShade ?
				side == Side.RIGHT
				? color.sub(0.25f, 0.25f, 0.25f, 0)
				: (
					side == Side.LEFT
						? color.add(0.25f, 0.25f, 0.25f, 0)
						: color
					)
				: color//pass color if not shading static
        );
		
		if (getBlockData().getHealth() <= 50) {
			if (null != side) //render damage
			switch (side) {
				case LEFT:
					renderDO(view, camera, getPosition().toPoint().add(-Block.GAME_DIAGLENGTH2/2, 0, 0), (byte) 3);
					break;
				case TOP:
					renderDO(view, camera, getPosition().toPoint().add(0, 0, Block.GAME_EDGELENGTH), (byte) 4);
					break;
				case RIGHT:
					renderDO(view, camera, getPosition().toPoint().add(Block.GAME_DIAGLENGTH2/2, 0, 0), (byte) 5);
					break;
				default:
					break;
			}
		}
				
		
		//render ambient occlusion
		if (ambientOcclusion > 0) {
			int aoFlags = getBlockData().getAOFlags();
			if (side == Side.LEFT && ((byte) (aoFlags)) != 0) {//only if top side and there is ambient occlusion
				Coordinate aopos = getPosition();
				if ((aoFlags & (1 << 2)) != 0) {//if right
					renderAO(view, camera, aopos, (byte) 11);
				}
				if ((aoFlags & (1 << 4)) != 0) {//if bottom
					renderAO(view, camera, aopos, (byte) 10);
				} else {
					if ((aoFlags & (1 << 3)) != 0) {//if bottom right
						renderAO(view, camera, aopos, (byte) 13);
					}
					if ((aoFlags & (1 << 5)) != 0) {//if bottom left
						renderAO(view, camera, aopos, (byte) 16);
					}
				}
				if ((aoFlags & (1 << 6)) != 0) {//if left
					renderAO(view, camera, aopos, (byte) 9);
				}
			}

			if (side == Side.TOP && ((byte) (aoFlags >> 8)) != 0) {//only if top side and there is ambient occlusion
				Coordinate aopos = getPosition().addVector(0, 0, 1);//move one up
				if ((aoFlags & (1 << 9)) != 0) {//if back right
					renderAO(view, camera, aopos, (byte) 0);
				}

				if ((aoFlags & (1 << 11)) != 0) {//if front right
					renderAO(view, camera, aopos, (byte) 4);
				} else if ((aoFlags & (1 << 10)) != 0) {//if right
					renderAO(view, camera, aopos, (byte) 3);
				}
				
				//12 is never visible
				
				if ((aoFlags & (1 << 13)) != 0) {//if front left
					renderAO(view, camera, aopos, (byte) 5);
				} else if ((aoFlags & (1 << 14)) != 0) {//if left
					renderAO(view, camera, aopos, (byte) 6);
				}
				if ((aoFlags & (1 << 15)) != 0) {//if back left
					renderAO(view, camera, aopos, (byte) 1);
				} else if ((aoFlags & 1 << 8) != 0) {//if back
					renderAO(view, camera, aopos, (byte) 2);
				}
				aopos.addVector(0, 0, -1); //move down again
			}

			if (side == Side.RIGHT && ((byte) (aoFlags >> 16)) != 0) {//only if top side and there is ambient occlusion
				Coordinate aopos = getPosition();
				if ((aoFlags & (1 << 18)) != 0) {//if right
					renderAO(view, camera, aopos, (byte) 7);
				}

				if ((aoFlags & (1 << 20)) != 0) {//if bottom
					renderAO(view, camera, aopos, (byte) 8);
				} else {
					if ((aoFlags & (1 << 19)) != 0) {//if bottom right
						renderAO(view, camera, aopos, (byte) 15);
					}
					if ((aoFlags & (1 << 21)) != 0) {//if bottom left
						renderAO(view, camera, aopos, (byte) 14);
					}
				}

				if ((aoFlags & (1 << 22)) != 0) {//if left
					renderAO(view, camera, aopos, (byte) 12);
				}
			}
		}
    }
	
	/**
	 * helper function
	 * @param view
	 * @param camera
	 * @param aopos does not alter the field
	 * @param value 
	 */
	private void renderAO(final GameView view, final Camera camera, final AbstractPosition aopos, final byte value){
		SimpleEntity ao = new SimpleEntity((byte) 2, value);
		ao.setPosition(aopos);
		ao.setColor(new Color(0.5f, 0.5f, 0.5f, ambientOcclusion));
		ao.render(view, camera);
	}
	
	/**
	 * helper function
	 * @param view
	 * @param camera
	 * @param aopos
	 * @param value 
	 */
	private void renderDO(final GameView view, final Camera camera, final AbstractPosition aopos, final byte value){
		SimpleEntity destruct = new SimpleEntity((byte) 3,value);
		destruct.setPosition(aopos);
		destruct.setColor(new Color(0.5f, 0.5f, 0.5f, 0.7f));
		destruct.render(view, camera);
	}
	
	
	
    /**
     * Ignores lightlevel.
     * @param view the view using this render method
     * @param xPos rendering position
     * @param yPos rendering position
     * @param side The number identifying the side. 0=left, 1=top, 2=right
     */
    public void renderSide(final GameView view, final int xPos, final int yPos, final Side side){
		Color color;
		if (Controller.getLightEngine() != null && !Controller.getLightEngine().isShadingPixelBased()) {
			color = Controller.getLightEngine().getColor(side);
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
     * @param color a tint in which the sprite gets rendered. If null color gets ignored
     */
    public void renderSide(final GameView view, final int xPos, final int yPos, final Side side, Color color){
		byte id = getSpriteId();
		if (id <= 0) return;
		byte value = getSpriteValue();
		if (value < 0) return;
        Sprite sprite = new Sprite(getBlockSprite(id, value, side));
        sprite.setPosition(xPos, yPos);
        if (getScaling() != 0) {
            sprite.setOrigin(0, 0);
            sprite.scale(getScaling());
        }
		
		if (color != null) {
			color.r *= blockData.getLightlevelR(side);
			if (color.r>1) color.r=1;
			color.g *= blockData.getLightlevelG(side);
			if (color.g>1) color.g=1;
			color.b *= blockData.getLightlevelB(side);
			if (color.b>1) color.b=1;
			
			sprite.setColor(color);
		}
 
		//draw only outline or regularly?
        if (view.debugRendering()){
            ShapeRenderer sh = view.getShapeRenderer();
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.rect(xPos, yPos, sprite.getWidth(), sprite.getHeight());
            sh.end();
        } else {
			sprite.draw(view.getSpriteBatch());
			increaseDrawCalls();
		}
    }

	/**
	 * Update the block. Should only be used for cosmetic logic because this is only called for blocks which are covered by a camera.
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
	@Override
	public int getDimensionZ() {
		return Block.GAME_EDGELENGTH;
	}

	@Override
	public Coordinate getPosition() {
		return coord;
	}

	@Override
	public void setPosition(AbstractPosition pos) {
		coord = pos.toCoord();
	}
	
	/**
	 * keeps reference
	 * @param coord 
	 */
	public void setPosition(Coordinate coord){
		this.coord = coord;
	}	
	
	/**
	 * gets the identifier and stores them in the map
	 * @return 
	 */
	public Block toStorageBlock(){
		return Block.getInstance(getSpriteId(), getSpriteValue());
	}
	
	/**
	 * hides the block after it?
	 * @return 
	 */
	public boolean hidingPastBlock(){
		return blockData.hasSides() && !blockData.isTransparent();
	}

	@Override
	public boolean isTransparent() {
		return blockData.isTransparent();
	}
	
	@Override
	public boolean isIndestructible() {
		return blockData.isIndestructible();
	}

	@Override
	public boolean hasSides() {
		if (blockData == null) {
			return false;
		}
		return blockData.hasSides();
	}

	@Override
	public float getLightlevelR() {
		return blockData.getLightlevelR();
	}
	
	@Override
	public float getLightlevelG() {
		return blockData.getLightlevelG();
	}
	
	@Override
	public float getLightlevelB() {
		return blockData.getLightlevelB();
	}

	@Override
	public void setLightlevel(float lightlevel) {
		blockData.setLightlevel(lightlevel);
	}

	/**
	 * 
	 * @param lightlevel 1 default
	 * @param side 
	 */
	public void setLightlevel(float lightlevel, Side side) {
		blockData.setLightlevel(lightlevel, side);
	}

	@Override
	public void setSpriteValue(byte value) {
		super.setSpriteValue(value);
		blockData.setSpriteValue(value);
	}

	@Override
	public boolean isLiquid() {
		if (blockData==null) return false;
		return blockData.isLiquid();
	}
	
	/**
	 * Get the pointer to the data.
	 * @return
	 */
	public Block getBlockData() {
		return blockData;
	}
}