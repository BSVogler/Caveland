/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import com.bombinggames.wurfelengine.core.Gameobjects.EntityNode;

/**
 * The node manages the ejira. You controll only the custom player of it.
 *
 * @author Benedikt Vogler
 */
public class EjiraNode extends EntityNode {

	private static final long serialVersionUID = 1L;

	private transient final SmokeEmitter emitter;
	private transient final SmokeEmitter emitter2;
	private transient final Ejira ejira;

	public EjiraNode(int number) {
		ejira = new Ejira(number);
		addChild(ejira);

		emitter = new SmokeEmitter();
		emitter.setParticleDelay(10);
		emitter.setParticleTTL(800);
		emitter.setParticleBrightness(0.1f);
		emitter.setActive(false);
		emitter.setHidden(true);
		addChild(emitter);
		SuperGlue connection1 = new SuperGlue(this, emitter);
		connection1.setOffset(new Vector3(-20, 0, Block.GAME_EDGELENGTH2));
		addChild(connection1);

		emitter2 = new SmokeEmitter();
		emitter2.setParticleDelay(10);
		emitter2.setParticleTTL(800);
		emitter2.setParticleBrightness(0.1f);
		emitter2.setHidden(true);
		emitter2.setActive(false);
		addChild(emitter2);
		SuperGlue conection2 = new SuperGlue(this, emitter2);
		conection2.setOffset(new Vector3(20, 0, Block.GAME_EDGELENGTH2));
		addChild(conection2);

		setSaveToDisk(false);//don't save this and the children
	}

	public SmokeEmitter getEmitter() {
		return emitter;
	}

	public SmokeEmitter getEmitter2() {
		return emitter2;
	}

	public Ejira getEjira() {
		return ejira;
	}
}
