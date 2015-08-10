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

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionSite extends CollectibleContainer implements Interactable  {
	private static final long serialVersionUID = 1L;
	private final byte result;
	private final CollectibleType[] neededItems;
	private final int[] neededAmount;
	private final byte resultValue;

	/**
	 * the resulting block
	 * @param resultId 
	 * @param resultValue 
	 */
	public ConstructionSite(byte resultId, byte resultValue) {
		super();
		setHidden(true);
		this.result = resultId;
		this.resultValue = resultValue;
		//if (result==11) {
			neededAmount = new int[]{2,1};
			neededItems = new CollectibleType[]{CollectibleType.Stone, CollectibleType.Wood };
		//}
	}
	
	public String getStatusString(){
		String string = "";
		for (int i = 0; i < neededItems.length; i++) {
			string += count(neededItems[i])+"/"+ neededAmount[i] +" "+neededItems[i] + ", ";
		}
		return string;
	}
	
	/**
	 * transforms the construction site into the wanted building
	 * @return true if success
	 */
	public boolean build(){
		//check ingredients
		for (int i = 0; i < neededItems.length; i++) {
			if (count(neededItems[i]) < neededAmount[i])
				return false;
		}
		getPosition().toCoord().setBlock(Block.getInstance(result, resultValue));
		Controller.getSoundEngine().play("construct");
		dispose();
		return true;
	}
	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer) {
			ConstructionSiteWindow selectionWindow = new ConstructionSiteWindow(view, actor, this);
			selectionWindow.register(view, ((CustomPlayer) actor).getPlayerNumber());
		}
	}
	
	public boolean canAddFrontItem(AbstractEntity actor){
		if (!(actor instanceof CustomPlayer))
			return false;
		Collectible frontItem = ((CustomPlayer) actor).getInventory().getFrontCollectible();
		boolean found = false;
		if (frontItem != null) {
			for (CollectibleType type : neededItems) {
				if (frontItem.getType() == type)
					found = true;
			}
		}
		return found;
	}
	
	private class ConstructionSiteWindow extends ActionBox {
		private final CollectibleContainer parent;

		public ConstructionSiteWindow(CustomGameView view, AbstractEntity actor, ConstructionSite parent) {
			super(view, "Build id: "+parent.result , ActionBox.BoxModes.SELECTION, null);
			this.parent = parent;
			//make list of options
			ArrayList<String> list = new ArrayList<>(parent.getChildren().size());
			if (actor instanceof CustomPlayer) {
				if (canAddFrontItem(actor))
					list.add("Add: " + ((CustomPlayer)actor).getInventory().getFrontCollectible().getName());
				else
					list.add("Add: You have nothing to add");
			} else
				list.add("Add");
			
			if (parent.getChildren().size() > 0)
				list.add("Take: " + parent.getChildren().get(parent.getChildren().size()-1).getName());
			else list.add("Empty");
			list.add("Build: "+ parent.getStatusString());
			addSelectionNames(list);
		}

		@Override
		public int confirm(CustomGameView view, AbstractEntity actor) {
			int num = super.confirm(view, actor);
			if (actor instanceof CustomPlayer) {
				CustomPlayer player = (CustomPlayer) actor;
				//add item?
				if (num == 0) {
					if (canAddFrontItem(actor)) {
						Collectible frontItem = player.getInventory().retrieveFrontItemReference();
						if (frontItem != null) {
							parent.addCollectible(frontItem);
						}
					}
				} else if (num==1){
					//fetch item
					parent.retrieveCollectible(num - 1);
				} else if (num==2) build();
			}
			return num;
		}
	}
}
