package com.BombingGames.WurfelEngine.Core.CVar;

/**
 *
 * @author Benedikt Vogler
 */
public class IntCVar extends CVar {
	private int value;
	private Integer defaultValue;

	public IntCVar(int value) {
		this.value = value;
		this.defaultValue = value;
	}
	
	
	@Override
	public Integer getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) 
			this.value = Integer.parseInt((String) value);
		else 
			this.value = (int) value;
		if (flags == CVarFlags.CVAR_ARCHIVE) parent.save();
	}


	@Override
	public String toString() {
		return Integer.toString(value);
	}

	@Override
	public Integer getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (Integer) defaultValue;
	}
	
	
}
