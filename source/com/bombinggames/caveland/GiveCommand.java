/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland;

import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.console.ConsoleCommand;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class GiveCommand implements ConsoleCommand {

	@Override
	public boolean perform(StringTokenizer parameters, GameplayScreen gameplay) {
		try {
			((CustomGameView) gameplay.getView()).getPlayer(0).getInventory().add(
				CollectibleType.valueOf(parameters.nextToken()).createInstance()
			);
		} catch (IllegalArgumentException | java.lang.NullPointerException ex) {
			WE.getConsole().add("Collectible not found or game not running.", "System");
			return false;
		}
		return true;
	}

	@Override
	public String getCommandName() {
		return "give";
	}

	@Override
	public String getManual() {
		return "gives you a collectible\nParameters: [name of collectible]";
	}
}
