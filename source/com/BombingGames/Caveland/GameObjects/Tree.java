package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.IsSelfAware;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Tree extends Block implements IsSelfAware {
	private Coordinate pos;

	/**
	 * creates a tree
	 */
	public Tree(){
		this(0);
	}
	
		
	public Tree(int value) {
		super(72);
		setNoSides();
		setObstacle(true);
		setTransparent(true);
		if (pos!=null){
			if (value==0)//if root
				Block.getInstance(72,1).spawn(pos.cpy().addVector(0, 0, 1));
			if (value==1)
				setHidden(true);
		}
	}

	@Override
	public void onDestroy(AbstractPosition pos) {
		super.onDestroy(pos.cpy().addVector(0, 0, 1));//destry top block
	}

	@Override
	public AbstractPosition getPosition() {
		return pos;
	}

	@Override
	public void setPosition(AbstractPosition pos) {
		this.pos = pos.getCoord();
	}

	@Override
	public Block spawn(Coordinate coord) {
		setPosition(coord);
		return super.spawn(coord);
	}

	
}
