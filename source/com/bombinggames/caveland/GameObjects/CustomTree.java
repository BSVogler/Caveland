package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.Game.Events;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomTree extends RenderBlock implements Telegraph {
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
		
		if (getSpriteValue() == TREETOPVALUE)
			setHidden(true);
		
		MessageManager.getInstance().addListener(this, Events.destroyed.getId());
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		if (msg.message == Events.destroyed.getId() && msg.extraInfo.equals(getPosition())){
			//destroy other half
			Coordinate otherHalf;
			if (getSpriteValue() == TREETOPVALUE){
				otherHalf = getPosition().cpy().addVector(0, 0, -1);
			} else {
				otherHalf = getPosition().cpy().addVector(0, 0, 1);
			}
			
			WE.SOUND.play("blockDestroy");//to-do should be a wood chop down sound

			if (otherHalf.getBlock().getSpriteId()==getSpriteId() && otherHalf.getBlock().getSpriteValue() != -1)
				otherHalf.destroy();
		}
		return true;
	}
	
}
