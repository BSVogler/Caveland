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
import com.bombinggames.wurfelengine.core.gameobjects.Sea;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.CustomBlocks;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.Position;
import java.util.LinkedList;

/**
 * It is something which can be rendered and therefore render information saved shared across cameras. A RenderCell should not use the event system. The class extends (wraps) the plain data of the block with a position and {@link AbstractGameObject} class methods. The wrapped cell is not referenced. It is possible to change there sprite id and value {@link AbstractGameObject#setSpriteId(byte)} but keeping the logic id and value. <br>
 * The internal wrapped block can have different id then used for rendering. The rendering sprite id's are set in the constructor or later manualy.<br>
 * @author Benedikt Vogler
 */
public class RenderCell extends AbstractGameObject {
    private static final long serialVersionUID = 1L;
	/**
	 * indexed acces to spritesheet {id}{value}{side}
	 */
    private static AtlasRegion[][][] blocksprites = new AtlasRegion[RenderCell.OBJECTTYPESNUM][RenderCell.VALUESNUM][3];
	
    /**
     * a list where a representing color of the block is stored
     */
    private static final Color[][] COLORLIST = new Color[RenderCell.OBJECTTYPESNUM][RenderCell.VALUESNUM];
	private static boolean fogEnabled;
	private static boolean staticShade;
	/**
	 * frame number of last rebuild
	 */
	private static long rebuildCoverList = 0;
	private static SimpleEntity destruct = new SimpleEntity((byte) 3,(byte) 0);
	private static Color tmpColor = new Color();
	
	/**
	 * Screen depth of a block/object sprite in pixels. This is the length from
	 * the top to the middle border of the block.
	 */
	public transient static final int VIEW_DEPTH = 100;
	/**
	 * The half (1/2) of VIEW_DEPTH. The short form of: VIEW_DEPTH/2
	 */
	public transient static final int VIEW_DEPTH2 = VIEW_DEPTH / 2;
	/**
	 * A quarter (1/4) of VIEW_DEPTH. The short form of: VIEW_DEPTH/4
	 */
	public transient static final int VIEW_DEPTH4 = VIEW_DEPTH / 4;

	/**
	 * The width (x-axis) of the sprite size.
	 */
	public transient static final int VIEW_WIDTH = 200;
	/**
	 * The half (1/2) of VIEW_WIDTH. The short form of: VIEW_WIDTH/2
	 */
	public transient static final int VIEW_WIDTH2 = VIEW_WIDTH / 2;
	/**
	 * A quarter (1/4) of VIEW_WIDTH. The short form of: VIEW_WIDTH/4
	 */
	public transient static final int VIEW_WIDTH4 = VIEW_WIDTH / 4;

	/**
	 * The height (y-axis) of the sprite size.
	 */
	public transient static final int VIEW_HEIGHT = 122;
	/**
	 * The half (1/2) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/2
	 */
	public transient static final int VIEW_HEIGHT2 = VIEW_HEIGHT / 2;
	/**
	 * A quarter (1/4) of VIEW_HEIGHT. The short form of: VIEW_WIDTH/4
	 */
	public transient static final int VIEW_HEIGHT4 = VIEW_HEIGHT / 4;

	/**
	 * The game space dimension size's aequivalent to VIEW_DEPTH or VIEW_WIDTH.
	 * Because the x axis is not shortened those two are equal.
	 */
	public transient static final int GAME_DIAGLENGTH = VIEW_WIDTH;

	/**
	 * Half (1/2) of GAME_DIAGLENGTH.
	 */
	public transient static final int GAME_DIAGLENGTH2 = VIEW_WIDTH2;

	/**
	 * Pixels per game spaces meter (edge length).<br>
	 * 1 game meter ^= 1 GAME_EDGELENGTH<br>
	 * The value is calculated by VIEW_HEIGHT*sqrt(2) because of the axis
	 * shortening.
	 */
	public transient static final int GAME_EDGELENGTH = (int) (GAME_DIAGLENGTH / 1.41421356237309504880168872420969807856967187537694807317667973799f);

	/**
	 * Half (1/2) of GAME_EDGELENGTH.
	 */
	public transient static final int GAME_EDGELENGTH2 = GAME_EDGELENGTH / 2;

