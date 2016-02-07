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
package com.bombinggames.caveland.gameobjects.logicblocks;

import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks.CLBlocks;
import com.bombinggames.caveland.game.ChunkGenerator;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.Interactable;
import com.bombinggames.caveland.gameobjects.Portal;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.ArrayList;

/**
 * Entry to the caves. Spawns a portal inside the block.
 *
 * @author Benedikt Vogler
 */
public class CaveEntryBlockLogic extends AbstractBlockLogicExtension implements Interactable {

	private static final long serialVersionUID = 2L;
	private Portal portal = null;

	/**
	 * teleports to level 1 by default
	 *
	 * @param block
	 * @param coord
	 */
	public CaveEntryBlockLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (getPosition() != null && isValid()) {
			//check if portal is inside the block
			if (portal == null) {
				ArrayList<Portal> portals = getPosition().getEntitiesInside(Portal.class);
				if (!portals.isEmpty()) {
					portal = portals.get(0);
				} else {
					portal = new Portal();

					//set target to new cave
					int portalnumber = ChunkGenerator.getCaveNumber(getPosition());
					if (portalnumber < 0) {//outside
						portal.setTarget(ChunkGenerator.getCaveUp(0));//every cave entry outside points to first cave
					} else {
						portal.setTarget(ChunkGenerator.getCaveUp(portalnumber + 1));
					}
				}
			}

			//keep portal there
			if (portal.shouldBeDisposed() || !portal.hasPosition()) {//respawn if needed
				portal.spawn(getPosition().toPoint());
			} else {
				portal.setPosition(getPosition().toPoint());//force at position
			}

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
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox("Construct a lift construction site", ActionBox.BoxModes.BOOLEAN, "Create construction site for lift?\n You can always enter the caves by jumping into the hole.")
				.setConfirmAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
					Coordinate top = getPosition().cpy().add(0, 0, 1);
					top.setBlock(CLBlocks.CONSTRUCTIONSITE.getInstance());
					ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(top);
					constructionSiteLogic.setResult(CLBlocks.LIFT.getId());
					WE.SOUND.play("metallic", actor.getPosition());
				})
				.register(view, ((Ejira) actor).getPlayerNumber(), actor, getPosition());
		}
	}

	@Override
	public boolean interactable() {
		if (getPosition().cpy().add(0, 0, 1).getBlock() == null) {
			return true;
		}
		return getPosition().cpy().add(0, 0, 1).getBlock().getId() == 0;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}

	@Override
	public void dispose() {
		portal.dispose();
	}

}
