/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland;

import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
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

		ArrayList<AbstractEntity> selected = gameplay.getEditorController().getSelectedEntities();
		for (AbstractEntity ent : selected) {
			if (ent instanceof Portal){
				((Portal)ent).setTarget(new Coordinate(x, y, z));
				return true;
			}
		}
		return true;
	}

	@Override
	public String getCommandName() {
		return "portaltarget";
	}
	
}
