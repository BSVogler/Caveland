package com.bombinggames.caveland.gameobjects.logicblocks;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class BoosterLogic extends AbstractBlockLogicExtension {

	private boolean power = false;
	private PointLightSource light;

	/**
	 *
	 *
	 * @param id
	 * @param coord
	 */
	public BoosterLogic(byte id, Coordinate coord) {
		super(id, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		if (light == null) {
			light = new PointLightSource(new Color(0.8f, 0.0f, 0.3f, 1f), 1, 12, WE.getGameplay().getView());
			light.setSaveToDisk(false);
			light.disable();
			light.spawn(getPosition().toPoint().add(0, 0, Block.GAME_EDGELENGTH2 * 0.5f));
		}
		//power surrounding cables
		power = false;
		int neighBlock = getPosition().cpy().goToNeighbour(1).getBlock();
		if ((byte) (neighBlock&255) == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& (byte) ((neighBlock>>8)&255) == 1
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(3).getBlock();
		if ((byte) (neighBlock&255) == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& ((neighBlock>>8)&255) == 3
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(5).getBlock();
		if ((byte) (neighBlock&255) == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& ((neighBlock>>8)&255) == 1
		) {
			power = true;
		}

		neighBlock = getPosition().cpy().goToNeighbour(7).getBlock();
		if ((byte) (neighBlock&255) == CavelandBlocks.CLBlocks.POWERCABLE.getId()
			&& ((neighBlock>>8)&255) == 3
		) {
			power = true;
		}

		if (power) {
			light.enable();
		} else {
			light.disable();
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isEnabled() {
		return power;
	}

	@Override
	public void dispose() {
		light.dispose();
	}
}
