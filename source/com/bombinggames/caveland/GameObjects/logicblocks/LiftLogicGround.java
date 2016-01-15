package com.bombinggames.caveland.GameObjects.logicblocks;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.Interactable;
import com.bombinggames.caveland.GameObjects.LiftBasket;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class LiftLogicGround extends AbstractBlockLogicExtension implements Interactable, Telegraph {
	
	private transient LiftBasket basket;

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
		
		//clear entry
		Block groundBlock = getPosition().cpy().goToNeighbour(5).add(0, 0, 1).getBlock();
		if (groundBlock != null && groundBlock.isObstacle()) {
			getPosition().cpy().goToNeighbour(5).add(0, 0, 1).destroy();
		}
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (basket!=null)
			basket.setMovementDir(1);
	}
	
	@Override
	public boolean interactable() {
		return basket!=null;
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
	
	private Portal getPortal() {
		AbstractBlockLogicExtension holeLogic = getPosition().toCoord().add(0, 0, -1).getLogic();
		if (holeLogic != null && holeLogic instanceof CaveEntryBlockLogic) {
			return ((CaveEntryBlockLogic) holeLogic).getPortal();
		}
		return null;
	}
}
