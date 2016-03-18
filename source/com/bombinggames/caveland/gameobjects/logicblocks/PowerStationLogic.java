/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.gameobjects.logicblocks;

import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class PowerStationLogic extends AbstractPowerBlock {

	/**
	 *
	 *
	 * @param block
	 * @param coord
	 */
	public PowerStationLogic(byte block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
	}

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
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean outgoingConnection(int id) {
		return true;
	}

}
