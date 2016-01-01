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

import com.badlogic.gdx.ai.msg.Telegram;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * An entity which contains collectibles.
 *
 * @author Benedikt Vogler
 */
public class CollectibleContainer extends AbstractEntity {

	private static final long serialVersionUID = 2L;
	
	/**
	 * links to items
	 */
	private final ArrayList<Collectible> content = new ArrayList<>(3);
	private Object owner;
	/**
	 * experimental feature. may cause problems when leaving the game and the saving. todo
	 */
	private boolean releaseContentOnDestroy = false;

	/**
	 * using the default backpack sprite
	 * @param owner
	 */
	public CollectibleContainer(Object owner) {
		super((byte) 56);
		this.owner = owner;
		setName("Container");
		setIndestructible(true);
	}
	
	/**
	 * 
	 * @param id for custom sprite id
	 * @param owner
	 */
	public CollectibleContainer(byte id, Object owner ) {
		super(id);
		this.owner = owner;
		setName("Container");
		setIndestructible(true);
	}

	public Object getOwner() {
		return owner;
	}

	/**
	 *
	 * @param collectible 
	 * @return  
	 */
	public boolean add(Collectible collectible) {
		if (!collectible.hasPosition()) {
			collectible.spawn(getPosition().cpy());
		}
		collectible.setHidden(true);
		collectible.preventPickup();
		collectible.setFloating(true);
		collectible.setPosition(getPosition().cpy());
		content.add(collectible);
		return true;
	}
	
	/**
	 * only allows collectibles to be added.
	 *
	 * @param collectible if not an collectible nothing happens
	 * @return 
	 */
	public boolean addFront(Collectible collectible) {
		if (!collectible.hasPosition()) {
			collectible.spawn(getPosition().cpy());
		}
		collectible.setHidden(true);
		collectible.preventPickup();
		collectible.setFloating(true);
		collectible.setPosition(getPosition().cpy());
		content.add(0, collectible);
		return true;
	}

	/**
	 * Get the n't collectible from inventory.
	 *
	 * @param index
	 * @return can return null
	 */
	public Collectible get(int index) {
		if (content.size() > index) {
			return getCollectibles().get(index);
		} else {
			return null;
		}
	}
	
	/**
	 * Get the content of the container.
	 * @return 
	 */
	public ArrayList<Collectible> getCollectibles(){
		ArrayList<Collectible> col = new ArrayList<>(3);
		content.stream().forEach((AbstractEntity ent) -> {
				if (ent instanceof Collectible)
					col.add((Collectible) ent);
			}
		);
		return col;
	}

	/**
	 * amount of contained items
	 *
	 * @return
	 */
	public int size() {
		return content.size();
	}

	/**
	 * Makes the object appear in the world
	 *
	 * @param pos
	 * @return
	 */
	public Collectible retrieveCollectible(int pos) {
		if (content.size() > pos) {
			Collectible collectible = content.remove(pos);
			collectible.setFloating(false);
			collectible.allowPickup();
			collectible.setHidden(false);
			return collectible;
		} else return null;
	}

	/**
	 * Removes the object from the container/world and returns only the
	 * reference.
	 *
	 * @param pos
	 * @return
	 */
	public Collectible retrieveCollectibleReference(int pos) {
		Collectible collectible = retrieveCollectible(pos);
		collectible.disposeFromMap();
		return collectible;
	}

	/**
	 * Removes the first occurance of this type from the container. Makes the object appear in the world.
	 *
	 * @param def the definition of the object you are want to fetch.
	 * @return can return null if not found
	 * @see
	 * #retrieveCollectible(int)
	 */
	public Collectible retrieveCollectible(CollectibleType def) {
		Collectible collectible = getCollectible(def);
		if (collectible != null) {
			content.remove(collectible);
			collectible.setFloating(false);
			collectible.allowPickup();
			collectible.setHidden(false);
		}
		return collectible;
	}
	
	/**
	 * Removes the object from the container/world and returns only the
	 * reference.
	 * 
	 * @param def
	 * @return 
	 */
	public Collectible retrieveCollectibleReference(CollectibleType def) {
		Collectible collectible = getCollectible(def);
		if (collectible != null) {
			content.remove(collectible);
			collectible.disposeFromMap();
		}
		return collectible;
	}
	
	/**
	 * Does not alter the container. Looks for the definition and retrieves the first occurence.
	 * @param def
	 * @return 
	 */
	public Collectible getCollectible(CollectibleType def){
		Iterator<Collectible> iter = content.iterator();
		Collectible collectible = null;
		while (iter.hasNext()) {
			collectible = iter.next();
			if (collectible.getType().equals(def)) {
				break;
			}
		}
		return collectible;
	}
	
	/**
	 * how many of this type are in this container?
	 * @param def
	 * @return 
	 */
	public int count(CollectibleType def) {
		int counter = 0;
		for (AbstractEntity children : content) {
			if (children instanceof Collectible && ((Collectible) children).getType() == def)
				counter++;
		}
		return counter;
	}

	/**
	 * Updates the items in the slots.
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		super.update(dt);
		content.removeIf(
			(AbstractEntity item) -> item.shouldBeDisposed()
		);
		if (hasPosition()) {
			//put every child at the position if the container
			for (AbstractEntity item : content) {
				if (item != null) {
					item.setPosition(getPosition().cpy());
				}
			}
		}
	}

	/**
	 * Switches the order of the items.
	 *
	 * @param left true if left, false to right
	 */
	public void switchItems(boolean left) {
		if (!content.isEmpty()) {
			if (left) {
				content.add(content.remove(0));
			} else {
				content.add(0, content.remove(content.size() - 1));
			}
		}
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
		//show if no owner
		if (owner==null) {
			if (content==null || content.isEmpty()) {
				dispose();
			} else {
				setHidden(false);
				//keep children hidden
				for (AbstractEntity children : content) {
					children.setHidden(true);
				}
			}
		}
	}

	/**
	 * makes the content appear in the world if configured
	 */
	@Override
	public void disposeFromMap() {
		if (releaseContentOnDestroy) {
			//makes the content appear in the world
			for (int i = 0; i < size(); i++) {
				this.retrieveCollectible(0);
			}
		} else {
			for (Collectible col : content) {
				col.dispose();
			}
		}
		super.disposeFromMap();
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<Collectible> getContent() {
		return content;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return true;
	}
}
