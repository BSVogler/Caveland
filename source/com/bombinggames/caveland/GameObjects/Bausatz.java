package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;

/**
 *
 * @author Benedikt Vogler
 */
public class Bausatz extends Collectible {
	private static final long serialVersionUID = 1L;

	public Bausatz() {
		super(CollectibleType.TOOLKIT);
	}

	@Override
	public void action(GameView view, AbstractEntity actor) {
		SelectionWindow selectionWindow = new SelectionWindow(view.getStage());
		view.getStage().addActor(selectionWindow);
		
	}
	
	private class SelectionWindow extends ActionBox{

		SelectionWindow(Stage stage) {
			super(stage, "Choose construction", BoxModes.SELECTION, null);
			addSelectionOptions("Oven","Rails","Factory");
		}
		
	}
	
}
