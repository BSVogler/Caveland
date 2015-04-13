package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class CoreData implements HasID {
	private static BlockFactory customBlockFactory;
	
		/**
	 * If you want to define custom id's >39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(BlockFactory customBlockFactory) {
		CoreData.customBlockFactory = customBlockFactory;
	}

	public static BlockFactory getFactory() {
		return customBlockFactory;
	}
	
	private byte id;
	private byte value;
	private byte health = 100;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera

	public CoreData(byte id) {
		this.id = id;
	}
	
	public CoreData(byte id, byte value) {
		this.id = id;
		this.value = value;
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
		return new RenderBlock(id, value);
	}

	@Override
	public boolean isObstacle() {
		//todo
		if (id>39 && customBlockFactory != null){
            return customBlockFactory.isObstacle(id, value);
         }
		if (id==9)
			return false;
		return id != 0;
	}

	@Override
	public boolean isTransparent() {
		//todo
		if (id>39 && customBlockFactory != null){
            return customBlockFactory.isTransparent(id, value);
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
		if (id>39 && customBlockFactory != null){
            return customBlockFactory.isLiquid(id, value);
        }
		if (id==9)
			return true;
		return false;
	}
	
    /**
     * Is the block a true block with sides or represents it another thing like a flower?
     * @return 
     */
	public boolean hasSides() {
		if (id==34 ||id==35 || id==36) return false;
		if (id>39 && customBlockFactory != null){
            return customBlockFactory.hasSides(id, value);
        }
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
			case 10:
				return "???";
			case 11:
				return "???";
			case 12:
				return "???";
			case 13:
				return "???";
			case 14:
			return "???";
			case 15:
				return "???";
			case 16:
				return "???";				
			case 34:
				return "flower";
			case 35:
				return "bush";
			case 36:
				return "tree";
								
			default:
				if (id > 39) {
                    if (customBlockFactory!=null){
                        return customBlockFactory.getName(id, value);
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}
}
