package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.Caveland.Game.ChatBox;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.math.Vector3;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 *Tutorial owl.
 * @author Benedikt Vogler
 */
public class Vanya extends AbstractInteractable {
	private static final long serialVersionUID = 3L;
	private transient int chatCounter;
	private transient ChatBox currentChat;

	public Vanya() {
		super(40, 3);
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
		
		//höchster Punkt erreicht
		//if (beforeUpdate>0 && getMovement().z<0)
			//new BlümchenKacke().spawn(getPosition().cpy());
		
		if (dt>0) {//update only if time is running
			if (getPosition().isInMemoryHorizontal() && isOnGround()) jump();
		}
	}

	@Override
	public void jump() {
		super.jump(6, true);
	}

	
	@Override
	public void interact(AbstractEntity actor, GameView view) {
		//show display textnew Explosion(1,500,view.getCameras().get(0)).spawn(getPosition());
		chatCounter++;
		if (currentChat!=null)
			currentChat.remove();
		String text = "";
		switch(chatCounter) {
			case 1:
				text = "Oh hello! Are you alright? I saw an spaceship crashing and I don't know you so I assume you are new here. \n"
					+ "Welcome to Caveland! I will be your guide.\n";
				break;
			case 2:	
				text = " I guess you wonder why I can speak. On this planet some things are bit different then you may be used to know.";
				break;
			case 3:	
				text = "You should definetely check the map editor by pressing 'G'.";
				break;
			case 4:	
				chatCounter = 0;
				break;
		}
		if (!"".equals(text)) {
			currentChat = new ChatBox(view.getStage(), getName(), text);
			view.getStage().addActor(currentChat);
		}
	}

	private class BlümchenKacke extends MovableEntity {
		private static final long serialVersionUID = 1L;

		BlümchenKacke() {
			super(41, 0);
			setMovement(new Vector3(0,0,-1));
			setFloating(false);
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
	
}

