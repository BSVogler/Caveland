package com.bombinggames.caveland.Game.igmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

/**
 * A menu for some basic options to show during gameplay
 * @author Benedikt Vogler
 */
public class IGMenu extends WidgetGroup {
	private Image editor = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/Game/igmenu/editor.png")));
	private Image save = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/Game/igmenu/save.png")));;
	private Image exit = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/Game/igmenu/exit.png")));;
	private Image background = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/Game/igmenu/background.png")));

	public IGMenu() {
		background.setPosition(0, 0);
		addActor(background);
		editor.setPosition(getWidth()/2-editor.getWidth()/2, 400);
		addActor(editor);
		save.setPosition(getWidth()/2-save.getWidth()/2, 150);
		addActor(save);
		exit.setPosition(getWidth()/2-exit.getWidth()/2, 5);
		addActor(exit);
	}

	@Override
	public float getWidth() {
		return background.getWidth();
	}

	@Override
	public float getHeight() {
		return background.getHeight();
	}
	
}
