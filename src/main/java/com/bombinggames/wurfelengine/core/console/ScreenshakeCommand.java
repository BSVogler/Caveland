/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class ScreenshakeCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		int id = 0;
		if (parameters.hasMoreElements()){
			id = Integer.valueOf(parameters.nextToken());  
		}
		float amp = 10;
		if (parameters.hasMoreElements()){
			amp = Float.valueOf(parameters.nextToken());  
		}
		float t = 500;
		if (parameters.hasMoreElements()){
			t = Float.valueOf(parameters.nextToken());  
		}
		if (id < gameplay.getView().getCameras().size())
			gameplay.getView().getCameras().get(id).shake(amp, t);
		else {
			WE.getConsole().add("Camera ID out of range\n","System");
			return false;
		}
		return true;
	}

	@Override
	public String getCommandName() {
		return "screenshake";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "Shakes the screen. works only if in the game. Parameters: [cameraID] [amplitude] [time]";
	}
}
