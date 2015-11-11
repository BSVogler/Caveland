package com.bombinggames.wurfelengine.core.Gameobjects;

/**
 * An entity which is rendered as a block without sides.
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
	public char getCategory() {
		return 'b';
	}
}
