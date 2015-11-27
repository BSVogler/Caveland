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
public class EditorCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "editor";
	}

	@Override
	public boolean perform(StringTokenizer par1, GameplayScreen gameplay) {
		WE.startEditor();
		return true;
	}

	@Override
	public String getManual() {
		return "loads the editor";
	}
	
}
