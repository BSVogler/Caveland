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
    public boolean shouldLoadMap() {
        return true;
    }
    
    

    @Override
    public int getWorldSpinAngle() {
        return 50;
    }

	@Override
	public BlockFactory getBlockFactoy() {
		return blockfactory;
	}
    
    @Override
    public int getRenderResolutionWidth() {
		return 1920;
    }

    @Override
    public boolean useLightEngine() {
        return false;
    }

	@Override
	public boolean shouldAutoShade() {
		return true;
	}


    @Override
    public boolean isChunkSwitchAllowed() {
        return true;
    }

    @Override
    public boolean debugObjects() {
        return false;
    }

    @Override
    public boolean useFog() {
        return true;
    }

	@Override
	public String getSpritesheetPath() {
		return "com/BombingGames/Caveland/Spritesheet";
	}

	@Override
	public int groundBlockID() {
		return 1;
	}

	
    

}

