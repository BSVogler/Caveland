package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *A small block object hich stores only id and value and is only used for storing in memory. Stores only 8 byte.
 * @author Benedikt Vogler
 */
public class CoreData implements HasID {
	private static BlockFactory customBlockFactory;
	
		/**
	 * If you want to define custo id's >39
	 *
	 * @param customBlockFactory new value of customBlockFactory
	 */
	public static void setCustomBlockFactory(BlockFactory customBlockFactory) {
		CoreData.customBlockFactory = customBlockFactory;
	}

	public static BlockFactory getFactory() {
		return customBlockFactory;
	}
	
	/**
	 * [00000000, 0hhhhhh, vvvvvvvvv, iiiiiiii]<br />
	 * h: health bytes, stored as 100-health<br />
	 * v: value bytes<br />
	 * i: id bytes<br />
	 */
	private int identifier;
	private float lightlevel = 1f;//saved here because it saves recalcualtion for every camera


	public CoreData(int id) {
		this.identifier = id;
	}
	
	public CoreData(int id, int value) {
		this.identifier = (value<<8)+id;
	}
	
	@Override
	public int getId() {
		return identifier%0xFF;
	}

	@Override
	public int getValue() {
		return (identifier>>8)%0xFF;
	}
	
	public void setValue(int value) {
		this.identifier = (value<<8)+identifier%0xFF;
	}
	
	/**
	 * value between 0-100
	 * @param health 
	 */
	public void setHealth(byte health){
		this.identifier = ((100-health)<<16)+(identifier%0xFFFF);
	}
	
	public byte getHealth(){
		return (byte) (100-((identifier>>16)%0xFF));
	}
	
	/**
	 * creates a new RenderBlock instance based on he data
	 * @return 
	 */
	public RenderBlock toBlock(){
		return new RenderBlock(identifier, getValue());
	}

	@Override
	public boolean isObstacle() {
		//todo
		if (getId()>39 && customBlockFactory != null){
            return customBlockFactory.isObstacle(getId(), getValue());
         }
		return getId() != 0;
	}

	@Override
	public boolean isTransparent() {
		//todo
		if (getId()>39 && customBlockFactory != null){
            return customBlockFactory.isTransparent(getId(), getValue());
         }
		return false;
	}
	
    /**
     * Check if the block is liquid.
     * @return true if liquid, false if not 
     */
	public boolean isLiquid() {
		if (getId()>39 && customBlockFactory != null){
            return customBlockFactory.isLiquid(getId(), getValue());
        }
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
		if (getId()>39 && customBlockFactory != null){
            return customBlockFactory.hasSides(getId(), getValue());
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
		switch (getId()) {
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
			case 34:
				return "flower";
			case 35:
				return "bush";
			case 36:
				return "tree";
								
			default:
				if (getId() > 39) {
                    if (customBlockFactory!=null){
                        return customBlockFactory.getName(getId(), getValue());
                    } else {
                        return "no custom blocks";
                    }
                } else {
                    return "engine block not properly defined";
                }
		}
	}
}
