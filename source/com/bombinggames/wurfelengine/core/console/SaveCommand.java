/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class SaveCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "save";
	}

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		return gameplay.getController().save();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "saves the currently loaded map in the currenty active save slot";
	}
}
