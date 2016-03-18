package com.bombinggames.caveland.gameobjects.collectibles;

import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.caveland.game.ActionBox.BoxModes;
import com.bombinggames.caveland.game.ActionBox.SelectionOption;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.game.CavelandBlocks.CLBlocks;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.caveland.gameobjects.Interactable;
import com.bombinggames.caveland.gameobjects.logicblocks.ConstructionSite;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.EntityBlock;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class ConstructionKit extends Collectible implements Interactable {
	private static final long serialVersionUID = 1L;
	private transient EntityBlock preview;

	/**
	 *
	 */
	public ConstructionKit() {
		super(CollectibleType.Toolkit);
	}
	
	/**
	 * 
	 * @param coord
	 * @param id the goal id
	 */
	protected void build(Coordinate coord, byte id){
		//don't allow lift on non-hole
		if (id == CLBlocks.LIFT.getId() && getPosition().toCoord().add(0, 0, -1).getBlockId() != CLBlocks.ENTRY.getId()){
			return;
		}
				
		//spawn construction site
		coord.setBlock((byte) 11);
		ConstructionSite constructionSiteLogic = (ConstructionSite) Controller.getMap().getLogic(coord);
		constructionSiteLogic.setResult(id, (byte) 0);
		WE.SOUND.play("metallic", coord);
		dispose();//dispose tool kit
		if (preview != null) {
			preview.dispose();
			preview = null;
		}
	}
	
	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof Ejira) {
			new ActionBox("Choose construction", BoxModes.SELECTION, null)
				.addSelection(
					new SelectionOption(
						CavelandBlocks.CLBlocks.OVEN.getId(),
						CavelandBlocks.CLBlocks.OVEN.toString()
					),
					new SelectionOption(
						CavelandBlocks.CLBlocks.ROBOTFACTORY.getId(),
						CavelandBlocks.CLBlocks.ROBOTFACTORY.toString()
					),
					new SelectionOption(
						CavelandBlocks.CLBlocks.POWERSTATION.getId(),
						CavelandBlocks.CLBlocks.POWERSTATION.toString()
					),
					new SelectionOption(
						CavelandBlocks.CLBlocks.TURRET.getId(),
						CavelandBlocks.CLBlocks.TURRET.toString()
					)
				)
				.setConfirmAction((SelectionOption result, AbstractEntity actor1) -> {
						build(actor1.getPosition().toCoord(), result.id);//spawn construction site
					}
				)
				.setCancelAction(
					(SelectionOption result, AbstractEntity actor1) -> {
						if (preview != null) {
							preview.dispose();
							preview = null;
						}
					}
				)
				.setSelectAction(
					(boolean up, SelectionOption result, AbstractEntity actor1) -> {
						//spawn preview
						if (preview == null) {
							preview = (EntityBlock) new EntityBlock(result.id,(byte) 0)
								.spawn(actor1.getPosition().toCoord().toPoint());
							preview.setName("preview");
							preview.setSaveToDisk(false);
							preview.setColor(new Color(0.8f, 0.8f, 1.0f, 0.3f));
						} else {
							preview.setSpriteId(result.id);
						}
					}
				)
				.register(view, ((Ejira) actor).getPlayerNumber(), actor, this);
		}
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return true;
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
}
