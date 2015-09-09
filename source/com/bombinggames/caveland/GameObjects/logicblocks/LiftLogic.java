package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

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
		AbstractBlockLogicExtension holeLogic = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (holeLogic != null && holeLogic instanceof PortalBlockLogic) {
			getPosition().getEntitiesInside(MineCart.class)
				.forEach(l -> {
					l.setPosition(((PortalBlockLogic) holeLogic).getPortal().getTarget());
				});
		}
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		AbstractBlockLogicExtension hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && hole instanceof PortalBlockLogic) {
			actor.setPosition(((PortalBlockLogic) hole).getPortal().getTarget().cpy());
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}
	
}
