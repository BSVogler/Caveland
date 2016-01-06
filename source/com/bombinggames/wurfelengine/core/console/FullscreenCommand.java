/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.badlogic.gdx.Gdx;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class FullscreenCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		WE.setFullscreen(!Gdx.graphics.isFullscreen());
        return true;
	}

	@Override
	public String getCommandName() {
		return "fullscreen";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "toggles the fullscreen";
	}
	
}
