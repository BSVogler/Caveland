package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.rendering.RenderBlock;

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
		setSpriteValue(data.getValue());
		
		if (getSpriteValue() == TREETOPVALUE)
			setHidden(true);
	}
}
