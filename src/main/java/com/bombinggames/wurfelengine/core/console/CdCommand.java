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
public class CdCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "cd";
	}

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		if (!parameters.hasMoreElements()) {
			return false;
		}
		String path = WE.getConsole().getPath();

		String enteredPath = parameters.nextToken();
		if (enteredPath.length() > 0) {
			switch (enteredPath) {
				case "/":
					path = "/";
					break;
				case "..":
					if (path.length() > 1) {
						path = "/";
					}
					break;
				default:
					if (WE.getConsole().getPath().length() > 0) {
						path = path.concat(":");
					}
					path = path.concat(enteredPath);//then add new path
					break;
			}
			if (path.length() > 0) {
				WE.getConsole().setPath(path);
			}
		}
		return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "change the directory";
	}

}
