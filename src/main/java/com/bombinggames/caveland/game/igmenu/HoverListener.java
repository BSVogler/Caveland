package com.bombinggames.caveland.game.igmenu;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 *
 * @author Benedikt Vogler
 */
public abstract class HoverListener extends ClickListener {
	private boolean hover;

	@Override
	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
		super.enter(event, x, y, pointer, fromActor);
		hover = true;
	}

	@Override
	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		super.exit(event, x, y, pointer, toActor);
		hover =false;
	}
	
	/**
	 *
	 */
	public abstract void hover();
	
	/**
	 *
	 */
	public void update(){
		if (hover) hover();
	
	}
	
	
	
	
	
	
}
