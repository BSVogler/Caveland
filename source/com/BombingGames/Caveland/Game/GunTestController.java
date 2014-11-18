package com.BombingGames.Caveland.Game;

import com.BombingGames.Caveland.GameObjects.CustomPlayer;
import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.PlayerWithWeapon;
import com.BombingGames.WurfelEngine.Core.Map.Map;
import com.BombingGames.WurfelEngine.shooting.Bullet;
import com.BombingGames.WurfelEngine.shooting.Weapon;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 *The <i>CustomGameController</i> is for the game code. Put engine code into <i>Controller</i>.
 * @author Benedikt
 */
public class GunTestController extends Controller {
    private boolean cooldown = false;
    private Music music;
    private boolean sprinting;
	private PlayerWithWeapon player;
    
        
    @Override
    public void init(){
        Gdx.app.log("CustomGameController", "Initializing");
        super.init();
                
        Weapon.init();
        Bullet.init();
        
        player = (PlayerWithWeapon) new CustomPlayer().spawn(Map.getCenter(Map.getGameHeight()));
        player.equipWeapon(1);
        
//        player.setDamageSounds(new Sound[]{
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream1.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream2.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream3.wav"),
//            (Sound) WEMain.getAsset("com/BombingGames/WeaponOfChoice/Sounds/scream4.wav")
//        });

        


    }

    
    @Override
    public void update(float dt){
        float origidelta = dt;
        dt *= CVar.get("gamespeed").getValuef();



//                for (int i = 0; i < round; i++) {
//                    Coordinate randomPlace = new Coordinate(
//                        (int) (Map.getBlocksX()*Math.random()),
//                        (int) (Map.getBlocksY()*Math.random()),
//                        (float) Map.getGameHeight(),
//                        true);
//                    Enemy enemy = (Enemy) AbstractMovableEntity.getInstance(14, 0,randomPlace.getPoint());
//                    enemy.setTarget(getPlayer());
//                    enemy.exist();
//                }


//        if (currentWeapon != null)
//            currentWeapon.update(input.isButtonPressed(0), delta);

        if (sprinting)
            getPlayer().setMana((int) (getPlayer().getMana()-dt));
        else {
            getPlayer().setMana((int) (getPlayer().getMana()+dt/2f));
        }
        if (getPlayer().getMana()>100)
            cooldown=false;
        if (getPlayer().getMana()<=0)
              cooldown=true;
        super.update(origidelta);
    }


    public Music getMusic() {
        return music;
    }
    
    public boolean sprint(){
         sprinting = (getPlayer().getMana()>0 &&!cooldown);
         return sprinting;
    }
	
	public PlayerWithWeapon getPlayer() {
		return player;
	}
}

