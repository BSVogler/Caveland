package com.BombingGames.Game;

import com.BombingGames.EngineCore.Controller;
import com.BombingGames.EngineCore.Gameobjects.AbstractCharacter;
import com.BombingGames.EngineCore.Gameobjects.AbstractEntity;
import com.BombingGames.EngineCore.GameplayScreen;
import com.BombingGames.EngineCore.Map.Chunk;
import com.BombingGames.EngineCore.Map.Map;
import com.BombingGames.EngineCore.Map.Minimap;
import com.BombingGames.EngineCore.Map.Point;
import com.BombingGames.EngineCore.WECamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.openal.Ogg.Sound;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class MinecraftController extends Controller {
    private AbstractEntity focusentity;
    private Sound gras1;
    private Sound gras2;
    private BlockToolbar blockToolbar;

        
    @Override
    public void init(){
         Chunk.setGenerator(20);
         super.init();

        gras1 = (Sound) Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/Game/Sounds/grass1.ogg"));
        gras2 = (Sound) Gdx.audio.newSound(Gdx.files.internal("com/BombingGames/Game/Sounds/grass2.ogg"));
        
        
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
        
        blockToolbar = new BlockToolbar();
        
        focusentity = AbstractEntity.getInstance(13, 0, new Point(0, 0, Map.getBlocksZ()-1, true));
        //focusentity.setPositionY(Block.DIM2+1f);
        focusentity.exist();

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
    
    
    
    public BlockToolbar getBlockToolbar() {
        return blockToolbar;
    }
    
    public AbstractEntity getFocusentity() {
        return focusentity;
    }
}
