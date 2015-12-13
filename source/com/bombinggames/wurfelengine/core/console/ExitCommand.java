/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.badlogic.gdx.Gdx;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class ExitCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "exit";
	}
	
	@Override
	public boolean perform(StringTokenizer par1, GameplayScreen gameplay) {
		Gdx.app.exit();
		return false;//hey, your getting a response-> it failed
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "exits the game";
	}
}
