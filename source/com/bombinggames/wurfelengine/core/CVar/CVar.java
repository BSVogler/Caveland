/*
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * If this software is used for a game the official „Wurfel Engine“ logo or its name must be
 *   visible in an intro screen or main menu.
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.bombinggames.wurfelengine.core.CVar;

/**
 *CVars start with a small letter and are CamelCase.
 * @author Benedikt Vogler
 * @since v1.4.2
 */
public abstract class CVar {
	/**
	 * @since v1.4.2
	 */
	public static enum CVarFlags {
		/**
		 * If changed is saved.
		*/
		CVAR_ARCHIVE, 
		/**
		 * never saved to file.
		 */
		 CVAR_VOLATILE,
		 /**
		 * Gets in all cases saved.
		 */
		 CVAR_ALWAYSSAVE
	}
	
	protected CVarSystem parent;
	protected CVarFlags flags;
	protected String name;
	
	public abstract Object getValue();
	public abstract void setValue(Object value);
	public abstract Object getDefaultValue();
	protected abstract void setDefaultValue(Object value);
	
	public String getName(){
		return name;
	}
	
	/**
	 * The values as string representation.
	 * @return 
	 */
	@Override
	public abstract String toString();
	
	/**
	 * Registering should only be done by the game or the engine in init phase. Also saves as defaultValue.
	 * if already registered updates the default and current value.
	 * @param name name of the cvar
	 * @param flag
	 * @param parent
	 * @since v1.4.2
	 */
	protected void register(String name, CVarFlags flag, CVarSystem parent){
		this.name = name;
		this.flags = flag;
		this.parent = parent;
	}
		
}
