/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.gameobjects;

import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;

/**
 *
 * @author Benedikt S. Vogler
 */
public class ColorBall extends SimpleEntity {
	
	public ColorBall() {
		super((byte) 5);
		setMass(2);
		setName("ColorBall");
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		getColor().set(
			Math.abs((getPosition().x%1*getPosition().x)%1),
			Math.abs((getPosition().y%1*getPosition().y)%1),
			Math.abs((getPosition().z%1*getPosition().z)%1),
		1);
	}
	
	
	
}
