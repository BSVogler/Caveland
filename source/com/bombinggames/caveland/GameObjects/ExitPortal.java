package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.Game.ChunkGenerator;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogic;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
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
		teleport(actor);
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
				
				//has lift on exit
				if (getTarget().add(0, 0, 1).getLogic() instanceof LiftLogic){
					setSpriteValue((byte) 1);
				} else {
					setSpriteValue((byte) 0);
				}
			}
		}
	}

	/**
	 * Get the ground coordinate under the portal.
	 * @return copy safe
	 */
	public Coordinate getGround(){
		Coordinate ground = getPosition().toCoord();
		//find ground
		while (ground.getBlock() == null || !ground.getBlock().isObstacle()) {
			ground.add(0, 0, -1);
		}
		return ground;
	}

	/**
	 * creates monsters
	 */
	public void enableEnemySpawner() {
		spawner = true;
	}

	@Override
	public boolean interactable() {
		return getSpriteId()==1;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
}
