package com.BombingGames.WurfelEngine.Core.Gameobjects;

/**
 *
 * @author Benedikt Vogler
 */
public class BlockDirt extends MovableEntity {
	private static final long serialVersionUID = 1L;
	private float timeofExistance;

	/**
	 *
	 */
	public BlockDirt() {
		super((byte) 44,(byte) 0);
		setSaveToDisk(false);
	}


	@Override
	public void update(float dt) {
		super.update(dt);
		timeofExistance+=dt;
		if (timeofExistance % 500 > 250) {
			setValue((byte) (int) (Math.random()*3));
		}

		if (timeofExistance>2000) dispose();
		
	}
	
}
