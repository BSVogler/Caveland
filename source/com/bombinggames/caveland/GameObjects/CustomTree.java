package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomTree extends RenderBlock {
	private static final long serialVersionUID = 1L;
	/**
	 * The treetop is used to identify the treetop. It is invisible but it is an obstacle.
	 */
	private final byte TREETOPVALUE = 8;

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
		super(data.getSpriteId());
		setSpriteValue(data.getSpriteValue());
		
		if (getSpriteValue()==TREETOPVALUE)
			setHidden(true);
	}

//	@Override
//	public void update(float dt) {
//		super.update(dt);
//		//check and grow treetop
//		Coordinate top = getPosition().cpy().addVector(0, 0, 1);
//		if (getSpriteValue() != TREETOPVALUE && top.getBlock().getSpriteId() != getSpriteId())//if root block grow treetop
//			new RenderBlock((byte) 72, TREETOPVALUE).spawn(top);
//	}

	

	
	@Override
	public void onDestroy() {	
		//destroy other half
		Coordinate otherHalf;
		if (getSpriteValue() == TREETOPVALUE){
			otherHalf = getPosition().cpy().addVector(0, 0, -1);
		} else {
			otherHalf = getPosition().cpy().addVector(0, 0, 1);
		}
		
		super.onDestroy();
		
		if (otherHalf.getBlock().getSpriteId()==getSpriteId() && otherHalf.getBlock().getSpriteValue() != -1)
			otherHalf.destroy();
			
		CollectibleType.Wood.createInstance().spawn(getPosition().toPoint());			
	}

	
}
