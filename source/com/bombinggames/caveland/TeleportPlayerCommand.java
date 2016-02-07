package com.bombinggames.caveland;

import com.bombinggames.caveland.game.CLGameController;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.console.ConsoleCommand;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class TeleportPlayerCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		int id = 0;
		if (!parameters.hasMoreTokens()) return false;
		int x = Integer.parseInt(parameters.nextToken());
		if (!parameters.hasMoreTokens()) return false;
		int y = Integer.parseInt(parameters.nextToken());
		if (!parameters.hasMoreTokens()) return false;
		int z = Integer.parseInt(parameters.nextToken());
		if (parameters.hasMoreTokens())
			id = Integer.parseInt(parameters.nextToken());
		((CLGameController) gameplay.getController()).getPlayer(id).setPosition(new Coordinate(x, y, z));
		return true;
	}

	@Override
	public String getCommandName() {
		return "tpplayer";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "teleports the player: <x> <y> <z> <id>";
	}
	
}
