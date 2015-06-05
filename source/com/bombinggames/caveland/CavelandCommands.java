package com.bombinggames.caveland;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.caveland.GameObjects.TFlint;
import com.bombinggames.wurfelengine.Core.CommandsInterface;
import com.bombinggames.wurfelengine.Core.Controller;
import com.bombinggames.wurfelengine.Core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.Core.GameplayScreen;
import com.bombinggames.wurfelengine.Core.Map.Coordinate;
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
        }
		
		//sets the target of the selected portals
		if (first.toLowerCase().startsWith("setportaltarget")){
			ArrayList<AbstractEntity> selected = gameplayRef.getEditorController().getSelectedEntities();
			if (!st.hasMoreTokens()) return false;
					int x = Integer.parseInt(st.nextToken());
					if (!st.hasMoreTokens()) return false;
					int y = Integer.parseInt(st.nextToken());
					if (!st.hasMoreTokens()) return false;
					int z = Integer.parseInt(st.nextToken());
			for (AbstractEntity ent : selected) {
				if (ent instanceof Portal){
					((Portal)ent).setTarget(new Coordinate(Controller.getMap(), x, y, z));
					return true;
				}
			}
		}
		
		if (first.toLowerCase().startsWith("setportaltentry")){
			ArrayList<AbstractEntity> selected = gameplayRef.getEditorController().getSelectedEntities();
			if (!st.hasMoreTokens()) return false;
			boolean entry = "1".equals(st.nextToken());
			for (AbstractEntity ent : selected) {
				if (ent instanceof Portal){
						((Portal)ent).setCaveEntry(entry);
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
