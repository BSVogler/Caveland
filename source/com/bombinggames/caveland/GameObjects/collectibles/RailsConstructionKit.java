/*
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
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
package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityBlock;

/**
 *
 * @author Benedikt Vogler
 */
public class RailsConstructionKit extends Collectible {

	private static final long serialVersionUID = 1L;
	private EntityBlock preview;
	
	public RailsConstructionKit() {
		super(CollectibleType.Rails);

	}

	@Override
	public void action(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox(view, "Choose rails type", ActionBox.BoxModes.SELECTION, null)
				.addSelectionNames("Straight SW-NE", "Straight NW-SE", "Curved", "Curved", "Curved", "Curved", "up", "up", "up", "up")
				.setConfirmAction(
					(int result, CustomGameView view1, AbstractEntity actor1) -> {
						//spawn rails
						actor1.getPosition().toCoord().setBlock(Block.getInstance((byte) 55, (byte) result));
						WE.getEngineView().getSoundEngine().play("metallic");
						if (preview != null) {
							preview.dispose();
							preview = null;
						}
						dispose();//dispose tool kit
					}
				)
				.setSelectAction(
					(int result, CustomGameView view1, AbstractEntity actor1) -> {
						//spawn rails
						if (preview == null) {
							preview = (EntityBlock) new EntityBlock((byte) 55,(byte) result)
								.spawn(actor1.getPosition().toCoord().toPoint());
							preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
						} else preview.setValue((byte) result);
					}
				)
				.setCancelAction(
					(int result, CustomGameView view1, AbstractEntity actor1) -> {
						if (preview != null) {
							preview.dispose();
							preview = null;
						}
					})
				.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}
}
