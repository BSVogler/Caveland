package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import java.io.IOException;

/**
 *
 * @author Benedikt Vogler
 */
public class CompleteMap extends AbstractMap {
	private static int blocksX;
	private static int blocksY;
	private static int blocksZ;
	private final Block[][][] data;

	public CompleteMap(final String name) throws IOException {
		this(name, defaultGenerator);
	}

	
	public CompleteMap(final String name, Generator generator) throws IOException {
		super(name, generator);
		blocksX = 100;
        blocksY = 200;
        blocksZ =  10;
		data = new Block[blocksX][blocksY][blocksZ];
	}

	
	@Override
	public Block getBlock(Coordinate coord) {
		return data[coord.getX()][coord.getY()][coord.getZ()];
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		return data[blocksX/2+x][blocksY/2+y][z];
	}

	@Override
	public boolean save() {
		return false;
	}

	@Override
	public void setBlock(Block block) {
	}
	
	 /**
     * Get the data of the map
     * @return
     */
	public Block[][][] getData() {
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
	public void printCoords() {
	}

	@Override
	public void postUpdate(float dt) {
	}
	
}
