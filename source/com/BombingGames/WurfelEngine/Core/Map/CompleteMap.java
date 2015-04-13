package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import java.io.File;
import java.io.IOException;

/**
 *A map where every available data is stored in one big array. No streaming.
 * @author Benedikt Vogler
 */
public class CompleteMap extends AbstractMap {
	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;
	private final CoreData[][][] data;

	public CompleteMap(final File name, int saveSlot) throws IOException {
		this(name, getDefaultGenerator(), saveSlot);
	}

	
	public CompleteMap(final File name, Generator generator, int saveSlot) throws IOException {
		super(name, generator, saveSlot);
		blocksX = 100;
        blocksY = 200;
        blocksZ =  10;
		data = new CoreData[blocksX][blocksY][blocksZ];
	}

	
	@Override
	public CoreData getBlock(Coordinate coord) {
		if (coord.getZ() < 0)
			return getGroundBlock();
		else
			return data[coord.getX()][coord.getY()][coord.getZ()];
	}

	@Override
	public CoreData getBlock(int x, int y, int z) {
		return data[blocksX/2+x][blocksY/2+y][z];
	}

	@Override
	public boolean save(int saveSlot) {
		return false;
	}

	@Override
	public void setBlock(RenderBlock block) {
	}
	
	 /**
     * Get the data of the map
     * @return
     */
	public CoreData[][][] getData() {
		return data;
	}

	@Override
	public void destroyBlockOnCoord(Coordinate coord) {
	}

	/**
	 * Returns the amount of Blocks inside the map in x-direction.
	 * @return
	 */
	@Override
	public int getBlocksX() {
		return blocksX;
	}

	/**
	 * Returns the amount of Blocks inside the map in y-direction.
	 * @return
	 */
	@Override
	public int getBlocksY() {
		return blocksY;
	}

	/**
	 * Returns the amount of Blocks inside the map in z-direction.
	 * @return
	 */
	@Override
	public int getBlocksZ() {
		return blocksZ;
	}

	@Override
	public void print() {
	}

	@Override
	public void postUpdate(float dt) {
	}

	@Override
	public AbstractMap clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