	/**
	 * Some magic number which is the factor by what the Z axis is distorted
	 * because of the angle of projection.
	 */
	public transient static final float ZAXISSHORTENING = VIEW_HEIGHT / (float) GAME_EDGELENGTH;

	/**
	 * the max. amount of different object types
	 */
	public transient static final int OBJECTTYPESNUM = 124;
	/**
	 * the max. amount of different values
	 */
	public transient static final int VALUESNUM = 64;

	/**
	 * the factory for custom blocks
	 */
	private static CustomBlocks customBlocks;
	
	/**
	 * If you want to define custom id's &gt;39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(CustomBlocks customBlockFactory) {
		customBlocks = customBlockFactory;
	}

	/**
	 *
	 * @return
	 */
	public static CustomBlocks getFactory() {
		return customBlocks;
	}

	/**
	 * Creates a new logic instance. This can happen before the chunk is filled
	 * at this position.
	 *
	 * @param id
	 * @param value
	 * @param coord
	 * @return null if has no logic
	 */
	public static AbstractBlockLogicExtension createLogicInstance(byte id, byte value, Coordinate coord) {
		if (customBlocks == null) {
			return null;
		}
		return customBlocks.newLogicInstance(id, value, coord);
	}
	
	
	public static boolean hasLogic(byte id, byte value) {
		if (customBlocks == null) {
			return false;
		}
		return customBlocks.hasLogic(id, value);
	}

	/**
	 * value between 0-100
	 *
	 * @param coord
	 * @param health
	 */
//	public static void setHealth(Coordinate coord, byte id, byte value, byte health) {
//		if (customBlocks != null) {
//			customBlocks.onSetHealth(coord, health, id, value);
//		}
//		if (health <= 0 && !isIndestructible(id, value)) {
//			//make an invalid air instance (should be null)
//			this.id = 0;
//			this.value = 0;
//		}
//	}

	/**
	 * The health is stored in a byte in the range [0;100]
	 *
	 * @param block
	 * @return
	 */
	public static byte getHealth(int block) {
		return (byte) ((block >> 16) & 255);
	}

	/**
	 * creates a new RenderCell instance based on the data
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public static RenderCell getRenderCell(byte id, byte value) {
		if (id == 0 || id == 4) {//air and invisible wall
			RenderCell a = new RenderCell(id, value);
			a.setHidden(true);
			return a;
		}

		if (id == 9) {
			return new Sea(id, value);
		}

		if (customBlocks != null) {
			return customBlocks.toRenderBlock(id, value);
		} else {
			return new RenderCell(id, value);
		}
	}

	/**
	 * 
	 * @param id
	 * @param value
	 * @return 
	 */
	public static boolean isObstacle(byte id, byte value) {
		if (id > 9 && customBlocks != null) {
			return customBlocks.isObstacle(id, value);
		}
		if (id == 9) {
			return false;
		}
		
		return id != 0;
	}
	
	/**
	 * 
	 * @param block
	 * @return 
	 */
	public static boolean isObstacle(int block) {
		return isObstacle((byte)(block&255), (byte)((block>>8)&255));
	}
	
	/**
	 * 
	 * @param id
	 * @param value
	 * @return 
	 */
	public static boolean isTransparent(byte id, byte value) {
		if (id==0 || id == 9 || id == 4) {
			return true;
		}
		
		if (id > 9 && customBlocks != null) {
			return customBlocks.isTransparent(id, value);
		}
		return false;
	}
	
	/**
	 * 
	 * @param block
	 * @return 
	 */
	public static boolean isTransparent(int block) {
		return isTransparent((byte)(block&255), (byte)((block>>8)&255));
	}

	/**
	 * Check if the block is liquid.
	 *
	 * @param id
	 * @param value
	 * @return true if liquid, false if not
	 */
	public static boolean isLiquid(byte id, byte value) {
		if (id > 9 && customBlocks != null) {
			return customBlocks.isLiquid(id, value);
		}
		return id == 9;
	}
	
