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


		@Override
		public void init(Controller controller, GameView oldView) {
			super.init(controller, oldView);
			Camera camera = new Camera(this);
			BenchmarkMovement movement = new BenchmarkMovement();
			movement.spawn(new Point(0,0,RenderCell.GAME_EDGELENGTH*6));
			camera.setFocusEntity(movement);
		}
		
		
	}

	private static class BenchmarkMovement extends MovableEntity {

		private static final long serialVersionUID = 1L;

		BenchmarkMovement() {
			super((byte) 0);
			setHidden(true);
			addComponent(new MoveToAi(new Point(5000, 0, RenderCell.GAME_EDGELENGTH*4)));
		}

		@Override
		public boolean handleMessage(Telegram msg) {
			return false;
		}
	}

}
