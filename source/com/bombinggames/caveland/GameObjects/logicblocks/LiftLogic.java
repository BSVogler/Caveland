/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractLogicBlock;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogic extends AbstractLogicBlock implements Interactable {
	private static final long serialVersionUID = 1L;
	
	public LiftLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		ArrayList<MineCart> nearbyLoren = getPosition().getEntitiesNearby(2, MineCart.class);
		AbstractLogicBlock hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && (hole instanceof PortalBlock))
			nearbyLoren.forEach(
				(l) -> {
					l.setPosition(((PortalBlock) hole).getTarget());
				}
			);
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
	}
	
}
