package com.bombinggames.caveland.GameObjects.logicblocks;

import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CableLogic extends AbstractBlockLogicExtension {
	private boolean powerGiven;
	
	/**
	 * 
	 *
	 * @param block
	 * @param coord
	 */
	public CableLogic(Block block, Coordinate coord) {
		super(block, coord);
		//turn off
		block.setValue((byte) (block.getValue() - block.getValue() % 2));
	}

	@Override
	public void update(float dt) {
		if (!powerGiven) {
			turnOff();
		}
		powerGiven = false;
	}
	
	private boolean hasPower(){
		return getPosition().getBlock().getValue() % 2 == 1;
	}
	/**
	 * if turned on this frame
	 * @param sourcedir where did the power came from?
	 */
	public void turnOn(int sourcedir){
		powerGiven = true;
		if (!hasPower())//is off
			getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue()+1));
		powerNeighbor(sourcedir);
	}
	
	private void turnOff(){
		Block lblock = getPosition().getBlock();
		lblock.setValue((byte) (lblock.getValue() - lblock.getValue() % 2));
	}
	
	/**
	 * 
	 * @param ignore 
	 */
	private void powerNeighbor(int ignore){
		if (hasPower()) {
			//power surrounding cables
			Block neighBlock = getPosition().cpy().goToNeighbour(1).getBlock();
			if (
				ignore != 1
				&& neighBlock != null
				&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
				&& (neighBlock.getValue() == 0 || neighBlock.getValue() == 1)
			) {
				((CableLogic) getPosition().cpy().goToNeighbour(1).getLogic()).turnOn(5);
			} else {

				neighBlock = getPosition().cpy().goToNeighbour(3).getBlock();
				if (ignore != 3
					&& neighBlock != null
					&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
					&& (neighBlock.getValue() == 2 || neighBlock.getValue() == 3)
				) {
					((CableLogic) getPosition().cpy().goToNeighbour(3).getLogic()).turnOn(7);
				} else {
					
					neighBlock = getPosition().cpy().goToNeighbour(5).getBlock();
					if (
						ignore != 5
						&& neighBlock != null
						&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
						&& (neighBlock.getValue() == 0 || neighBlock.getValue() == 1)
					) {
						((CableLogic) getPosition().cpy().goToNeighbour(5).getLogic()).turnOn(1);
					} else {
						
						neighBlock = getPosition().cpy().goToNeighbour(7).getBlock();
						if (ignore != 7
							&& neighBlock != null
							&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
							&& (neighBlock.getValue() == 2 || neighBlock.getValue() == 3)
						) {
							((CableLogic) getPosition().cpy().goToNeighbour(7).getLogic()).turnOn(3);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void dispose() {
	}
}
