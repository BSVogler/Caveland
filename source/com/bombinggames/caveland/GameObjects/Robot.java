package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.Events;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityAnimation;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.Point;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;

/**
 * An enemy which can follow a character.
 *
 * @author Benedikt Vogler
 */
public class Robot extends MovableEntity {

	private static final long serialVersionUID = 2L;
	/**
	 * the time for the attack animation
	 */
	public static final float ATTACKTIME = 600;
	private static final String KILLSOUND = "robot1destroy";
	private static final String RUNNINGSOUND = "robot1Wobble";

	private transient MovableEntity target;
	private int runningagainstwallCounter = 0;
	private Point lastPos;
	private long movementSoundPlaying;
	private float mana = 1000;
	/**
	 * countdown while the attack ins in progress. Used for animation.
	 */
	private float attackInProgess = 0;
	/**
	 * in m/s
	 */
	private float movementSpeed = 2;
	/**
	 * what kind of robot
	 */
	private int type = 0;
	private transient AimBand particleBand;
	private int teamId;

	public Robot() {
		this((byte) 45, 5);
	}

	public Robot(byte id, int steps) {
		super(id, steps);
		setType(0);
		setTeamId(0);
		setObstacle(true);
		setWalkingAnimationCycling(true);
		setDamageSounds(new String[]{"robotHit"});
	}

	@Override
	public AbstractEntity spawn(final Point point) {
		if (type == 0) {
			movementSoundPlaying = WE.SOUND.loop(RUNNINGSOUND, point);
		}
		return super.spawn(point);
	}

	@Override
	public void jump() {
		jump(5, true);
	}

	@Override
	public void update(float dt) {
		//update as usual
		super.update(dt);

		//set attack sprite
		if (attackInProgess > 0) {
			attackInProgess -= dt;
			movementSpeed = 0;
			Vector2 orientation = getOrientation();
			if (orientation.x < -Math.sin(Math.PI / 3)) {
				setSpriteValue((byte) 1);//west
			} else if (orientation.x < -0.5) {
				//y
				if (orientation.y < 0) {
					setSpriteValue((byte) 2);//north-west
				} else {
					setSpriteValue((byte) 0);//south-east
				}
			} else if (orientation.x < 0.5) {
				//y
				if (orientation.y < 0) {
					setSpriteValue((byte) 3);//north
				} else {
					setSpriteValue((byte) 7);//south
				}
			} else if (orientation.x < Math.sin(Math.PI / 3)) {
				//y
				if (orientation.y < 0) {
					setSpriteValue((byte) 4);//north-east
				} else {
					setSpriteValue((byte) 6);//sout-east
				}
			} else {
				setSpriteValue((byte) 5);//east
			}
			if (attackInProgess > ATTACKTIME * 2 / 3f) {
				setSpriteValue((byte) (getSpriteValue() + 40));
			} else if (attackInProgess > ATTACKTIME / 3f) {
				setSpriteValue((byte) (getSpriteValue() + 48));
			} else {
				setSpriteValue((byte) (getSpriteValue() + 56));
			}
		}

		//clamp at 0
		if (attackInProgess < 0) {
			attackInProgess = 0;
			movementSpeed = 2;
			playMovementAnimation();
		}
		
		if (hasPosition() && getPosition().isInMemoryAreaHorizontal()) {
			//follow the target
			if (target != null && target.hasPosition()) {
				if (getPosition().distanceTo(target) > Block.GAME_EDGELENGTH * 1.5f) {
					//movement logic
					Vector3 d = new Vector3();

					d.x = target.getPosition().getX() - getPosition().getX();
					d.y = target.getPosition().getY() - getPosition().getY();
					if (isFloating()) {
						d.z = target.getPosition().getZ() - getPosition().getZ();
					}
					d.nor();//direction only
					d.scl(movementSpeed);//speed at 2 m/s
					if (!isFloating()) {
						d.z = getMovement().z;
					}

					setMovement(d);// update the movement vector

				}

				//attack
				if (attackInProgess == 0) {
					performAttack();
				}
			}

			mana = ((int) (mana + dt));

			//find nearby target if there is none
			if (target == null && teamId == 0) {
				ArrayList<Ejira> nearby = getPosition().getEntitiesNearbyHorizontal(Block.GAME_DIAGLENGTH * 4, Ejira.class);
				if (!nearby.isEmpty()) {
					target = nearby.get(0);
				}
			}

			//Movement AI: if standing on same position as in last update
			if (!isFloating()) {
				if (getPosition().equals(lastPos) && getSpeed() > 0) {//not standing still
					runningagainstwallCounter += dt;
				} else {
					runningagainstwallCounter = 0;
					lastPos = getPosition().cpy();
				}

				//jump after some time
				if (runningagainstwallCounter > 500) {
					jump();
					mana = 0;
					runningagainstwallCounter = 0;
				}
			}
		}
		
		if (particleBand != null) {
			particleBand.update();
		}
	}

	/**
	 * attacks every other team id.
	 *
	 * @param id 0 is for enemy CP.
	 */
	public void setTeamId(int id) {
		this.teamId = id;
		if (teamId == 0) {
			setName("Evil Robot");
		} else {
			setName("Friendly Robot");
		}
	}

	/**
	 * Set the target which the zombie follows.
	 *
	 * @param target an character
	 */
	public void setTarget(MovableEntity target) {
		this.target = target;
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	private void performAttack() {
		if (mana >= 1000 && getPosition().distanceTo(target) < Block.GAME_EDGELENGTH * 2f) {
			mana = 0;//reset
			new SimpleEntity((byte) 33).spawn(target.getPosition().cpy()).setAnimation(
				new EntityAnimation(new int[]{300}, true, false)
			);
			MessageManager.getInstance().dispatchMessage(
				this,
				target,
				Events.damage.getId(),
				1
			);
			pauseMovementAnimation();
			attackInProgess = ATTACKTIME;//1500ms until the attack is done
		}
	}

	@Override
	public void disposeFromMap() {
		super.disposeFromMap();
		WE.SOUND.stop(RUNNINGSOUND, movementSoundPlaying);
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		super.handleMessage(msg);
		if (msg.message == Events.damage.getId()) {
			byte damage = ((Byte) msg.extraInfo);
			takeDamage(damage);
			if (getHealth() <= 0) {
				new DestructionParticle((byte) 34).spawn(getPosition().toPoint());
				new DestructionParticle((byte) 35).spawn(getPosition().toPoint());
				new DestructionParticle((byte) 36).spawn(getPosition().toPoint());

				if (getHealth() <= 0 && KILLSOUND != null) {
					WE.SOUND.play(KILLSOUND);
				}
			}
		} else if (msg.message == Events.deselectInEditor.getId()) {
			if (particleBand != null) {
				particleBand.dispose();
				particleBand = null;
			}
		} else if (msg.message == Events.selectInEditor.getId()) {
			if (particleBand == null) {
				particleBand = new AimBand(this, target);
			} else {
				particleBand.setTarget(target);
			}
		}
		return true;
	}

	public void setType(int type) {
		this.type = type;
		if (type == 1) {
			setSpriteId((byte) 58);
			setFloating(false);
			setContinuousWalkingAnimation(0);
		} else if (type==0) {
			setFloating(true);
			setContinuousWalkingAnimation(1f);
		}
	}

}
