package com.BombingGames.WurfelEngine.Core.CVar;

/**
 *
 * @author Benedikt Vogler
 */
public class FloatCVar extends CVar {
	private float value;
	private float defaultValue;

	public FloatCVar(float value) {
		this.value = value;
		defaultValue =value;
	}
	
	
	@Override
	public Float getValue() {
		return value;
	}

	@Override
	public void setValue(Object value) {
		if (value instanceof String) 
			this.value = Float.parseFloat((String) value);
		else 
			this.value = (float) value;
		if (flags == CVarFlags.CVAR_ARCHIVE) parent.save();
	}

	@Override
	public String toString() {
		return Float.toString(value);
	}

	@Override
	public Float getDefaultValue() {
		return defaultValue;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = (float) defaultValue;
	}
	
}
