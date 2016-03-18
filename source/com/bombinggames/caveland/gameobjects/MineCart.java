package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.gameobjects.collectibles.Collectible;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleType;
import com.bombinggames.caveland.gameobjects.logicblocks.BoosterLogic;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.GAME_EDGELENGTH;
import static com.bombinggames.wurfelengine.core.gameobjects.Block.GAME_EDGELENGTH2;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Point;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class MineCart extends MovableEntity implements Interactable {
	private static final long serialVersionUID = 2L;
	/**
	 * in m/s
	 */
	private static final float MAXSPEED = 6;
	private static final float BOOSTERSPEED = 20;
	/**
	 * the height of the bottom plate
	 */
	private static final int BOTTOMHEIGHT = GAME_EDGELENGTH/3;

	/**
	 * empirical factor to match the back side with the rear
	 */
	private final transient static float FRONTOFFSET = 63;
	private MovableEntity passenger;
	private ArrayList<MovableEntity> content = new ArrayList<>(5);
	private transient float rollingCycle;
	private transient long isPlayingSound;
	private transient SimpleEntity back;
	private transient SimpleEntity front;
	private transient PointLightSource lightsource;
	//private transient boolean passengerTeleported;
	
	/**
	 * Has a front and back plate while the main part ist hidden.
	 */
	public MineCart() {
		super((byte) 42, 0);
		setName("Minecart");
		setOrientation(new Vector2(1, 1));
		setObstacle(true);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		createBackAndFront();
		setHidden(true);
		//MessageManager.getInstance().addListener(this, Events.teleport.getId());
		return this;
	}
	
	private void createBackAndFront(){
		back = (SimpleEntity) new SimpleEntity((byte) 42,(byte) 0).spawn(getPosition().cpy());
		back.setSaveToDisk(false);
		back.setName("MineCart Back");
		//back = new SimpleEntity((byte) 42,(byte) 1);
		//back.spawn(getPosition().cpy().add(0, Block.GAME_DIAGLENGTH2, 0));//the back is located in back
		back.setSaveToDisk(false);
		front = (SimpleEntity) new SimpleEntity((byte) 42,(byte) 1).spawn(getPosition().cpy().add(0, Block.GAME_DIAGLENGTH2, 0));//the back is located in back
		front.setSaveToDisk(false);
		front.setName("MineCart Front");
	}

	@Override
	@SuppressWarnings("SuspiciousNameCombination")
	public void update(float dt) {
		super.update(dt);
		
		Point pos = getPosition();
		
		if (hasPosition() && pos.isInMemoryAreaHorizontal()) {
			int block = pos.getBlock();
			byte value = (byte) ((block>>8)&255);
			
			if (lightsource == null) {
				lightsource = new PointLightSource(Color.WHITE.cpy(), 1.0f, 1, WE.getGameplay().getView());
				lightsource.setSaveToDisk(false);
				lightsource.setPosition(getPosition().cpy());
			}
			
			lightsource.getPosition().set(getPosition()).add(0, 0, Block.GAME_EDGELENGTH2);
			lightsource.update(dt);

			//on rails?
			if ((block & 255) == CavelandBlocks.CLBlocks.RAILS.getId() || (block & 255) == CavelandBlocks.CLBlocks.RAILSBOOSTER.getId()) {
				lightsource.enable();
				setFriction(0.001f);

				switch (value) {
					case 0://straight left bottom to top right
					case 6:
						setOrientation(
							new Vector2(
								getMovement().y >= 0 && getMovement().x <= 0 ? -1 : 1,
								getMovement().y >= 0 && getMovement().x <= 0 ? 1 : -1
							).nor()
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
							).nor()
						);
						//move on y=-x
						x = pos.getRelToCoordX();
						pos.setPositionRelativeToCoord(
							x,
							x,
							pos.getRelToCoordZ()
						);
						break;
						//curve
					case 3:
					case 5:
						int dirY;
						if (
							getMovement().y > 0//moving down
							|| (getMovement().y == 0 && pos.getY() - pos.toCoord().toPoint().getY() < 0
						)
						) {//on top and moving down
							dirY = 1;
						} else {
							dirY = -1;
						}

						setOrientation(
							new Vector2(
								0,
								dirY//coming from top right
							).nor()
						);
						
				//the thing moved now put it back on the track
//				float percentageOfCurve = 0;
//				getPosition().y
				//percentageOfCurve += getMovementHor().len()*Block.GAME_EDGELENGTH/(Math.PI*Block.GAME_EDGELENGTH2);//divide by quaarter of circle outline
				//left or right side offset
						int offset = -1;
						if (value== 5) {
							offset = 1;
						}
						Vector3 circularVec = getPosition().cpy().sub(//0P
							getPosition().toCoord().toPoint().add(offset*Block.GAME_DIAGLENGTH2, 0, 0)//0C
						).nor().scl(Block.GAME_EDGELENGTH2);//movement is on radius of half of the block
						
						pos.setPositionRelativeToCoord(
							circularVec.add(offset*Block.GAME_DIAGLENGTH2, 0, 0)
						);
						break;
					case 2:
					case 4:
						setOrientation(
							new Vector2(
								getMovement().x > 0 || (getMovement().x == 0 && pos.getRelToCoordX() < 0) ? 1 : -1,//coming from left
								0
							).nor()
						);
						
						offset = 1;
						if (value== 4) {
							offset = -1;
						}
						circularVec = getPosition().cpy().sub(//0P
							getPosition().toCoord().toPoint().add(0, offset*Block.GAME_DIAGLENGTH2, 0)//0C
						).nor().scl(Block.GAME_EDGELENGTH2);//movement is on radius of half of the block
						
						pos.setPositionRelativeToCoord(
							circularVec.add(0, offset*Block.GAME_DIAGLENGTH2, 0)
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
					if ((block&255)== CavelandBlocks.CLBlocks.RAILSBOOSTER.getId()) {
						if (getPosition().toCoord().getLogic() instanceof BoosterLogic
							&& ((BoosterLogic) getPosition().toCoord().getLogic()).isEnabled()) {
							setSpeedHorizontal(BOOSTERSPEED);
						} else {
							setSpeedHorizontal(0);
						}
					}
					//start sound?
					if (getSpeedHor() > 0 && isPlayingSound == 0) {
						isPlayingSound = WE.SOUND.loop("wagon", getPosition());
					}
				}

				//jump on ramp
				if (
					   value== 6 && getMovementHor().x > 0
					|| value == 7 && getMovementHor().y < 0
					|| value == 8 && getMovementHor().x < 0
					|| value == 9 && getMovementHor().y > 0
				) {
					setMovement(new Vector3(getMovementHor().nor(), 0.8f).nor().scl(getMovement().len()*0.9f));
				}
				//roll down?
				if (value == 6 && getMovementHor().x <= 0) {
					setOrientation(new Vector2(-1, 1).nor());
					setSpeedHorizontal(MAXSPEED);
				}
				if (value == 7 && getMovementHor().x >= 0) {
					setOrientation(new Vector2(1, 1).nor());
					setSpeedHorizontal(MAXSPEED);
				}
				if (value == 8 && getMovementHor().x >= 0) {
					setOrientation(new Vector2(1, -1).nor());
					setSpeedHorizontal(MAXSPEED);
				}
				if (value == 9 && getMovementHor().x <= 0) {
					setOrientation(new Vector2(-1, -1).nor());
					setSpeedHorizontal(MAXSPEED);
				}
			} else {//offroad
				lightsource.disable();
				setFriction(0.005f);
				if (isPlayingSound != 0) {
					WE.SOUND.stop("wagon", isPlayingSound);
					isPlayingSound = 0;
				}
			}
			
			updatePassenger(pos);

			//hit objects in front
			checkCollisionInFront();
		
			//copy position to back
			if (back.shouldBeDisposed() || front.shouldBeDisposed()) {
				dispose();
				return;
			}
			back.getPosition().set(pos);
			front.getPosition().set(pos).add(0, FRONTOFFSET, 0);

			//animation
			//moving down left or up right
			if (
				(getOrientation().y > 0
				&&
				getOrientation().y > getOrientation().x)
				||
				(getOrientation().y < 0
				&&
				getOrientation().y < getOrientation().x)
			) {
				if (getMovement().z > 0.1f) {
					if (getOrientation().y < 0) {
						back.setHidden(false);
						back.setSpriteValue((byte) 6);//coming from bottom left and moving up to right
						front.setSpriteValue((byte) 7);
					} else {
						back.setHidden(true);
						front.setSpriteValue((byte) 9);
					}
				} else {
					back.setHidden(false);
					back.setSpriteValue((byte) 0);
					front.setSpriteValue((byte) 1);
				}
			} else if (getMovement().z > 0.1f) {
				if (getOrientation().y < 0) {
					back.setHidden(false);
					back.setSpriteValue((byte) 13);//coming from bottom left and moving up to right
					front.setSpriteValue((byte) 14);
				} else {
					back.setHidden(true);
					front.setSpriteValue((byte) 11);
				}
			} else {
				back.setHidden(false);
				back.setSpriteValue((byte) 3);
				front.setSpriteValue((byte) 4);
			}

			rollingCycle += getMovementHor().len() * GAME_EDGELENGTH * dt / 1000f;//save change in distance in this sprite, distance*m/s
			rollingCycle %= GAME_EDGELENGTH / 4; //cycle each 0.25m
			if (rollingCycle >= GAME_EDGELENGTH / 8) {//new sprite half of the circle length
				front.setSpriteValue((byte) (front.getSpriteValue() + 1)); //next step in animation
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
		
		centerPassenger(false);

	}

	/**
	 *
	 * @return
	 */
	public MovableEntity getPassenger() {
		return passenger;
	}
	
	/**
	 * set passenger in the center the mine cart
	 * @param forceHeight
	 */
	public void centerPassenger(boolean forceHeight){
		if (passenger!=null) {
			if (forceHeight) {
				passenger.getPosition().set(getPosition());
				passenger.getPosition().setZ(getPosition().getZ() + BOTTOMHEIGHT);//a little bit higher then the minecart
			} else {
				float oldHeight = passenger.getPosition().getZ();
				passenger.getPosition().set(getPosition());
				passenger.getPosition().setZ(oldHeight);
			}	
		}
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

	/**
	 *
	 * @param obj
	 * @return
	 */
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
	public void takeDamage(byte value) {
		super.takeDamage(value);
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
			if (passenger == null) {
				setPassanger((MovableEntity) actor);
			} else {
				passengerLeave();
			}
		}
	}
	
	void passengerLeave(){
		passenger.setFloating(false);
		passenger = null;
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
		createBackAndFront();
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

	private void checkCollisionInFront() {
		if (getSpeed() > 0) {
			ArrayList<MovableEntity> entitiesInFront;
			entitiesInFront = getPosition().cpy().add(getOrientation().scl(80)).getEntitiesNearby(Block.GAME_EDGELENGTH2, MovableEntity.class);
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
	
	private void updatePassenger(Point pos) {
		if (passenger != null) {
			//give same speed as minecart
			passenger.setMovement(getMovementHor());

			//while standing at ground in mine cart force into it
			if (passenger.getPosition().getZ() <= pos.getZ() + BOTTOMHEIGHT) {
				passenger.setFloating(true);
				centerPassenger(true);
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
				if (ent.canBePickedByParent(this) && ent.getPosition().distanceTo(pos) < 80 && ent.getMovement().z < 0) {
					if (add(ent)) {
						ent.dispose();
					}
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		super.handleMessage(msg);
		//possible hack to prevent passenger getting lost during teleportation
		if (msg.message == Events.teleport.getId()) {
			//passengerTeleported = true;
			//if (cart.getPassenger() != null) {
			centerPassenger(true);
		}
		return false;
	}

}
