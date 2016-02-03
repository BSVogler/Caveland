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
package com.bombinggames.wurfelengine.core.map.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_DEPTH;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_DEPTH2;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_DEPTH4;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_HEIGHT;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_HEIGHT2;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.VIEW_WIDTH2;
import com.bombinggames.wurfelengine.core.gameobjects.Renderable;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Position;
import java.util.ArrayList;

/**
 * It is something which can be rendered and therefore render information saved shared across cameras. A RenderBlock should not use the event system. The class extends (wraps) the plain data of the {@link Block} with a position and {@link AbstractGameObject} class methods. The wrapped {@link Block} is shared, so changing this {@link RenderBlock} changes the data in the map.<br>
 * The internal wrapped block can have different id then used for rendering. The rendering sprite id's are set in the constructor or later manualy.<br>
 * @see Block
 * @author Benedikt Vogler
 */
public class RenderBlock extends AbstractGameObject{
    private static final long serialVersionUID = 1L;
	/**
	 * indexed acces to spritesheet {id}{value}{side}
	 */
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[Block.OBJECTTYPESNUM][Block.VALUESNUM][3];
	
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] COLORLIST = new Color[Block.OBJECTTYPESNUM][Block.VALUESNUM];
	private static boolean fogEnabled;
	private static boolean staticShade;
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

		if (blocksprites[id][value][side.getCode()] != null) { //load if not already loaded
			return blocksprites[id][value][side.getCode()];
		} else {
			AtlasRegion sprite = getSpritesheet().findRegion('b' + Byte.toString(id) + "-" + value + "-" + side.getCode());
			if (sprite == null) {
				Gdx.app.debug("debug", 'b' + Byte.toString(id) + "-" + value + "-" + side.getCode() + " not found");
				//if there is no sprite show the default "sprite not found sprite" for this category
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
     * dipsose the static fields
     */
    public static void staticDispose(){
		//clear index
		for (AtlasRegion[][] blocksprite : blocksprites) {
			for (AtlasRegion[] atlasRegions : blocksprite) {
				for (int i = 0; i < atlasRegions.length; i++) {
					atlasRegions[i] = null;
				}
			}
		}
    }
	
	
	private final Block blockData;
	private Coordinate coord;
	//view data
	/**
	 * each side has RGB color stored as 10bit float. Obtained by dividing bits
	 * by fraction /2^10-1 = 1023.
	 * each field is vertex 0-3
	 */
	private final int[] colorLeft = new int[]{
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55
	};
	private final int[] colorTop = new int[]{
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55
	};
	private final int[] colorRight = new int[]{
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55,
		(55 << 16) + (55 << 8) + 55
	};
	/**
	 * byte 0: left side, byte 1: top side, byte 2: right side.<br>In each byte
	 * bit order: <br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | - | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 */
	private int aoFlags;
	/**
	 * three bits used, for each side one: TODO: move to aoFlags byte 3
	 */
	private byte clipping;
	private final ArrayList<Renderable> coveredBlocks = new ArrayList<>(7);
	private ArrayList<Renderable> allcovered;
	
	/**
	 * Does not wrap a {@link Block} instance.
	 */
	public RenderBlock(){
		super((byte) 0);
		blockData = null;
	}
	
	/**
	 * Does not wrap a {@link Block} instance.
	 * @param id 
	 * @see #RenderBlock(com.bombinggames.wurfelengine.core.Gameobjects.Block)  
	 */
    public RenderBlock(byte id){
        super(id);
		blockData = Block.getInstance(id);
		fogEnabled = WE.getCVars().getValueB("enableFog");//refresh cache
	}
	
	/**
	 * Does not wrap a {@link Block} instance.
	 * @param id
	 * @param value 
	 * @see #RenderBlock(com.bombinggames.wurfelengine.core.Gameobjects.Block)  
	 */
	public RenderBlock(byte id, byte value){
		super(id, value);
		blockData = Block.getInstance(id, value);
		fogEnabled = WE.getCVars().getValueB("enableFog");//refresh cache
	}
	
	/**
	 * Create a new render block referencing to an existing {@link Block} object.
	 * @param data 
	 */
	public RenderBlock(Block data){
		super(data.getId(), data.getValue());//copy id's from data for rendering
		blockData = data;
		fogEnabled = WE.getCVars().getValueB("enableFog");//refresh cache
	}

	public boolean isObstacle() {
		return blockData.isObstacle();
	}

    @Override
    public String getName() {
        return blockData.getName();
    }
	
	/**
	 * places the object on the map. You can extend this to get the coordinate. RenderBlock may be placed without this method call. A regular renderblock is not spawned expect explicitely called.
	 * @param rS
	 * @param coord the position on the map
	 * @return itself
	 * @see #setPosition(com.bombinggames.wurfelengine.core.map.AbstractPosition) 
	 */
	public RenderBlock spawn(RenderStorage rS, Coordinate coord){
		setPosition(rS, coord);
		Controller.getMap().setBlock(this);
		return this;
	};
    
    @Override
    public void render(final GameView view, final Camera camera) {
        if (!isHidden()) {
            if (hasSides()) {
				Coordinate coords = getPosition();
				byte clipping = getClipping();
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
					color,
					0
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
					color,
					0
				);

				if (staticShade) {
					color = color.cpy().sub(0.25f, 0.25f, 0.25f, 0);
				}
				renderSide(
					view,
					xPos,
					yPos,
					Side.RIGHT,
					color,
					0
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
		final Position coords,
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
			color = Controller.getLightEngine().getColor(side, getPosition()).mul(color.r + 0.5f, color.g + 0.5f, color.b + 0.5f, color.a + 0.5f);
		}
		
		Block blockdata = getBlockData();
		
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
				: color,//pass color if not shading static
			getAOFlags()
        );
		
		if (blockdata.getHealth() < 100) {
			int damageOverlayStep = 0;
			if (blockdata.getHealth() <= 50) {
				damageOverlayStep = 1;
			}
			if (blockdata.getHealth() <= 25) {
				damageOverlayStep = 2;
			}
			
			if (damageOverlayStep > -1) {
				//render damage
				switch (side) {
					case LEFT:
						renderDamageOverlay(
							view,
							camera,
							getPosition().toPoint().add(-Block.GAME_DIAGLENGTH2 / 2, 0, 0),
							(byte) (3 * damageOverlayStep)
						);
						break;
					case TOP:
						renderDamageOverlay(
							view,
							camera,
							getPosition().toPoint().add(0, 0, Block.GAME_EDGELENGTH),
							(byte) (3 * damageOverlayStep + 1)
						);
						break;
					case RIGHT:
						renderDamageOverlay(
							view,
							camera,
							getPosition().toPoint().add(Block.GAME_DIAGLENGTH2 / 2, 0, 0),
							(byte) (3 * damageOverlayStep + 2)
						);
						break;
					default:
						break;
				}
			}
		}
    }
	
	/**
	 * helper function
	 * @param view
	 * @param camera
	 * @param aopos
	 * @param value damage sprite value
	 */
	private void renderDamageOverlay(final GameView view, final Camera camera, final Position aopos, final byte value){
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
			color = Controller.getLightEngine().getColor(side, getPosition());
        } else
			color = Color.GRAY.cpy();
		 
        renderSide(
			view,
            xPos,
            yPos,
            side,
            color,
			0
        );
    }
  /**
	 * Draws a side of a block at a custom position. Apllies color before
	 * rendering and takes the lightlevel into account.
	 *
	 * @param view the view using this render method
	 * @param xPos rendering position
	 * @param yPos rendering position
	 * @param side The number identifying the side. 0=left, 1=top, 2=right
	 * @param color a tint in which the sprite gets rendered. If null color gets
	 * ignored
	 * @param ao ambient occlusion flags. if no ao pass 0
	 */
	public void renderSide(final GameView view, final int xPos, final int yPos, final Side side, Color color, int ao) {
		byte id = getSpriteId();
		if (id <= 0) {
			return;
		}
		byte value = getSpriteValue();
		if (value < 0) {
			return;
		}

		SideSprite sprite = new SideSprite(getBlockSprite(id, value, side), side, ao);
		sprite.setPosition(xPos, yPos);
		if (getScaling() != 0) {
			sprite.setOrigin(0, 0);
			sprite.scale(getScaling());
		}

		//draw only outline or regularly?
        if (view.debugRendering()){
            ShapeRenderer sh = view.getShapeRenderer();
            sh.begin(ShapeRenderer.ShapeType.Line);
            sh.rect(xPos, yPos, sprite.getWidth(), sprite.getHeight());
            sh.end();
        } else {
			//if (color != null) {
	//			color.r *= getLightlevelR(side);
	//			if (color.r > 1) {//values above 1 can not be casted later
	//				color.r = 1;
	//			}
	//			color.g *= getLightlevelG(side);
	//			if (color.g > 1) {//values above 1 can not be casted later
	//				color.g = 1;
	//			}
	//			color.b *= getLightlevelB(side);
	//			if (color.b > 1) {//values above 1 can not be casted later
	//				color.b = 1;
	//			}
			sprite.setColor(
				getLightlevel(side, 0, 0) / 2f,
				getLightlevel(side, 0, 1) / 2f,
				getLightlevel(side, 0, 2) / 2f,
				getLightlevel(side, 1, 0) / 2f,
				getLightlevel(side, 1, 1) / 2f,
				getLightlevel(side, 1, 2) / 2f,
				getLightlevel(side, 2, 0) / 2f,
				getLightlevel(side, 2, 1) / 2f,
				getLightlevel(side, 2, 2) / 2f,
				getLightlevel(side, 3, 0) / 2f,
				getLightlevel(side, 3, 1) / 2f,
				getLightlevel(side, 3, 2) / 2f
			);
			//}
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
    
    @Override
    public char getCategory() {
        return 'b';
    }

	@Override
	public int getDimensionZ() {
		return Block.GAME_EDGELENGTH;
	}

	@Override
	public final Coordinate getPosition() {
		return coord;
	}

	@Override
	public void setPosition(Position pos) {
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
	 * keeps reference
	 * @param rS
	 * @param coord 
	 */
	public void setPosition(RenderStorage rS, Coordinate coord){
		this.coord = coord;
		fillCoveredList(rS);
	}	
	
	/**
	 * gets the identifier and stores them in the map
	 * @return 
	 */
	public Block toStorageBlock(){
		return Block.getInstance(getSpriteId(), getSpriteValue());
	}
	
	/**
	 * Can light travel through object?
	 * @return
	 */
	public boolean isTransparent() {
		if (blockData==null) return true;
		return blockData.isTransparent();
	}
	
	public boolean isIndestructible() {
		return blockData.isIndestructible();
	}
	
	/**
	 * Is the block a true block with three sides or does it get rendered by a
	 * single sprite?<br>
	 * This field is only used for representation (view) related data.<br>
	 * Only used for blocks. Entities should return <i>false</i>.
	 *
	 * @return <i>true</i> if it has sides, <i>false</i> if is rendered as a
	 * single sprite
	 */
	public boolean hasSides() {
		if (blockData == null) {
			return false;
		}
		return blockData.hasSides();
	}

	public boolean isLiquid() {
		if (blockData == null) {
			return false;
		}
		return blockData.isLiquid();
	}

	/**
	 * Get the pointer to the data.
	 *
	 * @return
	 */
	public Block getBlockData() {
		return blockData;
	}
	
	@Override
	public float getLightlevelR() {
		return (getLightlevel(Side.LEFT, 0,0) + getLightlevel(Side.TOP, 0,0) + getLightlevel(Side.RIGHT, 0,0)) / 3f;
	}

	@Override
	public float getLightlevelG() {
		return (getLightlevel(Side.LEFT, 0,1) + getLightlevel(Side.TOP, 0,1) + getLightlevel(Side.RIGHT, 0,1)) / 3f;
	}

	@Override
	public float getLightlevelB() {
		return (getLightlevel(Side.LEFT, 0, 2) + getLightlevel(Side.TOP, 0,2) + getLightlevel(Side.RIGHT, 0,2)) / 3f;
	}

	/**
	 *
	 * @param side
	 * @param vert
	 * @param channel
	 * @return range 0-2.
	 */
	public float getLightlevel(Side side, int vert, int channel) {
		byte colorBitShift = (byte) (20 - 10 * channel);
		if (side == Side.LEFT) {
			return ((colorLeft[vert]  >> colorBitShift) & 0x3FF) / 511f;
		} else if (side == Side.TOP) {
			return ((colorTop[vert]  >> colorBitShift) & 0x3FF) / 511f;
		}
		return ((colorRight[vert]  >> colorBitShift) & 0x3FF) / 511f;
	}

	/**
	 * Stores the lightlevel overriding each side
	 *
	 * @param lightlevel range 0 -2
	 */
	@Override
	public void setLightlevel(float lightlevel) {
		int color;
		if (lightlevel <= 0) {
			color = 0;
		} else {
			int l = (int) (lightlevel * 512);
			//clamp
			if (l > 1023) {
				l = 1023;
			}
			color = (l << 20) + (l << 10) + l;
		}
		for (int i = 0; i < colorLeft.length; i++) {
			colorLeft[i] = color;//512 base 10 for each color channel
		}
		for (int i = 0; i < colorTop.length; i++) {
			colorTop[i] = color;//512 base 10 for each color channel
		}
		for (int i = 0; i < colorRight.length; i++) {
			colorRight[i] = color;//512 base 10 for each color channel
		}
	}
	
	/**
	 * sets the light to 1
	 */
	public void resetLight(){
		for (int i = 0; i < colorLeft.length; i++) {
			colorLeft[i] = 537395712;//512 base 10 for each color channel
		}
		for (int i = 0; i < colorTop.length; i++) {
			colorTop[i] = 537395712;//512 base 10 for each color channel
		}
		for (int i = 0; i < colorRight.length; i++) {
			colorRight[i] = 537395712;//512 base 10 for each color channel
		}
	}

	/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 * @param vertex
	 */
	public void setLightlevel(float lightlevel, Side side, int vertex) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}
		int l = (int) (lightlevel * 512);
		if (l > 1023) {
			l = 1023;
		}

		switch (side) {
			case LEFT:
				colorLeft[vertex] = (l << 20) + (l << 10) + l;//RGB;
				break;
			case TOP:
				colorTop[vertex] = (l << 20) + (l << 10) + l;//RGB;
				break;
			default:
				colorRight[vertex] = (l << 20) + (l << 10) + l;//RGB
				break;
		}
	}
	
		/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 * @param channel r g oder b,
	 * @param vertex
	 */
	public void setLightlevel(float lightlevel, Side side, int channel, int vertex) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}
		
		byte colorBitShift = (byte) (20 - 10 * channel);
		
		int l = (int) (lightlevel * 512);
		if (l > 1023) {
			l = 1023;
		}
		
		switch (side) {
			case LEFT:
				colorLeft[vertex] |= (l << colorBitShift);
				break;
			case TOP:
				colorTop[vertex] |= (l << colorBitShift);
				break;
			default:
				colorRight[vertex] |= (l << colorBitShift);
				break;
		}
	}
	
	/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 * @param channel 0 = R, 1 =G, 2=B
	 * @param vertex
	 */
	public void addLightlevel(float lightlevel, Side side, int channel, int vertex) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}

		byte colorBitShift = (byte) (20 - 10 * channel);

		float l = lightlevel * 512;
		if (l > 1023) {
			l = 1023;
		}

		switch (side) {
			case LEFT: {
				int newl = (int) (((colorLeft[vertex] >> colorBitShift) & 0x3FF) / 511f + l);
				if (newl > 1023) {
					newl = 1023;
				}
				colorLeft[vertex] |= (newl << colorBitShift);
				break;
			}
			case TOP: {
				int newl = (int) (((colorTop[vertex] >> colorBitShift) & 0x3FF) / 511f + l);
				if (newl > 1023) {
					newl = 1023;
				}
				colorTop[vertex] |= (newl << colorBitShift);
				break;
			}
			default: {
				int newl = (int) (((colorRight[vertex] >> colorBitShift) & 0x3FF) / 511f + l);
				if (newl > 1023) {
					newl = 1023;
				}
				colorRight[vertex] |= (newl << colorBitShift);
				break;
			}
		}
	}
	
	/**
	 * 
	 * @return true if it hides the block behind and below
	 */
	public boolean hidingPastBlock() {
		return hasSides() && !isTransparent();
	}

	/**
	 * Set flags for the ambient occlusion algorithm to true
	 * @param side
	 */
	public void setAOFlagTrue(int side) {
		this.aoFlags |= 1 << side;//set n'th bit to true via OR operator
	}

	/**
	 * Set flags for the ambient occlusion algorithm to false
	 * @param side 
	 */
	public void setAOFlagFalse(int side) {
		this.aoFlags &= ~(1 << side);//set n'th bit to false via AND operator
	}

	/**
	 * byte 0: left side, byte 1: top side, byte 2: right side.<br>In each byte
	 * bit order: <br>
	 * 7 \ 0 / 1<br>
	 * -------<br>
	 * 6 | - | 2<br>
	 * -------<br>
	 * 5 / 4 \ 3<br>
	 *
	 * @return four bytes in an int
	 */
	public int getAOFlags() {
		return aoFlags;
	}

	/**
	 * Set all flags at once
	 *
	 * @param aoFlags
	 */
	public void setAoFlags(int aoFlags) {
		this.aoFlags = aoFlags;
	}

	/**
	 * a block is only clipped if every side is clipped
	 *
	 * @return
	 */
	public byte getClipping() {
		return clipping;
	}

	/**
	 * a block is only clipped if every side is clipped
	 *
	 * @return
	 */
	public boolean isClipped() {
		return clipping == 0b111;
	}

	/**
	 *
	 */
	public void setClippedLeft() {
		clipping |= 1;
	}

	/**
	 *
	 */
	public void setClippedTop() {
		clipping |= 1 << 1;
	}

	/**
	 *
	 */
	public void setClippedRight() {
		clipping |= 1 << 2;
	}

	/**
	 *
	 */
	public void setUnclipped() {
		clipping = (byte) 0;
	}

	/**
	 * adds the entitiy into a cell
	 *
	 * @param ent
	 */
	public void addCoveredEnts(AbstractEntity ent) {
		allcovered.add(ent);
	}

	@Override
	public boolean shouldBeRendered(Camera camera) {
		return blockData != null
				&& !isClipped()
				&& !isHidden()
				&& camera.inViewFrustum(
					coord.getViewSpcX(),
					coord.getViewSpcY()
				);
	}

	@Override
	public ArrayList<Renderable> getCovered(RenderStorage rs) {
		return allcovered;
	}

	/**
	 * fill lists containing the nodes which are hidden by this block 
	 * @param rs
	 */
	public void fillCoveredList(RenderStorage rs){
		coveredBlocks.clear();
		Coordinate nghb = getPosition().toCoord();
		RenderBlock block;
		if (nghb.getZ() > 0) {
			nghb.add(0, 0, -1);
			block = nghb.getRenderBlock(rs);
			if (block != null) {
				coveredBlocks.add(block);
			}
			nghb.goToNeighbour(1);//back right
			block = nghb.getRenderBlock(rs);
			if (block != null) {
				coveredBlocks.add(block);
			}
			nghb.goToNeighbour(6);//back left
			block = nghb.getRenderBlock(rs);
			if (block != null) {
				coveredBlocks.add(block);
			}
			nghb.goToNeighbour(1);//back
			block = nghb.getRenderBlock(rs);
			if (block != null) {
				coveredBlocks.add(block);
			}
			nghb.add(0, 0, 1);
		}
		block = nghb.getRenderBlock(rs);//back
		if (block != null) {
			coveredBlocks.add(block);
		}
		nghb.goToNeighbour(3);//back right
		block = nghb.getRenderBlock(rs);
		if (block != null) {
			coveredBlocks.add(block);
		}
		nghb.goToNeighbour(6);//back left
		block = nghb.getRenderBlock(rs);
		if (block != null) {
			coveredBlocks.add(block);
		}
		
		allcovered = new ArrayList<>(coveredBlocks.size());
		allcovered.addAll(coveredBlocks);
	}
}