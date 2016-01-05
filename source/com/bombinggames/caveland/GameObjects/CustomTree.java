package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;

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
		this(Block.getInstance((byte)72, (byte) (Math.random()*8)));
	}
	
	/**
	 *
	 * @param data
	 */
	public CustomTree(Block data) {
		super(data);
		setSpriteValue(data.getSpriteValue());
		
		if (getSpriteValue() == TREETOPVALUE)
			setHidden(true);
	}
}
