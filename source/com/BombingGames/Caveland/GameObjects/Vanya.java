package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.Caveland.Game.ChatBox;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.SimpleEntity;
import com.badlogic.gdx.math.Vector3;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 *
 * @author Benedikt Vogler
 */
public class Vanya extends MovableEntity implements Interactable, Serializable {
	private static final long serialVersionUID = 3L;
	private transient SimpleEntity interactButton;
	private transient int chatCounter;
	private transient ChatBox currentChat;

	public Vanya() {
		super(40, 0);
		setFloating(false);
		setName("Vanya");
		setJumpingSound("vanya_jump");
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
	public void showButton() {
		if (interactButton == null) {
			interactButton = (SimpleEntity) new SimpleEntity(23,1).spawn(
				getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH)
			);
			interactButton.setLightlevel(1);
			interactButton.setSaveToDisk(false);
		}
	}

	@Override
	public void hideButton() {
		if (interactButton != null) {
			interactButton.dispose();
			interactButton = null;
		}
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

	@Override
	public void dispose() {
		super.dispose();
		if (interactButton != null)
			interactButton.dispose();
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
		setFloating(false);
        setJumpingSound("vanya_jump");
    }
	
}

