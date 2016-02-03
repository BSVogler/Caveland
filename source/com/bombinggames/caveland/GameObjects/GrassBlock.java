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
package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.bombinggames.wurfelengine.core.GameView;
import static com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject.getSprite;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.rendering.RenderBlock;
import com.bombinggames.wurfelengine.core.gameobjects.Side;
import java.util.Random;

/**
 *
 * @author Benedikt Vogler
 */
public class GrassBlock extends RenderBlock {

	private static final long serialVersionUID = 1L;
	private static Sprite grasSprite;
	private static float wind;
	private static float windWholeCircle;
	public static final float WINDAMPLITUDE = 30f;
	private final static Random randomGenerator = new java.util.Random();
	
	private final float seed;

	public static void initGrass(){
		grasSprite = new Sprite(getSprite('e', (byte) 7, (byte) 0));
		grasSprite.setOrigin(grasSprite.getWidth()/2f,0);
	}
	
	public GrassBlock(Block data) {
		super(data);
		seed = randomGenerator.nextFloat();
	}
	

	public static void updateWind(float dt) {
		windWholeCircle = (windWholeCircle + dt * 0.01f) % WINDAMPLITUDE;
		wind = Math.abs(windWholeCircle - WINDAMPLITUDE / 2)//value between 0 and amp/2
			-WINDAMPLITUDE / 2;//value between -amp/2 and + amp/2
	}

	@Override
	public void renderSide(GameView view, int xPos, int yPos, Side side, Color color, int ao) {
		super.renderSide(view, xPos, yPos, side, color, ao);
		Sprite gras = grasSprite;
		for (int i = 0; i < 10; i++) {
			int xOffset = (int) (Math.abs((xPos - seed*17) * i * (yPos)) % Block.VIEW_WIDTH - Block.VIEW_WIDTH2);
			int yOffset = (int) (Math.abs(((xPos - i) * 3 * (yPos * seed*11 - i))) % Block.VIEW_DEPTH - Block.VIEW_DEPTH2);
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
				gras.setRotation(wind+randomGenerator.nextFloat()*0.1f*WINDAMPLITUDE/2);
				gras.draw(view.getSpriteBatch());
			}
		}
	}

}
