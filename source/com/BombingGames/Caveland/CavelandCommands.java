package com.BombingGames.Caveland;

import com.BombingGames.Caveland.Game.CustomGameView;
import com.BombingGames.Caveland.GameObjects.TFlint;
import com.BombingGames.WurfelEngine.Core.CommandsInterface;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
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
