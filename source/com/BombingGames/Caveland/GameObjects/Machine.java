package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.RenderBlock;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class Machine extends RenderBlock {
	private static final long serialVersionUID = 1L;
	private int sulfurcount;
	private int coalcount;
	private int flintcount;

	/**
	 *
	 */
	public Machine() {
		super((byte) 60);
	}

	@Override
	public void update(float dt) {
		Coordinate coords = getPosition().cpy();
		ArrayList<MineCart> lorenIncoming = coords.goToNeighbour(1).getEntitysInside(MineCart.class);
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

		ArrayList<MineCart> lorenOutgoing = coords.goToNeighbour(3).getEntitysInside(MineCart.class);
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
				if (((Collectible) obj).getType()==Collectible.CollectibleType.COAL){
					coalcount++;
					content.remove(i);
				}else if (((Collectible) obj).getType()==Collectible.CollectibleType.SULFUR){
					sulfurcount++;
					content.remove(i);
				}
			}
		}
		return content;//return everything left
	}
	
	
	
}
