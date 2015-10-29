package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 * An entitiy which can be used to exit the caves
 * @author Benedikt Vogler
 */
public class ExitPortal extends Portal implements Interactable  {
	private static final long serialVersionUID = 2L;
	private boolean spawner;
	private ArrayList<Enemy> spawnedList = new ArrayList<>(3);

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 */
	public ExitPortal() {
		super((byte) 15);
		setName("Exit Portal");
		setActive(false);
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (getTarget() != null) {
			actor.setPosition(getTarget());
		}
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

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
}
