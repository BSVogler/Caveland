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
package com.bombinggames.caveland.gameobjects.collectibles;

import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import java.util.ArrayList;

/**
 *A window which lets you add and take out items.
 * @author Benedikt Vogler
 */
public class CollectibleContainerWindow extends ActionBox {
	private final CollectibleContainer parent;

	CollectibleContainerWindow(CLGameView view, CollectibleContainer parent) {
		super("Choose construction", ActionBox.BoxModes.SELECTION, null);
		this.parent = parent;
		//make list of options
		ArrayList<SelectionOption> list = new ArrayList<>(parent.getContent().size());
		list.add(new SelectionOption((byte) 0, "Add"));
		if (parent.getContent().size() > 0) {
			list.add(new SelectionOption((byte) 1, "Take: " + parent.getContent().get(parent.getContent().size() - 1).getName()));
		} else {
			list.add(new SelectionOption((byte) 1, "Empty"));
		}
		addSelection(list);
	}

	@Override
	public SelectionOption confirm(AbstractEntity actor) {
		SelectionOption selection = super.confirm(actor);
		if (actor instanceof Ejira) {
			Ejira player = (Ejira) actor;
			//add item?
			if (selection.id == 0) {
				Collectible frontItem = player.getInventory().retrieveCollectible(0);
				if (frontItem != null) {
					parent.add(frontItem);
				}
			} else if (selection.id==1){
				//fetch item
				parent.retrieveCollectible(selection.id - 1);
			}
		}
		return selection;
	}
	
}
