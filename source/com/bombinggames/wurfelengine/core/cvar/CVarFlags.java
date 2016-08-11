package com.bombinggames.wurfelengine.core.cvar;

/**
 * @since v1.4.2
 */
public enum CVarFlags {
	/**
	 * Saved when cvars are saved.
	 */
	ARCHIVE, /**
	 * never saved to file.
	 */
	VOlATILE, /**
	 * Gets saved when changed.
	 */
	INSTANTSAVE

}
