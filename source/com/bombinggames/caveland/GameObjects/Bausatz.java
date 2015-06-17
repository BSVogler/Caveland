package com.bombinggames.caveland.GameObjects;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;

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
			SelectionWindow selectionWindow = new SelectionWindow(view);
			selectionWindow.register(view, ((CustomPlayer) actor).getPlayerNumber());
		}
	}
	
	private class SelectionWindow extends ActionBox{

		SelectionWindow(CustomGameView view) {
			super(view, "Choose construction", BoxModes.SELECTION, null);
			addSelectionNames("Oven","Rails","Factory");
		}

		@Override
		public int confirm(CustomGameView view, AbstractEntity actor) {
			int num = super.confirm(view, actor);
			//build
			if (num==0) {
				actor.getPosition().toCoord().setBlock( Block.getInstance((byte) 11) );
			}
			return num;
		}

		
		
		
	}
	
}
