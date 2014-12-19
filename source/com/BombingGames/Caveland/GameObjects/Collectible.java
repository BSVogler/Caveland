package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
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

	public static enum Def {
		IRONORE(Color.RED.cpy()),
		COAL(Color.DARK_GRAY.cpy()),
		GOLD(Color.YELLOW.cpy()),
		IRON(Color.GRAY.cpy()),
		SULFUR(new Color(0.8f, 0.8f, 0.1f, 1f));
		
		private transient Color color;

		private Def(Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

	}

	private Def def;
	private transient Color color;

	public Collectible(Def def) {
		super(46, 0);
		setGraphicsId(43);
		this.color = def.getColor();
		this.def = def;
		setFloating(false);
		setSpeed(0.2f);
		setFriction(2000);
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
		color = collectible.color;
		setFloating(false);
		setIndestructible(true);
		setCollectable(true);
	}


	
	@Override
	public void render(View view, Camera camera, Color color) {
		render(
            view,
            camera,
            this.color.cpy().mul(color),
            CVar.get("enableScalePrototype").getValueb()//if using scale prototype scale the objects
                ? getPosition().getZ()/(Map.getGameHeight())
                : -0.4f
        );
	}

	@Override
	public void render(View view, int xPos, int yPos, float scale) {
		super.render(view, xPos, yPos, color.cpy(), scale-0.4f);
	}
	
	@Override
	public Collectible clone() throws CloneNotSupportedException {
		return new Collectible(this);
	}
	
	/**
	 * some effect
	 */
	public void sparkle(){
		setMovementDir(new Vector3((float) Math.random()-0.5f,(float) Math.random()-0.5f,(float)  Math.random()));
	}

	public Def getDef() {
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
		 color = def.getColor();
    }
	
}
