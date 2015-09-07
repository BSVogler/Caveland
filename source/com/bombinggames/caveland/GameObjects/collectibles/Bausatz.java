package com.bombinggames.caveland.GameObjects.collectibles;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.Game.ActionBox;
import com.bombinggames.caveland.Game.ActionBox.BoxModes;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Ejira;
import com.bombinggames.caveland.GameObjects.logicblocks.ConstructionSite;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityBlock;
import com.bombinggames.wurfelengine.core.Map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Bausatz extends Collectible {
	private static final long serialVersionUID = 1L;
	private transient EntityBlock preview;

	public Bausatz() {
		super(CollectibleType.Toolkit);
	}
	
	/**
	 * 
	 * @param coord
	 * @param id the goal id
	 */
	protected void build(Coordinate coord, byte id){
		if (id == 15 && getPosition().toCoord().addVector(0, 0, -1).getBlock().getId() != 16){
			return;
		}
				
		//spawn construction site
		coord.setBlock(Block.getInstance((byte) 11));
		ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(coord);
		constructionSiteLogic.setResult(id, (byte) 0);
		WE.SOUND.play("metallic");
		dispose();//dispose tool kit
		if (preview != null) {
			preview.dispose();
			preview = null;
		}
	}
	
	byte getResult(int index){
		if (index == 0) {
			return 12;
		} else if (index == 3) {
			return 15;
		} else {
			return 12;
		}
	}

	@Override
	public void action(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox("Choose construction", BoxModes.SELECTION, null)
				.addSelectionNames(
					"Oven",
					"Robot Factory (not implemented yet)",
					"Power Station",
					"Lift"
				)
				.setConfirmAction((int result, AbstractEntity actor1) -> {
						build(actor1.getPosition().toCoord(), getResult(result));//spawn construction site
					}
				)
				.setCancelAction(
					(int result, AbstractEntity actor1) -> {
						if (preview != null) {
							preview.dispose();
							preview = null;
						}
					}
				)
				.setSelectAction(
					(int result, AbstractEntity actor1) -> {
						//spawn rails
						if (preview == null) {
							preview = (EntityBlock) new EntityBlock(getResult(result),(byte) 0)
								.spawn(actor1.getPosition().toCoord().toPoint());
							preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
						} else {
							preview.setValue((byte) result);
						}
					}
				)
				.register(view, ((Ejira) actor).getPlayerNumber(), actor);
		}
	}
}
