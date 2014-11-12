package com.BombingGames.Caveland;

import com.BombingGames.Caveland.Game.CustomBlockFactory;
import com.BombingGames.WurfelEngine.Core.Configuration;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockFactory;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomConfiguration extends Configuration {
	private CustomBlockFactory blockfactory = new CustomBlockFactory();
	

	@Override
	public BlockFactory getBlockFactoy() {
		return blockfactory;
	}
    
	@Override
	public String getSpritesheetPath() {
		return "com/BombingGames/Caveland/Spritesheet";
	}
}

