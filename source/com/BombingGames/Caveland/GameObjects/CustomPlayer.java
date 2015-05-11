package com.BombingGames.Caveland.GameObjects;

import com.BombingGames.Caveland.Game.CavelandBlocks;
import com.BombingGames.WurfelEngine.Core.Camera;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.GameView;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.BlockDirt;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Controllable;
import com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData;
import static com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData.GAME_EDGELENGTH;
import static com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData.GAME_EDGELENGTH2;
import static com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData.VIEW_HEIGHT2;
import static com.BombingGames.WurfelEngine.Core.Gameobjects.CoreData.VIEW_WIDTH2;
import com.BombingGames.WurfelEngine.Core.Gameobjects.MovableEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Particle;
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

	/**
	 * loads the spritesheets for the custom player
	 */
	public static void loadSheet() {
		if (spritesheet == null) {
			spritesheet = WE.getAsset("com/BombingGames/Caveland/playerSheet.txt");
		}
		textureDiff = spritesheet.getTextures().first();
		if (WE.CVARS.getValueB("LEnormalMapRendering")) {
			textureNormal = WE.getAsset("com/BombingGames/Caveland/playerSheetNormal.png");
		}
	}

	/**
	 * Returns a sprite texture.
	 *
	 * @param category the category of the sprite e.g. "w" for walking
	 * @param value the index of the animation
	 * @return
	 */
	public static AtlasRegion getSprite(final char category, final int value) {
		if (spritesheet == null) {
			return null;
		}
		if (sprites[category][value] == null) { //load if not already loaded

			AtlasRegion sprite = spritesheet.findRegion("diff/"+category + "/" + Integer.toString(value));
			if (sprite == null) { //if there is no sprite show the default "sprite not found sprite" for this category
				Gdx.app.debug("Player animation", category + Integer.toString(value) + " not found");
				return null;
			}
			sprites[category][value] = sprite;
			return sprite;
		} else {
			return sprites[category][value];
		}
	}

	private boolean canPlayLoadingSound = false;

	private int timeSinceDamage;

	private Inventory inventory = new Inventory(this);

	/**
	 * true if last jump was airjump.
	 */
	private boolean airjump = false;

	private transient Camera camera;

	/**
	 * time of loading
	 */
	private float loadAttack = 0;
	private transient AbstractInteractable nearestEntity;

	/**
	 * the current playing sprite value */
	private transient int spriteNum = 1;
	/**
	 * where in the cycle it is [0:999]
	 */
	private transient float animationCycle;
	/**
	 * false means pause
	 */
	private transient boolean playAnimation;
	private transient char action = 'w';
	/**
	 * play the throw animation till load is finished
	 */
	private boolean prepareThrow;
	private boolean bunnyHopForced;
	private SmokeEmitter emitter;
	private SmokeEmitter emitter2;
	
	/**
	 * creates a new Ejira
	 */
	public CustomPlayer() {
		super((byte) 30, 0);

		setName("Ejira");
		setStepSound1Grass("step");
		//setRunningSound( (Sound) WE.getAsset("com/BombingGames/Caveland/sounds/victorcenusa_running.ogg"));
		setJumpingSound("urfJump");
		setFriction((float) WE.CVARS.get("playerfriction").getValue());
		setDimensionZ(CoreData.GAME_EDGELENGTH);
		setSaveToDisk(false);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		emitter = new SmokeEmitter();
		emitter.setSaveToDisk(false);
		emitter.setParticleDelay(10);
		emitter.setParticleTTL(800);
		emitter.setHidden(true);
		//emitter.setFloating(true);
		emitter.spawn(point.cpy().addVector(-20, 0, CoreData.GAME_EDGELENGTH2));
		AbstractEntity conection1 = new SuperGlue(this, emitter).spawn(point);
		conection1.setSaveToDisk(false);
		emitter2 = new SmokeEmitter();
		emitter2.setSaveToDisk(false);
		emitter2.setParticleDelay(10);
		emitter2.setParticleTTL(800);
		emitter2.setHidden(true);
		//emitter2.setFloating(true);
		emitter2.spawn(point.cpy().addVector(20, 0, CoreData.GAME_EDGELENGTH2));
		AbstractEntity conection2 = new SuperGlue(this, emitter2).spawn(point);
		conection2.setSaveToDisk(false);
		return this;
	}
	
	

	/**
	 * Get the value of inventory
	 *
	 * @return the value of inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		Point pos = getPosition();
		
		//slow down in air
		if (!isOnGround()) {
			if (getMovementHor().len() > 0.1f) {
				setHorMovement(getMovementHor().scl(1f / (dt * getFriction() + 1f)));//with this formula this fraction is always <1
			} else {
				setHorMovement(new Vector2());
			}
		}

		inventory.update(dt);

		//some redundant code from movable to have a custom animation
		if (playAnimation) {
			if (action == 'w') {
				animationCycle += dt * getSpeed() * (float) WE.CVARS.get("walkingAnimationSpeedCorrection").getValue();//multiply by factor to make the animation fit the movement speed
			} else {
				animationCycle += dt * 5;
			}
		}
		updateSprite();
		
		//detect button hold
		if (loadAttack != 0f) {
			loadAttack += dt;
		}
		if (loadAttack > 0) {
			Vector3 newmov = getMovement();
			newmov.z /= 2;//half vertical speed
			if (newmov.z < 0) {
				newmov.z *= 2 / 3;//if falling then more "freeze in air"
			}
			setMovement(newmov);
		}

		if (loadAttack > 300) {//time till registered as a "hold"
			action = 'l';
			if (!canPlayLoadingSound) {
				Controller.getSoundEngine().play("loadAttack");
				canPlayLoadingSound = true;
			}
			Vector3 newmov = getMovement();
			newmov.z /= 4;
			setMovement(newmov);
		}

		if (loadAttack >= LOADATTACKTIME) {
			attackLoadingStopped();
		}

		//get loren
		ArrayList<MineCart> nearbyLoren = pos.toPoint().getEntitiesNearby(CoreData.GAME_EDGELENGTH2, MineCart.class);

		if (!nearbyLoren.isEmpty()) {
			MineCart lore = nearbyLoren.get(0);
			if (lore.getPassenger() == null) {//if contact with lore and it has no passenger
				if (pos.getZ() > (lore.getPosition().getZ() + CoreData.GAME_EDGELENGTH2/2)) {//enter chu chu
					lore.setPassanger(this);
				}
			}
		}

		//loren anstupsen
		if (nearbyLoren.size() > 0 && nearbyLoren.get(0).getSpeedHor() < 0.1) {//anstupsen
			nearbyLoren.get(0).addMovement(new Vector2(getOrientation().scl(getMovementHor().len() + 5f)));
		}

		//collect collectibles
		ArrayList<Collectible> collectibles = pos.toCoord().getEntitysInside(Collectible.class);
		boolean playCollectSound = false;
		for (Collectible collectible : collectibles) {
			if (collectible.canBePickedByParent(this) && inventory.add(collectible)) {
				collectible.disposeFromMap();
				playCollectSound = true;
			}
		}
		if (playCollectSound) {
			Controller.getSoundEngine().play("collect");
		}

		if (timeSinceDamage > 4000) {
			heal((byte) (dt / 2f));
		} else {
			timeSinceDamage += dt;
		}

		//update interactable
		ArrayList<AbstractInteractable> nearbyInteractable = getPosition().getEntitiesNearbyHorizontal(GAME_EDGELENGTH * 2, AbstractInteractable.class);

		if (!nearbyInteractable.isEmpty()) {
			//check if a different one
			if (nearestEntity != nearbyInteractable.get(0) && nearestEntity != null) {
				nearestEntity.hideButton();
			}
			nearestEntity = nearbyInteractable.get(0);
			nearestEntity.showButton(AbstractInteractable.RT);
		} else if (nearestEntity != null) {
			nearestEntity.hideButton();
			nearestEntity = null;
		}
			
		//play walking animation
		if (isOnGround() && getSpeedHor() > 0 && (!playAnimation)) {
			playAnimation('w');
		}

		if (isOnGround()) {
			airjump = false;
		}
		
		//update emitter
		if (airjump) {
			emitter.setActive(getMovement().z>2f);
			emitter.setParticleStartMovement(new Vector3(0, 0, -getMovement().z*1.5f));
			emitter.setParticleSpread(new Vector3(1f, 1f, 0.7f));
			emitter2.setActive(getMovement().z>2f);
			emitter2.setParticleStartMovement(new Vector3(0, 0, -getMovement().z*1.5f));
			emitter2.setParticleSpread(new Vector3(0.4f, 0.4f, 0.3f));
		}
	}

	@Override
	public void render(GameView view, Camera camera) {
		view.getBatch().end();//inject new batch here

		//bind normal map to texture unit 1
		if ((boolean) WE.CVARS.get("LEnormalMapRendering").getValue()) {
			textureNormal.bind(1);
		}

		textureDiff.bind(0);

		view.getBatch().begin();
		AtlasRegion texture = getSprite(action, spriteNum);
		Sprite sprite = new Sprite(texture);
		sprite.setOrigin(VIEW_WIDTH2, VIEW_HEIGHT2 + texture.offsetY);
		sprite.rotate(getRotation());
		//sprite.scale(get);
		sprite.setColor(getColor());

		sprite.setPosition(
			getPosition().getViewSpcX(view) + texture.offsetX - texture.originalWidth / 2,
			getPosition().getViewSpcY(view)//center
			- VIEW_HEIGHT2
			+ texture.offsetY
			- 50 //only this player sprite has an offset because it has overize
		);
		sprite.draw(view.getBatch());
		view.getBatch().end();

		//bind normal map to texture unit 1
		if ((boolean) WE.CVARS.get("LEnormalMapRendering").getValue()) {
			AbstractGameObject.getTextureNormal().bind(1);
		}

		//bind diffuse color to texture unit 0
		//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
		AbstractGameObject.getTextureDiffuse().bind(0);
		view.getBatch().begin();
	}

	/**
	 *
	 * @return null if nothing in reach
	 */
	public AbstractInteractable getNearestInteractable() {
		return nearestEntity;
	}

	/**
	 * if inventory is empty does nothing
	 */
	public void prepareThrow() {
		if (!inventory.isEmpty()) {
			playAnimation('t');
			prepareThrow = true;
		}
	}

	/**
	 * try throwing an item from inventory
	 */
	public void throwItem() {
		Collectible item = inventory.fetchFrontItem();
		if (item != null) {//throw is performed if there is an item to throw
			//play animation
			if (action != 't') {//check if not in loaded position
				playAnimation('t');
			}
			playAnimation = true;
			prepareThrow = false;

			item.setMovement(getMovement().cpy().add(getAiming().scl(3f)));//throw with 3 m/s+current movement
			item.preventPickup(this, 400);
			item.spawn(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH * 1.5f));
		}
	}

	/**
	 * Does an attack move.
	 */
	public void attack(){
		attack((byte)50);
	}
	
	/**
	 * does an attack move
	 *
	 * @param damage custom damage
	 */
	public void attack(byte damage) {
		if (action == 'l') {
			playAnimation('i');
		} else {
			playAnimation('h');
		}

		Controller.getSoundEngine().play("sword");
		addToHor(13f);//add 13 m/s in move direction

		//from current position go 80px in aiming direction and get entities 80px around there
		ArrayList<AbstractEntity> entities = getPosition().cpy().addVector(getAiming().scl(160)).getEntitiesNearby(120);
		entities.addAll(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH2).getEntitiesNearby(39));//add entities under player, what if duplicate?
		//check hit
		for (AbstractEntity entity : entities) {
			if (entity instanceof MovableEntity && entity != this) {
				MovableEntity movable = (MovableEntity) entity;
				movable.damage(damage);
				getCamera().shake(20, 50);
				movable.setMovement(
					new Vector3(
						(float) (getAiming().x + Math.random() * 0.5f - 0.25f),
						(float) (getAiming().y + Math.random() * 0.5f - 0.25f),
						(float) Math.random()
					)
				);
				movable.setSpeedHorizontal(2);
			}
		}

		//damage blocks
		Coordinate aimCoord = getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH2).addVector(getAiming().scl(80)).toCoord();
		//check if the player can damage the blocks
		if (aimCoord.getBlock() != null) {
			if (!CavelandBlocks.hardMaterial( aimCoord.getBlock().getId() )){
				//destructible by hand
				if (aimCoord.damage(damage)) {
					Controller.getSoundEngine().play("impact");
					getCamera().shake(20, 50);
					MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(aimCoord.toPoint().cpy());
					dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
					dirt.setRotation((float) Math.random()*360);
				}
			} else {
				//indestructible by hand
					Controller.getSoundEngine().play("impact");//todo different sound
					getCamera().shake(20, 50);
					Particle dirt = (Particle) new Particle((byte)22).spawn(aimCoord.toPoint().cpy());
					dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
					dirt.setRotation((float) Math.random()*360);
			}
		}

		//set to a small value to indicate that it is active
		loadAttack = 0.00001f;
	}

	@Override
	public void damage(byte value) {
		super.damage(value);
		Controller.getSoundEngine().play("urfHurt");
		if (getCamera() != null) {
			getCamera().setDamageoverlayOpacity(1f - getHealth() / 100f);
		}
		timeSinceDamage = 0;
	}

	@Override
	public void heal(byte value) {
		super.heal(value);
		if (getCamera() != null) {
			getCamera().setDamageoverlayOpacity(1f - getHealth() / 100f);
		}
	}

	@Override
	public MovableEntity clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public void jump() {
		if (!airjump || bunnyHopAllowed()) {
			if (!bunnyHopAllowed()) {
				airjump = true;//do an airjump
			} else {
				if (!isOnGround()) {//doing bunnyhop and not on ground
					onCollide();
					onLand();
					String landingSound = getLandingSound();
					if (landingSound != null) {
						Controller.getSoundEngine().play(landingSound, getPosition());//play landing sound
					}
					step();
				}
				Vector3 resetZ = getMovement().cpy();
				resetZ.z = 0;
				setMovement(resetZ);
			}
			jump(5, !airjump);
			playAnimation('j');
			if (airjump) {
				Controller.getSoundEngine().play("jetpack");
//				for (int i = 0; i < 80; i++) {
//					Dust dust = (Dust) new Dust(
//						1000f,
//						new Color(1, 1, 0, 1)
//					).spawn(
//						getPosition().cpy().addVector(0, 0, AbstractGameObject.GAME_EDGELENGTH2 + (float) Math.random() * AbstractGameObject.GAME_EDGELENGTH)
//					);
//					dust.addMovement(
//						new Vector3(
//							(float) (Math.random()-0.5f)*2f,
//							(float) (Math.random()-0.5f)*2f,
//							-5f
//						)
//					);
//				}
			}
		}
	}

	/**
	 * Checks if the groudn is near. Allows jumping wihtout checking the floor
	 *
	 * @return
	 * @see AbstractEntity#isOnGround()
	 */
	private boolean bunnyHopAllowed() {
		if (bunnyHopForced) {
			bunnyHopForced = false;
			return true;
		}
		final int bunnyhopDistance = 20;
		getPosition().setZ(getPosition().getZ() - bunnyhopDistance);
		boolean colission = isOnGround();
		getPosition().setZ(getPosition().getZ() + bunnyhopDistance);
		return colission;
	}
	
	/**
	 * allows the player to perform the next jump without touching the ground
	 */
	public void forceBunnyHop(){
		bunnyHopForced = true;
	}

	/**
	 * should be called on button release. Performs the load attack if loaded
	 * enough.
	 */
	public void attackLoadingStopped() {
		if (loadAttack >= LOADATTACKTIME) {
			//perform loadattack
			playAnimation('i');
			Controller.getSoundEngine().play("release");
			addToHor(40f);
			attack((byte)100);
		}

		loadAttack = 0f;
		canPlayLoadingSound = false;
	}

	/**
	 * Set the camera which is renderin the player to calculate the aiming. If
	 * camera is null
	 *
	 * @param camera
	 */
	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	/**
	 *
	 * @return
	 */
	public Camera getCamera() {
		return camera;
	}

	@Override
	public void step() {
		super.step();
		Particle dust = (Particle) new Particle(
			(byte) 22,
			1000f
		).spawn(getPosition().cpy());
		dust.setType(Particle.ParticleType.SMOKE);
		dust.setColor(new Color(0.2f, 0.25f, 0.05f, 1f));
		dust.addMovement(new Vector3(0, 0, CoreData.GAME_EDGELENGTH / 500f));
	}

	@Override
	public void onLand() {
		playAnimation('w');
		step();
	}

	@Override
	public void dispose() {
		super.dispose();
		Coordinate coord = getPosition().toCoord();
		Controller.getMap().getSaveCVars().get("PlayerLastSaveX").setValue(coord.getX());
		Controller.getMap().getSaveCVars().get("PlayerLastSaveY").setValue(coord.getY());
		Controller.getMap().getSaveCVars().get("PlayerLastSaveZ").setValue(coord.getZ());
	}

	/**
	 * starts the animation
	 * @param c 
	 */
	private void playAnimation(char c) {
		action = c;
		animationCycle = 0;
		playAnimation = true;
		if (c != 't') {
			prepareThrow = false;
		}
		updateSprite();
	}

	/**
	 * updates the number of the sprite using the cicle information
	 */
	private void updateSprite() {
		//cycle the cycle
		if (animationCycle >= 1000) {
			animationCycle %= 1000;
			if (action == 'l' || action == 'i' || action == 't') {//animation to play only once
				playAnimation = false;//pause
			} else if (action == 'h') {//play hit only once then continue with load
				action = 'l';
			}
		}

		//detect direction
		int animationStart;
		if (getOrientation().x < -Math.sin(Math.PI / 3)) {
			animationStart = 6;//west
		} else {
			if (getOrientation().x < -0.5) {
				//y
				if (getOrientation().y < 0) {
					animationStart = 5;//north-west
				} else {
					animationStart = 7;//south-east
				}
			} else {
				if (getOrientation().x < 0.5) {
					//y
					if (getOrientation().y < 0) {
						animationStart = 4;//north
					} else {
						animationStart = 0;//south
					}
				} else {
					if (getOrientation().x < Math.sin(Math.PI / 3)) {
						//y
						if (getOrientation().y < 0) {
							animationStart = 3;//north-east
						} else {
							animationStart = 1;//sout-east
						}
					} else {
						animationStart = 2;//east
					}
				}
			}
		}
		//fix for different starting positions
		if (action != 'w' && action != 'j' && action != 't') {
			animationStart -= 1;
		}

		if (animationStart < 0) {
			animationStart = 7;
		}

		if (action == 't' || action == 'i') {
			animationStart *= 6;
		} else {
			animationStart *= 8;
		}

		animationStart += 1;

		//manage animation
		int animationStep;
		if (action == 't' || action == 'i') {//throw or hit2
			animationStep = (int) (animationCycle / (1000 / 6f));//six sprites for each animation

			//stop throwing anitmation if it is loaded
			if (prepareThrow && animationStep > 0) {
				playAnimation = false;
				animationStep = 1;
			}
		} else {
			animationStep = (int) (animationCycle / (1000 / 8f));//animation sprites with 8 steps

			if (action == 'j' && animationStep > 3) { //todo temporary fix to avoid landing animation
				animationStep = 3;
				playAnimation = false;
			}
		}
		spriteNum = animationStart + animationStep;
	}
	
	/**
	 * goes in idle position
	 */
	public void idle(){
		playAnimation('w');
		playAnimation = false;
	}

}