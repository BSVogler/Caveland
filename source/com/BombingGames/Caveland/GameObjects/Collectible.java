package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.AbstractPosition;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.View;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Etwas zum aufsammeln.
 * @author Benedikt Vogler
 */
public class Collectible extends MovableEntity {
	
	public static enum Def {
		IRONORE(46, Color.RED.cpy()),
		COAL(47, Color.DARK_GRAY.cpy()),
		GOLD(48, Color.YELLOW.cpy()),
		IRON(49, Color.GRAY.cpy()),
		SULFUR(50, new Color(0.8f, 0.8f, 0.1f, 1f));
		
		private int id;
		private Color color;

		private Def(int id, Color color) {
			this.id = id;
			this.color = color;
		}

		public int getId() {
			return id;
		}

		public Color getColor() {
			return color;
		}

	}

	private Def def;
	private Color color;

	public Collectible(Def def) {
		super(def.getId(), 0);
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
	public void render(View view, Camera camera, AbstractPosition pos, Color color) {
		render(
            view,
            camera,
            pos,
            this.color.cpy().mul(color),
            CVar.get("enableScalePrototype").getValueb()//if using scale prototype scale the objects
                ? pos.getPoint().getHeight()/(Map.getGameHeight())
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
		setMovement(new Vector3((float) Math.random()-0.5f,(float) Math.random()-0.5f,(float)  Math.random()));
	}

	public Def getDef() {
		return def;
	}
	
}