		/**
	 * Check if the block is liquid.
	 *
	 * @param block first byte id, second value, third health
	 * @return true if liquid, false if not
	 */
	public static boolean isLiquid(int block) {
		return isLiquid((byte)(block&255), (byte)((block>>8)&255));
	}
	
	public static boolean isIndestructible(byte id, byte value) {
		if (customBlocks != null) {
			return customBlocks.isIndestructible(id, value);
		}
		return false;
	}

	/**
	 * get the name of a combination of id and value
	 *
	 * @param id
	 * @param value
	 * @return
	 */
	public static String getName(byte id, byte value) {
		if (id < 10) {
			switch (id) {
				case 0:
					return "air";
				case 1:
					return "grass";
				case 2:
					return "dirt";
				case 3:
					return "stone";
				case 4:
					return "invisible obstacle";
				case 8:
					return "sand";
				case 9:
					return "water";
				default:
					return "undefined";
			}
		} else {
			if (customBlocks != null) {
				return customBlocks.getName(id, value);
			} else {
				return "undefined";
			}
		}
	}

	public static boolean hasSides(byte spriteId, byte spriteValue) {
		if (spriteId == 0 || spriteId == 4) {
			return false;
		}
		
		if (spriteId > 9 && customBlocks != null) {
			return customBlocks.hasSides(spriteId, spriteValue);
		}
		return true;
	}
	
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
	 * @param spriteId
	 * @param spriteValue
	 * @return 
	 */
	public static boolean isSpriteDefined(byte spriteId, byte spriteValue) {
		return spriteId != 0
			&& getSpritesheet() != null
			&& getSpritesheet().findRegion('b' + Byte.toString(spriteId) + "-" + spriteValue + "-0" + (RenderCell.hasSides(spriteId, spriteValue) ? "-0" : "")) != null;
	}
	
	/**
	 * set the timestamp when the content changed
	 */
	public static void rebuildCoverList() {
		RenderCell.rebuildCoverList = WE.getGameplay().getFrameNum();
	}

