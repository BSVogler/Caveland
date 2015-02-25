package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Lore extends MovableEntity {
	private static final long serialVersionUID = 1L;

	private MovableEntity passenger;
	private ArrayList<MovableEntity> content = new ArrayList<>(5);

	public Lore() {
		super(42, 0);
		setMovement(new Vector3(1, 1, 0));
		//setSpeed(0);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		Point pos = getPosition();
		Block block = pos.getBlock();

		//on tracks?
		if (block.getId() == 55) {
			setFriction(10000);
			if (getSpeed() > 0) {
				setSpeedHorizontal(1);
			}

			float mov_z = getMovement().z;
			switch (block.getValue()) {
				case 0:
					setMovement(new Vector3(
						getMovement().y >= 0 && getMovement().x <= 0 ? -1 : 1,
						getMovement().y >= 0 && getMovement().x <= 0 ? 1 : -1,
						mov_z)
					);
					float x = pos.getRelToCoordX();
					pos.setPositionRelativeToCoord(
						x,
						-x,
						pos.getRelToCoordZ()
					);
					break;
				case 1:
					setMovement(new Vector3(
						getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1,
						getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1,
						mov_z)
					);
					x = pos.getRelToCoordX();
					pos.setPositionRelativeToCoord(
						x,
						x,
						pos.getRelToCoordZ()
					);
					break;
				case 3:
				case 5:
					int y;
					if (getMovement().y > 0
						|| (getMovement().y == 0 && pos.getY() - pos.getCoord().getPoint().getY() < 0)) {//on top and moving down
						y = 1;
					} else {
						y = -1;
					}

					setMovement(new Vector3(
						0,
						y,//coming from top right
						mov_z)
					);
					break;
				case 2:
				case 4:
					setMovement(new Vector3(
						getMovement().x >= 0 ? 1 : -1,//coming from left
						0,
						mov_z)
					);
					break;
			}
		} else {//offroad
			setFriction(500);
		}

		//if transporting object
		if (passenger != null) {
			passenger.getMovement().x = getMovement().x;
			passenger.getMovement().y = getMovement().y;

			passenger.setFriction(getFriction());
			//passenger.setSpeed(getSpeed());

			if (passenger.isOnGround()) {//while standing force into lore
				Point tmp = pos.cpy();
				tmp.setZ(passenger.getPosition().getZ());
				passenger.setPosition(tmp);
			}
			if (pos.distanceTo(passenger) > 200) {//object exits
				passenger.setFriction(200);
				passenger = null;
			}
		} else {
			//add objects
			ArrayList<MovableEntity> ents = Controller.getMap().getEntitys(MovableEntity.class);
			for (MovableEntity ent : ents) {
				if (ent.isCollectable() && ent.getPosition().distanceTo(pos)<80 && ent.getMovement().z <0){
					if (add(ent))
						ent.dispose();
				}
			}
		}

		//hit objects in front
		if (getSpeed() > 0) {
			ArrayList<MovableEntity> entitiesInFront;
			entitiesInFront = pos.cpy().addVector(getOrientation().scl(80)).getEntitiesNearby(40, MovableEntity.class);
			for (MovableEntity ent : entitiesInFront) {
				if (this != ent) {//don't collide with itself
					ent.setMovement(
						new Vector3(
							(float) (getMovement().x + Math.random() * 0.5f - 0.25f),
							(float) (getMovement().y + Math.random() * 0.5f - 0.25f),
							(float) Math.random()
						)
					);
					ent.addToHor(getSpeed());
				}
			}
		}

	}

	void setPassanger(MovableEntity passenger) {
		this.passenger = passenger;
		Point tmp = getPosition().cpy();
		if (passenger.getMovement().z > 0) {
			passenger.getMovement().z = 0;//fall into chuchu
		}
		tmp.setZ(passenger.getPosition().getZ());
		passenger.setPosition(tmp);
	}

	public MovableEntity getPassenger() {
		return passenger;
	}

	/**
	 * emtpies teh lore
	 *
	 * @return
	 */
	public ArrayList<MovableEntity> getContent() {
		ArrayList<MovableEntity> tmp = content;
		content = new ArrayList<>(5);

		return tmp;
	}

	boolean add(MovableEntity obj) {
		if (content.size() < 5) {
			content.add(obj);
			return true;
		}
		return false;
	}

	public void addAll(ArrayList<MovableEntity> list) {
		if (list != null) {
			content.addAll(list);
		}
	}

	@Override
	public void dispose() {
		((Collectible) new Collectible(Collectible.ColTypes.IRON).spawn(getPosition())).sparkle();
		super.dispose();
	}

	public void turn() {
		setMovement(
			new Vector3(
				-getOrientation().x,
				-getOrientation().y,
				getMovement().z
			)
		);
	}
}
