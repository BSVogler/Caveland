package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogic extends AbstractBlockLogicExtension implements Interactable {
	private static final long serialVersionUID = 1L;
	
	public LiftLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		ArrayList<MineCart> nearbyLoren = getPosition().getEntitiesNearby(2, MineCart.class);
		AbstractBlockLogicExtension hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && (hole instanceof PortalBlock))
			nearbyLoren.forEach(
				(l) -> {
					l.setPosition(((PortalBlock) hole).getPortal().getTarget());
				}
			);
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		AbstractBlockLogicExtension hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && (hole instanceof PortalBlock))
			actor.setPosition(((PortalBlock) hole).getPortal().getTarget().cpy());
	}

	@Override
	public boolean interactable() {
		return true;
	}
	
}
