package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Tree extends Block {
	private static final long serialVersionUID = 1L;
	private Coordinate pos;

	/**
	 * creates a tree in a random shape
	 */
	public Tree(){
		this((int) (Math.random()*8));
	}
	
		
	public Tree(int value) {
		super(72);
		setValue(value);
		setNoSides();
		setObstacle(true);
		setTransparent(true);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		Coordinate top = getPosition().cpy().addVector(0, 0, 1);
		if (getValue()==0 && top.getBlock().getId() != getId())//if root
				Block.getInstance(72,1).spawn(top);
		if (getValue()==1)
			setHidden(true);
	}
	
	

	@Override
	public void onDestroy(AbstractPosition pos) {
		super.onDestroy(pos.cpy().addVector(0, 0, 1));//destroy top block
	}

	
}
