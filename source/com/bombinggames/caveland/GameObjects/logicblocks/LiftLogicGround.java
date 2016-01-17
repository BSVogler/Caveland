package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.ExitPortal;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.LiftBasket;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
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
public class LiftLogicGround extends AbstractBlockLogicExtension implements Interactable, Telegraph {

	private transient LiftBasket basket;
	private Coordinate requestedBasketAt;
	private AbstractEntity requestor;

	/**
	 *
	 * @param block
	 * @param coord
	 */
	public LiftLogicGround(Block block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		//does not create a basket only connects
		ArrayList<LiftBasket> possibleBaskets = getPosition().getEntitiesNearbyHorizontal(Block.GAME_EDGELENGTH, LiftBasket.class);
		if (!possibleBaskets.isEmpty()) {
			basket = possibleBaskets.get(0);
		}

		//stop at ground
		if (basket != null && basket.getMovementDir() < 0 && basket.isOnGround() && getPosition().distanceToHorizontal(basket) < Block.GAME_EDGELENGTH) {
			basket.setMovementDir(0);
		}

		if (basket != null) {
			requestedBasketAt = null;
			requestor = null;
		}

		//requested basket and fulfil request
		if (requestedBasketAt != null
			&& requestedBasketAt.isInMemoryArea()
			&& requestedBasketAt.getLogic() instanceof LiftLogic) {
			//request basket
			if (basket == null) {
				basket = new LiftBasket();
			}
			if (!basket.hasPosition()) {
				basket.spawn(requestedBasketAt.toPoint());
			}
			toggleBasket(requestor);
			return;
		}

		//clear entry
		Block groundBlock = getPosition().cpy().goToNeighbour(5).add(0, 0, 1).getBlock();
		if (groundBlock != null && groundBlock.isObstacle()) {
			getPosition().cpy().goToNeighbour(5).add(0, 0, 1).destroy();
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (basket.shouldBeDisposed()) {
			basket = null;
		}
		if (basket != null) {
			//has basket, so toggle him
			toggleBasket(actor);
			return;
		}

		ArrayList<ExitPortal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(Block.GAME_EDGELENGTH * 2, ExitPortal.class);
		if (!possibleExitPortals.isEmpty()) {
			try {
				if (!possibleExitPortals.get(0).getTarget().isInMemoryAreaHorizontal()) {
					Controller.getMap().loadChunk(possibleExitPortals.get(0).getTarget());
				}
				requestedBasketAt = possibleExitPortals.get(0).getTarget();
				requestor = actor;
				return;
			} catch (NullPointerException ex) {

			}
		}

		WE.SOUND.play("interactionFail", getPosition());
	}

	private void toggleBasket(AbstractEntity actor) {
		//start
		if (basket.getMovementDir() == 0 && getPosition().distanceToHorizontal(basket) < Block.GAME_EDGELENGTH) {
			basket.setMovementDir(1);

			if (actor instanceof MovableEntity) {
				basket.setPassenger((MovableEntity) actor);
			}
		} else//get back down
		if (basket.getMovementDir() >= 0) {
			basket.setMovementDir(-1);
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
