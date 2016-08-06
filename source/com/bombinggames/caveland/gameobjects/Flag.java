/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2015 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
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
package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.EntityAnimation;
import com.bombinggames.wurfelengine.core.map.Coordinate;

/**
 *
 * @author Benedikt Vogler
 */
public class Flag extends AbstractEntity implements Interactable, HasTeam {

	private static final long serialVersionUID = 1L;
	/**
	 * 0=neutral
	 */
	private int teamId = 0;

	public Flag() {
		super((byte) 21);
		EntityAnimation anim = new EntityAnimation(new int[]{300, 300}, true, true);
		anim.setOffset((float) (Math.random()*600f));
		addComponent(anim);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (!hasPosition()) return;

		switch (getTeamId()) {
			case 1:
				setColor(HasTeam.COLORENEMY.cpy());
				break;
			case 2:
				setColor(HasTeam.COLORTEAM.cpy());
				break;
			default:
				setColor(Color.WHITE.cpy());
				break;
		}
		Coordinate respawn = new Coordinate(
			WE.getCVarsSave().getValueI("respawnX"),
			WE.getCVarsSave().getValueI("respawnY"),
			WE.getCVarsSave().getValueI("respawnZ")+1
		);
		if (respawn.equals(getPosition().toCoord())){
			setColor(HasTeam.COLORTEAM.cpy().sub(0.1f, 0.1f, 0.1f, 0f));
		}
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}

	@Override
	public void interact(CLGameView view, AbstractEntity actor) {
		if (actor instanceof HasTeam) {
			if (getTeamId() != ((HasTeam) actor).getTeamId()) {
				teamId = ((HasTeam) actor).getTeamId();
			}
			if (teamId == ((HasTeam) actor).getTeamId()) {
				WE.getCVarsSave().get("respawnX").setValue(getPosition().toCoord().getX());
				WE.getCVarsSave().get("respawnY").setValue(getPosition().toCoord().getY());
				WE.getCVarsSave().get("respawnZ").setValue(getPosition().toCoord().getZ() - 1);
			}
		}
	}

	@Override
	public boolean interactable() {
		return true;
	}

	@Override
	public boolean interactableOnlyWithPickup() {
		return false;
	}

	@Override
	public int getTeamId() {
		return teamId;
	}

}
