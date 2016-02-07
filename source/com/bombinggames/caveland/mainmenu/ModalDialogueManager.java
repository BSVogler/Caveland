package com.bombinggames.caveland.mainmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bombinggames.caveland.game.ActionBox;
import com.bombinggames.wurfelengine.WE;

/**
 * Manages one central modal actionbox.
 * @author Benedikt Vogler
 */
public class ModalDialogueManager implements InputProcessor  {
	private ActionBox actionBox;

	/**
	 *
	 * @param stage
	 * @param actionBox
	 */
	public void setActionBox(Stage stage, ActionBox actionBox) {
		this.actionBox = actionBox;
		actionBox.setPosition(
			stage.getWidth()/2 - actionBox.getWidth() / 2,
			stage.getHeight()/2 - actionBox.getHeight() / 2
		);
		stage.addActor(actionBox);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (!WE.getConsole().isActive()) {
			if (actionBox != null) {
				if (keycode == Input.Keys.W) {
					actionBox.up(null);
					return true;
				}

				if (keycode == Input.Keys.S) {
					actionBox.down( null);
					return true;
				}

				if (keycode == Input.Keys.UP) {
					actionBox.up( null);
					return true;
				}

				if (keycode == Input.Keys.DOWN) {
					actionBox.down(null);
					return true;
				}

				if (keycode == Input.Keys.ENTER) {
					actionBox.confirm(null);
					actionBox = null;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			if (actionBox != null) {
				actionBox.confirm(null);
				actionBox = null;
				return true;
			}
		}
		if (button == Input.Buttons.RIGHT) {
			if (actionBox != null) {
				actionBox.cancel(null);
				actionBox = null;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
