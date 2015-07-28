package com.bombinggames.caveland;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.caveland.GameObjects.collectibles.TFlint;
import com.bombinggames.wurfelengine.core.CommandsInterface;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class CavelandCommands implements CommandsInterface {
	private GameplayScreen gameplayRef;

	@Override
	public boolean executeCommand(String command) {
		if (command.length() <= 0) return false;
        StringTokenizer st = new StringTokenizer(command, " ");
		String first = st.nextToken().toLowerCase();
        switch (first) {
            case "givetnt":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(new TFlint());
				return true;
			 case "givewood":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(Collectible.create(CollectibleType.Wood));
				return true;
			case "givecoal":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(Collectible.create(CollectibleType.Coal));
				return true;
			case "giveironore":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(Collectible.create(CollectibleType.Ironore));
				return true;
			case "givesulfur":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(Collectible.create(CollectibleType.Sulfur));
				return true;
			case "giveiron":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(Collectible.create(CollectibleType.Iron));
				return true;
        }
		
		//sets the target of the selected portals
		if (first.toLowerCase().startsWith("setportaltarget")){
			if (!st.hasMoreTokens()) return false;
			int x = Integer.parseInt(st.nextToken());
			if (!st.hasMoreTokens()) return false;
			int y = Integer.parseInt(st.nextToken());
			if (!st.hasMoreTokens()) return false;
			int z = Integer.parseInt(st.nextToken());
			
			ArrayList<AbstractEntity> selected = gameplayRef.getEditorController().getSelectedEntities();
			for (AbstractEntity ent : selected) {
				if (ent instanceof Portal){
					((Portal)ent).setTarget(new Coordinate(x, y, z));
					return true;
				}
			}
		}
		
		return false;
	}
	
	 /**
     *
     * @param gameplayRef
     */
	@Override
    public void setGameplayRef(GameplayScreen gameplayRef) {
        this.gameplayRef = gameplayRef;
    }
	
}
