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
public class CreditsCommand implements ConsoleCommand{

	@Override
	public String getCommandName() {
		return "credits";
	}
	
	@Override
	public boolean perform(StringTokenizer par1, GameplayScreen gameplay) {
		WE.getConsole().add("Wurfel Engine Version:"+WE.VERSION+"\nFor a list of available commands visit the GitHub Wiki.\n"+WE.getCredits(), "System");
		return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "outputs the credits in the console";
	}
}
