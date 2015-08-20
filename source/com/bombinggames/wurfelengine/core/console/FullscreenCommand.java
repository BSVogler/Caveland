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
public class FullscreenCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		WE.setFullscreen(!WE.isFullscreen());
        return true;
	}

	@Override
	public String getCommandName() {
		return "fullscreen";
	}

	@Override
	public String getManual() {
		return "toggles the fullscreen";
	}
	
}
