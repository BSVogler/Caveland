package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 *Tutorial owl.
 * @author Benedikt Vogler
 */
public class Vanya extends MovableEntity implements Interactable {
	private static final long serialVersionUID = 3L;
	private transient int chatCounter;
	private transient ActionBox currentChat;
	private Coordinate movementGoal;

	/**
	 *
	 */
	public Vanya() {
		super((byte) 40, 3);
		setFloating(false);
		setName("Vanya");
		setJumpingSound("vanya_jump");
		setWalkingSpeedIndependentAnimation(2f);
		setWalkingAnimationCycling(false);
		setWalkingStepMode(false);
	}

	@Override
	public void update(float dt) {
		//float beforeUpdate = getMovement().z;
		super.update(dt);
		
		//destroy nearby vanyas
		getPosition().getEntitiesNearby(10*Block.GAME_EDGELENGTH, Vanya.class).forEach(( Object t) -> {
			((AbstractEntity)t).dispose();
		});
		//höchster Punkt erreicht
		//if (beforeUpdate>0 && getMovement().z<0)
			//new BlümchenKacke().spawn(getPosition().cpy());
		
		if (dt > 0) {//update only if time is running
			if (isSpawned() && getPosition().isInMemoryAreaHorizontal() && isOnGround()) {
				jump();
			}
		}
		
		//go to movement goal
		if (movementGoal != null && getPosition().distanceToHorizontal(movementGoal) > Block.GAME_EDGELENGTH/4){
			//movement logic
			Vector3 d = new Vector3();

			d.x = movementGoal.toPoint().getX() - getPosition().getX();
			d.y = movementGoal.toPoint().getY() - getPosition().getY();
			d.nor();//direction only
			d.scl(1.3f);
			d.z = getMovement().z;

			setMovement(d);// update the movement vector
		} else {
			movementGoal = null;
		}
		
		//look at player
		if (movementGoal == null && isSpawned()){
			ArrayList<Ejira> ejiraList = getPosition().getEntitiesNearbyHorizontal(4*Block.GAME_EDGELENGTH, Ejira.class);
			if (!ejiraList.isEmpty()) {
				Vector3 vec3 = ejiraList.get(0).getPosition().getVector().sub(getPosition().toPoint().getVector());
				setOrientation(new Vector2(vec3.x, vec3.y).nor());
			}
		}
	}

	@Override
	public void jump() {
		super.jump(6, true);
	}

	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira){
			chatCounter++;
			if (currentChat!=null)
				currentChat.remove();
			String text = "";
			switch(chatCounter) {
				case 1:
					text = "Oh hello! Are you alright? I saw your spaceship crashing. But you look good. \n"
						+ "Welcome to Caveland! I'm Vanya. I will be your guide.\n";
					break;
				case 2:
					text = "I guess you wonder why I can speak. On this planet some things are bit different then you may be used to know.";
					break;
				case 3:
					text = "I will explain you later. First let's go. It's dangerous here. (Follow me. [Next Update])";
					chatCounter = 0;
					break;
			}
			if (!"".equals(text)) {
				currentChat = new ActionBox(getName(), ActionBox.BoxModes.SIMPLE, text);
				if (chatCounter > 0) {
					currentChat.setConfirmAction((int result, AbstractEntity actor1) -> {
						interact(view, actor);
					});
				}
				currentChat.register(view, ((Ejira)actor).getPlayerNumber(), actor);
			}
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public boolean interactable() {
		return true;
	}

	private class BlümchenKacke extends MovableEntity {
		private static final long serialVersionUID = 1L;

		BlümchenKacke() {
			super((byte) 41, 0);
			setMovement(new Vector3(0,0,-1));
			setFloating(false);
		}

		@Override
		public MovableEntity clone() throws CloneNotSupportedException {
			return super.clone();
		}
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
	 * 
	 * @param coord 
	 */
	public void goTo(Coordinate coord){
		movementGoal = coord;
	}
}

