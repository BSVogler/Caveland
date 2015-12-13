/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class ManCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (parameters.hasMoreElements()) {
			String command = parameters.nextToken();
			ArrayList<ConsoleCommand> registeredCommands = WE.getConsole().getRegisteredCommands();
			for (ConsoleCommand comm : registeredCommands) {
				if (comm.getCommandName().equals(command)) {
					WE.getConsole().add(comm.getManual());
					return true;
				}
			}
			//not found
			WE.getConsole().add("Not found");
			return false;
		} else {
			WE.getConsole().add("Parameter missing");
			return false;
		}
	}

	@Override
	public String getCommandName() {
		return "man";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "outputs the manual entry for this command";
	}
}
