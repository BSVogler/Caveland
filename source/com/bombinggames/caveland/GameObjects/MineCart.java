package com.bombinggames.caveland.GameObjects;

import com.bombinggames.wurfelengine.core.Gameobjects.PointLightSource;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.caveland.Game.CLGameView;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.logicblocks.BoosterLogic;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH2;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.Point;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class MineCart extends MovableEntity implements Interactable {
	private static final long serialVersionUID = 2L;
	private static final float MAXSPEED = 6;
	private static final float BOOSTERSPEED = 20;
	/**
	 * the height of the bottom plate
	 */
	private static final int BOTTOMHEIGHT = GAME_EDGELENGTH/3;

	/**
	 * empirical factor to match the front side with the rear
	 */
	private final transient static float frontOffset = 63;
	private MovableEntity passenger;
	private ArrayList<MovableEntity> content = new ArrayList<>(5);
	private transient float rollingCycle;
	private transient long isPlayingSound;
	private transient SimpleEntity front = new SimpleEntity((byte) 42,(byte) 1);
	private PointLightSource lightsource;
	
	/**
	 *
	 */
	public MineCart() {
		super((byte) 42, 0);
		setName("MineCart");
		setOrientation(new Vector2(1, 1));
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		front = (SimpleEntity) front.spawn(point.cpy().addVector(0, Block.GAME_DIAGLENGTH2, 0));//the front is located in front
		front.setSaveToDisk(false);
		front.setName("MineCart Front");
		
		
		return this;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		Point pos = getPosition();
		
				
		if (hasPosition() && pos.isInMemoryAreaHorizontal()) {
			Block block = pos.getBlock();
			
			if (lightsource==null){
				lightsource = new PointLightSource(Color.WHITE.cpy(), 1.0f, 1);
				lightsource.setSaveToDisk(false);
				lightsource.spawn(getPosition().cpy());
				SuperGlue lConn = new SuperGlue(this, lightsource);
				lConn.setSaveToDisk(false);
				lConn.setOffset(new Vector3(0, 0, Block.GAME_EDGELENGTH2));
				lConn.spawn(pos.cpy());
			}

			//on tracks?
			if (block != null && (
				block.getId() == CavelandBlocks.CLBlocks.RAILS.getId() || block.getId() == CavelandBlocks.CLBlocks.RAILSBOOSTER.getId())
			) {
				lightsource.enable();
				setFriction(0.001f);

				switch (block.getValue()) {
					case 0://straight left bottom to top right
					case 6:
						setOrientation(
							new Vector2(
								getMovement().y >= 0 && getMovement().x <= 0 ? -1 : 1,
								getMovement().y >= 0 && getMovement().x <= 0 ? 1 : -1
							)
						);
						//move on y=-x
						float x = pos.getRelToCoordX();
						pos.setPositionRelativeToCoord(
							x,
							-x,
							pos.getRelToCoordZ()
						);
						break;
					case 1:
					case 7:
						setOrientation(
							new Vector2(
								getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1,
								getMovement().y >= 0 && getMovement().x >= 0 ? 1 : -1
							)
						);
						//move on y=-x
						x = pos.getRelToCoordX();
						pos.setPositionRelativeToCoord(
							x,
							x,
							pos.getRelToCoordZ()
						);
						break;
					case 3:
					case 5:
						int dirY;
						if (getMovement().y > 0
							|| (getMovement().y == 0 && pos.getY() - pos.toCoord().toPoint().getY() < 0)) {//on top and moving down
							dirY = 1;
						} else {
							dirY = -1;
						}

						setOrientation(
							new Vector2(
								0,
								dirY//coming from top right
							)
						);
						int offset = -1;
						if (block.getValue() == 5) {
							offset = 1;
						}
						pos.setPositionRelativeToCoord(
							offset * Block.GAME_EDGELENGTH2 / 2,
							pos.getRelToCoordY(),
							pos.getRelToCoordZ()
						);
						break;
					case 2:
					case 4:
						setOrientation(
							new Vector2(
								getMovement().x > 0 || (getMovement().x==0 && pos.getRelToCoordX()<0)  ? 1 : -1,//coming from left
								0
							)
						);
						offset=1;
						if (block.getValue()==4)
							offset = -1;
						pos.setPositionRelativeToCoord(
							pos.getRelToCoordX(),
							offset*Block.GAME_EDGELENGTH2/2,
							pos.getRelToCoordZ()
						);
						break;
				}

				//start moving?
				if (getSpeedHor() > 0) {
					//move
					if(getSpeedHor() <= MAXSPEED) {
						setSpeedHorizontal(MAXSPEED);
					}

					//booster
					if (
						block.getId() == CavelandBlocks.CLBlocks.RAILSBOOSTER.getId()
					) {
						if (getPosition().toCoord().getLogic() instanceof BoosterLogic
						&& ((BoosterLogic) getPosition().toCoord().getLogic()).isEnabled())
							setSpeedHorizontal(BOOSTERSPEED);
						else
							setSpeedHorizontal(0);
					}
					//start sound?
					if (getSpeedHor() > 0 && isPlayingSound == 0) {
						isPlayingSound = WE.SOUND.loop("wagon", getPosition());
					}
				}

				//jump on ramp
				if (
					   block.getValue() == 6 && getMovementHor().x > 0
					|| block.getValue() == 7 && getMovementHor().y < 0
					|| block.getValue() == 8 && getMovementHor().x < 0
					|| block.getValue() == 9 && getMovementHor().y > 0
				) {
					setMovement(new Vector3(getMovementHor().nor(), 0.8f).nor().scl(getMovement().len()*0.9f));
				}
				//roll down?
				if (block.getValue() == 6 && getMovementHor().x <= 0) {
					setOrientation(new Vector2(-1, 1));
					setSpeedHorizontal(MAXSPEED);
				}
				if (block.getValue() == 7 && getMovementHor().x >= 0) {
					setOrientation(new Vector2(1, 1));
					setSpeedHorizontal(MAXSPEED);
				}
				if (block.getValue() == 8 && getMovementHor().x >= 0) {
					setOrientation(new Vector2(1, -1));
					setSpeedHorizontal(MAXSPEED);
				}
				if (block.getValue() == 9 && getMovementHor().x <= 0) {
					setOrientation(new Vector2(-1, -1));
					setSpeedHorizontal(MAXSPEED);
				}
				
			} else {//offroad
				lightsource.disable();
				setFriction(0.005f);
				if (isPlayingSound!=0) {
					WE.SOUND.stop("wagon", isPlayingSound);
					isPlayingSound = 0;
				}
			}
			
			//logic
//			if transporting object
			if (passenger != null) {
				//give same speed as minecart
				passenger.setMovement(getMovementHor());
				
				//while standing at ground in mine cart force into it
				if (passenger.getPosition().getZ() <= pos.getZ()+BOTTOMHEIGHT) {
					Point tmp = pos.cpy();
					tmp.setZ( pos.getZ()+BOTTOMHEIGHT );//a little bit higher then the minecart
					passenger.setPosition(tmp);
					passenger.setFloating(true);
					if (passenger instanceof Ejira) {
						((Ejira) passenger).idle();
						((Ejira) passenger).forceBunnyHop();
					}
				} else {
					passenger.setFloating(false);
				}
				
				//check if passenger exited the cart
				if (
					passenger.getPosition().getZ() - pos.getZ() > GAME_EDGELENGTH2
					|| getPosition().distanceToHorizontal(passenger) > GAME_EDGELENGTH
				) {
					passengerLeave();
				}
			} else {
				//add objects
				ArrayList<Collectible> ents = Controller.getMap().getEntitys(Collectible.class);
				for (Collectible ent : ents) {
					if (ent.canBePickedByParent(this) && ent.getPosition().distanceTo(pos)<80 && ent.getMovement().z <0){
						if (add(ent))
							ent.dispose();
					}
				}
			}

//			hit objects in front
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
		
//			copy position to front
			front.setPosition(pos.cpy().addVector(0, frontOffset, 0));

//			animation
//			moving down left or up right
			setHidden(false);
			if (
				(getOrientation().y > 0
				&&
				getOrientation().y > getOrientation().x)
				||
				(getOrientation().y < 0
				&&
				getOrientation().y < getOrientation().x)
			) {
				if (getMovement().z > 0.1f){
					if (getOrientation().y < 0) {
						setValue((byte) 6);//coming from bottom left and moving up to right
						front.setValue((byte) 7);
					} else {
						setHidden(true);
						front.setValue((byte) 9);
					}
				} else {
					setValue((byte) 0);
					front.setValue((byte) 1);
				}
			} else {
				if (getMovement().z > 0.1f){
					if (getOrientation().y < 0) {
						setValue((byte) 13);//coming from bottom left and moving up to right
						front.setValue((byte) 14);
					} else {
						setHidden(true);
						front.setValue((byte) 11);
					}
				} else {
					setValue((byte) 3);
					front.setValue((byte) 4);
				}
			}

			rollingCycle += getMovementHor().len()*GAME_EDGELENGTH*dt/1000f;//save change in distance in this sprite, distance*m/s
			rollingCycle %= GAME_EDGELENGTH/4; //cycle each 0,25m
			if (rollingCycle >= GAME_EDGELENGTH/8) {//new sprite half of the circle length
				front.setValue((byte) (front.getValue()+1)); //next step in animation
			}
		}
	}

	/**
	 * the passengers must enter by themself
	 * @param passenger 
	 */
	public void setPassanger(MovableEntity passenger) {
		this.passenger = passenger;
//		if (passenger.getMovement().z > 0) {
//			passenger.getMovement().z = 0;//fall into chuchu
//		}
		
		
		//set passenger in the center the mine cart
		Point tmp = passenger.getPosition().cpy();
		tmp.setZ(passenger.getPosition().getZ());//kepp z
		passenger.setPosition(tmp);
	}

	/**
	 *
	 * @return
	 */
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

	public boolean add(MovableEntity obj) {
		if (content.size() < 5) {
			content.add(obj);
			return true;
		}
		return false;
	}

	/**
	 *
	 * @param list
	 */
	public void addAll(ArrayList<MovableEntity> list) {
		if (list != null) {
			content.addAll(list);
		}
	}

	@Override
	public void damage(byte value) {
		super.damage(value);
		if (getHealth()<=0) {
			WE.SOUND.stop("wagon", isPlayingSound);
			WE.SOUND.play("robot1destroy", getPosition());
			((Collectible) CollectibleType.Iron.createInstance().spawn(getPosition())).sparkle();
		}
	}
	
	/**
	 *
	 */
	public void turn() {
		setOrientation(
			new Vector2(
				-getOrientation().x,
				-getOrientation().y
			)
		);
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof MovableEntity){
			if (passenger==null){
				setPassanger((MovableEntity) actor);
			} else {
				passengerLeave();
			}
		}
	}
	
	void passengerLeave(){
		passenger.setFloating(false);
		passenger=null;
	}
	
	/**
	 * overrides deserialisation
	 *
	 * @param stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject(); //fills fld1 and fld2;
		front = new SimpleEntity((byte) 42,(byte) 1);
		front.spawn(getPosition().cpy().addVector(0, Block.GAME_DIAGLENGTH2, 0));//the front is located in front
		front.setSaveToDisk(false);
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean interactable() {
		return (passenger==null);
	}
	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}

}
