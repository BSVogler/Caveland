package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *A block which stores only id and value and is only used for storing in memory. Stores only 8 byte. Want to reduce it to 4 byte.
 * @author Benedikt Vogler
 */
public class StorageBlock implements HasID {
	private int id;
	private int value;
	private float lightlevel;


	public StorageBlock(int id) {
		this.id = id;
	}
	
	public StorageBlock(int id, int value) {
		this.id = id;
		this.value = value;
	}
	
	@Override
	public int getId() {
		return id;
	}

	@Override
	public int getValue() {
		return value;
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		return RenderBlock.getInstance(id, value);
	}

	public boolean isObstacle() {
		//todo
		if (id==0) return false;
		return true;
	}

	@Override
	public boolean isTransparent() {
		//todo
		return false;
	}

	public boolean isLiquid() {
		//todo
		return false;
	}

	public boolean hasSides() {
		//todo
		return true;
	}

	@Override
	public float getLightlevel() {
		return lightlevel;
	}

	@Override
	public void setLightlevel(float lightlevel) {
		this.lightlevel = lightlevel;
	}
}
