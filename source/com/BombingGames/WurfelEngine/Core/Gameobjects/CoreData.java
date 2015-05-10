package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import java.io.Serializable;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class CoreData implements HasID, Serializable {
	private static CustomBlocks customBlocks;
	private static final long serialVersionUID = 1L;
	
		/**
	 * If you want to define custom id's &gt;39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(CustomBlocks customBlockFactory) {
		CoreData.customBlocks = customBlockFactory;
	}

	public static CustomBlocks getFactory() {
		return customBlocks;
	}
	
	private byte id;
	private byte value;
	private byte health = 100;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera

	private CoreData(byte id) {
		this.id = id;
	}
	
	private CoreData(byte id, byte value) {
		this.id = id;
		this.value = value;
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @return 
	 */
	public static CoreData getInstance(byte id){
		if (id==0) return null;
		return new CoreData(id, (byte) 0);
	}
	
	/**
	 * Use for creating new objects.
	 * @param id
	 * @param value
	 * @return 
	 */
	public static CoreData getInstance(byte id, byte value){
		if (id==0) return null;
		return new CoreData(id, value);
	}
	
	@Override
	public byte getId() {
		return id;
	}

	@Override
	public byte getValue() {
		return value;
	}
	
	public void setValue(byte value) {
		this.value = value;
	}
	
	/**
	 * value between 0-100
	 * @param coord
	 * @param health 
	 */
	public void setHealth(Coordinate coord, byte health){
		this.health = health;
		if (customBlocks != null){
			customBlocks.setHealth(coord, health, id, value);
        }
		if (health <= 0) {
			//make an invalid air instance (should be null)
			this.id = 0;
			this.value = 0;
		}
	}
	
	/**
	 * value between 0-100. This method should only be used for non-bocks.
	 * @param health 
	 */
	public void setHealth(byte health){
		this.health = health;
	}
	
	public byte getHealth(){
		return health;
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		if (id==0)
			return null;
		if (id==9)
			return new Sea(id);
		if (id>9 && customBlocks != null){
            return customBlocks.toRenderBlock(id, value);
        }
		return new RenderBlock(id, value);
	}

	@Override
	public boolean isObstacle() {
		if (id>9 && customBlocks != null){
            return customBlocks.isObstacle(id, value);
        }
		if (id==9)
			return false;
		return id != 0;
	}

	@Override
	public boolean isTransparent() {
		if (id>9 && customBlocks != null){
            return customBlocks.isTransparent(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	public boolean isLiquid() {
		if (id>9 && customBlocks != null){
            return customBlocks.isLiquid(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
	@Override
	public float getLightlevel() {
		return lightlevel;
	}

	@Override
	public void setLightlevel(float lightlevel) {
		this.lightlevel = lightlevel;
	}
	
	/**
	 * get the name of a combination of id and value
	 * @return 
	 */
	@Override
	public String getName(){
		switch (id) {
			case 0:
				return "air";
			case 1:
				return "grass";
			case 2:
				return "dirt";
			case 3:
				return "???";
			case 4:
				return "???";
			case 5:
				return "???";
			case 6:
				return "???";
			case 7:
				return "???";
			case 8:
				return "sand";
			case 9:
				return "water";								
			default:
				if (id > 9) {
                    if (customBlocks!=null){
                        return customBlocks.getName(id, value);
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}

	/**
	 * Creates a new instance of {@link RenderBlock} to check if it has sides. You should prefer the hasSides call to a {@link RenderBlock} object.
	 * @return true if it has sides
	 * @see RenderBlock#hasSides() 
	 */
	public boolean hasSides() {
		RenderBlock block = toBlock();
		if (block==null) return false;
		return block.hasSides();
	}
}
