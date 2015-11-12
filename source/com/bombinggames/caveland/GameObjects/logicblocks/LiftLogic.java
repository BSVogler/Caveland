package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.math.Vector2;
import com.bombinggames.caveland.Game.CLGameView;
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
		AbstractBlockLogicExtension holeLogic = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (holeLogic != null && holeLogic instanceof PortalBlockLogic) {
			ArrayList<MineCart> mineCarts = getPosition().getEntitiesInside(MineCart.class);
			if (!mineCarts.isEmpty()) {
				//make sure that there is an exitPortal
				Coordinate ground = ((PortalBlockLogic) holeLogic).getPortal().getExitPortal().getGround();

				//teleport minecarts to the ground
				mineCarts.forEach(cart -> {
					cart.setPosition(ground.addVector(0, 1, 0));
					cart.setMovement(new Vector2(-1, 1));//little bit of movement
				});
			}
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		AbstractBlockLogicExtension hole = getPosition().toCoord().addVector(0, 0, -1).getLogic();
		if (hole != null && hole instanceof PortalBlockLogic) {
			((PortalBlockLogic) hole).getPortal().teleport(actor);
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
}
