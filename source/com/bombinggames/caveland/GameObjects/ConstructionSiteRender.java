package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.rendering.RenderBlock;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionSiteRender extends RenderBlock {
	private static final long serialVersionUID = 1L;
	
	/**
	 *
	 * @param data
	 */
	public ConstructionSiteRender(Block data) {
		super(data);
		setSpriteValue((byte) 0);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (getSpriteValue() != 0)
			setSpriteValue((byte) 0);
	}
	
}
