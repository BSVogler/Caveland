/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombinggames.wurfelengine.core.GameView;
import static com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject.getSprite;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.RenderBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.Side;

/**
 *
 * @author Benedikt Vogler
 */
public class GrassBlock extends RenderBlock {
	
	private static final long serialVersionUID = 1L;
	private final Sprite grasSprite;

	public GrassBlock(Block data) {
		super(data);
		grasSprite = new Sprite(getSprite('e', (byte) 7, (byte) 0));
	}

	@Override
	public void renderSide(GameView view, int xPos, int yPos, Side side, Color color, int ao) {
		super.renderSide(view, xPos, yPos, side, color, ao);
		Sprite gras = grasSprite;

		for (int i = 0; i < 10; i++) {
			int xOffset = Math.abs((xPos - 3) * i * (yPos)) % Block.VIEW_WIDTH - Block.VIEW_WIDTH2;
			int yOffset = Math.abs(((xPos - i) * 3 * (yPos * 3 - i))) % Block.VIEW_DEPTH - Block.VIEW_DEPTH2;
			if (Math.abs(xOffset) + Math.abs(yOffset) < Block.VIEW_WIDTH2 - 10) {
				gras.setColor(
					getLightlevel(side, 1, 0) / 2f - 0.1f,
					getLightlevel(side, 1, 1) / 2f - (xOffset * yPos + i) % 3 * 0.02f,
					getLightlevel(side, 1, 2) / 2f,
					1
				);
				gras.setPosition(
					xPos + xOffset + Block.VIEW_WIDTH2,
					yPos - yOffset + Block.VIEW_DEPTH2 - 10
				);
				float windRotate = (float) xOffset % 17 / 17f * 10f - 2.5f;
				gras.rotate(windRotate);
				gras.draw(view.getSpriteBatch());
			}
		}
	}

	
	
	
	
	
	
}
