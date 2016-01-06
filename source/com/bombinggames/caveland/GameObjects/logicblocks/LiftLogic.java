package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogic extends AbstractBlockLogicExtension implements Interactable, Telegraph {
	private static final long serialVersionUID = 1L;
	
	/**
	 *
	 * @param block
	 * @param coord
	 */
	public LiftLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		//teleport minecarts
		AbstractBlockLogicExtension holeLogic = getPosition().toCoord().add(0, 0, -1).getLogic();
		if (holeLogic != null && holeLogic instanceof CaveEntryBlockLogic) {
			ArrayList<MineCart> mineCarts = getPosition().getEntitiesInside(MineCart.class);
			if (!mineCarts.isEmpty()) {
				//make sure that there is an exitPortal
				Coordinate ground = ((CaveEntryBlockLogic) holeLogic).getPortal().getExitPortal().getGround();

				//teleport minecarts to the ground
				mineCarts.forEach(cart -> {
					MessageManager.getInstance().dispatchMessage(
						this,
						cart,
						Events.teleport.getId(),
						ground.goToNeighbour(5).toPoint()
					);
					if (cart.getPassenger() != null){
						cart.centerPassenger(true);
					}
					cart.setMovement(new Vector2(-1, 1));//little bit of movement
				});
			}
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		AbstractBlockLogicExtension hole = getPosition().toCoord().add(0, 0, -1).getLogic();
		if (hole != null && hole instanceof CaveEntryBlockLogic) {
			((CaveEntryBlockLogic) hole).getPortal().teleport(actor);
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

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}
