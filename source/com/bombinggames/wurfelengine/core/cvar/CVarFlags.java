package com.bombinggames.wurfelengine.core.cvar;

/**
 * @since v1.4.2
 */
public enum CVarFlags {
	/**
	 * If changed is saved.
	 */
	CVAR_ARCHIVE, /**
	 * never saved to file.
	 */
	CVAR_VOLATILE, /**
	 * Gets in all cases saved.
	 */
	CVAR_ALWAYSSAVE

}
