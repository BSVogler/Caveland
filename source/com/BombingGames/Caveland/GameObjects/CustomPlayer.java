package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Map.Coordinate;
import com.BombingGames.WurfelEngine.Core.Map.Point;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
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
	
	private transient static TextureAtlas spritesheet;
	private transient static AtlasRegion[][] sprites = new AtlasRegion['z'][65];
	private transient static Texture textureDiff;
	private transient static Texture textureNormal;
	
	public static void loadSheet(){
		if (spritesheet == null) {
            spritesheet = WE.getAsset("com/BombingGames/Caveland/playerSheet.txt");
        }
		textureDiff = spritesheet.getTextures().first();
        if (CVar.get("LEnormalMapRendering").getValueb())
			textureNormal = WE.getAsset("com/BombingGames/Caveland/playerSheetNormal.png");
	}
	
	/**
     * Returns a sprite texture.
     * @param category the category of the sprite e.g. "w" for walking
     * @param value the index of the animation
     * @return 
     */
    public static AtlasRegion getSprite(final char category, final int value) {
        if (spritesheet == null) return null;
        if (sprites[category][value] == null){ //load if not already loaded
			
            AtlasRegion sprite = spritesheet.findRegion(category+"/"+Integer.toString(value));
            if (sprite == null){ //if there is no sprite show the default "sprite not found sprite" for this category
                Gdx.app.debug("Player animation", category+Integer.toString(value) + " not found");
                return null;
			}
            sprites[category][value] = sprite;
            return sprite;
        } else {
            return sprites[category][value];
        }
    }
	
    private float aimHeight;
	private boolean loadingSoundPlaying = false;

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
	private Interactable nearestEntity;
	
	private int spriteNum = 1;
	private int animationCycle;
	private char action = 'w';
	
    public CustomPlayer() {
        super(30, 0);
	
		setStepSound1Grass("step");
		//setRunningSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg"));
		setJumpingSound("urfJump");
		setFriction(CVar.get("playerfriction").getValuef());
		setDimensionZ(AbstractGameObject.GAME_EDGELENGTH);
		setSaveToDisk(false);
		
		inventory.add(new Collectible(Collectible.ColTypes.COAL));
		inventory.add(new Collectible(Collectible.ColTypes.SULFUR));
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
		
		//some redundant code from movable to have a custom animation
		Point pos = getPosition();
		if (action=='h')
			animationCycle += dt*5;
		else
			animationCycle += dt*getSpeed()*CVar.get("walkingAnimationSpeedCorrection").getValuef();//multiply by factor to make the animation fit the movement speed
		
		//cycle the cycle
		if (animationCycle >= 1000) {
			if (action=='h')//play hit only once
				action='w';
			animationCycle %= 1000;
		}
		
		//detect direciton
		int animationStart;
		if (getOrientation().x < -Math.sin(Math.PI/3)){
			animationStart = 49;//west
		} else {
			if (getOrientation().x < - 0.5){
				//y
				if (getOrientation().y<0){
					animationStart = 41;//north-west
				} else {
					animationStart = 57;//south-east
				}
			} else {
				if (getOrientation().x <  0.5){
					//y
					if (getOrientation().y<0){
						animationStart = 33;//north
					}else{
						animationStart = 1;//south
					}
				}else {
					if (getOrientation().x < Math.sin(Math.PI/3)) {
						//y
						if (getOrientation().y < 0){
							animationStart = 25;//north-east
						} else{
							animationStart = 9;//sout-east
						}
					} else{
						animationStart = 17;//east
					}
				}
			}
		}
		
		//fix for different starting positions
		if (action!='w' && action!='j')
			animationStart -=8;
		if (animationStart<0)
			animationStart=57;
		
		//animation
		int animationStep;
		if (action=='t') {
			animationStep = animationCycle/(1000/6);//six sprites for throwing
			spriteNum = animationStart + animationStep;
			if (spriteNum>48) spriteNum=48;//clamp at 48 //todo temporary fix to avoid crash
		} else {
			animationStep = animationCycle/(1000/8);//animation walking sprites
		
			if (action=='j' && animationStep>3) { //todo temporary fix to avoid landing animation
				animationStep=3;
				animationCycle=375;
			}
			spriteNum = animationStart + animationStep;
		}
		
		
		//detect button hold
		if (loadAttack != 0f) loadAttack+=dt;
		if (loadAttack>0) {
			Vector3 newmov = getMovement();
			newmov.z /=2;//half vertical speed
			if (newmov.z<0)
				newmov.z *= 2/3;//if falling then more "freeze in air"
			setMovement(newmov);
		}
		
		if (loadAttack>300) {//time till registered as a "hold"
			action = 'l';
			if (!loadingSoundPlaying){
				Controller.getSoundEngine().play("loadAttack");
				loadingSoundPlaying=true;
			}
			Vector3 newmov = getMovement();
			newmov.z /=4;
			setMovement(newmov);
		}
		
		if (loadAttack >= LOADATTACKTIME){
			loadAttack();
		}
		
		//get loren
		ArrayList<MineCart> nearbyLoren = pos.getPoint().getEntitiesNearby(AbstractGameObject.GAME_EDGELENGTH2, MineCart.class);

		if (!nearbyLoren.isEmpty()){
			MineCart lore = nearbyLoren.get(0);
			if (lore != null && lore.getPassenger()==null)//if contact with lore and it has no passenger
				if (pos.getZ() > (pos.getZ()+0.5f)*(AbstractGameObject.GAME_EDGELENGTH)) //enter chu chu
					lore.setPassanger(this);
		}
		
		
		//loren anstupsen
		if (nearbyLoren.size() > 0 && nearbyLoren.get(0).getSpeedHor() < 0.1){//anstupsen
			nearbyLoren.get(0).addMovement(new Vector2(getOrientation().scl(getMovementHor().len()+5f)));
		}
		
		//collect collectibles
		ArrayList<MovableEntity> collectibles = pos.getCoord().getEntitysInside(Collectible.class);
		for (MovableEntity collectible : collectibles) {
			if (collectible.isCollectable() && inventory.add(collectible))
				collectible.dispose();
		}
		
		if (timeSinceDamage>4000)
			heal(dt/2f);
		else timeSinceDamage+=dt;
		
 		//update interactable
		ArrayList<Interactable> nearbyInteractable = getPosition().getEntitiesNearbyHorizontal(GAME_EDGELENGTH*2, Interactable.class);

		if (! nearbyInteractable.isEmpty()) {
			//check if a different one
			if (nearestEntity != nearbyInteractable.get(0) && nearestEntity != null)
				nearestEntity.hideButton();
			nearestEntity = nearbyInteractable.get(0);
			nearestEntity.showButton();
		} else if (nearestEntity != null)
			nearestEntity.hideButton();
		
		
		if (isOnGround()) airjump=false;
	}

	@Override
	public void render(GameView view, Camera camera) {
		view.getBatch().end();//inject new batch here

		//bind normal map to texture unit 1
		if (CVar.get("LEnormalMapRendering").getValueb())
			textureNormal.bind(1);
		
		textureDiff.bind(0);
				
		view.getBatch().begin();
			AtlasRegion texture = getSprite(action, spriteNum);
			Sprite sprite = new Sprite(texture);
			sprite.setOrigin(VIEW_WIDTH2, VIEW_HEIGHT2+texture.offsetY);
			sprite.rotate(getRotation());
			//sprite.scale(get);
			sprite.setColor(getColor());

			sprite.setPosition(
				getPosition().getViewSpcX(view)+texture.offsetX-texture.originalWidth/2,
				getPosition().getViewSpcY(view)//center
					-VIEW_HEIGHT2
					+texture.offsetY
					-50 //only this player sprite has an offset because it has overize
			);
			sprite.draw(view.getBatch());
		view.getBatch().end();
		
		//bind normal map to texture unit 1
		if (CVar.get("LEnormalMapRendering").getValueb())
			AbstractGameObject.getTextureNormal().bind(1);

		//bind diffuse color to texture unit 0
		//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
		AbstractGameObject.getTextureDiffuse().bind(0);
		view.getBatch().begin();
	}

	
	/**
	 * 
	 * @return null if nothing in reach
	 */
	public Interactable getNearestInteractable() {
		return nearestEntity;
	}
	
	
	
	public void throwItem(){
		try {
			MovableEntity item = inventory.getFrontItem();
			if (item != null) {
				//throw is performed
				playAnimation('t');
				item.setMovement(getAiming().scl(3f));//throw with 3 m/s
				//item.setSpeed(0.5f);
				item.spawn(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH*2));
			}
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(CustomPlayer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	/**
	 * does an attack move
	 * @param damage
	 */
	public void attack(int damage){
		playAnimation('h');
		Controller.getSoundEngine().play("attack");
		addToHor(8f);//add 5 m/s in move direction
		
		//from current position go 80px in aiming direction and get entities 80px around there
		ArrayList<AbstractEntity> entities = getPosition().cpy().addVector(getAiming().scl(160)).getEntitiesNearby(120);
		entities.addAll(getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH2).getEntitiesNearby(39));//add entities under player, what if duplicate?
		//check hit
		for (AbstractEntity entity : entities) {
			if (entity instanceof MovableEntity && entity != this) {
				MovableEntity movable = (MovableEntity) entity;
				movable.damage(damage);
				getCamera().shake(20, 50);
				movable.setMovement(
					new Vector3(
							(float) (getAiming().x+Math.random()*0.5f-0.25f),
							(float) (getAiming().y+Math.random()*0.5f-0.25f),
							(float) Math.random()
					)
				);
				movable.setSpeedHorizontal(2);
			}
		}
		
		//destroy blocks
		if (
			getPosition().cpy().addVector(0, 0, Block.GAME_EDGELENGTH2)
				.addVector(getAiming().scl(80)).getCoord().damage(damage)
			)
		{
			Controller.getSoundEngine().play("impact");
			getCamera().shake(20, 50);
		}
		
		//set to a small value to indicate that it is active
		loadAttack=0.00001f;
	}
	
	@Override
	public void damage(int value) {
		super.damage(value);
		Controller.getSoundEngine().play("urfHurt");
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
			playAnimation('j');
			if (airjump) {
				Controller.getSoundEngine().play("jetpack");
				for (int i = 0; i < 40; i++) {
					new Dust(
						1000f,
						new Vector3(
							(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH,
							(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH,
							-4*AbstractGameObject.GAME_EDGELENGTH
						),
						new Color(1, 1, 0,1)
					).spawn(
						getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH2+(float) Math.random()*AbstractGameObject.GAME_EDGELENGTH)
					);
				}
				
			}
		}
	}

	/**
	 * should be called on button release. Checks if a big attack should be performed.
	 */
	public void loadAttack() {
		if (loadAttack >= LOADATTACKTIME) {
			playAnimation('i');
			Controller.getSoundEngine().play("ha");
			addToHor(40f);
			attack(1000);
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
		new Dust(
			1000f,
			new Vector3(0, 0, AbstractGameObject.GAME_EDGELENGTH/8),
			new Color(0.2f,0.5f,0.2f,1f)
		).spawn(getPosition().cpy());
	}

	@Override
	public void onLand() {
		playAnimation('w');
	}
	
	@Override
	public void dispose() {
		super.dispose();
		Coordinate coord = getPosition().getCoord();
		CVar.get("PlayerLastSaveX").setValuei(coord.getX());
		CVar.get("PlayerLastSaveY").setValuei(coord.getY());
		CVar.get("PlayerLastSaveZ").setValuei(coord.getZ());
	}

	private void playAnimation(char c) {
		action = c;
		animationCycle=0;
	}
	
	
}
