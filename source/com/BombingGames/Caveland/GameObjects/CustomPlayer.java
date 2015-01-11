package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
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
public class CustomPlayer extends Controllable {
	/**
	 * Time till fully loaded attack.
	 */
	public static final float LOADATTACKTIME = 1000; 
	private static final long serialVersionUID = 2L;
    private float aimHeight;
	private transient final Sound jetPackSound;
	private transient final Sound loadingSound;
	private boolean loadingSoundPlaying = false;
	private transient final Sound releaseSound;
	private transient final Sound attackSound;

	private int timeSinceDamage;
	
	private Inventory inventory = new Inventory();
	
	/**
	 * true if last jump was airjump.
	 */
	private boolean airjump = false;
	
	private transient Camera camera;
	
	/**
	 * time of loading
	 */
	private float loadAttack =0;
	
    public CustomPlayer() {
        super(30, 4);
		jetPackSound = WE.getAsset("com/BombingGames/Caveland/sounds/jetpack.wav");
		loadingSound = WE.getAsset("com/BombingGames/Caveland/sounds/loadAttack.wav");
		releaseSound = WE.getAsset("com/BombingGames/Caveland/sounds/ha.wav");
		attackSound = WE.getAsset("com/BombingGames/Caveland/sounds/attack.wav");
		setStepSound1Grass( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/step.wav"));
		//setRunningSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg"));
        setJumpingSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/jump_man.wav"));
		loadEngineFallingSound();
		setJumpingSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/urf_jump.wav"));
		
		loadEngineLandingSound();
		setFriction(50);
		setDimensionZ(AbstractGameObject.GAME_EDGELENGTH);
		setSaveToDisk(false);
		
		inventory.add(new Collectible(Collectible.Def.COAL));
		inventory.add(new Collectible(Collectible.Def.SULFUR));
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
        Vector3 aim = new Vector3(getOrientation(), aimHeight);
        aim.nor();
        return aim;
    }

	@Override
	public void update(float dt) {
		super.update(dt);
		Point pos = getPosition();
		
		//detect button hold
		if (loadAttack != 0f) loadAttack+=dt;
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
			loren.get(0).addMovement(new Vector3(getOrientation().scl(1f),0));
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
				item.setMovement(getAiming().scl(3f));//throw with 3 m/s
				//item.setSpeed(0.5f);
				item.spawn(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH*2));
			}
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(CustomPlayer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void attack(){
		attackSound.play();
		addToHor(8f);//add 5 m/s in move direction
		
		//from current position go 80px in aiming direction and get entities 80px around there
		ArrayList<AbstractEntity> entities = getPosition().cpy().addVector(getAiming().scl(160)).getEntitiesNearby(120);
		entities.addAll(getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH2).getEntitiesNearby(39));//add entities under player, what if duplicate?
		//check hit
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
				//movable.setSpeed(2);
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
			if (airjump) {
				jetPackSound.play();
				for (int i = 0; i < 40; i++) {
					new Dust(1000f,
						new Vector3(
							(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH,
							(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH,
							-4*AbstractGameObject.GAME_EDGELENGTH
						)).
						spawn(getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH2+(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH));
				}
				
			}
		}
	}

	/**
	 * should be called on button release
	 */
	public void loadAttack() {
		if (loadAttack >= LOADATTACKTIME && releaseSound != null) {
			releaseSound.play();
			addToHor(20f);
			attack();
		}
	
		loadAttack=0f;
		loadingSoundPlaying =false;
	}
	
	    /**
     *Set the camera which is renderin the player to calculate the aiming. If camera is null 
     * @param camera 
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

	public Camera getCamera() {
		return camera;
	}

	@Override
	public void step() {
		super.step();
		new Dust(1500f, new Vector3(0, 0, AbstractGameObject.GAME_EDGELENGTH/8)).spawn(getPosition().cpy());
	}

	@Override
	public void dispose() {
		super.dispose();
		Coordinate coord = getPosition().getCoord();
		CVar.get("PlayerLastSaveX").setValuei(coord.getX());
		CVar.get("PlayerLastSaveY").setValuei(coord.getY());
		CVar.get("PlayerLastSaveZ").setValuei(coord.getZ());
	}
	
	
	
}