   /**
     * Returns a color representing the block. Picks from the sprite sprite.
     * @param id id of the RenderCell
     * @param value the value of the block.
     * @return copy of a color representing the block
     */
    public static Color getRepresentingColor(final byte id, final byte value){
        if (COLORLIST[id][value] == null){ //if not in list, add it to the list
            COLORLIST[id][value] = new Color();
            int colorInt;
            
            if (RenderCell.hasSides(id, value)){//if has sides, take top block    
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

	/**
	 * game logic value. Sprite Id may differ.
	 */
	private final byte id;
	private byte value;
	private Coordinate coord = new Coordinate(0, 0, 0);
	
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
	 * byte 0: left side, byte 1: top side, byte 2: right side.<br>In each byte the
	 * bit order: <br>
	 * &nbsp;&nbsp;\&nbsp;0/<br>
	 * 7&nbsp;&nbsp;\/1<br>
	 * \&nbsp;&nbsp;/\&nbsp;&nbsp;/<br>
	 * 6\/8&nbsp;\/2<br>
	 * &nbsp;/\&nbsp;&nbsp;/\<br>
	 * /&nbsp;&nbsp;\/&nbsp;3\<br>
	 * &nbsp;&nbsp;5/\<br>
	 * &nbsp;&nbsp;/&nbsp;4\<br>
	 * <br>
	 **/
	private int aoFlags;
	/**
	 * three bits used, for each side one: TODO: move to aoFlags byte #3
	 */
	private byte clipping;
	/**
	 * stores references to neighbor blocks which are covered.
	 */
	private final LinkedList<AbstractGameObject> covered = new LinkedList<>();
	/**
	 * for topological sort. At the end contains both entities and blocks
	 */
	private final LinkedList<AbstractGameObject> coveredEnts = new LinkedList<>();
	private SideSprite site1;
	private SideSprite site3;
	private SideSprite site2;
	/**
	 * frame number to avoid multiple calculations in one frame
	 */
	private long lastRebuild;
	
	/**
	 * For direct creation. You should use the factory method instead.
	 * @param id 
	 * @see #getRenderCell(byte, byte) 
	 */
    public RenderCell(byte id){
        super(id);
		this.id = id;
	}
	
	/**
	 * For direct creation. You should use the factory method instead.
	 * @param id
	 * @param value 
	 * @see #getRenderCell(byte, byte) 
	 */
	public RenderCell(byte id, byte value){
		super(id, value);
		this.id = id;
		this.value = value;
	}
	
	/**
	 * game logic value. Sprite Id may differ.
	 * @return 
	 * @see #getSpriteId() 
	 */
	public byte getId() {
		return id;
	}

	/**
	 * game logic value. Sprite value may differ.
	 * @return 
	 * @see #getSpriteValue()
	 */
	public byte getValue() {
		return value;
	}
	
	public boolean isObstacle() {
		return RenderCell.isObstacle(id, value);
	}

    @Override
    public String getName() {
        return RenderCell.getName(id, value);
    }

	@Override
	public Point getPoint() {
		return coord.toPoint();
	}

	@Override
	public Coordinate getCoord() {
		return coord;
	}
	
	@Override
	public int getDimensionZ() {
		return RenderCell.GAME_EDGELENGTH;
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
	 * places the object on the map. You can extend this to get the coordinate.
 RenderCell may be placed without this method call. A regular renderblock
 is not spawned expect explicitely called.
	 *
	 * @param rS
	 * @param coord the position on the map
	 * @return itself
	 * @see #setPosition(com.bombinggames.wurfelengine.core.map.Position)
	 */
	public RenderCell spawn(RenderStorage rS, Coordinate coord) {
		setPosition(coord);
		Controller.getMap().setBlock(this);
		return this;
	}
    
    @Override
	public void render(final GameView view, final Camera camera) {
		if (!isHidden()) {
			if (hasSides()) {
				Coordinate coords = getPosition();
				byte clipping = getClipping();
				if ((clipping & (1 << 1)) == 0) {
					renderSide(view, camera, coords, Side.TOP, staticShade);
				}
				if ((clipping & 1) == 0) {
					renderSide(view, camera, coords, Side.LEFT, staticShade);
				}
				if ((clipping & (1 << 2)) == 0) {
					renderSide(view, camera, coords, Side.RIGHT, staticShade);
				}
			} else {
				super.render(view, camera);
			}
		}
	}
    
 /**
	 * Render the whole block at a custom position. Checks if hidden.
	 *
	 * @param view the view using this render method
	 * @param xPos rendering position (screen)
	 * @param yPos rendering position (screen)
	 */
	@Override
	public void render(final GameView view, final int xPos, final int yPos) {
		if (!isHidden()) {
			if (hasSides()) {
				renderSide(view, xPos, yPos + (VIEW_HEIGHT + VIEW_DEPTH), Side.TOP);
				renderSide(view, xPos, yPos, Side.LEFT);
				renderSide(view, xPos + VIEW_WIDTH2, yPos, Side.RIGHT);
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
				float scale = getScaling();
				renderSide(
					view,
					(int) (xPos - VIEW_WIDTH2 * scale),
					(int) (yPos + VIEW_HEIGHT * scale),
					Side.TOP,
					color
				);

				if (staticShade) {
					if (color == null) {
						color = new Color(0.75f, 0.75f, 0.75f, 1);
					} else {
						color = color.cpy().add(0.25f, 0.25f, 0.25f, 0);
					}
				}
				renderSide(
					view,
					(int) (xPos - VIEW_WIDTH2 * scale),
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
			} else {
				super.render(view, xPos, yPos + VIEW_DEPTH4, color);
			}
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
		Color color = tmpColor;
		if (fogEnabled) {
			//can use CVars for dynamic change. using harcored values for performance reasons
			float factor = (float) (Math.exp(0.025f * (camera.getVisibleFrontBorderHigh() - coords.toCoord().getY() - 18.0)) - 1);
			//float factor = (float) (Math.exp( 0.0005f*(coords.getDepth(view)-500) )-1 );
			color.set(0.5f + 0.3f * factor, 0.5f + 0.4f * factor, 0.5f + 1f * factor, 1);
		} else {
			color.set(Color.GRAY);
		}

		//if vertex shaded then use different shading for each side
		if (Controller.getLightEngine() != null && !Controller.getLightEngine().isShadingPixelBased()) {
			color = Controller.getLightEngine().getColor(side, getPosition()).mul(color.r + 0.5f, color.g + 0.5f, color.b + 0.5f, color.a + 0.5f);
		}
		
        renderSide(
			view,
            coords.getViewSpcX() - VIEW_WIDTH2 + ( side == Side.RIGHT ? (int) (VIEW_WIDTH2*(getScaling())) : 0),//right side is  half a block more to the right,
            coords.getViewSpcY() - VIEW_HEIGHT2 + ( side == Side.TOP ? (int) (VIEW_HEIGHT*(getScaling())) : 0),//the top is drawn a quarter blocks higher,
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
		
		byte health = getHealth();
		if (health < 100) {
			int damageOverlayStep = 0;
			if (health <= 50) {
				damageOverlayStep = 1;
			}
			if (health <= 25) {
				damageOverlayStep = 2;
			}
			
			if (damageOverlayStep > -1) {
				//render damage
				switch (side) {
					case LEFT:
						renderDamageOverlay(view,
							camera,
							getPosition().toPoint().add(-RenderCell.GAME_DIAGLENGTH2 / 2, 0, 0),
							(byte) (3 * damageOverlayStep)
						);
						break;
					case TOP:
						renderDamageOverlay(view,
							camera,
							getPosition().toPoint().add(0, 0, RenderCell.GAME_EDGELENGTH),
							(byte) (3 * damageOverlayStep + 1)
						);
						break;
					case RIGHT:
						renderDamageOverlay(view,
							camera,
							getPosition().toPoint().add(RenderCell.GAME_DIAGLENGTH2 / 2, 0, 0),
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
	 *
	 * @param view
	 * @param camera
	 * @param aopos
	 * @param value damage sprite value
	 */
	private void renderDamageOverlay(final GameView view, final Camera camera, final Position aopos, final byte value) {
		destruct.setSpriteValue(value);
		destruct.setPosition(aopos);
		destruct.getColor().set(0.5f, 0.5f, 0.5f, 0.7f);
		destruct.render(view, camera);
	}

	/**
	 * Ignores lightlevel.
	 *
	 * @param view the view using this render method
	 * @param xPos rendering position
	 * @param yPos rendering position
	 * @param side The number identifying the side. 0=left, 1=top, 2=right
	 */
	public void renderSide(final GameView view, final int xPos, final int yPos, final Side side) {
		Color color;
		if (Controller.getLightEngine() != null && !Controller.getLightEngine().isShadingPixelBased()) {
			color = Controller.getLightEngine().getColor(side, getPosition());
		} else {
			color = Color.GRAY.cpy();
		}

		renderSide(
			view,
			xPos,
			yPos,
			side,
			color
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
	 */
	public void renderSide(final GameView view, final int xPos, final int yPos, final Side side, Color color) {
		byte id = getSpriteId();
		if (id <= 0) {
			return;
		}
		byte value = getSpriteValue();
		if (value < 0) {
			return;
		}

		//lazy init
		SideSprite sprite;
		switch (side) {
			case LEFT:
				if (site1 == null) {
					site1 = new SideSprite(getBlockSprite(id, value, side), side, aoFlags);
				}
				sprite = site1;
				break;
			case TOP:
				if (site2 == null) {
					site2 = new SideSprite(getBlockSprite(id, value, side), side, aoFlags);
				}
				sprite = site2;
				break;
			default:
				if (site3 == null) {
					site3 = new SideSprite(getBlockSprite(id, value, side), side, aoFlags);
				}
				sprite = site3;
				break;
		}
		sprite.setPosition(xPos, yPos);
		if (getScaling() != 1) {
			sprite.setOrigin(0, 0);
			sprite.setScale(getScaling());
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

	/**
	 * keeps reference
	 * @param coord 
	 */
	public void setPosition(Coordinate coord){
		this.coord = coord;
	}	
	
	/**
	 * Can light travel through object?
	 * @return
	 */
	public boolean isTransparent() {
		if (id==0) return true;
		return RenderCell.isTransparent(getSpriteId(),getSpriteValue());//sprite id because view related
	}
	
	public boolean isIndestructible() {
		return RenderCell.isIndestructible(id,value);//game logic related
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
		if (id == 0) {
			return false;
		}
		return RenderCell.hasSides(getSpriteId(),getSpriteValue());
	}

	public boolean isLiquid() {
		if (id == 0) {
			return false;
		}
		return RenderCell.isLiquid(id,value);
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
		if (aoFlags!=this.aoFlags){
			if (site1 != null) {
				site1.setAoFlags(aoFlags);
			}
			if (site2 != null) {
				site2.setAoFlags(aoFlags);
			}
			if (site3 != null) {
				site3.setAoFlags(aoFlags);
			}
		}
			
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
	 * Makes every side visible
	 */
	public void setUnclipped() {
		clipping = 0;
	}

	/**
	 * adds the entitiy into a cell
	 *
	 * @param ent
	 */
	public void addCoveredEnts(AbstractEntity ent) {
		coveredEnts.add(ent);
	}

	@Override
	public boolean shouldBeRendered(Camera camera) {
		return id != 0
				&& !isClipped()
				&& !isHidden()
				&& camera.inViewFrustum(coord);
	}

	@Override
	public LinkedList<AbstractGameObject> getCovered(RenderStorage rs) {
		if (lastRebuild < rebuildCoverList) {//only rebuild once per frame
			rebuildCovered(rs);
		}
		if (!coveredEnts.isEmpty()) {
			//sort valid in order of depth
			coveredEnts.sort((AbstractGameObject o1, AbstractGameObject o2) -> {
				float d1 = o1.getDepth();
				float d2 = o2.getDepth();
				if (d1 > d2) {
					return 1;
				} else {
					if (d1 == d2) {
						return 0;
					}
					return -1;
				}
			});
			coveredEnts.addAll(covered);
			return coveredEnts;
		}
		return covered;
	}
	
	/**
	 * Rebuilds the list of covered cells by this cell.
	 * @param rs 
	 */
	private void rebuildCovered(RenderStorage rs) {
		LinkedList<AbstractGameObject> covered = this.covered;
		covered.clear();
		Coordinate nghb = getPosition();
		RenderCell block;
		if (nghb.getZ() > 0) {
			block = rs.getCell(nghb.add(0, 0, -1));//go down
			if (block != null) {
				covered.add(block);
			}
			//back right
			block = rs.getCell(nghb.goToNeighbour(1));
			if (block != null) {
				covered.add(block);
			}
			//back left
			block = rs.getCell(nghb.goToNeighbour(6));
			if (block != null) {
				covered.add(block);
			}
			//back
			block = rs.getCell(nghb.goToNeighbour(1));
			if (block != null) {
				covered.add(block);
			}
			nghb.add(0, 2, 1);//go back to origin
		}
		block = rs.getCell(nghb.goToNeighbour(0));//back
		if (block != null) {
			covered.add(block);
		}
		block = rs.getCell(nghb.goToNeighbour(3));//back right
		if (block != null) {
			covered.add(block);
		}

		block = rs.getCell(nghb.goToNeighbour(6));//back left
		if (block != null) {
			covered.add(block);
		}
		if (nghb.getZ() < Chunk.getBlocksZ() - 1) {
			block = rs.getCell(nghb.add(0, 0, 1));//back left above
			if (block != null) {
				covered.add(block);
			}
			block = rs.getCell(nghb.goToNeighbour(2));//back right above
			if (block != null) {
				covered.add(block);
			}
			nghb.add(-1, 0, -1);//back to back left
		}

		nghb.goToNeighbour(3);//return to origin
		lastRebuild = WE.getGameplay().getFrameNum();
	}

	public void clearCoveredEnts() {
		coveredEnts.clear();
	}

	/**
	 * get the health byte from the map.
	 *
	 * @return if no coordiante returns 100
	 */
	public byte getHealth() {
		if (coord == null) {
			return 100;
		}
		return Controller.getMap().getHealth(coord);
	}

	@Override
	public String toString() {
		return Integer.toHexString(hashCode())+" @"+getPosition().toString()+" id: "+ id+" value: "+value;
	}

}