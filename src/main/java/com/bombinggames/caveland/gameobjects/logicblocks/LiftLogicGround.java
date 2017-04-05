package com.bombinggames.caveland.gameobjects.logicblocks;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.gameobjects.ExitPortal;
import com.bombinggames.caveland.gameobjects.Interactable;
import com.bombinggames.caveland.gameobjects.LiftBasket;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import java.util.LinkedList;

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
	public LiftLogicGround(byte block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		//does not create a basket only connects
		LinkedList<LiftBasket> possibleBaskets = getPosition().getEntitiesNearbyHorizontal(RenderCell.GAME_EDGELENGTH, LiftBasket.class);
		if (!possibleBaskets.isEmpty()) {
			basket = possibleBaskets.getFirst();
		}

		//stop at ground
		if (basket != null && basket.getMovementDir() < 0 && basket.isOnGround() && getPosition().distanceToHorizontal(basket) < RenderCell.GAME_EDGELENGTH) {
			basket.setMovementDir(0);
		}

		if (basket != null) {
			requestedBasketAt = null;
			requestor = null;
		}

		//requested basket and fulfil request
		if (requestedBasketAt != null
			&& requestedBasketAt.isInMemoryAreaXYZ()
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
		int groundBlock = getPosition().cpy().goToNeighbour(5).add(0, 0, 1).getBlock();
		if ((groundBlock&255) != 0 && RenderCell.isObstacle(groundBlock)) {
			getPosition().cpy().goToNeighbour(5).add(0, 0, 1).destroy();
		}
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (basket != null && basket.shouldBeDisposed()) {
			basket = null;
		}
		if (basket != null) {
			//has basket, so toggle him
			toggleBasket(actor);
			
			return;
		}

		LinkedList<ExitPortal> possibleExitPortals = getPosition().getEntitiesNearbyHorizontal(RenderCell.GAME_EDGELENGTH * 2, ExitPortal.class);
		if (!possibleExitPortals.isEmpty()) {
			try {
				if (!possibleExitPortals.getFirst().getTarget().isInMemoryAreaXY()) {
					Controller.getMap().loadChunk(possibleExitPortals.getFirst().getTarget());
				}
				requestedBasketAt = possibleExitPortals.getFirst().getTarget();
				requestor = actor;
				return;
			} catch (NullPointerException ex) {

			}
		}

		WE.SOUND.play("interactionFail", getPosition());
	}

	private void toggleBasket(AbstractEntity actor) {
		//start
		if (basket.getMovementDir() == 0 && getPosition().distanceToHorizontal(basket) < RenderCell.GAME_EDGELENGTH) {
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
