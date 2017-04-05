package com.bombinggames.caveland.gameobjects.logicblocks;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 * A block which can popagate power thrugh the map
 *
 * @author Benedikt Vogler
 */
public abstract class AbstractPowerBlock extends AbstractBlockLogicExtension {

	private long lastUpdateFrame;
	private boolean power;
	private boolean initalized = false;

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (!initalized) {
			initalized = true;
			hasPowerPropagate();
		}
	}

	/**
	 *
	 * @return true if has power
	 */
	protected boolean hasPowerPropagate() {
		//stop recursion to avoid circles
		if (lastUpdateFrame == WE.getGameplay().getFrameNum()) {
			return power;
		}

		lastUpdateFrame = WE.getGameplay().getFrameNum();
		power = false;//set back to off

		//neighbor nodes which are without power
		AbstractPowerBlock a = null;
		AbstractPowerBlock b = null;
		AbstractPowerBlock c = null;
		AbstractPowerBlock d = null;

		//system has changed so propagate changes
		AbstractPowerBlock powerBlock = getConnectedNodes(1);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				a = powerBlock;
			}
		}

		powerBlock = getConnectedNodes(3);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				b = powerBlock;
			}
		}

		powerBlock = getConnectedNodes(5);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				c = powerBlock;
			}
		}

		powerBlock = getConnectedNodes(7);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				d = powerBlock;
			}
		}

		//if destroyed stop pushing the power
		if (!isValid()) {
			power = false;
		}

		//if got power not push power to neighbor nodes without power
		if (power) {
			if (a != null) {
				a.pushPower();
			}
			if (b != null) {
				b.pushPower();
			}
			if (c != null) {
				c.pushPower();
			}
			if (d != null) {
				d.pushPower();
			}
		}

		return power;
	}

	/**
	 * Returns if has power
	 * @return true if has power
	 */
	public boolean hasPower() {
		return power;
	}

	/**
	 * recursive power pushing method. Every connected objects gets power.
	 */
	public void pushPower() {
		power = true;//give power
		
		//pass power
		AbstractPowerBlock powerBlock = getConnectedNodes(1);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getConnectedNodes(3);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getConnectedNodes(5);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getConnectedNodes(7);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}
	}

	/**
	 * Returns conncted nodes to this block
	 * @param id neighbor id
	 * @return
	 * @see Coordinate#goToNeighbour(int) 
	 */
	public AbstractPowerBlock getConnectedNodes(int id) {
		Coordinate neigh = getPosition().cpy().goToNeighbour(id);
		//load chunk if out of bounds
		if (!neigh.isInMemoryAreaXY()) {
			Controller.getMap().loadChunk(neigh);
		}

		//return connected block
		AbstractBlockLogicExtension logic = neigh.getLogic();
		if (outgoingConnection(id)
			&& logic instanceof AbstractPowerBlock
			&& ((AbstractPowerBlock) logic).outgoingConnection((id + 4) % 8)//opposite side
			) {
			return (AbstractPowerBlock) logic;
		} else {
			return null;
		}
	}
	
	/**
	 * Has this block an cable connection to a neighbor?
	 * @param id the neighbor id
	 * @return true if has conncetion
	 */
	public abstract boolean outgoingConnection(int id);
	
	@Override
	public void dispose() {
		power = false;
		hasPowerPropagate();
	}

}
