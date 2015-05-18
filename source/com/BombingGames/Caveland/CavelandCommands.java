package com.bombinggames.caveland;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.TFlint;
import com.bombinggames.wurfelengine.Core.CommandsInterface;
import com.bombinggames.wurfelengine.Core.GameplayScreen;
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
		String first = st.nextToken();
        switch (first) {
            case "giveTNT":
				((CustomGameView) gameplayRef.getView()).getPlayer(0).getInventory().add(new TFlint());
				return true;
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
