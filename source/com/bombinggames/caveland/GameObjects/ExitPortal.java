package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector2;
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
	private final ArrayList<Robot> spawnedList = new ArrayList<>(3);
	private transient SimpleEntity fahrstuhlkorb;
	private float spawnCooldown;

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
		if (hasPosition()) {
			if (spawner) {
				//remove killed enemys
				spawnedList.removeIf(r -> r.shouldBeDisposed());
				if (spawnCooldown > 0)
					spawnCooldown -= dt;
				
				//if a player is not nearby
				if (getPosition().getEntitiesNearby(Block.GAME_EDGELENGTH * 13, Ejira.class).isEmpty() && spawnCooldown <= 0) {
					spawnCooldown += 5000;//only spawn every 5 seconds
					
					//spawn enemies
					while (spawnedList.size() < 3) {
						Coordinate coord = getPosition().toCoord();
						int cavenumber = ChunkGenerator.getCaveNumber(coord.getX(), coord.getY(), 4);
						Robot robot;
						if (Math.random() > 0.5f) {
							robot = new SpiderRobot();
						} else {
							robot = new Robot();
						}
						robot.spawn(
							ChunkGenerator.getCaveCenter(cavenumber).add(
								(int) (Math.random() * 4 - 2),
								(int) (Math.random() * 4 - 2),
								2
							).toPoint()
						);
						spawnedList.add(robot);
					}
				}
			}

			//spawn lift only if a lift is built
			if (getLift() == null){
				//has no lift
				setSpriteValue((byte) 1);
				if (fahrstuhlkorb != null){
					fahrstuhlkorb.disposeFromMap();
				}
			} else {
				//has lift
				setSpriteValue((byte) 0);
				if (fahrstuhlkorb == null || !fahrstuhlkorb.hasPosition()) {
					fahrstuhlkorb = new LiftBasket();
					fahrstuhlkorb.spawn(getGround().toPoint());
					Block groundBlock = getGround().add(0, 1, 0).getBlock();
					if (groundBlock != null && groundBlock.isObstacle()) {
						getGround().add(0, 1, 0).destroy();
					}
				} else {
					//teleport non-moving objects on the liftUp
					ArrayList<MovableEntity> entsOnLiftUp = fahrstuhlkorb.getPosition().toCoord().getEntitiesInside(MovableEntity.class);
					for (MovableEntity ent : entsOnLiftUp) {
						teleport(ent);
						ent.setMovement(new Vector2(-1, 1));
					}
				}
			}
		}
	}

	/**
	 * Get the groudn under the portal.
	 * @return copy safe
	 */
	public Coordinate getGround(){
		Coordinate ground = getPosition().toCoord();
		//find ground
		while (ground.getBlock() == null || !ground.getBlock().isObstacle()) {
			ground.add(0, 0, -1);
		}
		return ground.add(0, 0, 1);
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

		AbstractBlockLogicExtension logic = getTarget().add(0, -1, 0).getLogic();
		if (logic instanceof LiftLogic) {
			return (LiftLogic) logic;
		} else {
			return null;
		}
	}
}
