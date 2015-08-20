package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 * An entitiy which can be used to exit the caves
 * @author Benedikt Vogler
 */
public class ExitPortal extends AbstractEntity implements Interactable  {
	private static final long serialVersionUID = 2L;
	private Coordinate target = new Coordinate(0, 0, Chunk.getBlocksZ()-1);
	private boolean spawner;
	private ArrayList<Enemy> spawnedList = new ArrayList<>(3);

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 */
	public ExitPortal() {
		super((byte) 15, (byte) 1);
	}
	
	/**
	 * copy safe
	 * @return 
	 */
	public Coordinate getTarget() {
		return target.cpy();
	}
	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		actor.setPosition(target.cpy());
	}

	public void setTarget(Coordinate target) {
		this.target = target;
	}

	@Override
	public void update(float dt) {
		if (spawner){
			if (!getPosition().getEntitiesNearby(Block.GAME_EDGELENGTH*6, Ejira.class).isEmpty()){//if a player is nearby
				if (spawnedList.size()<3) {
					Coordinate coord = getPosition().toCoord();
					int cavenumber = ChunkGenerator.getCaveNumber(coord.getX(), coord.getY(), 4); 
					Enemy e = (Enemy) new Enemy().spawn(
						ChunkGenerator.getCaveCenter(cavenumber).toPoint().addVector(
							(float) ((Math.random()*4-2)*Block.GAME_EDGELENGTH),
							(float) ((Math.random()*4-2)*Block.GAME_EDGELENGTH),
							8
						)
					);
					spawnedList.add(e);
				}
				//remove killed enemys
				for (int i = 0; i < spawnedList.size(); i++) {
					Enemy e = spawnedList.get(i);
					if (e.shouldBeDisposed()) spawnedList.remove(e);
				}
			}
		}
	}
	
	/**
	 * creates monsters
	 */
	public void enableEnemySpawner(){
		spawner = true;
	}

}