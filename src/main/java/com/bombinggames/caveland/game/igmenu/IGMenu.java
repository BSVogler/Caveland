package com.bombinggames.caveland.game.igmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.wurfelengine.WE;

/**
 * A menu for some basic options to show during gameplay
 * @author Benedikt Vogler
 */
public class IGMenu extends WidgetGroup {
	private final Image continueImg = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/continue.png")));
	private final Image editor = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/editor.png")));
	private final Image save = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/save.png")));;
	private final Image exit = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/exit.png")));;
	private final Image background = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/background.png")));
	private final Image haken = new Image(new Texture(Gdx.files.local("com/bombinggames/caveland/game/igmenu/haken.png")));
	private final HoverListener saveWiggle;
	private final HoverListener exitWiggle;
	private final HoverListener editorWiggle;
	private final HoverListener continueWiggle;

	/**
	 *
	 * @param view
	 */
	public IGMenu(CLGameView view) {
		background.setPosition(0, 0);
		addActor(background);
		
		continueWiggle = new HoverListener() {

			@Override
			public void hover() {
				continueImg.setRotation(continueImg.getRotation()+(float) (Math.random()*2)-1);;
			}
		};
		continueImg.addListener(continueWiggle);
		continueImg.addListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				view.setModal(null);
				view.continueTime();
				return true;
			}
		});
		continueImg.setPosition(getWidth()/2-continueImg.getWidth()/2, 380);
		continueImg.setOriginX(continueImg.getWidth()/2f);
		addActor(continueImg);
		
		editorWiggle = new HoverListener() {

			@Override
			public void hover() {
				editor.setRotation(editor.getRotation()+(float) (Math.random()*2)-1);;
			}
		};
		editor.addListener(editorWiggle);
		editor.addListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				haken.setVisible(!WE.getCVars().getValueB("editorVisible"));
				WE.getCVars().get("editorVisible").setValue(haken.isVisible());
				return true;
			}
		});
		editor.setPosition(getWidth()/2-editor.getWidth()/2, 270);
		editor.setOriginX(editor.getWidth()/2f);
		addActor(editor);
		
		haken.setVisible(WE.getCVars().getValueB("editorVisible"));
		haken.setPosition(150, 280);
		addActor(haken);
		
		save.setPosition(getWidth()/2-save.getWidth()/2, 150);
		save.setOriginX(save.getWidth()/2f);
		saveWiggle = new HoverListener() {

			@Override
			public void hover() {
				save.setRotation(save.getRotation()+(float) (Math.random()*2)-1);
			}
		};
		save.addListener(saveWiggle);
		save.addListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				return view.getController().save();
			}
		});
		addActor(save);
		
		exitWiggle = new HoverListener() {

			@Override
			public void hover() {
				exit.setRotation(exit.getRotation()+(float) (Math.random()*2)-1);
			}
		};
		exit.setPosition(getWidth()/2-exit.getWidth()/2, 50);
		exit.addListener(exitWiggle);
		exit.addListener(new ClickListener(){

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				super.touchDown(event, x, y, pointer, button);
				WE.showMainMenu();
				return true;
			}
		});
		addActor(exit);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getWidth() {
		return background.getWidth();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getHeight() {
		return background.getHeight();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		editorWiggle.update();
		saveWiggle.update();
		exitWiggle.update();
		continueWiggle.update();
	}
	
}
