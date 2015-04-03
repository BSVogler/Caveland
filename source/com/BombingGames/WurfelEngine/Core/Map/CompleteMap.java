package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import java.io.IOException;

/**
 *
 * @author Benedikt Vogler
 */
public class CompleteMap extends AbstractMap{
	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;
	private final Block data[][][];

	public CompleteMap(final String name, Generator generator) throws IOException {
		super(name, generator);
		data = new Block[blocksX][blocksY][blocksZ];
	}

	
	@Override
	public Block getBlock(Coordinate coord) {
		return data[coord.getX()][coord.getY()][coord.getZ()];
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return data[x][y][z];
	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public void setData(Block block) {
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
	public void printCoords() {
	}

	@Override
	public void postUpdate(float dt) {
	}
	
}
