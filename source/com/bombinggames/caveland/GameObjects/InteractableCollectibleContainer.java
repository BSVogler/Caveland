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
package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import java.util.ArrayList;

/**
 *An object which contains collectibles
 * @author Benedikt Vogler
 */
public class InteractableCollectibleContainer extends AbstractEntity implements Interactable {
	private static final long serialVersionUID = 1L;

	public InteractableCollectibleContainer() {
		super((byte) 0);
		setHidden(true);
	}
	
	/**
	 * Stores the objects as children. Hides the collectible.
	 * @param collectible 
	 */
	public void addCollectible(Collectible collectible){
		collectible.setHidden(true);
		addChild(collectible);
	}

	/**
	 * only allows collectibles to be added
	 * @param child 
	 */
	@Override
	public void addChild(AbstractEntity child) {
		if (child instanceof Collectible)
			super.addChild(child);
	}
	
	public Collectible retrieveCollectible(int pos){
		Collectible collectible = (Collectible) getChildren().remove(pos);
		collectible.setHidden(false);
		return collectible;
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer) {
			SelectionWindow selectionWindow = new SelectionWindow(view, this);
			selectionWindow.register(view, ((CustomPlayer) actor).getPlayerNumber());
		}
	}

	
	private class SelectionWindow extends ActionBox{
		private final InteractableCollectibleContainer parent;

		SelectionWindow(CustomGameView view, InteractableCollectibleContainer parent) {
			super(view, "Choose construction", ActionBox.BoxModes.SELECTION, null);
			this.parent = parent;
			//make list of options
			ArrayList<String> list = new ArrayList<>(parent.getChildren().size());
			list.add("Add");
			for (Object collectible : parent.getChildren()) {
				list.add("Take out "+((Collectible) collectible).getName());
			}
			addSelectionNames(list);
		}

		@Override
		public int confirm(CustomGameView view, AbstractEntity actor) {
			int num = super.confirm(view, actor);
			if (actor instanceof CustomPlayer) {
				CustomPlayer player = (CustomPlayer) actor;
				//add item?
				if (num==0) {
					Collectible frontItem = player.getInventory().fetchFrontItemAndDisposeFromWorld();
					if (frontItem != null)
						parent.addCollectible(frontItem);
				} else {
					//fetch item
					Collectible fetch = parent.retrieveCollectible(num-1);
				}
			}
			return num;
		}
	}
	
}
