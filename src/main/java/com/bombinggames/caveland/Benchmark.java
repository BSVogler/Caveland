package com.bombinggames.caveland;

import com.badlogic.gdx.ai.msg.Telegram;
import static com.bombinggames.caveland.Caveland.VERSION;
import com.bombinggames.caveland.mainmenu.CustomLoading;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.MoveToAi;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;

/**
 *
 * @author Benedikt S. Vogler
 */
public class Benchmark {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Caveland.configureEngine();
		//WE.getCVars().register(cvar, VERSION);
		WE.getCVars().get("ignorePlayer").setValue(true);
		WE.addPostLaunchCommands(() -> {
			WE.initAndStartGame(
				new CustomLoading(),
				new BenchmarkController(),
				new BenchmarkView()
			);
		});

		WE.launch("Caveland Benchmark " + VERSION, args);
	}

	private static class BenchmarkController extends Controller {

		public BenchmarkController() {
			super();
			setMapName("demo");
			useSaveSlot(0);
		}
	}

	private static class BenchmarkView extends GameView {

		private Camera camera;

		@Override
		public void init(Controller controller, GameView oldView) {
			super.init(controller, oldView);
			camera = new Camera(this);
			BenchmarkMovement movement = new BenchmarkMovement(controller, this);
			movement.spawn(new Point(0, 0, RenderCell.GAME_EDGELENGTH * 6));
			camera.setFocusEntity(movement);
		}

		private Camera getCamera() {
			return camera;
		}

	}

	private static class BenchmarkMovement extends MovableEntity {

		private static final long serialVersionUID = 1L;
		private final BenchmarkView view;
		private final Controller controller;

		BenchmarkMovement(Controller controller, BenchmarkView view) {
			super((byte) 0);
			this.controller = controller;
			this.view = view;
			setHidden(true);
			addComponent(new MoveToAi(new Point(5000, 0, RenderCell.GAME_EDGELENGTH * 4)));
		}

		@Override
		public void update(float dt) {
			boolean firstStage = getPoint().x < 1000;
			super.update(dt);
			if (getPoint().x > 1000 && firstStage) {
				view.addCamera(view.getCamera());
			}
		}

		@Override
		public boolean handleMessage(Telegram msg) {
			return false;
		}
	}

}
