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
	}


	@Override
	public byte getSpriteValue() {
		return 0;
	}
	
	
}
