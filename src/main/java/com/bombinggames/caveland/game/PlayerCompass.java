/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
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
package com.bombinggames.caveland.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;

/**
 * SHows the direction of the coop player.
 * @author Benedikt Vogler
 */
public class PlayerCompass {
	
	/**
	 * 
	 * @param player player where to point
	 * @param view rendering view
	 * @param camera overlay for this camera
	 */
	public void drawHUD(MovableEntity player, GameView view, Camera camera){
		if (player != null && player.hasPosition()) {
			Vector2 cent = new Vector2(
				camera.getScreenPosX() + camera.getWidthInScreenSpc() / 2f,//center
				camera.getScreenPosY() + camera.getHeightInScreenSpc() / 2f//center
			);
			Vector2 to = new Vector2(
				player.getPosition().getProjectionSpaceX(view, camera),
				player.getPosition().getProjectionSpaceY(view, camera)
			);
			Vector2 vecTo = to.cpy().sub(cent);
			if (vecTo.len2() > 200000) {
				cent.add(vecTo.nor().scl(camera.getWidthInScreenSpc()/2*0.9f));//300px in direction of player
				Sprite bgSprite = new Sprite(AbstractGameObject.getSprite('i', (byte) 11, (byte) 1));
				bgSprite.setScale(player.getHealth()/100f);
				bgSprite.setPosition(cent.x, cent.y);
				bgSprite.draw(view.getProjectionSpaceSpriteBatch());
				Sprite fgSprite = new Sprite(AbstractGameObject.getSprite('i', (byte) 11, (byte) 0));
				fgSprite.setPosition(cent.x, cent.y);
				fgSprite.draw(view.getProjectionSpaceSpriteBatch());
			}
		}
	}
}
