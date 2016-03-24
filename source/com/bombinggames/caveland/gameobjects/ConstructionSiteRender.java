package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionSiteRender extends RenderCell {
	private static final long serialVersionUID = 1L;
	
	/**
	 *
	 * @param id
	 * @param value
	 */
	public ConstructionSiteRender(byte id, byte value) {
		super(id, value);
		setSpriteValue((byte) 0);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (getSpriteValue() != 0)
			setSpriteValue((byte) 0);
	}
	
}
