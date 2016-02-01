/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class TeleportCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (!parameters.hasMoreTokens()) {
				WE.getConsole().add("Expected more parameters", "System");
				return false;
			}
			int x = Integer.parseInt(parameters.nextToken());
			if (!parameters.hasMoreTokens()) {
				WE.getConsole().add("Expected more parameters", "System");
				return false;
			}
			int y = Integer.parseInt(parameters.nextToken());
			gameplay.getView().getCameras().get(0).setCenter(new Coordinate(x, y, 0).toPoint());
			return true;
	}

	@Override
	public String getCommandName() {
		return "tp";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "set the focus of the camera.\nParameters: [x game world][y game world]";
	}
	
	
	
}
