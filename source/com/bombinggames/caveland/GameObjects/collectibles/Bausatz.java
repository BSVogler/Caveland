package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.ActionBox.BoxModes;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.CustomPlayer;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;

/**
 *
 * @author Benedikt Vogler
 */
public class Bausatz extends Collectible {
	private static final long serialVersionUID = 1L;

	public Bausatz() {
		super(CollectibleType.Toolkit);
	}

	@Override
	public void action(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer) {
			new ActionBox(view, "Choose construction", BoxModes.SELECTION, null)
				.addSelectionNames("Oven","Rails","Factory")
				.setConfirmAction(
					(int result, CustomGameView view1, AbstractEntity actor1) -> {
						if (result==0) {
							//spawn construction site
							actor1.getPosition().toCoord().setBlock(Block.getInstance((byte) 11));
							new ConstructionSite((byte) 12, (byte) 0).spawn(actor1.getPosition().toCoord().toPoint());
							dispose();//dispose tool kit
						}
						return result;
					}
				)
				.register(view, ((CustomPlayer) actor).getPlayerNumber());
		}
	}
}
