package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Block;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Position;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Tutorial owl.
 *
 * @author Benedikt Vogler
 */
public class Vanya extends MovableEntity implements Interactable, Telegraph {

	private static final long serialVersionUID = 3L;
	private transient int chatCounter;
	private transient ActionBox currentChat;
	private transient Waypoint nextWaypoint;
	private int tutorialStep = 0;
	private int completedTutorialStep =0;
	private transient AimBand particleBand;
	private transient LinkedList<Waypoint> waypoints = new LinkedList<>();//should be a queque

	/**
	 *
	 */
	public Vanya() {
		super((byte) 40, 3);
		setFloating(false);
		setName("Vanya");
		setJumpingSound("vanya_jump");
		setIndestructible(true);
		setContinuousWalkingAnimation(2f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	@Override
	public void update(float dt) {
		//float beforeUpdate = getMovement().z;
		super.update(dt);

		if (hasPosition()) {
			//destroy nearby vanyas
			getPosition().getEntitiesNearby(10 * Block.GAME_EDGELENGTH, Vanya.class).forEach((Object t) -> {
				if (!t.equals(this)) {
					((AbstractEntity) t).dispose();
				}
			});

			//update only if time is running
			if (dt > 0) {
				if (getPosition().isInMemoryAreaHorizontal() && isOnGround() && !isFloating()) {
					jump();
				}
			}

			if (waypoints == null) {
				waypoints = new LinkedList<>();
			}
			
			//go to movement goal
			if (nextWaypoint != null) {
				setFloating(nextWaypoint.isFly());
				if (reachedWaypoint()) {
					//movement logic
					Vector3 d = new Vector3();

					d.x = nextWaypoint.getPos().toPoint().getX() - getPosition().getX();
					d.y = nextWaypoint.getPos().toPoint().getY() - getPosition().getY();

					if (isFloating()) {
						if (getPosition().distanceToHorizontal(nextWaypoint.getPos()) > nextWaypoint.initialDistance/2) {
							//up
							d.nor();//direction only
							//limit flight height
							if (getPosition().toCoord().getZ() < Chunk.getBlocksZ() + 2) {
								d.z = 1;
							}
						} else {
							//down
							d.z = nextWaypoint.getPos().toPoint().getZ() - getPosition().getZ();
						}
						d.nor();//direction only
						d.scl(2f);
					} else {
						d.nor();//direction only
						d.scl(1.3f);
						d.z = getMovement().z;//keep vertical momentum
					}

					setMovement(d);// update the movement vector
				} else {
					//reached goal
					if (waypoints.isEmpty()) {
						nextWaypoint = null;
					} else {
						nextWaypoint = waypoints.poll();
						nextWaypoint.setActive(getPosition());
					}
				}
			}

			//look at players
			if (nextWaypoint == null) {
				ArrayList<Ejira> ejiraList = getPosition().getEntitiesNearbyHorizontal(4 * Block.GAME_EDGELENGTH, Ejira.class);
				if (!ejiraList.isEmpty()) {
					Vector3 vecToEjira = ejiraList.get(0).getPosition().cpy().sub(getPosition());
					setSpeedHorizontal(0);
					setOrientation(new Vector2(vecToEjira.x, vecToEjira.y).nor());
				}
			}
			
			if (particleBand != null){
				particleBand.update();
			}

			//check if players steped on next part of the tutorial
//			ArrayList<Ejira> players = getPosition().getEntitiesNearby(Block.GAME_EDGELENGTH * 10, Ejira.class);
//			for (Ejira player : players) {
//				if (player.getPosition().toCoord().getX() > -3 && player.getPosition().toCoord().getY() > 9) {
//					goTo(new Coordinate(0, 11, 5));
//				}
//				tutorialStep = 1;
//			}
			if(tutorialStep >= 1 && completedTutorialStep<1) {
				goTo(new Coordinate(-2, 1, 6));
				completedTutorialStep=1;
			}

			if(tutorialStep >= 2 && completedTutorialStep<2) {
				goTo(new Coordinate(-2, 10, 6));
				completedTutorialStep=2;
			}
			if (tutorialStep >= 3 && completedTutorialStep<3) {
				flyTo(new Coordinate(2, 13, 7));
				completedTutorialStep=3;
			}
			if (tutorialStep >= 4 && completedTutorialStep<4) {
				flyTo(new Coordinate(17, 24, 6));
				completedTutorialStep=4;
			}
			if (tutorialStep >= 5 && completedTutorialStep<5) {
				flyTo(new Coordinate(25, 20, 7));
				completedTutorialStep=5;
			}
		}
		
	}
	
	private boolean reachedWaypoint(){
		return getPosition().distanceToHorizontal(nextWaypoint.getPos()) > Block.GAME_EDGELENGTH / 4
				||
			(isFloating() && getPosition().distanceTo(nextWaypoint.getPos()) > Block.GAME_EDGELENGTH / 4 );
	}

	@Override
	public void jump() {
		super.jump(6, true);
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		nextChat(view, actor, true);
	}
	
	private void nextChat(CLGameView view, AbstractEntity actor, boolean confirm){
		if (actor instanceof Ejira) {
			if (currentChat != null) {
				currentChat.remove();
			}
			String text = "";
			boolean choice = false;
			switch (chatCounter) {
				case 0:
					text = "Oh hello! Are you alright? I saw your spaceship crashing. But you look good. \n"
						+ "Welcome to Caveland! I'm Vanya. I will be your guide.\n";
					chatCounter++;
					break;
				case 1:
					text = "I guess you wonder why I can speak. On this planet some things are bit different then you may be used to know.";
					chatCounter++;
					break;
				case 2:
					text = "I will explain you later. First let's go. It's dangerous here. Follow me.";
					chatCounter++;
					tutorialStep = 1;
					
					break;
					
				case 3:
					text="";
					if (tutorialStep > 0) {
						chatCounter++;
					} else {
						chatCounter=0;
					}
					break;
					
				case 4:
					text = "You can use your jetpack if you press the jump button a second time in air. Press it at the peak of your jump to jump higher.";
					tutorialStep = 2;
					chatCounter++;
					break;
					
				case 5:
					//if (tutorialStep > 2) {
						chatCounter++;
					//} else {
					//	chatCounter = 4;
					//}
					text="";
					break;
					
				case 6:
					text="You must go through the caves. Go though that hole there. I will see you at the other side.";
					tutorialStep = 3;
					chatCounter=7;
					break;
				case 7:
					chatCounter++;
					text="";
					break;
				case 8:
					text="At the end of the tracks there is a cable missing. You must repair it in order to continue. Collect one sulfur and one coal to craft dynamite.";
					chatCounter++;
					break;
				case 9:
					text="Once you have the dynamite you can use it to obtain iron ore. Put the iron ore and one coal block in the oven to get iron. With the iron you can craft a minecart.";
					chatCounter++;
					break;
				case 10:
					text="Don't destroy the track or else you must find another way to exit.";
					chatCounter++;
					break;
				case 11:
					text="";
					chatCounter++;
					break;
				case 12:
					text="Do you want me to wait at the other side?";
					choice = true;
					chatCounter++;
					break;
				case 13:
					text = "";
					if (confirm)
						tutorialStep = 4;
					break;
			}
			//register and open the chat
			if (!"".equals(text)) {
				WE.SOUND.play("huhu", getPosition());
				currentChat = new ActionBox(
					getName(),
					choice ? ActionBox.BoxModes.BOOLEAN : ActionBox.BoxModes.SIMPLE,
					text
				);
				if (chatCounter > 0) {
					currentChat.setConfirmAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
						nextChat(view, actor, true);
					});
					currentChat.setCancelAction((ActionBox.SelectionOption result, AbstractEntity actor1) -> {
						nextChat(view, actor, false);
					});
				}
				currentChat.register(view, ((Ejira) actor).getPlayerNumber(), actor, this);
			}
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean interactable() {
		return !isMovingToWaypoint();
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		setFloating(false);
		setJumpingSound("vanya_jump");
		setContinuousWalkingAnimation(2f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	/**
	 * Is Vanya moving to a coordinate.
	 *
	 * @return
	 */
	public boolean isMovingToWaypoint() {
		return nextWaypoint != null;
	}

	/**
	 *
	 * @param coord
	 */
	public void goTo(Coordinate coord) {
		if (nextWaypoint== null || nextWaypoint.getPos().distanceTo(coord) > 0) {//don't add twice
			waypoints.add(new Waypoint(false, coord));
			if (nextWaypoint == null) {
				if (!waypoints.isEmpty()) {
					nextWaypoint = waypoints.poll();
					nextWaypoint.setActive(getPosition());
				}
			}
		}
	}
	
	/**
	 *
	 * @param coord
	 */
	public void flyTo(Coordinate coord) {
		if (nextWaypoint== null || nextWaypoint.getPos().distanceTo(coord) > 0) {//don't add twice
			waypoints.add(new Waypoint(true, coord));
			if (nextWaypoint == null) {
				if (!waypoints.isEmpty()) {
					nextWaypoint = waypoints.poll();
					nextWaypoint.setActive(getPosition());
				}
			}
		}
	}

	/**
	 * the last toturial step which was completed
	 * @return 
	 */
	public int getCompletedTutorialStep() {
		return tutorialStep;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
	
	/**
	 * Can only increase
	 * @param tutorialStep	 
	*/
	public void setTutorialStep(int tutorialStep) {
		if (tutorialStep > this.tutorialStep)
			this.tutorialStep = tutorialStep;
	}

	private static class Waypoint {
		boolean fly;
		Position goalPos;
		float initialDistance;

		Waypoint(boolean fly, Position pos) {
			this.fly = fly;
			this.goalPos = pos;
		}
		
		public void setActive(Position pos){
			initialDistance = goalPos.distanceToHorizontal(pos);
		}

		/**
		 * 
		 * @return 
		 */
		public Position getPos() {
			return goalPos;
		}

		/**
		 * fly to the next waypoint
		 * @return 
		 */
		public boolean isFly() {
			return fly;
		}
		
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		 if (msg.message == Events.deselectInEditor.getId()){
			if (particleBand != null) {
				particleBand.dispose();
				particleBand = null;
			}
		} else if (msg.message == Events.selectInEditor.getId()){
			 if (nextWaypoint != null && particleBand == null) {
				 particleBand = new AimBand(this, nextWaypoint.getPos());
			 }
		}
		return true;
	}
}
