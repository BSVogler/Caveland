/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class PowerStationLogic extends AbstractBlockLogicExtension {

	/**
	 *
	 *
	 * @param block
	 * @param coord
	 */
	public PowerStationLogic(Block block, Coordinate coord) {
		super(block, coord);
	}

	@Override
	public void update(float dt) {
		//power surrounding cables
		Block neighBlock = getPosition().cpy().goToNeighbour(1).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 0
		) {
			((CableLogic) getPosition().cpy().goToNeighbour(1).getLogic()).turnOn();
		}

		neighBlock = getPosition().cpy().goToNeighbour(3).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 2
		) {
			((CableLogic) getPosition().cpy().goToNeighbour(3).getLogic()).turnOn();
		}

		neighBlock = getPosition().cpy().goToNeighbour(5).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 0
		) {
			((CableLogic) getPosition().cpy().goToNeighbour(5).getLogic()).turnOn();
		}

		neighBlock = getPosition().cpy().goToNeighbour(7).getBlock();
		if (neighBlock != null
			&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& neighBlock.getValue() == 2
		) {
			((CableLogic) getPosition().cpy().goToNeighbour(7).getLogic()).turnOn();
		}
	}

	@Override
	public void dispose() {
	}

}