/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.gameobjects.EntityShadow;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;

/**
 *
 * @author Benedikt S. Vogler
 */
public class ColorBall extends MovableEntity {

	private static final long serialVersionUID = 1L;
	
	public ColorBall() {
		super((byte) 5);
		setMass(2);
		setName("ColorBall");
		addComponent(new EntityShadow());
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		getColor().set(
			Math.abs((getPosition().y%1*getPosition().x)%1),
			Math.abs((getPosition().y%1*getPosition().y)%1),
			Math.abs((getPosition().y%1*getPosition().z)%1),
		0.5f);
	}
	
	
	
}
