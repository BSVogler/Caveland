package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Tutorial owl.
 *
 * @author Benedikt Vogler
 */
public class Vanya extends MovableEntity implements Interactable {

	private static final long serialVersionUID = 3L;
	private transient int chatCounter;
	private transient ActionBox currentChat;
	private Coordinate movementGoal;
	private int completedTutorialStep = 0;
	private float distanceWaypoint;
	private AimBand particleBand;

	/**
	 *
	 */
	public Vanya() {
		super((byte) 40, 3);
		setFloating(false);
		setName("Vanya");
		setJumpingSound("vanya_jump");
		setIndestructible(true);
		setWalkingSpeedIndependentAnimation(2f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	@Override
	public void update(float dt) {
		//float beforeUpdate = getMovement().z;
		super.update(dt);

		if (isSpawned()) {
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

			//go to movement goal
			if (movementGoal != null && getPosition().distanceToHorizontal(movementGoal) > Block.GAME_EDGELENGTH / 4) {
				//movement logic
				Vector3 d = new Vector3();

				d.x = movementGoal.toPoint().getX() - getPosition().getX();
				d.y = movementGoal.toPoint().getY() - getPosition().getY();
				
				if (isFloating()) {
					if (getPosition().distanceToHorizontal(movementGoal) > distanceWaypoint/2) {
						//up
						d.nor();//direction only
						if (getPosition().toCoord().getZ() < Chunk.getBlocksZ()+2)
							d.z = 1;
					} else {
						//down
						d.z = movementGoal.toPoint().getZ() - getPosition().getZ();
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
				movementGoal = null;
			}

			//look at players
			if (movementGoal == null) {
				ArrayList<Ejira> ejiraList = getPosition().getEntitiesNearbyHorizontal(4 * Block.GAME_EDGELENGTH, Ejira.class);
				if (!ejiraList.isEmpty()) {
					Vector3 vec3 = ejiraList.get(0).getPosition().getVector().sub(getPosition().toPoint().getVector());
					setSpeedHorizontal(0);
					setOrientation(new Vector2(vec3.x, vec3.y).nor());
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

			if(completedTutorialStep == 1)
				goTo(new Coordinate(-2, 10, 6));
			if(completedTutorialStep == 2)
				flyTo(new Coordinate(2, 13, 7));
			if(completedTutorialStep == 3)
				flyTo(new Coordinate(17, 24, 4));
			if(completedTutorialStep == 3)
				flyTo(new Coordinate(25, 20, 7));
		}
		
	}

	@Override
	public void jump() {
		super.jump(6, true);
	}

	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		nextChat(view, actor, true);
	}
	
	private void nextChat(CustomGameView view, AbstractEntity actor, boolean confirm){
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
					completedTutorialStep = 1;
					
					break;
					
				case 3:
					text="";
					if (completedTutorialStep > 0) {
						chatCounter++;
					} else {
						chatCounter=0;
					}
					break;
					
				case 4:
					text = "You can use your jetpack if you press the jump button a second time in air. Press it at the peak of your jump to jump higher.";
					completedTutorialStep = 2;
					chatCounter++;
					break;
					
				case 5:
					//if (completedTutorialStep > 2) {
						chatCounter++;
					//} else {
					//	chatCounter = 4;
					//}
					text="";
					break;
					
				case 6:
					text="You must go through the caves. Go though that hole there. I will see you at the other side.";
					completedTutorialStep = 3;
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
						completedTutorialStep = 4;
					break;
			}
			//register and open the chat
			if (!"".equals(text)) {
				WE.SOUND.play("huhu", getPosition());
				currentChat = new ActionBox(getName(), choice ? ActionBox.BoxModes.BOOLEAN : ActionBox.BoxModes.SIMPLE, text);
				if (chatCounter > 0) {
					currentChat.setConfirmAction((int result, AbstractEntity actor1) -> {
						nextChat(view, actor, true);
					});
					currentChat.setCancelAction((int result, AbstractEntity actor1) -> {
						nextChat(view, actor, false);
					});
				}
				currentChat.register(view, ((Ejira) actor).getPlayerNumber(), actor);
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
		setWalkingSpeedIndependentAnimation(2f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	/**
	 * Is Vanya moving to a coordinate.
	 *
	 * @return
	 */
	public boolean isMovingToWaypoint() {
		return movementGoal != null;
	}

	/**
	 *
	 * @param coord
	 */
	public void goTo(Coordinate coord) {
		if (getPosition().distanceToHorizontal(coord) > Block.GAME_EDGELENGTH / 4) {
			movementGoal = coord;
			distanceWaypoint = getPosition().distanceToHorizontal(coord);
		}
	}
	
	public void flyTo(Coordinate coord) {
		if (getPosition().distanceToHorizontal(coord) > Block.GAME_EDGELENGTH / 4) {
			setFloating(true);
			movementGoal = coord;
			distanceWaypoint = getPosition().distanceToHorizontal(coord);
		}
	}

	/**
	 * the last toturial step which was completed
	 * @return 
	 */
	public int getCompletedTutorialStep() {
		return completedTutorialStep;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}
	
	@Override
	public void onSelectInEditor(){
		if (movementGoal != null)
			particleBand = new AimBand(this, movementGoal);
	}
	
	@Override
	public void onUnSelectInEditor(){
		if (particleBand != null)
			particleBand = null;
	}

	/**
	 * Can only increase
	 * @param tutorialStep	 
	*/
	public void setTutorialStep(int tutorialStep) {
		if (tutorialStep > this.completedTutorialStep)
			this.completedTutorialStep = tutorialStep;
	}
	
}
