package com.bombinggames.caveland.game;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.bombinggames.caveland.gameobjects.Ejira;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A dialog which can fucntion as a selection tool or a message.
 *
 * @author Benedikt Vogler
 */
public class ActionBox extends WidgetGroup {

	/**
	 * contains the content of the window
	 */
	private final Window window;
	private final BoxModes mode;
	private ArrayList<SelectionOption> selections;
	/**
	 * the number of the selected item
	 */
	private byte selectionNum;
	private String text;
	private ActionBoxConfirmAction confirmAction;
	private ActionBoxCancelAction cancelAction;
	private ActionBoxSelectAction selectAction;
	private boolean closed = false;
	private String confirmSound = "menuConfirm";

	/**
	 *
	 */
	public static enum BoxModes {

		/**
		 * select from yes or no
		 */
		BOOLEAN(),
		/**
		 * Select from various entries
		 */
		SELECTION(),
		/**
		 * Just a message to confirm
		 */
		SIMPLE(),
		/**
		 * reserved for later use
		 */
		CUSTOM();
	}

	/**
	 * creates a new chat box and set this as a modal window
	 *
	 * @param title the title of the box
	 * @param text the text of the box. can be null
	 * @param mode
	 */
	public ActionBox(final String title, final BoxModes mode, final String text) {
		this.mode = mode;

		window = new Window(title, WE.getEngineView().getSkin());
		window.setWidth(600);
		window.setHeight(200);
		window.align(Align.center);
		addActor(window);
		if (text != null) {
			this.text = text;
			Label textArea = new Label(text, WE.getEngineView().getSkin());
			textArea.setX(10);
			textArea.setWrap(true);
			textArea.setWidth(580);
			textArea.setHeight(180);
			window.addActor(textArea);
		}

		TextureAtlas.AtlasRegion sprite = AbstractGameObject.getSprite('i', (byte) 23, (byte) 0);
		if (sprite != null) {
			Image confirm = new Image(new TextureRegionDrawable(sprite));
			confirm.setPosition(window.getWidth() - 50, -40);
			addActor(confirm);
		}
		Label confirmLabel = new Label("Confirm", WE.getEngineView().getSkin());
		confirmLabel.setPosition(window.getWidth() - 50, -40);
		addActor(confirmLabel);

		if (mode != BoxModes.SIMPLE) {
			sprite = AbstractGameObject.getSprite('i', (byte) 23, (byte) 2);
			if (sprite != null) {
				Image cancel = new Image(new TextureRegionDrawable(sprite));
				cancel.setPosition(window.getWidth() - 250, -40);
				addActor(cancel);
			}
			Label cancelLabel = new Label("Cancel", WE.getEngineView().getSkin());
			cancelLabel.setPosition(window.getWidth() - 250, -40);
			addActor(cancelLabel);
		}
		
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			selections = new ArrayList<>(8);
		}
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getWidth() {
		return window.getWidth();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public float getHeight() {
		return window.getHeight();
	}

	/**
	 * registeres the window as a modal box so that the input gets redirected to
	 * it.
	 *
	 * @param view for displaying the window
	 * @param playerId starting with 1. Needed for position.
	 * @param actor the entitiy which is connected to the dialogue. can be null
	 * @return itself
	 * @see #register(CLGameView, int, AbstractEntity, Coordinate)
	 */
	public ActionBox register(final CLGameView view, final int playerId, AbstractEntity actor) {
		view.setModalDialogue(this, playerId);
		if (mode == BoxModes.SELECTION && selectAction != null) {
			if (selections.isEmpty()) {
				selectAction.select(false, null, actor);
			} else {
				selectAction.select(false, selections.get(selectionNum), actor);
			}
		}
		return this;
	}

	/**
	 * registeres the window as a modal box so that the input gets redirected to
	 * it.
	 *
	 * @param view for displaying the window
	 * @param playerId starting with 1. Needed for position.
	 * @param actor the entitiy which is connected to the dialogue. can be null
	 * @param actedObject the object which gets acted on. can be null
	 * @return itself
	 * @see #register(CLGameView, int, AbstractEntity, Coordinate)
	 */
	public ActionBox register(final CLGameView view, final int playerId, AbstractEntity actor, AbstractEntity actedObject) {
		register(view, playerId, actor);
		if (actedObject != null && actor instanceof Ejira) {
			((Ejira) actor).startInteraction(actedObject);
		}
		return this;
	}

	/**
	 * registeres the window as a modal box so that the input gets redirected to
	 * it.
	 *
	 * @param view for displaying the window
	 * @param playerId starting with 1. Needed for position.
	 * @param actor the entitiy which is connected to the dialogue. can be null
	 * @param coord the coordinates where the interaction is aimed at
	 * @return itself
	 * @see #register(CLGameView, int, Gameobjects.AbstractEntity,
	 * AbstractEntity)
	 */
	public ActionBox register(final CLGameView view, final int playerId, AbstractEntity actor, Coordinate coord) {
		register(view, playerId, actor);
		if (coord != null && actor instanceof Ejira) {
			((Ejira) actor).startInteraction(coord);
		}
		return this;
	}

	/**
	 *
	 * @return
	 */
	public Window getWindow() {
		return window;
	}

	/**
	 *
	 * @return
	 */
	public BoxModes getMode() {
		return mode;
	}

	/**
	 * Set the command which should be triggered once you confirm the dialogue.
	 *
	 * @param action
	 * @return itself for chaining
	 */
	public ActionBox setConfirmAction(ActionBoxConfirmAction action) {
		this.confirmAction = action;
		return this;
	}

	/**
	 * Set the command which should be triggered once you confirm the dialogue.
	 *
	 * @param action
	 * @return itself for chaining
	 */
	public ActionBox setCancelAction(ActionBoxCancelAction action) {
		this.cancelAction = action;
		return this;
	}

	/**
	 * Set the action what should happen if you select something before
	 * confirming it.
	 *
	 * @param hoverAction new value of selectAction
	 * @return itself for chaining
	 */
	public ActionBox setSelectAction(ActionBoxSelectAction hoverAction) {
		this.selectAction = hoverAction;
		return this;
	}

	/**
	 * unregisters this window
	 *
	 * @param actor
	 * @return
	 */
	public SelectionOption confirm(AbstractEntity actor) {
			SelectionOption result = null;
		if (selections != null && !selections.isEmpty())
			result = selections.get(selectionNum);
		remove();
		if (actor instanceof Ejira) {
			((Ejira) actor).endInteraction();
		}
		WE.SOUND.play(confirmSound, actor.getPosition());
		closed = true;
		if (confirmAction != null) {
			confirmAction.confirm(result, actor);
		}

		return result;
	}

	/**
	 * unregisters this window
	 *
	 * @param actor
	 * @return
	 */
	public SelectionOption cancel(AbstractEntity actor) {
		SelectionOption result = null;
		if (selections != null && !selections.isEmpty())
			result = selections.get(selectionNum);
		remove();
		closed = true;
		WE.SOUND.play("menuAbort");
		if (cancelAction != null) {
			cancelAction.cancel(result, actor);
		}
		if (actor instanceof Ejira) {
			((Ejira) actor).endInteraction();
		}
		return result;
	}

	/**
	 * Adds strings as options.
	 *
	 * @param options
	 * @return itself for chaining
	 */
	public ActionBox addSelection(SelectionOption... options) {
		if (selections == null) {
			selections = new ArrayList<>(options.length);
		}
		selections.addAll(Arrays.asList(options));
		updateContent();
		return this;
	}

	/**
	 * Adds a list of strings as options.
	 *
	 * @param options
	 */
	public void addSelection(Collection<SelectionOption> options) {
		if (mode == BoxModes.SELECTION) {
			if (selections == null) {
				selections = new ArrayList<>(options.size());
			}
			selections.addAll(options);
			updateContent();
		}
	}

	/**
	 *
	 * @return
	 */
	public SelectionOption getSelected() {
		if (selections == null || selections.isEmpty()) {
			return null;
		}
		return selections.get(selectionNum);
	}
	
	/**
	 * go a selection downwards
	 *
	 * @param actor
	 */
	public void down(AbstractEntity actor) {
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectionNum < selections.size() - 1) {
				selectionNum++;
			}
			WE.SOUND.play("menuSelect");
			updateContent();
		}
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectAction != null) {
				selectAction.select(false, selections.get(selectionNum), actor);
			}
		}
	}

	/**
	 * go a selection upwards
	 *
	 * @param actor
	 */
	public void up(AbstractEntity actor) {
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectionNum > 0) {
				selectionNum--;
			}
			WE.SOUND.play("menuSelect");
			updateContent();
		}
		if (mode == BoxModes.SELECTION || mode == BoxModes.CUSTOM) {
			if (selectAction != null) {
				selectAction.select(true, selections.get(selectionNum), actor);
			}
		}
	}

	/**
	 * the position of the selection.
	 * @return 
	 */
	public byte getSelectionNum() {
		return selectionNum;
	}
	

	/**
	 * clears window content then add new
	 */
	public void updateContent() {
		window.clear();
		window.add(text);
		window.row();
		//adds every selection
		int max = selections.size();
		if (max > 4) {
			max = 4;
		}
		for (int i = 0; i < max; i++) {
			String entry = selections.get(i).name;
			if (selectionNum == i) {
				window.add(new Label("[" + entry + "]", WE.getEngineView().getSkin()));
			} else {
				window.add(new Label(entry, WE.getEngineView().getSkin()));
			}
			if (selections.size() > 4 && i + 4 < selections.size()) {
				entry = selections.get(i + 4).name;
				if (selectionNum == i + 4) {
					window.add(new Label("[" + entry + "]", WE.getEngineView().getSkin()));
				} else {
					window.add(new Label(entry, WE.getEngineView().getSkin()));
				}
			}
			if (selections.size() > 8 && i + 8 < selections.size()) {
				entry = selections.get(i + 8).name;
				if (selectionNum == i + 8) {
					window.add(new Label("[" + entry + "]", WE.getEngineView().getSkin()));
				} else {
					window.add(new Label(entry, WE.getEngineView().getSkin()));
				}
			}
			window.row();
		}
		//window.pack();
	}
	
	public void setConfirmSound(String sound) {
		this.confirmSound = sound;
	}

	boolean closed() {
		return closed;
	}

	/**
	 *
	 */
	@FunctionalInterface
	public interface ActionBoxCancelAction {

		/**
		 *
		 * @param result
		 * @param actor can be null
		 */
		public void cancel(SelectionOption result, AbstractEntity actor);
	}

	/**
	 * Actions which are called if you close the window by confirming
	 *
	 * @author Benedikt Vogler
	 */
	@FunctionalInterface
	public interface ActionBoxConfirmAction {

		/**
		 *
		 * @param result the id of the selection
		 * @param actor can be null
		 */
		public void confirm(SelectionOption result, AbstractEntity actor);

	}

	/**
	 *
	 */
	@FunctionalInterface
	public interface ActionBoxSelectAction {

		/**
		 *
		 * @param up
		 * @param result
		 * @param actor can be null
		 */
		public void select(boolean up, SelectionOption result, AbstractEntity actor);
	}

	/**
	 *
	 */
	public static class SelectionOption {

		/**
		 * arbitrary semantic
		 */
		public final byte id;

		/**
		 *arbitrary semantic
		 */
		public String name;

		/**
		 * Saves information about a possible selection
		 * @param id
		 * @param name
		 */
		public SelectionOption(byte id, String name) {
			this.id = id;
			this.name = name;
		}

	}

}
