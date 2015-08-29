package com.bombinggames.wurfelengine.core.Gameobjects;

/**
 * An entity which can render as a block without sides.
 * @author Benedikt Vogler
 */
public class EntityBlock extends AbstractEntity {
	
	private static final long serialVersionUID = 1L;

	public EntityBlock(byte id) {
		super((byte) 1);//-1 and 0 are reserverd, so I don't know a good alternative
		setGraphicsId(id);
	}
	
	public EntityBlock(byte id, byte value) {
		super((byte) 1, value);//-1 and 0 are reserverd, so I don't know a good alternative
		setGraphicsId(id);
	}

	@Override
	public char getCategory() {
		return 'b';
	}
}
