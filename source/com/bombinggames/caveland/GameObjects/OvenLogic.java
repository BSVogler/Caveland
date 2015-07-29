/*
 *
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
package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.CustomGameView;
import com.bombinggames.caveland.GameObjects.collectibles.Collectible;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleContainer;
import com.bombinggames.caveland.GameObjects.collectibles.CollectibleType;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Map.Point;

/**
 * The manager of the logic of the oven block.
 * @author Benedikt Vogler
 */
public class OvenLogic extends CollectibleContainer implements Interactable{
	private static final long serialVersionUID = 1L;
	private SmokeEmitter emitter;
	private final float PRODUCTIONTIME = 3000;
	private float productionCountDown;

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		emitter = (SmokeEmitter) new SmokeEmitter().spawn(point);
		emitter.setHidden(true);
		emitter.setParticleStartMovement(Vector3.Z.cpy());
		emitter.setParticleTTL(1000);
		return this;
	}
	
	@Override
	public void interact(CustomGameView view, AbstractEntity actor) {
		if (actor instanceof CustomPlayer){
			//lege objekte aus Inventar  hier rein
			Collectible frontItem = ((CustomPlayer) actor).getInventory().retrieveFrontItem();
			if (frontItem != null && (frontItem.getType()==CollectibleType.Coal || frontItem.getType()==CollectibleType.Ironore))
				addChild(frontItem);
		}
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		startProduction();
		
		float oldCountDown = productionCountDown;
		if (productionCountDown > 0)
			productionCountDown -= dt;
		
		if (productionCountDown<0)
			productionCountDown = 0;
		
		//if reached zero this frame
		if (oldCountDown > 0 && productionCountDown==0) {
			//produce
			Collectible coal = getCollectible(CollectibleType.Coal);
			if (coal != null) coal.dispose();
			Collectible ironore = getCollectible(CollectibleType.Ironore);
			if (ironore != null) ironore.dispose();
			( (Collectible) Collectible.create(CollectibleType.Iron).spawn(getPosition().toCoord().addVector(0, 0, 1).toPoint())).sparkle();
			emitter.setActive(false);
		}
	}
	
	public boolean canStartProduction(){
		if (productionCountDown==0) {
			Collectible coal = getCollectible(CollectibleType.Coal);
			if (coal == null) return false;
			Collectible ironore = getCollectible(CollectibleType.Ironore);
			if (ironore == null) return false;
			return true;
		} else {
			return false;
		}
	}
	
	protected void startProduction(){
		if (canStartProduction()) {
			emitter.setActive(true);
			productionCountDown = PRODUCTIONTIME;
		}
	}
}
