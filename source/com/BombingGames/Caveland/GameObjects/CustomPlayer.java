package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.PlayerWithWeapon;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomPlayer extends PlayerWithWeapon {
	/**
	 * Time till fully loaded attack.
	 */
	public static final float LOADATTACKTIME = 1000; 
	private static final long serialVersionUID = 1L;
    private float aimHeight;
	private transient Sound jetPackSound;
	private transient Sound loadingSound;
	private boolean loadingSoundPlaying = false;
	private transient Sound releaseSound;

	private int timeSinceDamage;
	
	private Inventory inventory = new Inventory();
	
	/**
	 * true if last jump was airjump.
	 */
	private boolean airjump = false;
	
	private float loadAttack =0;
	
    public CustomPlayer() {
        super(1,AbstractGameObject.GAME_EDGELENGTH);
		jetPackSound = WE.getAsset("com/BombingGames/Caveland/sounds/jetpack.wav");
		loadingSound = WE.getAsset("com/BombingGames/Caveland/sounds/loadAttack.wav");
		releaseSound = WE.getAsset("com/BombingGames/Caveland/sounds/ha.wav");
		setStepSound1Grass( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/step.wav"));
		//setRunningSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg"));
        setJumpingSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/jump_man.wav"));
    }
	
	/**
	 * Get the value of inventory
	 *
	 * @return the value of inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}
	
    public void setAimHeight(float aimHeight) {
        if (aimHeight>1)aimHeight=1;
        if (aimHeight<-1)aimHeight=-1;
        this.aimHeight = aimHeight;
    }

    public float getAimHeight() {
        return aimHeight;
    }
    
    
    @Override
    public Vector3 getAiming() {
        Vector3 aim = getMovement().cpy();
        aim.z = aimHeight;
        aim.nor();
        return aim;
    }

	@Override
	public void update(float dt) {
		super.update(dt);
		Point pos = getPosition();
		
		//detect button hold
		if (loadAttack!=0f) loadAttack+=dt;
		if (loadAttack>300) {//time till registered as a "hold"
			if (loadingSound != null && !loadingSoundPlaying){
				loadingSound.play();
				loadingSoundPlaying=true;
			}
		}
		
		if (loadAttack >= LOADATTACKTIME){
			loadAttack();
		}
		
		//get loren
		ArrayList<Lore> items = pos.getCoord().getEntitysInside(Lore.class);

		if (!items.isEmpty()){
			Lore lore = items.get(0);
			if (lore != null && lore.getPassenger()==null)//if contact with lroe and it has no passenger
				if (pos.getZ() > (pos.getZ()+0.5f)*(AbstractGameObject.GAME_EDGELENGTH)) //enter chu chu
					lore.setPassanger(this);
		}
		
		//collect collectibles
		ArrayList<MovableEntity> collectibles = pos.getCoord().getEntitysInside(Collectible.class);
		for (MovableEntity collectible : collectibles) {
			if (collectible.isCollectable() && inventory.add(collectible))
				collectible.dispose();
		}
		
		ArrayList<Lore> loren = pos.getCoord().getEntitysInside(Lore.class);
		if (loren.size()>0 && loren.get(0).getSpeed()==0){//anstupsen
			loren.get(0).setSpeed(1f);
			loren.get(0).setMovement(getMovement());
		}
		
		if (timeSinceDamage>4000)
			heal(dt/2f);
		else timeSinceDamage+=dt;
		
		if (isOnGround()) airjump=false;
	}
	
	public void throwItem(){
		try {
			MovableEntity item = inventory.getFrontItem();
			if (item != null) {
				item.setMovement(getAiming());
				item.setSpeed(0.5f);
				item.spawn(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH*2));
			}
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(CustomPlayer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void attack(){
		//from current position go 80px in aiming direction and get entities 80px around there
		ArrayList<AbstractEntity> entities = getPosition().cpy().addVector(getAiming().scl(160)).getEntitiesNearby(120);
		entities.addAll(getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH2).getEntitiesNearby(39));//add entities under player, what if duplicate?
		for (AbstractEntity entity : entities) {
			if (entity instanceof MovableEntity && entity != this) {
				MovableEntity movable = (MovableEntity) entity;
				movable.damage(500);
				getCamera().shake(20, 50);
				movable.setMovement(
						new Vector3(
							(float) (getAiming().x+Math.random()*0.5f-0.25f),
							(float) (getAiming().y+Math.random()*0.5f-0.25f),
							(float) Math.random()
						)
					);
				movable.setSpeed(2);
			}
		}
		
		//destroy blocks
		if (
			getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH2)
				.addVector(getAiming().scl(80)).getCoord().damage(350)
			)
		{
			getCamera().shake(20, 50);
		}
		
		//set to a small value to indicate that it is active
		loadAttack=0.00001f;
	}
	
	@Override
	public void damage(int value) {
		super.damage(value);
		if (getCamera()!=null) getCamera().setDamageoverlayOpacity(1f-getHealth()/1000f);
		timeSinceDamage=0;
	}

	@Override
	public void heal(float value) {
		super.heal(value);
		if (getCamera()!=null) getCamera().setDamageoverlayOpacity(1f-getHealth()/1000f);
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void jump() {
		if (!airjump || isOnGround()){
			if (!isOnGround()) airjump=true;
			jump(5, !airjump);
			if (airjump) jetPackSound.play();
		}
	}

	/**
	 * should be called on button release
	 */
	public void loadAttack() {
		if (loadAttack>=LOADATTACKTIME &&releaseSound!=null) {
			releaseSound.play();
			setSpeed(getSpeed()+1.5f);
		}
	
		
		attack();
		loadAttack=0f;
		loadingSoundPlaying =false;
	}
	
}
