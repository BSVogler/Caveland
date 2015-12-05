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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 * Instantly creates an object without spawning a construction site.
 *
 * @author Benedikt Vogler
 */
public class InstantConstructionKit extends Collectible implements Interactable {

	private static final long serialVersionUID = 1L;
	private transient EntityBlock preview;
	private final byte resultBlockId;
	private int amountLeft = 3;
	private byte lastChoice;

	/**
	 *
	 * @param type supports only some types
	 */
	public InstantConstructionKit(CollectibleType type) {
		super(type);
		if (type == CollectibleType.Rails) {
			resultBlockId = CavelandBlocks.CLBlocks.RAILS.getId();
		} else {
			resultBlockId = CavelandBlocks.CLBlocks.POWERCABLE.getId();
		}
	}
	
	public void setOrder(byte lastChoice, ActionBox ab, boolean up){
		if (up) {
			switch (lastChoice){
				case 0: case 5:case 6:
					ab.addSelection(
						new ActionBox.SelectionOption((byte) 0, "Straight SW-NE"),
						new ActionBox.SelectionOption((byte) 2, "Curved SW-SE"),
						new ActionBox.SelectionOption((byte) 3, "Curved SW-NW"),
						new ActionBox.SelectionOption((byte) 6, "up SW-NE"),
						new ActionBox.SelectionOption((byte) 1, "Straight NW-SE"),
						new ActionBox.SelectionOption((byte) 4, "Curved NW-NE"),
						new ActionBox.SelectionOption((byte) 5, "Curved SE-NE"),
						new ActionBox.SelectionOption((byte) 7, "up SE-NW"),
						new ActionBox.SelectionOption((byte) 8, "up NE-SW"),
						new ActionBox.SelectionOption((byte) 9, "up NW-SE")
					);
					break;
				case 1: case 3: case 7: 
					ab.addSelection(
						new ActionBox.SelectionOption((byte) 1, "Straight NW-SE"),
						new ActionBox.SelectionOption((byte) 2, "Curved SW-SE"),
						new ActionBox.SelectionOption((byte) 5, "Curved SE-NE"),
						new ActionBox.SelectionOption((byte) 7, "up SE-NW"),
						new ActionBox.SelectionOption((byte) 0, "Straight SW-NE"),
						new ActionBox.SelectionOption((byte) 3, "Curved SW-NW"),
						new ActionBox.SelectionOption((byte) 6, "up SW-NE"),
						new ActionBox.SelectionOption((byte) 4, "Curved NW-NE"),
						new ActionBox.SelectionOption((byte) 8, "up NE-SW"),
						new ActionBox.SelectionOption((byte) 9, "up NW-SE")
					);
					break;
				default:
					ab.addSelection(
						new ActionBox.SelectionOption((byte) 0, "Straight SW-NE"),
						new ActionBox.SelectionOption((byte) 1, "Straight NW-SE"),
						new ActionBox.SelectionOption((byte) 2, "Curved SW-SE"),
						new ActionBox.SelectionOption((byte) 3, "Curved SW-NW"),
						new ActionBox.SelectionOption((byte) 4, "Curved NW-NE"),
						new ActionBox.SelectionOption((byte) 5, "Curved SE-NE"),
						new ActionBox.SelectionOption((byte) 6, "up SW-NE"),
						new ActionBox.SelectionOption((byte) 7, "up SE-NW"),
						new ActionBox.SelectionOption((byte) 8, "up NE-SW"),
						new ActionBox.SelectionOption((byte) 9, "up NW-SE")
					);
			}
		}
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			ActionBox box = new ActionBox("Choose direction", ActionBox.BoxModes.SELECTION, null);
			setOrder(lastChoice, box, true);
			box.setConfirmAction((byte result, AbstractEntity actor1) -> {
				lastChoice = result;
				amountLeft--;
				//spawn rails
				actor1.getPosition().toCoord().setBlock(
					Block.getInstance(resultBlockId, result)
				);
				WE.SOUND.play("metallic");
				if (preview != null) {
					preview.dispose();
					preview = null;
				}

				if (actor instanceof MovableEntity) {
					Point beforeMove = actor1.getPosition().cpy();
					Point nextCell = actor1.getPosition().toCoord().toPoint();
					Vector2 orient = ((MovableEntity) actor1).getOrientation();
					if ((result == 0 || result == 8)  && (orient.x >= 0 && orient.y <= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(1).toPoint();
					} else if ((result == 0 || result == 6) && (orient.x <= 0 && orient.y >= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(5).toPoint();
					} else if ((result == 1 || result == 9)  && (orient.x <= 0 && orient.y <= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(7).toPoint();
					} else if ((result == 1 || result == 7) && (orient.x >= 0 && orient.y >= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(3).toPoint();
					} else if (result == 2 && orient.x >= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(3).toPoint();
					} else if (result == 2 && orient.x <= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(5).toPoint();
					} else if (result == 3 && orient.y <= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(7).toPoint();
					} else if (result == 3 && orient.y >= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(5).toPoint();
					} else if (result == 4 && orient.x <= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(7).toPoint();
					}else if (result == 4 && orient.x >= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(1).toPoint();
					}else if (result == 5 && orient.y <= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(1).toPoint();
					}else if (result == 5 && orient.y >= 0) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(3).toPoint();
					} else if (result == 6 && (orient.x >= 0 && orient.y <= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(1).addVector(0, 0, 1).toPoint();
					} else if (result == 7 && (orient.x <= 0 && orient.y <= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(7).addVector(0, 0, 1).toPoint();
					} else if (result == 8 && (orient.x <= 0 && orient.y >= 0)) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(5).addVector(0, 0, 1).toPoint();
					}

					actor.getPosition().setValues(nextCell);
					//update orientation
					Vector3 newOr = actor1.getPosition().cpy().sub(beforeMove).nor();
					((MovableEntity) actor1).setOrientation(
						new Vector2(newOr.x, newOr.y)
					);

				}

				if (amountLeft <= 0) {
					dispose();//dispose tool kit
				}
			}
			)
			.setSelectAction((boolean up, byte result, AbstractEntity actor1) -> {
				//spawn rails
				if (preview == null) {
					preview = (EntityBlock) new EntityBlock(resultBlockId)
						.spawn(actor1.getPosition().toCoord().toPoint());
					preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
				}
				preview.setSpriteValue((byte) (getType() == CollectibleType.Rails ? result : result * 2));
			})
			.setCancelAction(
				(int result, AbstractEntity actor1) -> {
					if (preview != null) {
						preview.dispose();
						preview = null;
					}
				}
			);
			box.register(view, ((Ejira) actor).getPlayerNumber(), actor, this);
		}
	}

	@Override
	public boolean interactable() {
		return getPosition().getBlock()==null;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return true;
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
