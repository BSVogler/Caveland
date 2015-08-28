/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.Map.Generators.AirGenerator;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class FillWithAirCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (parameters.hasMoreElements()) {
			Controller.getMap().getChunk(
				Integer.valueOf(parameters.nextToken()),
				Integer.valueOf(parameters.nextToken())
			).fill(new AirGenerator());
			return true;
		}
		return false;
	}

	@Override
	public String getCommandName() {
		return "fillwithair";
	}

	@Override
	public String getManual() {
		return "fills chunk <x> <y> with air ";
	}
	
}
