package com.bombinggames.caveland.gameobjects.logicblocks;

/**
 *
 * @author Benedikt Vogler
 */
public class PowerStationLogic extends AbstractPowerBlock {

	@Override
	protected boolean hasPowerPropagate() {
		super.hasPowerPropagate();
		return isValid();
	}

	@Override
	public boolean hasPower() {
		return isValid();
	}

	@Override
	public boolean outgoingConnection(int id) {
		return true;
	}

}
