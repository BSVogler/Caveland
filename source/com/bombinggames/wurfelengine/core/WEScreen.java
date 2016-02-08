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
package com.bombinggames.wurfelengine.core;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.bombinggames.wurfelengine.WE;
import java.util.ArrayList;

/**
 * A WEScreen is a {@link Screen} which supports Wurfel Engine features like the
 * {@link com.bombinggames.wurfelengine.core.console.Console}.
 *
 * @author Benedikt Vogler
 */
public abstract class WEScreen implements Screen {

	ArrayList<Actor> buttons = new ArrayList<>(5);
	private int selection;

	@Override
	@SuppressWarnings("AssignmentToMethodParameter")
	public final void render(float delta) {
		delta *= 1000;//to ms
		if (delta >= WE.getCVars().getValueF("MaxDelta")) {
			delta = 1f / 60f;//if <1 FPS assume it was stopped and set delta to 16,66ms ^= 60FPS
		}
		renderImpl(delta);
		WE.updateAndRender(delta);
	}

	/**
	 * Main method which get's called every frame. Should be split up in data
	 * managment and data displaying.
	 *
	 * @param dt time in ms
	 */
	public abstract void renderImpl(float dt);

	public void addButton(Actor button) {
		buttons.add(button);
	}

	;
	
	public void select(int i) {
		selection = i;
	}

	public void enterSelection() {
		buttons.get(selection).fire(new ChangeListener.ChangeEvent());
	}

	public int getSelection() {
		return selection;
	}

	public int getButtonAmount() {
		return buttons.size();
	}

}
