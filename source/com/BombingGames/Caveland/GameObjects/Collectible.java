package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.EntityAnimation;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.math.Vector3;
import java.io.IOException;
import java.io.Serializable;

/**
 * Etwas zum aufsammeln.
 * @author Benedikt Vogler
 */
public class Collectible extends MovableEntity implements Serializable {
	private static final long serialVersionUID = 2L;

	/**
	 * a enum which lists the types of collectibles
	 */
	public static enum CollectibleType {
		WOOD(46),
		EXPLOSIVES(47),
		IRONORE(48),
		COAL(49),
		CRISTALL(50),
		SULFUR(51);
		
		private int id;

		private CollectibleType(int id) {
			this.id = id;
		}
	}

	/**
	 * factory method
	 * @param def
	 * @return 
	 */
	public static Collectible create(CollectibleType def){
		if (def==CollectibleType.EXPLOSIVES)
			return new TFlint(def);
		else return new Collectible(def);
	}
	
	private CollectibleType def;

	protected Collectible(CollectibleType def) {
		super(def.id, 0);
		this.def = def;
		setFloating(false);
		enableShadow();
		//setSpeed(0.2f);
		setFriction(CVar.get("friction").getValuef());
		setIndestructible(true);
		setCollectable(true);
		setAnimation(
			new EntityAnimation(
				new int[]{80, 80, 80, 80, 80},
				true,
				true)
		);
	}
	
	/**
	 * copy c
	 * @param collectible 
	 */
	public Collectible(Collectible collectible) {
		super(collectible);
		setFloating(false);
		setIndestructible(true);
		setCollectable(true);
		setAnimation(
			new EntityAnimation(
				new int[]{80, 80, 80, 80, 80},
				true,
				true)
		);
	}


	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Collectible(this);
	}
	
	/**
	 * some effect
	 */
	public void sparkle(){
		setMovement(new Vector3((float) Math.random()-0.5f,(float) Math.random()-0.5f,(float)  Math.random()));
	}

	public CollectibleType getType() {
		return def;
	}
	
	/**
	 * overrides deserialisation
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
