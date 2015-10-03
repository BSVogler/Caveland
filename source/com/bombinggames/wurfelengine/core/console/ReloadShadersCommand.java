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
public class ReloadShadersCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		try {
			gameplay.getView().loadShaders();
		} catch (Exception ex) {
			WE.getConsole().add(ex.getMessage());
		}
		return true;
	}

	@Override
	public String getCommandName() {
		return "reloadshaders";
	}

	@Override
	public String getManual() {
		return "reloads the shaders";
	}
}
