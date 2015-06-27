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

import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import java.io.IOException;
import java.util.Iterator;

/**
 *An entity which contains collectibles.
 * @author Benedikt Vogler
 */
public class CollectibleContainer extends AbstractEntity{
	private static final long serialVersionUID = 2L;
	private boolean backpack;

	public CollectibleContainer() {
		super((byte) 53);//use sprite of construction box temp
		setName("Container");
		setHidden(true);
		setIndestructible(true);
	}

	public void setBackpack(boolean backpack) {
		this.backpack = backpack;
	}
	
	/**
	 * only allows collectibles to be added 
	 * @param collectible
	 */
	@Override
	public void addChild(AbstractEntity collectible) {
		if (collectible instanceof Collectible){
			collectible.setHidden(true);
			collectible.setPosition(getPosition().cpy());
			collectible.setHidden(true);
			((Collectible) collectible).preventPickup();
			((Collectible) collectible).setFloating(true);
			super.addChild(collectible);
		}
	}
	
	/**
	 * Get the n't element from inventory. in future should skip stuff like the shadow.
	 * @param index
	 * @return 
	 */
	public Collectible get(int index){
		if (getChildren().size() > index)
			return (Collectible) getChildren().get(index);
		else return null;
	}
	
	/**
	 * amount of contained items
	 * @return 
	 */
	public int size(){
		return getChildren().size();
	}
	
	/**
	 * Makes the object appear in the world
	 * @param pos
	 * @return 
	 */
	public Collectible retrieveCollectible(int pos){
		Collectible collectible = (Collectible) getChildren().remove(pos);
		collectible.setFloating(false);
		collectible.allowPickup();
		collectible.setHidden(false);
		return collectible;
	}
	
	/**
	 * Removes the object from the container/world and returns only the reference
	 * @param pos
	 * @return 
	 */
	public Collectible retrieveCollectibleReference(int pos){
		Collectible collectible = retrieveCollectible(pos);
		collectible.disposeFromMap();
		return collectible;
	}
	
	/**
	 * Removes the first occurance of this tipes from the container.
	 * @param def
	 * @return can return null
	 * @see #getCollectible(com.bombinggames.caveland.GameObjects.Collectible.CollectibleType) 
	 */
	public Collectible fetchCollectible(Collectible.CollectibleType def){
		Iterator<AbstractEntity> iter = getChildren().iterator();
		Collectible collectible = null;
		while (iter.hasNext()) {
			collectible = (Collectible) iter.next();
			if (collectible.getType().equals(def))
				break;
		}
		if (collectible!=null) {
			getChildren().remove(collectible);
			collectible.setFloating(false);
			collectible.allowPickup();
			collectible.setHidden(false);
		}
		return collectible;
	}

	/**
	 * Updates the items in the slots.
	 * @param dt 
	 */
	@Override
	public void update(float dt){
		super.update(dt);
		//put every child at the position if the container
		for (AbstractEntity item : getChildren()) {
			if (item != null){
				item.setPosition(getPosition().cpy());
			}
		}
		getChildren().removeIf((AbstractEntity item) -> item.shouldBeDisposed());
	}
	
	/**
	 * Switches the order of the items.
	 * @param left true if left, false to right
	 */
	public void switchItems(boolean left){
		if (!getChildren().isEmpty())
			if (left)
				getChildren().add(getChildren().remove(0));
			else
				getChildren().add(0, getChildren().remove(getChildren().size()-1));
	}
	
	/**
	 * overrides deserialisation
	 *
	 * @param stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		//show if loaded a backpack
		if (backpack) {
			if(getChildren().isEmpty())
				dispose();
			else {
				setHidden(false);
				//keep children hidden
				for (AbstractEntity children : getChildren()) {
					children.setHidden(true);
				}
			}
		}
	}
}