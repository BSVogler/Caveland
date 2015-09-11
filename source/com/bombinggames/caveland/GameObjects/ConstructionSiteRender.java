package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionSiteRender extends RenderBlock {
	private static final long serialVersionUID = 1L;
	
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
