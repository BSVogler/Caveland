package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomTree extends Block {
	private static final long serialVersionUID = 1L;
	private Coordinate pos;
	/**
	 * The treetop is used to identify the treetop. It is invisible but it is an obstacle.
	 */
	private final int TREETOPVALUE = 8;

	/**
	 * creates a tree in a random shape
	 */
	public CustomTree(){
		this((int) (Math.random()*8));
	}
	
		
	public CustomTree(int value) {
		super(72);
		setValue(value);
		setNoSides();
		setObstacle(true);
		setTransparent(true);
		
		if (getValue()==TREETOPVALUE)
			setHidden(true);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		//check and grow treetop
		Coordinate top = getPosition().cpy().addVector(0, 0, 1);
		if (getValue() != TREETOPVALUE && top.getBlock().getId() != getId())//if root block grow treetop
			Block.getInstance(72, TREETOPVALUE).spawn(top);
	}
	
	

	@Override
	public void onDestroy(AbstractPosition pos) {
		super.onDestroy(pos.cpy().addVector(0, 0, 1));//destroy top block
		
	}

	
}
