/*
 * Copyright 2013 Benedikt Vogler.
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
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
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
package com.BombingGames.WurfelEngine.Core.Gameobjects;

import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.LightEngine.PseudoGrey;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.View;
import com.badlogic.gdx.graphics.Color;

/**
 *
 * @author Benedikt Vogler
 */
class EntityShadow extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * the parent class. The object where this is the shadow
	 */
    private AbstractEntity character;

    protected EntityShadow(AbstractEntity character) {
		super(32);
		this.character = character;
		setSaveToDisk(false);
    }

    @Override
    public void update(float dt) {
		if (character==null)
			dispose();
		else {
			Coordinate tmpPos = character.getPosition().getCoord().cpy();
			tmpPos.setZ(tmpPos.getZ());
			while (tmpPos.getZ() > 0 && tmpPos.cpy().addVector(new float[]{0, 0, -1}).getBlockClamp().isTransparent())
				tmpPos.addVector(new float[]{0, 0, -1});

			setPosition(character.getPosition().cpy());
			getPosition().setHeight(tmpPos.getHeight());
		}
    }

    @Override
    public void render(View view, Camera camera) {
		if (!shouldBeDisposed()){
			Color color = PseudoGrey.toColor(
					(character.getPosition().getHeight() - getPosition().getHeight())/Block.GAME_EDGELENGTH
					);//make color out of distance from player
			super.render(view, camera,color);
		}
    }
}