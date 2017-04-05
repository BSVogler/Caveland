package com.bombinggames.wurfelengine.core.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;

/**
 * An entity which is rendered using the block sprites without sides.
 * @author Benedikt Vogler
 */
public class EntityBlock extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param id 
	 */
	public EntityBlock(byte id) {
		super(id);
	}
	
	/**
	 * 
	 * @param id
	 * @param value 
	 */
	public EntityBlock(byte id, byte value) {
		super(id, value);//-1 and 0 are reserverd, so I don't know a good alternative
	}

	@Override
	public char getSpriteCategory() {
		return 'b';
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
