package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomTree extends RenderBlock {
	private static final long serialVersionUID = 1L;
	/**
	 * The treetop is used to identify the treetop. It is invisible but it is an obstacle.
	 */
	public static final byte TREETOPVALUE = 8;

	/**
	 * creates a tree in a random shape
	 */
	public CustomTree(){
		this((byte)72, (byte) (Math.random()*8), (byte) 100);
	}
	
	/**
	 *
	 * @param id
	 * @param value
	 * @param health
	 */
	public CustomTree(byte id, byte value, byte health) {
		super(id, value);
		setSpriteValue(value);
		
		if (getSpriteValue() == TREETOPVALUE)
			setHidden(true);
	}
}
