package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
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
	public void action(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer) {
			SelectionWindow selectionWindow = new SelectionWindow(
				view,
				((CustomPlayer) actor).getPlayerNumber()
				);
			view.getStage().addActor(selectionWindow);
		}
		
	}
	
	private class SelectionWindow extends ActionBox{

		SelectionWindow(CustomGameView view, int playerId) {
			super(view, playerId, "Choose construction", BoxModes.SELECTION, null);
			addSelectionOptions("Oven","Rails","Factory");
		}
		
	}
	
}
