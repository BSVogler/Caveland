package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

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
	 *
	 * @param block
	 * @param coord
	 */
	public AbstractPowerBlock(Block block, Coordinate coord) {
		super(block, coord);
	}

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
	 * @return
	 */
	protected boolean hasPowerPropagate() {
		//stop recursion to avoid circles
		if (lastUpdateFrame == WE.getGameplay().getFrameNum()) {
			return power;
		}

		lastUpdateFrame = WE.getGameplay().getFrameNum();
		power = false;

		//neighbor nodes which are without power
		AbstractPowerBlock a = null;
		AbstractPowerBlock b = null;
		AbstractPowerBlock c = null;
		AbstractPowerBlock d = null;

		//system has changed so propagate changes
		AbstractPowerBlock powerBlock = getPowerNode(1);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				a = powerBlock;
			}
		}

		powerBlock = getPowerNode(3);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				b = powerBlock;
			}
		}

		powerBlock = getPowerNode(5);
		if (powerBlock != null) {
			if (powerBlock.hasPowerPropagate()) {
				power = true;
			} else {
				c = powerBlock;
			}
		}

		powerBlock = getPowerNode(7);
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

		//got power not push power to neighbor nodes without power
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
	 * returns if the blokc has power but only reads this.
	 * @return 
	 */
	public boolean hasPower() {
		return power;
	}

	public void pushPower() {
		power = true;
		AbstractPowerBlock powerBlock = getPowerNode(1);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getPowerNode(3);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getPowerNode(5);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}

		powerBlock = getPowerNode(7);
		if (powerBlock != null) {
			if (!powerBlock.hasPower()) {
				powerBlock.pushPower();
			}
		}
	}

	/**
	 *
	 * @param id
	 * @return
	 */
	public AbstractPowerBlock getPowerNode(int id) {
		AbstractBlockLogicExtension logic = getPosition().cpy().goToNeighbour(id).getLogic();
		if (logic instanceof AbstractPowerBlock) {
			return (AbstractPowerBlock) logic;
		} else {
			return null;
		}
	}

	@Override
	public void dispose() {
		power = false;
		hasPowerPropagate();
	}

}
