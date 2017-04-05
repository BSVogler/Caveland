package com.bombinggames.caveland.gameobjects.logicblocks;

import com.bombinggames.caveland.gameobjects.MineCart;
import com.bombinggames.caveland.gameobjects.collectibles.Collectible;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleType;
import com.bombinggames.caveland.gameobjects.collectibles.Flint;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Benedikt Vogler
 */
public class Machine extends AbstractBlockLogicExtension  {
	private static final long serialVersionUID = 1L;
	private int sulfurcount;
	private int coalcount;
	private int flintcount;

	/**
	 *
	 * @param block
	 * @param coord
	 */
	public Machine(byte block, Coordinate coord) {
		super(block, coord);
	}

	/**
	 *
	 * @param dt
	 */
	@Override
	public void update(float dt) {
		Coordinate coords = getPosition().cpy();
		LinkedList<MineCart> lorenIncoming = coords.goToNeighbour(1).getEntitiesInside(MineCart.class);
		if (lorenIncoming.size()>0) {
			MineCart loreIncoming = lorenIncoming.get(0);
			ArrayList<MovableEntity> content = loreIncoming.getContent();
			if (content != null && content.size()>0)
				loreIncoming.addAll(fillContent(content));
			loreIncoming.turn();
			loreIncoming.addToHor(1);
		}
		
		//produce 2 sulfur + 1 coal = 1 flint
		for (int i = 0; i < coalcount; i++) {
			if (sulfurcount>1){
				sulfurcount--;
				flintcount++;
			}else {
				break;
			}
		}

		LinkedList<MineCart> lorenOutgoing = coords.goToNeighbour(3).getEntitiesInside(MineCart.class);
		if (lorenIncoming.size()>0 && flintcount>0){//if loreIncoming davor && produkt im speicher
			if (lorenOutgoing.get(0).add(new Flint()))
				flintcount--;
		}

		//füllen und losschicken
	}

	/**
	 * removes relevant objects
	 * @param content
	 * @return 
	 */
	private ArrayList<MovableEntity> fillContent(ArrayList<MovableEntity> content) {
		for (int i = 0; i < content.size(); i++) {
			MovableEntity obj = content.get(i);
			if (obj instanceof Collectible){
				if (((Collectible) obj).getType()==CollectibleType.Coal){
					coalcount++;
					content.remove(i);
				}else if (((Collectible) obj).getType()==CollectibleType.Sulfur){
					sulfurcount++;
					content.remove(i);
				}
			}
		}
		return content;//return everything left
	}

	@Override
	public void dispose() {
	}
}
