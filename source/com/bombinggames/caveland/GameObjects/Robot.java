package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.gameobjects.EntityAnimation;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.Point;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A robot which can be evil or friendly.
 *
 * @author Benedikt Vogler
 */
public class Robot extends MovableEntity implements Telegraph, HasTeam{

	private static final long serialVersionUID = 3L;
	/**
	 * the time for the attack animation
	 */
	public static final float ATTACKTIME = 600;
	private static final String KILLSOUND = "robot1destroy";
	private static final String RUNNINGSOUND = "robot1Wobble";

	/**
	 * if following an enemy
	 */
	private transient MovableEntity enemyTarget;
	/**
	 * sound played if activated
	 */
	private transient long runningSound;
	/**
	 * in m/s
	 */
	private transient float movementSpeed = 2;
	
	private float energy = 1000;
	/**
	 * countdown while the attack ins in progress. Used for animation.
	 */
	private float attackInProgess = 0;
	/**
	 * what kind of robot
	 */
	private int type = 0;
	/**
	 * 0 is neutral, 1 is PC, 2 is player
	 */
	private int teamId;
	
	private final IdleAI idleaAI = new IdleAI(this);

	/**
	 *
	 */
	public Robot() {
		this((byte) 45, 5);
	}

	/**
	 *
	 * @param id
	 * @param steps
	 */
	public Robot(byte id, int steps) {
		super(id, steps);
		setType(0);
		setTeamId(0);
		setObstacle(true);
		setWalkingAnimationCycling(true);
		setDamageSounds(new String[]{"robotHit"});
		setObstacle(true);
		setMass(40);
	}

	@Override
	public AbstractEntity spawn(final Point point) {
		if (type == 0) {
			runningSound = WE.SOUND.loop(RUNNINGSOUND, point);
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
		
		if (dt == 0 && runningSound != 0) {
			WE.SOUND.stop(RUNNINGSOUND, runningSound);
			runningSound = 0;
		}

		
		if (idleaAI != null) {
			idleaAI.update(dt);
		}
		
		//set attack sprite
		if (attackInProgess > 0) {
			attackInProgess -= dt;
			movementSpeed = 0;
			playAttackAnimation();
		} else {
			attackInProgess = 0;//clamp
			movementSpeed = 2;
			playMovementAnimation();
		}
		
		if (hasPosition() && getPosition().isInMemoryAreaHorizontal()) {
			//follow the target
			if (enemyTarget != null && enemyTarget.hasPosition()) {
				if (getPosition().distanceTo(enemyTarget) > Block.GAME_EDGELENGTH * 1.5f) {
					MessageManager.getInstance().dispatchMessage(this,
						this,
						Events.moveTo.getId(),
						enemyTarget.getPosition()
					);
					setSpeedHorizontal(movementSpeed);
				} else {
					MessageManager.getInstance().dispatchMessage(
						this,
						this,
						Events.standStill.getId()
					);
				}

				//attack
				if (attackInProgess == 0) {
					performAttack();
				}
			} else {
				enemyTarget = null;
			}

			energy = ((int) (energy + dt));

			//find nearby target if there is none
			if (enemyTarget == null && getTeamId() != 0) {
				ArrayList<HasTeam> nearbyWithFaction = getPosition().getEntitiesNearbyHorizontal(Block.GAME_DIAGLENGTH * 4, HasTeam.class);
				if (!nearbyWithFaction.isEmpty()) {
					Iterator<HasTeam> it = nearbyWithFaction.iterator();
					while (it.hasNext()) {
						HasTeam next = it.next();
						if (next instanceof MovableEntity && next.getTeamId() != getTeamId()) {
							enemyTarget = (MovableEntity) next;
						}
					}
				}
			}
		}
	}

	@Override
	public void jump(float velo, boolean playSound) {
		super.jump(velo, playSound);
		energy = 0;
	}

	/**
	 * attacks every other team id.
	 *
	 * @param id 0 is neutral, 1 is PC, 2 is player
	 */
	public void setTeamId(int id) {
		this.teamId = id;
		if (teamId == 1) {
			setName("Evil Robot");
		} else {
			setName("Friendly Robot");
		}
	}

	@Override
	public int getTeamId() {
		return teamId;
	}
	
	/**
	 * Set the target which the zombie follows.
	 *
	 * @param target an character
	 */
	public void setTarget(MovableEntity target) {
		this.enemyTarget = target;
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/**
	 *	
	 * @return if attack was started return true 
	 */
	protected boolean performAttack() {
		if (energy >= 1000) {
			energy = 0;//reset
			if (enemyTarget != null && getPosition().distanceTo(enemyTarget) < Block.GAME_EDGELENGTH * 2f) {
				SimpleEntity hit = (SimpleEntity) new SimpleEntity((byte) 33).spawn(enemyTarget.getPosition().cpy());
				hit.setAnimation(
					new EntityAnimation(new int[]{300}, true, false)
				);
				hit.setName("hit sprite");
				MessageManager.getInstance().dispatchMessage(this,
					enemyTarget,
					Events.damage.getId(),
					(byte) 1
				);
			}
			pauseMovementAnimation();
			attackInProgess = ATTACKTIME;//1500ms until the attack is done
			return true;
		}
		return false;
	}

	@Override
	public void disposeFromMap() {
		super.disposeFromMap();
		WE.SOUND.stop(RUNNINGSOUND, runningSound);
		runningSound = 0;
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

				if (teamId != 2) {
					new Money().spawn(getPosition().toPoint());
				}

				if (KILLSOUND != null) {
					WE.SOUND.play(KILLSOUND, getPosition());
				}
			}
		}
		return true;
	}

	/**
	 *
	 * @param type
	 */
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

	private void playAttackAnimation() {
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
}