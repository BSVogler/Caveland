package com.bombinggames.caveland.gameobjects.collectibles;

import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.gameobjects.CLMovableEntity;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.EntityAnimation;
import java.io.IOException;
import java.io.Serializable;

/**
 * Etwas zum aufsammeln.
 *
 * @author Benedikt Vogler
 */
public class Collectible extends CLMovableEntity implements Serializable {

	private static final long serialVersionUID = 2L;

	private boolean preventPickup;
	private transient CollectibleType def;
	/**
	 * the last object which held the item for pickup prevention
	 */
	private transient AbstractGameObject lastParent;
	private transient float timeParentBlocked = 1500;

	/**
	 * Creates a new collectible.
	 * @param def the definition.
	 */
	protected Collectible(CollectibleType def) {
		super(def.getId(), 0);
		this.def = def;
		setFloating(false);
		enableShadow();
		//setSpeed(0.2f);
		setFriction(WE.getCVars().getValueF("friction"));
		setIndestructible(true);
		int[] animationsteps = new int[def.getAnimationSteps()];
		for (int i = 0; i < animationsteps.length; i++) {
			animationsteps[i] = 140;//time in ms for each step
		}

		addComponent(
			new EntityAnimation(
				animationsteps,
				true,
				true
			)
		);
		setName("Collectible " + def.name());
	}

	/**
	 * copy constructor
	 *
	 * @param collectible
	 */
	public Collectible(Collectible collectible) {
		super(collectible);
		setFloating(false);
		setIndestructible(true);
		addComponent(
			new EntityAnimation(
				new int[]{80, 80, 80, 80, 80},
				true,
				true
			)
		);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (timeParentBlocked > 0) {
			timeParentBlocked -= dt;
		}
	}

	/**
	 *
	 */
	public void allowPickup() {
		preventPickup = false;
	}

	/**
	 *
	 */
	public void preventPickup() {
		preventPickup = true;
	}

	/**
	 * prevents being picked up (msut be checked with {@link #canBePickedByParent(AbstractGameObject)
	 * }.
	 *
	 * @param lastParent
	 * @param time time in ms
	 * @see #canBePickedByParent(AbstractGameObject)
	 */
	public void preventPickup(AbstractGameObject lastParent, float time) {
		this.lastParent = lastParent;
		timeParentBlocked = time;
	}

	/**
	 * can be picked up if timer has run out for this object
	 *
	 * @param parent
	 * @return true if can be picked up
	 */
	public boolean canBePickedByParent(AbstractGameObject parent) {
		return !preventPickup && (timeParentBlocked < 0 || !parent.equals(lastParent));
	}

	/**
	 * some movement effect
	 */
	public void sparkle() {
		setMovement(new Vector3((float) Math.random() - 0.5f, (float) Math.random() - 0.5f, (float) Math.random()));
	}

	/**
	 *
	 * @return
	 */
	public CollectibleType getType() {
		return def;
	}

	@Override
	public String getName() {
		return def.name();
	}

	/**
	 * overrides deserialisation
	 *
	 * @param stream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject(); //fills fld1 and fld2;
		String defString = (String) stream.readObject();
		def = CollectibleType.fromValue(defString);
		//based on http://www.vineetmanohar.com/2010/01/3-ways-to-serialize-java-enums/
	}

	private void writeObject(java.io.ObjectOutputStream oos) throws IOException {
		// default serialization 
		oos.defaultWriteObject();
		// write the object
		oos.writeObject(def.name());
	}
}
