package com.bombinggames.caveland.GameObjects.collectibles;

import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.ActionBox.BoxModes;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.logicblocks.ConstructionSite;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Bausatz extends Collectible {
	private static final long serialVersionUID = 1L;

	public Bausatz() {
		super(CollectibleType.Toolkit);
	}
	
	protected void build(Coordinate coord, byte id){
		//spawn construction site
		coord.setBlock(Block.getInstance((byte) 11));
		ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(coord);
		constructionSiteLogic.setResult(id, (byte) 0);
		WE.getEngineView().getSoundEngine().play("metallic");
		dispose();//dispose tool kit
	}

	@Override
	public void action(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox(view, "Choose construction", BoxModes.SELECTION, null)
				.addSelectionNames(
					"Oven",
					"Robot Factory (not implemented yet)",
					"Power Station",
					"Lift"
				)
				.setConfirmAction((int result, CustomGameView view1, AbstractEntity actor1) -> {
						if (result==0) {
							build(actor1.getPosition().toCoord(), (byte) 12);//spawn construction site
						} else if (result==3) {
							build(actor1.getPosition().toCoord(), (byte) 15);//spawn construction site
						}
						return result;
					}
				)
				.register(view, ((Ejira) actor).getPlayerNumber());
		}
	}
}
