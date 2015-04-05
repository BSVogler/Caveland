package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *A small object which stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class StorageBlock implements HasID {
	private int identifier;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera


	public StorageBlock(int id) {
		this.identifier = id;
	}
	
	public StorageBlock(int id, int value) {
		this.identifier = (value<<8)+id;
	}
	
	@Override
	public int getId() {
		return identifier%256;
	}

	@Override
	public int getValue() {
		return (identifier>>8)%256;
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		return RenderBlock.getInstance(identifier, getValue());
	}

	public boolean isObstacle() {
		//todo
		if (getId()==0) return false;
		return true;
	}

	@Override
	public boolean isTransparent() {
		//todo
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	public boolean isLiquid() {
		if (getId()==9) return true;
		return false;
	}
	
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
     * @return 
     */
	public boolean hasSides() {
		int id = getId();
		if (id==34 ||id==35 || id==36) return false;
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
	
	public static boolean hasSides(int id, int value){
		return new StorageBlock(id, value).hasSides();
	}

	void setValue(int value) {
		this.identifier = (value<<8)+getValue();
	}
}
