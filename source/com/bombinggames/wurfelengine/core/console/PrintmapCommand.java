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
public class PrintmapCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		//Controller.getMap().print();
		return false;
	}

	@Override
	public String getCommandName() {
		return "printmap";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "prints the map in the java console";
	}
}
