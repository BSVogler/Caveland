package com.bombinggames.wurfelengine.core.console;

import com.bombinggames.wurfelengine.core.GameplayScreen;
import java.util.StringTokenizer;

/**
 *
 * @author Benedikt Vogler
 */
public interface ConsoleCommand {
	
	/**
	 * 
	 * @param parameters the value of parameters
	 * @param gameplay the value of gameplay
	 * @return the boolean 
	 */
	public abstract boolean perform(StringTokenizer parameters, GameplayScreen gameplay);
	
	/**
	 * always lowercase
	 * @return 
	 */
	public abstract String getCommandName();
}
