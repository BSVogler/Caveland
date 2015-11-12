package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogic;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;

/**
 * An entitiy which can be used to exit the caves
 *
 * @author Benedikt Vogler
 */
public class ExitPortal extends Portal implements Interactable {

	private static final long serialVersionUID = 2L;
	private boolean spawner;
	private final ArrayList<Enemy> spawnedList = new ArrayList<>(3);
	private transient SimpleEntity fahrstuhlkorb;

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
		if (spawner) {
			//if a player is not nearby
			if (getPosition().getEntitiesNearby(Block.GAME_EDGELENGTH * 9, Ejira.class).isEmpty()) {
				//spawn enemies
				while (spawnedList.size() < 3) {
					Coordinate coord = getPosition().toCoord();
					int cavenumber = ChunkGenerator.getCaveNumber(coord.getX(), coord.getY(), 4);
					Enemy e = (Enemy) new Enemy().spawn(
						ChunkGenerator.getCaveCenter(cavenumber).addVector(
							(int) (Math.random() * 4 - 2),
							(int) (Math.random() * 4 - 2),
							2
						).toPoint()
					);
					spawnedList.add(e);
				}
			}

			//remove killed enemys
			for (int i = 0; i < spawnedList.size(); i++) {
				Enemy e = spawnedList.get(i);
				if (e.shouldBeDisposed()) {
					spawnedList.remove(e);
				}
			}
		}

		//spawn lift only if a lft is built
		if (hasPosition()) {
			if (getLift() == null){
				if (fahrstuhlkorb != null){
					fahrstuhlkorb.disposeFromMap();
				}
			} else {
				if (fahrstuhlkorb == null || !fahrstuhlkorb.hasPosition()) {
					fahrstuhlkorb = new SimpleEntity((byte) 22);
					fahrstuhlkorb.setName("Lift Basket");
					fahrstuhlkorb.spawn(getGround().toPoint());
				} else {
					//teleport non-moving objects on the liftUp
					ArrayList<MovableEntity> entsOnLiftUp = fahrstuhlkorb.getPosition().toCoord().getEntitiesInside(MovableEntity.class);
					entsOnLiftUp.removeIf(ent -> ent.getMovement().len() > 0.1);
					for (MovableEntity ent : entsOnLiftUp) {
						teleport(ent);
					}
				}
			}
		}
	}

	/**
	 *
	 * @return copy safe
	 */
	public Coordinate getGround(){
		Coordinate ground = getPosition().toCoord();
		//find ground
		while (ground.getBlock() == null || !ground.getBlock().isObstacle()) {
			ground.addVector(0, 0, -1);
		}
		return ground.addVector(0, 0, 1);
	}

	/**
	 * creates monsters
	 */
	public void enableEnemySpawner() {
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

	/**
	 *
	 * @return can be null
	 */
	public LiftLogic getLift(){
		if (getTarget() == null) {
			return null;
		}

		AbstractBlockLogicExtension logic = getTarget().addVector(0, -1, 0).getLogic();
		if (logic instanceof LiftLogic) {
			return (LiftLogic) logic;
		} else {
			return null;
		}
	}
}
