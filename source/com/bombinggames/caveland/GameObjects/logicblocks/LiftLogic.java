package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.LiftBasket;
import com.bombinggames.caveland.GameObjects.MineCart;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogic extends AbstractBlockLogicExtension implements Interactable, Telegraph {

	private transient LiftBasket basket;

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
		Portal portal = getPortal();
		if (portal != null) {
			portal.setActive(false);
			
			ArrayList<MineCart> mineCarts = getPosition().getEntitiesInside(MineCart.class);
			if (!mineCarts.isEmpty()) {
				//make sure that there is an exitPortal
				Coordinate ground = portal.getExitPortal().getGround();

				//teleport minecarts to the ground
				mineCarts.forEach(cart -> {
					MessageManager.getInstance().dispatchMessage(
						this,
						cart,
						Events.teleport.getId(),
						ground.goToNeighbour(5).toPoint()
					);
					if (cart.getPassenger() != null) {
						cart.centerPassenger(true);
					}
					cart.setMovement(new Vector2(-1, 1));//little bit of movement
				});
			}
		}

		//spawn lift only if a lift is built
		if (portal != null) {
			Coordinate ground = portal.getExitPortal().getGround();
			if (basket == null) {
				basket = new LiftBasket();
			}
			if (!basket.hasPosition()) {
				basket.spawn(getPortal().getTarget().toPoint());
			}

			if (ground.getBlock() == null || ground.getBlock().getId() != CavelandBlocks.CLBlocks.LIFT_Ground.getId()) {
				ground.setBlock(Block.getInstance(CavelandBlocks.CLBlocks.LIFT_Ground.getId()));
			}
			
			
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (basket != null) {
			//start
			if (basket.getMovementDir() == 0 && getPosition().distanceToHorizontal(basket) < Block.GAME_EDGELENGTH) {
				basket.setMovementDir(-1);

				if (actor instanceof MovableEntity) {
					basket.setPassenger((MovableEntity) actor);
				}
			} else {
				//get back up
				if (basket.getMovementDir() <= 0) {
					basket.setMovementDir(1);
				}
			}
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public void dispose() {
		if (basket == null) {
			basket.dispose();
		}
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	/**
	 * get portal which leads inside
	 * @return 
	 */
	private Portal getPortal() {
		AbstractBlockLogicExtension holeLogic = getPosition().toCoord().add(0, 0, -1).getLogic();
		if (holeLogic != null && holeLogic instanceof CaveEntryBlockLogic) {
			return ((CaveEntryBlockLogic) holeLogic).getPortal();
		}
		return null;
	}
}
