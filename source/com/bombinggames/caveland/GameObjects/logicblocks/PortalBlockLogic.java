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
package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CavelandBlocks.CLBlocks;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class PortalBlockLogic extends AbstractBlockLogicExtension implements Interactable {

	private static final long serialVersionUID = 2L;
	private Portal portal = null;

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 *
	 * @param block
	 * @param coord
	 */
	public PortalBlockLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		if (getPosition() != null && isValid()) {
			//check if portal is there
			if (portal == null) {
				ArrayList<AbstractEntity> portals = getPosition().getEntitiesInside(Portal.class);
				if (!portals.isEmpty()) {
					portal = (Portal) portals.get(0);
				} else {
					portal = new Portal();
				}
				if (!portal.isSpawned()) {
					portal.spawn(getPosition().toPoint());
				}
			}
			portal.setPosition(getPosition().toPoint());

			if (!interactable()) {
				//inactive for falling into it
				getPosition().getBlock().setValue((byte) 1);
				portal.setActive(false);
			} else {
				//active for falling into it
				getPosition().getBlock().setValue((byte) 0);
				portal.setActive(true);
			}
		}
	}

	/**
	 * Get the portal inside the block
	 *
	 * @return
	 */
	public Portal getPortal() {
		return portal;
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox("Construct a lift construction site", ActionBox.BoxModes.BOOLEAN, "Create construction site for lift? You can always enter the caves by jumping into the hole.")
				.setConfirmAction((int result, AbstractEntity actor1) -> {
					Coordinate top = getPosition().cpy().addVector(0, 0, 1);
					top.setBlock(CLBlocks.CONSTRUCTIONSITE.getInstance());
					ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(top);
					constructionSiteLogic.setResult(CLBlocks.LIFT.getId());
					WE.SOUND.play("metallic");
				})
				.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}

	@Override
	public boolean interactable() {
		if (getPosition().cpy().addVector(0, 0, 1).getBlock() == null) {
			return true;
		}
		return getPosition().cpy().addVector(0, 0, 1).getBlock().getId() == 0;
	}

	@Override
	public void dispose() {
	}
}
