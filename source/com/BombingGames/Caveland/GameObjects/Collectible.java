package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityAnimation;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.math.Vector3;
import java.io.IOException;
import java.io.Serializable;

/**
 * Etwas zum aufsammeln.
 *
 * @author Benedikt Vogler
 */
public class Collectible extends MovableEntity implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * a enum which lists the types of collectibles
	 */
	public static enum CollectibleType {

		/**
		 *
		 */
		WOOD((byte) 46),
		/**
		 * T.N.T.! I'm dynamite!
		 */
		EXPLOSIVES((byte) 47),
		/**
		 *
		 */
		IRONORE((byte) 48),
		/**
		 *
		 */
		COAL((byte) 49),
		/**
		 *
		 */
		CRISTALL((byte) 50),
		/**
		 *
		 */
		SULFUR((byte) 51);

		private byte id;

		private CollectibleType(byte id) {
			this.id = id;
		}

		public byte getId() {
			return id;
		}
	}

	/**
	 * factory method
	 *
	 * @param def
	 * @return
	 */
	public static Collectible create(CollectibleType def) {
		Collectible obj;
		if (def == CollectibleType.EXPLOSIVES) {
			obj = new TFlint();
		} else {
			obj = new Collectible(def);
		}

		return obj;
	}

	private CollectibleType def;
	/**
	 * the last object which held the item for pickup prevention
	 */
	private AbstractGameObject lastParent;
	private float timeParentBlocked = 1500;

	/**
	 *
	 * @param def
	 */
	protected Collectible(CollectibleType def) {
		super(def.id, 0);
		this.def = def;
		setFloating(false);
		enableShadow();
		//setSpeed(0.2f);
		setFriction(WE.CVARS.getValueF("friction"));
		setIndestructible(true);
		setAnimation(
			new EntityAnimation(
				new int[]{80, 80, 80, 80, 80},
				true,
				true
			)
		);
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
		setAnimation(
			new EntityAnimation(
				new int[]{80, 80, 80, 80, 80},
				true,
				true)
		);
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
		timeParentBlocked = 1500;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (timeParentBlocked > 0) {
			timeParentBlocked -= dt;
		}
	}

	/**
	 * can be picked up if timer has run out for this object
	 *
	 * @param parent
	 * @return true if can be picked up
	 */
	public boolean canBePickedByParent(AbstractGameObject parent) {
		return timeParentBlocked < 0 || !parent.equals(lastParent);
	}

	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Collectible(this);
	}

	/**
	 * some effect
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

	/**
	 * the action if you active the item in the inventory
	 */
	public void action() {

	}

	@Override
	public String getName() {
		return "Collectible"+def.name();
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
		def = CollectibleType.COAL;//todo, proper serialisation and ddeserialisation of enum
		//http://www.vineetmanohar.com/2010/01/3-ways-to-serialize-java-enums/
	}
}
