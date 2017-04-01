/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.BenchmarkBall;
import com.bombinggames.wurfelengine.core.GameplayScreen;
import com.bombinggames.wurfelengine.core.map.Chunk;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public class BenchmarkCommand implements ConsoleCommand {

	@Override
	public String getCommandName() {
		return "benchmark";
	}
	

	@Override
	public boolean perform(StringTokenizer par1, GameplayScreen gameplay) {
		new BenchmarkBall().spawn(Controller.getMap().getCenter(Chunk.getGameHeight()));
            //add("Spawned a benchmark ball.", "System");
        return true;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getManual() {
		return "spawns a benchmark ball";
	}
	
}
