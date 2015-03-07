package com.BombingGames.WurfelEngine.Core;

/**
 *
 * @author Benedikt Vogler
 */
public interface CommandsInterface {

	public boolean executeCommand(String command);
	/**
	 * the gameplay on which the commands are executed
	 * @param gameplayRef 
	 */
	public void setGameplayRef(GameplayScreen gameplayRef);
}
