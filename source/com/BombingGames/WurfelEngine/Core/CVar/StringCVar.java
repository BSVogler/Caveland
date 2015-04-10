package com.BombingGames.WurfelEngine.Core.CVar;

/**
 *
 * @author Benedikt Vogler
 */
public class StringCVar extends CVar{
	private String value;
	private String defaultValue;

	public StringCVar(String value) {
		this.value = value;
		this.defaultValue = value;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		this.value = (String) value;
		if (flags == CVarFlags.CVAR_ARCHIVE) save();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return value;
	}
	
	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (String) defaultValue;
	}
	
	
}
