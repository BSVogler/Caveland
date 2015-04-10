package com.BombingGames.WurfelEngine.Core.CVar;

/**
 *
 * @author Benedikt Vogler
 */
public class BooleanCVar extends CVar {
	private boolean value;
	private Boolean defaultValue;
	

	public BooleanCVar(boolean value) {
		this.value = value;
		this.defaultValue = value;
	}
	
	@Override
	public Boolean getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) 
			this.value = value.equals("1");
		else 
			this.value = (boolean) value;
		if (flags == CVarFlags.CVAR_ARCHIVE) save();
	}

	@Override
	public String toString() {
		if (value)
			return "1";
		else
			return "0";
	}

	@Override
	public Boolean getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Boolean) defaultValue;
	}
	
}
