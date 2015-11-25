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
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.io.Serializable;

/**
 * A small block object hich stores only id and value and is only used for
 * storing in memory. Stores only 8 bytes.
 *
 * @author Benedikt Vogler
 */
public class Block implements HasID, Serializable {

	private static final long serialVersionUID = 1L;

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
	 * The game spaces dimension in pixel (edge length). 1 game meter ^= 1
	 * GAME_EDGELENGTH The value is calculated by VIEW_HEIGHT*sqrt(2) because of
	 * the axis shortening.
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
		Block.customBlocks = customBlockFactory;
	}

	public static CustomBlocks getFactory() {
		return customBlocks;
	}

	/**
	 * Use for creating new blocks.
	 *
	 * @param id in range 0 to {@link #OBJECTTYPESNUM}
	 * @return returns null for id==0
	 */
	public static Block getInstance(byte id) {
		if (id == 0) {
			return null;
		}
		return new Block(id, (byte) 0);
	}

	/**
	 * Use for creating new objects.
	 *
	 * @param id in range 0 to {@link #OBJECTTYPESNUM}
	 * @param value sub-id in range 0 to {@link #VALUESNUM}
	 * @return returns null for id==0
	 */
	public static Block getInstance(byte id, byte value) {
		if (id == 0) {
			return null;
		}
		if (id > OBJECTTYPESNUM) {
			return null;
		}
		if (value > VALUESNUM) {
			return null;
		}
		return new Block(id, value);
	}

	/**
	 * Creates a new logic instance. This can happen before the chunk is filled
	 * at this position.
	 *
	 * @param coord
	 * @return
	 */
	public AbstractBlockLogicExtension createLogicInstance(Coordinate coord) {
		if (customBlocks == null) {
			return null;
		}
		return customBlocks.newLogicInstance(this, coord);
	}

	private byte id;
	private byte value;
	private byte health = 100;

	/**
	 * each side has RGB color stored as 10bit float. Obtained by dividing bits
	 * by fraction /1023.
	 */
	private int colorLeft = (55 << 16) + (55 << 8) + 55;
	private int colorTop = (55 << 16) + (55 << 8) + 55;
	private int colorRight = (55 << 16) + (55 << 8) + 55;
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
	private byte clipping;

	private Block(byte id) {
		this.id = id;
	}

	private Block(byte id, byte value) {
		this.id = id;
		this.value = value;
	}

	/**
	 * 
	 * @return 
	 */
	public byte getId() {
		return id;
	}
	
	/**
	 * 
	 * @return 
	 */
	public byte getValue(){
		return value;
	}
	
	/**
	 * This method should not be used to change the value of a block because the map gets not informed about the change if you do this directly. Use {@link Coordinate#setValue(byte)} instead.
	 * @param value 
	 * @see Coordinate#setValue(byte) 
	 */
	public void setValue(byte value) {
		this.value = value;
	}
	
	@Override
	public byte getSpriteId() {
		return id;
	}


	@Override
	public byte getSpriteValue() {
		return value;
	}
	
	/**
	 * This method should not be used to change the value of a block because the map gets not informed about the change if you do this directly. Use {@link Coordinate#setValue(byte)} instead.
	 * @see Coordinate#setValue(byte) 
	 */
	@Override
	public void setSpriteValue(byte value) {
		this.value = value;
	}

	/**
	 * value between 0-100
	 *
	 * @param coord
	 * @param health
	 */
	public void setHealth(Coordinate coord, byte health) {
		this.health = health;
		if (customBlocks != null) {
			customBlocks.onSetHealth(coord, health, id, value);
		}
		if (health <= 0 && !isIndestructible()) {
			//make an invalid air instance (should be null)
			this.id = 0;
			this.value = 0;
		}
	}

	/**
	 * value between 0-100. This method should only be used for non-bocks.
	 *
	 * @param health
	 */
	public void setHealth(byte health) {
		this.health = health;
	}

	/**
	 * The health is stored in a byte in the range [0;100]
	 * @return 
	 */
	public byte getHealth() {
		return health;
	}

	/**
	 * creates a new RenderBlock instance based on he data
	 *
	 * @return
	 */
	public RenderBlock toRenderBlock() {
		if (id == 0) {
			return null;
		}
		
		if (id == 4) {//invisible wall
			RenderBlock a = new RenderBlock(this);
			a.setHidden(true);
			return a;
		}

		if (id == 9) {
			return new Sea(id);
		}

		if (customBlocks != null) {
			return customBlocks.toRenderBlock(this);
		}

		return new RenderBlock(this);
	}

	@Override
	public boolean isObstacle() {
		if (id > 9 && customBlocks != null) {
			return customBlocks.isObstacle(id, value);
		}
		if (id == 9) {
			return false;
		}
		
		return id != 0;
	}

	@Override
	public boolean isTransparent() {
		if (id == 9) {
			return true;
		}
		
		if (id == 4) {
			return true;
		}
		
		if (id > 9 && customBlocks != null) {
			return customBlocks.isTransparent(id, value);
		}
		return false;
	}

	/**
	 * Check if the block is liquid.
	 *
	 * @return true if liquid, false if not
	 */
	@Override
	public boolean isLiquid() {
		if (id > 9 && customBlocks != null) {
			return customBlocks.isLiquid(id, value);
		}
		return id == 9;
	}
	
	@Override
	public boolean isIndestructible() {
		if (customBlocks != null) {
			return customBlocks.isIndestructible(id, value);
		}
		return false;
	}

	/**
	 * get the average brightness above all the sides
	 *
	 * @return
	 */
	@Override
	public float getLightlevelR() {
		return (getLightlevelR(Side.LEFT) + getLightlevelR(Side.TOP) + getLightlevelR(Side.RIGHT)) / 3f;
	}

	@Override
	public float getLightlevelG() {
		return (getLightlevelG(Side.LEFT) + getLightlevelG(Side.TOP) + getLightlevelG(Side.RIGHT)) / 3f;
	}

	@Override
	public float getLightlevelB() {
		return (getLightlevelB(Side.LEFT) + getLightlevelB(Side.TOP) + getLightlevelB(Side.RIGHT)) / 3f;
	}

	/**
	 *
	 * @param side
	 * @return range 0-2.
	 */
	public float getLightlevelR(Side side) {
		if (side == Side.LEFT) {
			return ((colorLeft >> 20) & 0x3FF) / 511f;
		} else if (side == Side.TOP) {
			return ((colorTop >> 20) & 0x3FF) / 511f;
		}
		return ((colorRight >> 20) & 0x3FF) / 511f;
	}

	/**
	 *
	 * @param side
	 * @return range 0-2.
	 */
	public float getLightlevelG(Side side) {
		if (side == Side.LEFT) {
			return ((colorLeft >> 10) & 0x3FF) / 511f;
		} else if (side == Side.TOP) {
			return ((colorTop >> 10) & 0x3FF) / 511f;
		}
		return ((colorRight >> 10) & 0x3FF) / 511f;
	}

	/**
	 *
	 * @param side
	 * @return range 0 -2
	 */
	public float getLightlevelB(Side side) {
		if (side == Side.LEFT) {
			return (colorLeft & 0x3FF) / 511f;
		} else if (side == Side.TOP) {
			return (colorTop & 0x3FF) / 511f;
		}
		return (colorRight & 0x3FF) / 511f;
	}

	/**
	 * Stores the lightlevel overriding each side
	 *
	 * @param lightlevel range 0 -2
	 */
	@Override
	public void setLightlevel(float lightlevel) {
		if (lightlevel <= 0) {
			colorLeft = 0;
			this.colorTop = colorLeft;
			this.colorRight = colorLeft;
		} else {
			int l = (int) (lightlevel * 512);
			if (l > 1023) {
				l = 1023;
			}
			colorLeft = (l << 20) + (l << 10) + l;//RGB;
			this.colorTop = colorLeft;
			this.colorRight = colorLeft;
		}
	}

	/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 */
	public void setLightlevel(float lightlevel, Side side) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}
		int l = (int) (lightlevel * 512);
		if (l > 1023) {
			l = 1023;
		}

		if (side == Side.LEFT) {
			colorLeft = (l << 20) + (l << 10) + l;//RGB;
		} else if (side == Side.TOP) {
			colorTop = (l << 20) + (l << 10) + l;//RGB;
		} else {
			colorRight = (l << 20) + (l << 10) + l;//RGB
		}
	}
	
		/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 * @param channel
	 */
	public void setLightlevel(float lightlevel, Side side, int channel) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}
		
		byte colorBitshift = 0;
		if (channel==0)
			colorBitshift = 20;
		else if (channel==1)
			colorBitshift = 10;
		
		int l = (int) (lightlevel * 512);
		if (l > 1023) {
			l = 1023;
		}
		
		if (side == Side.LEFT) {
			colorLeft |= (l << colorBitshift);
		} else if (side == Side.TOP) {
			colorTop |= (l << colorBitshift);
		} else {
			colorRight |= (l << colorBitshift);
		}
	}
	
		/**
	 *
	 * @param lightlevel a factor in range [0-2]
	 * @param side
	 * @param channel 0 = R, 1 =G, 2=B
	 */
	public void addLightlevel(float lightlevel, Side side, int channel) {
		if (lightlevel < 0) {
			lightlevel = 0;
		}
		
		byte colorBitshift = 0;
		if (channel==0)
			colorBitshift = 20;
		else if (channel==1)
			colorBitshift = 10;
		
		float l = lightlevel * 512;
		if (l > 1023) {
			l = 1023;
		}
		
		if (side == Side.LEFT) {
			int newl = (int) (((colorLeft >> colorBitshift) & 0x3FF) / 511f+l);
			if (newl > 1023)
				newl = 1023;
			colorLeft |= (newl << colorBitshift);
		} else if (side == Side.TOP) {
			int newl = (int) (((colorTop >> colorBitshift) & 0x3FF) / 511f+l);
			if (newl > 1023)
				newl = 1023;
			colorTop  |= (newl << colorBitshift);
		} else {
			int newl = (int) (((colorRight >> colorBitshift) & 0x3FF) / 511f+l);
			if (newl > 1023)
				newl = 1023;
			colorRight |= (newl << colorBitshift);
		}
	}

	/**
	 * get the name of a combination of id and value
	 *
	 * @return
	 */
	@Override
	public String getName() {
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

	@Override
	public boolean hasSides() {
		if (id == 0) {
			return false;
		}
		
		if (id==4)
			return false;
		
		if (id > 9 && customBlocks != null) {
			return customBlocks.hasSides(id, value);
		}
		return true;
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

	public void setClippedLeft() {
		clipping |= 1;
	}

	public void setClippedTop() {
		clipping |= 1 << 1;
	}

	public void setClippedRight() {
		clipping |= 1 << 2;
	}

	public void setUnclipped() {
		clipping = (byte) 0;
	}

}
