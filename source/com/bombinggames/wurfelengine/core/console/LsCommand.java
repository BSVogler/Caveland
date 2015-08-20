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
public class LsCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "ls";
	}

	@Override
	public boolean perform(StringTokenizer par1, GameplayScreen gameplay) {
		WE.getConsole().ls().forEach((dir) -> WE.getConsole().add(dir));
		return true;
	}

	@Override
	public String getManual() {
		return "shows the content of the directory.";
	}
}
