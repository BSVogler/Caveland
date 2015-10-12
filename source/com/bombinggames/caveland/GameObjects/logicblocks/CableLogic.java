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
	private boolean turnedOn;
	
	/**
	 * 
	 *
	 * @param block
	 * @param coord
	 */
	public CableLogic(Block block, Coordinate coord) {
		super(block, coord);
		if (block.getValue() % 2 == 1)
			block.setValue((byte) (block.getValue() - 1));
	}

	@Override
	public void update(float dt) {
		if (!turnedOn) {
			//if on, turn off
			if (getPosition().getBlock().getValue() % 2 == 1)
				getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue() - 1));

			//power surrounding cables
			Block neighBlock = getPosition().cpy().goToNeighbour(1).getBlock();
			if (
				neighBlock != null
				&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
				&& neighBlock.getValue() == 1
			) {
				getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue() + 1));
			} else {

				neighBlock = getPosition().cpy().goToNeighbour(3).getBlock();
				if (neighBlock != null
					&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
					&& neighBlock.getValue() == 3
				) {
					getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue() + 1));
				} else {
					
					neighBlock = getPosition().cpy().goToNeighbour(5).getBlock();
					if (neighBlock != null
						&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
						&& neighBlock.getValue() == 1
					) {
						getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue() + 1));
					} else {
						
						neighBlock = getPosition().cpy().goToNeighbour(7).getBlock();
						if (neighBlock != null
							&& neighBlock.getId() == CavelandBlocks.CLBlocks.POWERCABLE.getId()
							&& neighBlock.getValue() == 3
						) {
							getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue() + 1));
						}
					}
				}
			}
		}
		turnedOn = false;
	}
	
	/**
	 * if turned on this frame
	 */
	public void turnOn(){
		turnedOn = true;
		if (getPosition().getBlock().getValue() % 2 ==0)
			getPosition().getBlock().setValue((byte) (getPosition().getBlock().getValue()+1));
	}
	
	@Override
	public void dispose() {
	}
}
