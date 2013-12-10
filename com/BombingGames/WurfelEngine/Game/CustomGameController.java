package com.BombingGames.WurfelEngine.Game;

import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractCharacter;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.GameplayScreen;
import com.BombingGames.WurfelEngine.Core.Map.Chunk;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.Core.Map.Minimap;
import com.BombingGames.WurfelEngine.Core.WECamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class CustomGameController extends Controller {
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
         Chunk.setGenerator(2);
         super.init();

         AbstractCharacter player = (AbstractCharacter) AbstractEntity.getInstance(
                40,
                0,
                Map.getCenter(Map.getGameHeight())
        );
        player.setControls("WASD");
        setPlayer(player);
        
        addCamera(
            new WECamera(
                getPlayer(),
                0, //left
                0, //top
                Gdx.graphics.getWidth(), //width 
                Gdx.graphics.getHeight()//height
            )
        );
        
//        addCamera(
//            new WECamera(
//                Gdx.graphics.getWidth()/2, //left
//                0, //top
//                Gdx.graphics.getWidth()/2, //width 
//                Gdx.graphics.getHeight()//height
//            )
//        );
        
        setMinimap(
            new Minimap(this, getCameras().get(0), Gdx.graphics.getWidth() - 400,10)
        );
        
        useLightEngine(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
    }

    
    @Override
    public void update(float delta){
        //get input and do actions
        Input input = Gdx.input;
        
        if (!GameplayScreen.msgSystem().isListeningForInput()) {

            //walk
            if (getPlayer() != null){
                if ("WASD".equals(getPlayer().getControls()))
                    getPlayer().walk(
                        input.isKeyPressed(Input.Keys.W),
                        input.isKeyPressed(Input.Keys.S),
                        input.isKeyPressed(Input.Keys.A),
                        input.isKeyPressed(Input.Keys.D),
                        .25f+(input.isKeyPressed(Input.Keys.SHIFT_LEFT)? 0.75f: 0)
                    );
                if (input.isKeyPressed(Input.Keys.SPACE)) getPlayer().jump();
            } else {
                //update camera position
                WECamera camera = getCameras().get(0);
                camera.setOutputPosY( camera.getOutputPosY()
                    - (input.isKeyPressed(Input.Keys.W)? 3: 0)
                    + (input.isKeyPressed(Input.Keys.S)? 3: 0)
                    );
                camera.setOutputPosX( camera.getOutputPosX()
                    + (input.isKeyPressed(Input.Keys.D)? 3: 0)
                    - (input.isKeyPressed(Input.Keys.A)? 3: 0)
                    );
            }
        }
        
        super.update(delta);
    }
}
