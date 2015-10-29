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
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleContainer;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 * The logic for a construciton site
 * @author Benedikt Vogler
 */
public class ConstructionSite extends AbstractBlockLogicExtension implements Interactable  {
	
	private static final long serialVersionUID = 1L;
	private CollectibleContainer container;
	private CollectibleType[] neededItems;
	private int[] neededAmount;
	private byte result = -1;
	private byte resultValue;

	/**
	 * the resulting block
	 *
	 * @param block the block where this logic is performed
	 * @param coord
	 */
	public ConstructionSite(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 * The result if you finish the construction.
	 *
	 * @param result
	 * @param resultValue
	 */
	public void setResult(byte result, byte resultValue) {
		setResult(result);
		this.resultValue = resultValue;
	}

	private void restoreResultFromValue() {
		//set value for saving
		if (getPosition().getBlock().getValue() == 0) {
			setResult(CavelandBlocks.CLBlocks.OVEN.getId());
		} else if (getPosition().getBlock().getValue() == 0) {
			setResult(CavelandBlocks.CLBlocks.POWERSTATION.getId());
		} else if (result == CavelandBlocks.CLBlocks.LIFT.getId()) {
			setResult(CavelandBlocks.CLBlocks.LIFT.getId());
		} else {
			setResult(CavelandBlocks.CLBlocks.POWERSTATION.getId());
		}
	}
	
	/**
	 * The result if you finish the construction. Value is set to 0
	 *
	 * @param result
	 */
	public void setResult(byte result) {
		this.result = result;
		this.resultValue = 0;
		if (CavelandBlocks.CLBlocks.OVEN.getId() == result) {
			neededAmount = new int[]{2, 1};
			neededItems = new CollectibleType[]{CollectibleType.Stone, CollectibleType.Wood};
		} else {
			neededAmount = new int[]{2, 1};
			neededItems = new CollectibleType[]{CollectibleType.Iron, CollectibleType.Wood};
		}
		//set value for saving
		if (result == CavelandBlocks.CLBlocks.OVEN.getId()) {
			getPosition().getBlock().setValue((byte) 0);
		} else if (result == CavelandBlocks.CLBlocks.POWERSTATION.getId()) {
			getPosition().getBlock().setValue((byte) 1);
		} else if (result == CavelandBlocks.CLBlocks.LIFT.getId()) {
			getPosition().getBlock().setValue((byte) 2);
		}
	}

	/**
	 * A string shows what is there and what is needed for construction.
	 *
	 * @return
	 */
	public String getStatusString() {
		String string = "";
		for (int i = 0; i < neededItems.length; i++) {
			string += container.count(neededItems[i]) + "/" + neededAmount[i] + " " + neededItems[i] + ", ";
		}
		return string;
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			ConstructionSiteWindow selectionWindow = new ConstructionSiteWindow(view, actor, this);
			selectionWindow.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}
	
	public boolean canAddFrontItem(AbstractEntity actor) {
		if (!(actor instanceof Ejira)) {
			return false;
		}
		Collectible frontItem = ((Ejira) actor).getInventory().getFrontCollectible();
		boolean found = false;
		if (frontItem != null) {
			for (CollectibleType type : neededItems) {
				if (frontItem.getType() == type) {
					found = true;
				}
			}
		}
		return found;
	}
	
	/**
	 * transforms the construction site into the wanted building
	 *
	 * @return true if success
	 */
	public boolean build() {
		if (!canBuild()) {
			return false;
		}

		getPosition().toCoord().setBlock(Block.getInstance(result, resultValue));
		container.dispose();
		WE.SOUND.play("construct");
		return true;
	}
	
	/**
	 * if the block can be transformed
	 *
	 * @return
	 */
	public boolean canBuild() {
		//check ingredients
		for (int i = 0; i < neededItems.length; i++) {
			if (container.count(neededItems[i]) < neededAmount[i]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public void update(float dt) {
		if (result==-1) {
			restoreResultFromValue();
		}
		
		if (isValid()) {
			//find existing container
			ArrayList<AbstractEntity> list = getPosition().getEntitiesInside(CollectibleContainer.class);
			if (!list.isEmpty()) {
				container = (CollectibleContainer) list.get(0);
			}

			//respawn container if needed
			if (container == null || container.shouldBeDisposed()) {
				container = (CollectibleContainer) new CollectibleContainer((byte) 0).spawn(getPosition().toPoint());
			}
		}
	}

	@Override
	public void dispose() {
	}

	private class ConstructionSiteWindow extends ActionBox {

		private final ConstructionSite parent;

		ConstructionSiteWindow(CLGameView view, AbstractEntity actor, ConstructionSite parent) {
			super("Build id: " + CavelandBlocks.CLBlocks.valueOf(parent.result).toString(), ActionBox.BoxModes.SELECTION, null);
			this.parent = parent;
			//make list of options
			ArrayList<String> list = new ArrayList<>(parent.container.getContent().size());
			if (actor instanceof Ejira) {
				if (canAddFrontItem(actor)) {
					list.add("Add: " + ((Ejira) actor).getInventory().getFrontCollectible().getName());
				} else {
					list.add("Add: You have nothing to add");
				}
			} else {
				list.add("Add");
			}

			if (parent.container.getContent().size() > 0) {
				list.add("Take: " + parent.container.getContent().get(parent.container.getContent().size() - 1).getName());
			} else {
				list.add("Empty");
			}
			list.add("Build: " + parent.getStatusString());
			addSelectionNames(list);
		}

		@Override
		public int confirm(AbstractEntity actor) {
			int num = super.confirm(actor);
			if (actor instanceof Ejira) {
				Ejira player = (Ejira) actor;
				//add item?
				switch (num) {
					case 0:
						if (canAddFrontItem(actor)) {
							Collectible frontItem = player.getInventory().retrieveFrontItemReference();
							if (frontItem != null) {
								parent.container.addCollectible(frontItem);
							}
						}	break;
					case 1:
						//fetch item
						parent.container.retrieveCollectible(num - 1);
						break;
					case 2:
						build();
						break;
					default:
				}
			}
			return num;
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
	
}
