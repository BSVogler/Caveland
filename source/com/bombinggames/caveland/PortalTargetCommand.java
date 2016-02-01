package com.bombinggames.caveland;

import com.bombinggames.caveland.gameobjects.ExitPortal;
import com.bombinggames.caveland.gameobjects.Portal;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.console.ConsoleCommand;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class PortalTargetCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (!parameters.hasMoreTokens()) return false;
		int x = Integer.parseInt(parameters.nextToken());
		if (!parameters.hasMoreTokens()) return false;
		int y = Integer.parseInt(parameters.nextToken());
		if (!parameters.hasMoreTokens()) return false;
		int z = Integer.parseInt(parameters.nextToken());

		ArrayList<AbstractEntity> selected = gameplay.getController().getSelectedEntities();
		for (AbstractEntity ent : selected) {
			if (ent instanceof Portal){
				((Portal)ent).setTarget(new Coordinate(x, y, z));
				return true;
			} else if (ent instanceof ExitPortal){
				((ExitPortal)ent).setTarget(new Coordinate(x, y, z));
				return true;
			}
		}
		return true;
	}

	@Override
	public String getCommandName() {
		return "portaltarget";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "Sets the target of the portal.\nParameters: [x][y][z]";
	}
}
