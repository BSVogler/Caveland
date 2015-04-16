package com.BombingGames.WurfelEngine.Core;

/**
 *An interface for objeccts which can execute a Wurfel Engine command.
 * @author Benedikt Vogler
 */
public interface CommandsInterface {

	/**
	 * Delegates commands like a factory method but does not spawn objects.
	 * @param command the command which should be executed
	 * @return true if it worked. false if the command failed
	 */
	public boolean executeCommand(String command);
	
	/**
	 * the gameplay on which the commands are executed
	 * @param gameplayRef 
	 */
	public void setGameplayRef(GameplayScreen gameplayRef);
}
