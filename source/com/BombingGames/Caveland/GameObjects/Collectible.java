package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.badlogic.gdx.graphics.Color;
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
	public static enum ColTypes {
		IRONORE(Color.RED.cpy(), 46),
		COAL(Color.DARK_GRAY.cpy(), 47),
		GOLD(Color.YELLOW.cpy(), 48),
		IRON(Color.GRAY.cpy(), 49),
		SULFUR(new Color(0.8f, 0.8f, 0.1f, 1f), 50);
		
		private transient Color color;
		private int id;

		private ColTypes(Color color, int id) {
			this.color = color;
			this.id = id;
		}

		public Color getColor() {
			return color;
		}

	}

	private ColTypes def;

	public Collectible(ColTypes def) {
		super(def.id, 0);
		setGraphicsId(43);
		setColor(def.color);
		this.def = def;
		setFloating(false);
		//setSpeed(0.2f);
		setFriction(CVar.get("friction").getValuef());
		setIndestructible(true);
		setCollectable(true);
	}
	
	/**
	 * copy c
	 * @param collectible 
	 */
	public Collectible(Collectible collectible) {
		super(collectible);
		setGraphicsId(43);
		setFloating(false);
		setIndestructible(true);
		setCollectable(true);
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

	public ColTypes getColTypes() {
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
		 def = ColTypes.COAL;//todo, proper serialisation and ddeserialisation of enum
		 //http://www.vineetmanohar.com/2010/01/3-ways-to-serialize-java-enums/
		 setColor(def.getColor());
    }
}
