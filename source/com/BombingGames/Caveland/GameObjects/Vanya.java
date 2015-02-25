package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.Caveland.Game.ChatBox;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Explosion;
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

	public Vanya() {
		super(40, 0);
		setFloating(false);
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
		//show display text
		new Explosion(1,500,view.getCameras().get(0)).spawn(getPosition());
		view.getStage().addActor(new ChatBox("I dare you motherfucker! Speak to me again and I will explode again!"));
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
        setJumpingSound("vanya_jump.wav");
    }
	
}

