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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.ActionBox.SelectionOption;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.Interactable;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.EntityBlock;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.map.Point;

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
	private byte lastDir;

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
	
	/**
	 * 
	 * @param ab the action box with the choices
	 * @param toSide the last direction used
	 */
	public void setOrder(ActionBox ab, int toSide){
		byte m = (byte) (resultBlockId == CavelandBlocks.CLBlocks.POWERCABLE.getId() ?2:1);
		switch (toSide) {
			case 1:
				ab.addSelection(
					new ActionBox.SelectionOption((byte) (m*0), "Straight SW-NE"),
					new ActionBox.SelectionOption((byte) (m*2), "Curved SW-SE"),
					new ActionBox.SelectionOption((byte) (m*3), "Curved SW-NW"),
					new ActionBox.SelectionOption((byte) (m*6), "up SW-NE"),
					new ActionBox.SelectionOption((byte) (m*1), "Straight NW-SE"),
					new ActionBox.SelectionOption((byte) (m*4), "Curved NW-NE"),
					new ActionBox.SelectionOption((byte) (m*5), "Curved SE-NE"),
					new ActionBox.SelectionOption((byte) (m*7), "up SE-NW"),
					new ActionBox.SelectionOption((byte) (m*8), "up NE-SW"),
					new ActionBox.SelectionOption((byte) (m*9), "up NW-SE")
				);	break;
			case 5:
				ab.addSelection(
					new ActionBox.SelectionOption((byte) (m*0), "Straight SW-NE"),
					new ActionBox.SelectionOption((byte) (m*4), "Curved NW-NE"),
					new ActionBox.SelectionOption((byte) (m*5), "Curved SE-NE"),
					new ActionBox.SelectionOption((byte) (m*8), "up NE-SW"),
					new ActionBox.SelectionOption((byte) (m*6), "up SW-NE"),
					new ActionBox.SelectionOption((byte) (m*1), "Straight NW-SE"),
					new ActionBox.SelectionOption((byte) (m*3), "Curved SW-NW"),
					new ActionBox.SelectionOption((byte) (m*2), "Curved SW-SE"),
					new ActionBox.SelectionOption((byte) (m*7), "up SE-NW"),
					new ActionBox.SelectionOption((byte) (m*9), "up NW-SE")
				);	break;
			case 7:
				ab.addSelection(
					new ActionBox.SelectionOption((byte) (m*1), "Straight NW-SE"),
					new ActionBox.SelectionOption((byte) (m*2), "Curved SW-SE"),
					new ActionBox.SelectionOption((byte) (m*5), "Curved SE-NE"),
					new ActionBox.SelectionOption((byte) (m*7), "up SE-NW"),
					new ActionBox.SelectionOption((byte) (m*0), "Straight SW-NE"),
					new ActionBox.SelectionOption((byte) (m*3), "Curved SW-NW"),
					new ActionBox.SelectionOption((byte) (m*6), "up SW-NE"),
					new ActionBox.SelectionOption((byte) (m*4), "Curved NW-NE"),
					new ActionBox.SelectionOption((byte) (m*8), "up NE-SW"),
					new ActionBox.SelectionOption((byte) (m*9), "up NW-SE")
				);	break;
			case 3:
				ab.addSelection(
					new ActionBox.SelectionOption((byte) (m*1), "Straight NW-SE"),
					new ActionBox.SelectionOption((byte) (m*3), "Curved SW-NW"),
					new ActionBox.SelectionOption((byte) (m*4), "Curved NW-NE"),
					new ActionBox.SelectionOption((byte) (m*9), "up NW-SE"),
					new ActionBox.SelectionOption((byte) (m*7), "up SE-NW"),
					new ActionBox.SelectionOption((byte) (m*0), "Straight SW-NE"),
					new ActionBox.SelectionOption((byte) (m*5), "Curved SE-NE"),
					new ActionBox.SelectionOption((byte) (m*8), "up NE-SW"),
					new ActionBox.SelectionOption((byte) (m*6), "up SW-NE"),
					new ActionBox.SelectionOption((byte) (m*2), "Curved SW-SE")
				);	break;
			default:
				ab.addSelection(
					new ActionBox.SelectionOption((byte) (m*0), "Straight SW-NE"),
					new ActionBox.SelectionOption((byte) (m*1), "Straight NW-SE"),
					new ActionBox.SelectionOption((byte) (m*2), "Curved SW-SE"),
					new ActionBox.SelectionOption((byte) (m*3), "Curved SW-NW"),
					new ActionBox.SelectionOption((byte) (m*4), "Curved NW-NE"),
					new ActionBox.SelectionOption((byte) (m*5), "Curved SE-NE"),
					new ActionBox.SelectionOption((byte) (m*6), "up SW-NE"),
					new ActionBox.SelectionOption((byte) (m*7), "up SE-NW"),
					new ActionBox.SelectionOption((byte) (m*8), "up NE-SW"),
					new ActionBox.SelectionOption((byte) (m*9), "up NW-SE")
				);	break;
		}
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			ActionBox box = new ActionBox("Choose direction", ActionBox.BoxModes.SELECTION, null);
			setOrder(box, lastDir);
			box.setConfirmAction((SelectionOption result, AbstractEntity actor1) -> {
				amountLeft--;
				//spawn rails
				actor1.getPosition().toCoord().setBlock(
					resultBlockId,
					result.id
				);
				WE.SOUND.play("metallic");
				if (preview != null) {
					preview.dispose();
					preview = null;
				}
				
				byte resultNum = 0;
				if (resultBlockId == CavelandBlocks.CLBlocks.POWERCABLE.getId()) {
					resultNum = (byte) (result.id / 2);
				} else {
					resultNum = result.id;
				}

				if (actor instanceof MovableEntity) {
					Point beforeMove = actor1.getPosition().cpy();
					Point nextCell;
					boolean up = false;
					Vector2 orient = ((MovableEntity) actor1).getOrientation();
					if ((resultNum == 0 || resultNum == 8) && (orient.x >= 0 && orient.y <= 0)) {
						lastDir = 1;
					} else if ((resultNum == 0 || resultNum == 6) && (orient.x <= 0 && orient.y >= 0)) {
						lastDir = 5;
					} else if ((resultNum == 1 || resultNum == 9) && (orient.x <= 0 && orient.y <= 0)) {
						lastDir = 7;
					} else if ((resultNum == 1 || resultNum == 7) && (orient.x >= 0 && orient.y >= 0)) {
						lastDir = 3;
					} else if (resultNum == 2 && orient.x >= 0) {
						lastDir = 3;
					} else if (resultNum == 2 && orient.x <= 0) {
						lastDir = 5;
					} else if (resultNum == 3 && orient.y <= 0) {
						lastDir = 7;
					} else if (resultNum == 3 && orient.y >= 0) {
						lastDir = 5;
					} else if (resultNum == 4 && orient.x <= 0) {
						lastDir = 7;
					} else if (resultNum == 4 && orient.x >= 0) {
						lastDir = 1;
					} else if (resultNum == 5 && orient.y <= 0) {
						lastDir = 1;
					} else if (resultNum == 5 && orient.y >= 0) {
						lastDir = 3;
					} else if (resultNum == 6 && (orient.x >= 0 && orient.y <= 0)) {
						lastDir = 1;
						up = true;
					} else if (resultNum == 7 && (orient.x <= 0 && orient.y <= 0)) {
						lastDir = 7;
						up = true;
					} else if (resultNum == 8 && (orient.x <= 0 && orient.y >= 0)) {
						lastDir = 5;
						up = true;
					} else if (resultNum == 9 && (orient.x >= 0 && orient.y >= 0)) {
						lastDir = 3;
						up = true;
					}
					
					if (up) {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(lastDir).add(0, 0, 1).toPoint();
					} else {
						nextCell = actor1.getPosition().toCoord().goToNeighbour(lastDir).toPoint();
					}
					
					actor.getPosition().set(nextCell);
					//update orientation
					Vector3 newOr = actor1.getPosition().cpy().sub(beforeMove).nor();
					((MovableEntity) actor1).setOrientation(
						new Vector2(newOr.x, newOr.y)
					);

				}

				if (amountLeft <= 0) {
					dispose();//dispose tool kit
				}
			})
			.setSelectAction((boolean up, SelectionOption result, AbstractEntity actor1) -> {
				//spawn rails
				if (preview == null) {
					preview = (EntityBlock) new EntityBlock(resultBlockId)
						.spawn(actor1.getPosition().toCoord().toPoint());
					preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
				}
				preview.setSpriteValue(result.id);
			})
			.setCancelAction(
				(SelectionOption result, AbstractEntity actor1) -> {
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
		return getPosition().getBlock()==0;
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
