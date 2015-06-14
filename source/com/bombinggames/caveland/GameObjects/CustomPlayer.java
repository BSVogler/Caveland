package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.Game.CavelandBlocks;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.Gameobjects.Block;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.GAME_EDGELENGTH2;
import static com.bombinggames.wurfelengine.core.Gameobjects.Block.VIEW_HEIGHT2;
import com.bombinggames.wurfelengine.core.Gameobjects.BlockDirt;
import com.bombinggames.wurfelengine.core.Gameobjects.Controllable;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.Particle;
import com.bombinggames.wurfelengine.core.Gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.Map.AbstractPosition;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Point;
import com.bombinggames.wurfelengine.WE;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class CustomPlayer extends Controllable implements EntityNode {

	/**
	 * Time till fully loaded attack.
	 */
	public static final float LOADATTACKTIME = 1000;
	private static final long serialVersionUID = 2L;

	private transient static TextureAtlas spritesheet;
	private static final transient AtlasRegion[][] sprites = new AtlasRegion['z'][65];
	private transient static Texture textureDiff;
	private transient static Texture textureNormal;
		
	/**
	 * loads the spritesheets for the custom player
	 */
	public static void loadSheet() {
		if (spritesheet == null) {
			spritesheet = WE.getAsset("com/bombinggames/Caveland/playerSheet.txt");
		}
		textureDiff = spritesheet.getTextures().first();
		if (WE.CVARS.getValueB("LEnormalMapRendering")) {
			textureNormal = WE.getAsset("com/bombinggames/Caveland/playerSheetNormal.png");
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
	
	private final int playerNumber;

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
	private float loadAttack = Float.NEGATIVE_INFINITY;
	private transient final float LOAD_THRESHOLD = 300;//300ms until loading starts
	private transient AbstractEntity nearestEntity;

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
	private boolean usedLoadAttackInAir;
	private final SmokeEmitter emitter;
	private final SmokeEmitter emitter2;
	private SimpleEntity interactButton = null;
	private Coordinate nearestInteractableBlock = null;
	private int spriteNumOverlay;
	/**
	 * true if attack in loadattackMode
	 */
	private boolean performingLoadAttack = false;
	
	/**
	 * timer for delaying impact
	 */
	private float timeTillImpact; 
	/**
	 * save the damage until it is used on the impact
	 */
	private byte attackDamage;
	
	/**
	 * creates a new Ejira
	 * @param number
	 */
	public CustomPlayer(int number) {
		super((byte) 30, 0);
		playerNumber = number;
		setName("Ejira");
		setStepSound1Grass("step");
		//setRunningSound( (Sound) WE.getAsset("com/bombinggames/Caveland/sounds/victorcenusa_running.ogg"));
		setJumpingSound("urfJump");
		setFriction((float) WE.CVARS.get("playerfriction").getValue());
		setDimensionZ(Block.GAME_EDGELENGTH);
		
		emitter = new SmokeEmitter();
		emitter.setParticleDelay(10);
		emitter.setParticleTTL(800);
		emitter.setHidden(true);
		SuperGlue connection1 = new SuperGlue(this, emitter);
		connection1.setOffset(new Vector3(-20, 0, Block.GAME_EDGELENGTH2));
		addChild(connection1);
		
		emitter2 = new SmokeEmitter();
		emitter2.setParticleDelay(10);
		emitter2.setParticleTTL(800);
		emitter2.setHidden(true);
		SuperGlue conection2 = new SuperGlue(this, emitter2);
		conection2.setOffset(new Vector3(20, 0, Block.GAME_EDGELENGTH2));
		addChild(conection2);
		setSaveToDisk(false);
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
		} else {
			if (usedLoadAttackInAir)
				setFriction((float) WE.CVARS.get("playerfriction").getValue());
			usedLoadAttackInAir = false;
		}
		
		//if in cave force in it
		if (( WE.CVARS.getChildSystem().getChildSystem().getValueB("P"+playerNumber+"InCave") )){
			if (pos.getZ()>Chunk.getGameHeight()-Block.GAME_EDGELENGTH)
				pos.setZ(Chunk.getGameHeight()-Block.GAME_EDGELENGTH);
		}

		inventory.update(dt);

		/*ANIMATION*/
		{
			//some redundant code from movable to have a custom animation
			if (playAnimation) {
				if (action == 'w') {
					animationCycle += dt * getSpeed() * (float) WE.CVARS.get("walkingAnimationSpeedCorrection").getValue();//multiply by factor to make the animation fit the movement speed
				} else {
					animationCycle += dt * 5;
				}
			}
			//update regular sprite
			updateSprite(false);
		}

		//delay for attack
		if (timeTillImpact != Float.POSITIVE_INFINITY){
			timeTillImpact -= dt;
			if (timeTillImpact <= 0)
				attackImpact();
		}
		
		//stop loadAttack if speed it too low
		if (performingLoadAttack && getSpeedHor() < 0.1){
			performingLoadAttack=false;
		}
		
		//detect button hold
		if (loadAttack != Float.NEGATIVE_INFINITY) {
			loadAttack += dt;
			
			
			//loading attack
			if (loadAttack > LOAD_THRESHOLD) {//time till registered as a "hold"
				if (action!='l' && action!='i' && !playAnimation)
					playAnimation('l');
				if (!canPlayLoadingSound) {
					Controller.getSoundEngine().play("loadAttack");
					canPlayLoadingSound = true;
				}
				Vector3 newmov = getMovement();
				newmov.z /= 4;
				setMovement(newmov);
				
				//update overlay
				updateSprite(true);
			}

			if (loadAttack >= LOADATTACKTIME) {
				attackLoadingStopped();
			}
		}
//		if (loadAttack > 0) {
//			Vector3 newmov = getMovement();
//			newmov.z /= 2;//half vertical speed
//			if (newmov.z < 0) {
//				newmov.z *= 2 / 3;//if falling then more "freeze in air"
//			}
//			setMovement(newmov);
//		}

		

		//get loren
		ArrayList<MineCart> nearbyLoren = pos.toPoint().getEntitiesNearby(Block.GAME_EDGELENGTH2, MineCart.class);

		if (!nearbyLoren.isEmpty()) {
			MineCart lore = nearbyLoren.get(0);
			if (lore.getPassenger() == null) {//if contact with lore and it has no passenger
				if (pos.getZ() > (lore.getPosition().getZ() + Block.GAME_EDGELENGTH2/2)) {//enter chu chu
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
				playCollectSound = true;
			}
		}
		if (playCollectSound) {
			Controller.getSoundEngine().play("collect");
		}

		//auto heal
		if (timeSinceDamage > 4000) {
			heal((byte) (dt / 2f));
		} else {
			timeSinceDamage += dt;
		}

		//update interactable focus
		//check entitys
		ArrayList<Interactable> nearbyInteractable = getPosition().getEntitiesNearbyHorizontal(
			GAME_EDGELENGTH * 2,
			Interactable.class
		);

		if (!nearbyInteractable.isEmpty()) {
			//check if a different one
			nearestEntity = (AbstractEntity) nearbyInteractable.get(0);
			showButton(Interactable.RT, nearestEntity.getPosition());
		} else if (nearestEntity != null) {
			hideButton();
			nearestEntity = null;
		}
		
		//check interactable blocks
		Block blockBelow = getPosition().toCoord().getBlock();
		if (blockBelow!= null && CavelandBlocks.interactAble(blockBelow.getId(), blockBelow.getValue())){
			//todo only overwrite if block is nearer
			nearestInteractableBlock = getPosition().toCoord();
			showButton(Interactable.RT, nearestInteractableBlock);
		} else {
			//no nearby block
			//hide button if also no nearestEntity
			if (nearestEntity==null)
				hideButton();
		}
			
		//play walking animation
//		if (isOnGround() && getSpeedHor() > 0 && !playAnimation && loadAttack==Float.NEGATIVE_INFINITY) {
//			if (action!='i')
//				playAnimation('w');
//		}

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
		if (!WE.CVARS.getValueB("ignorePlayer")) {
			view.getBatch().end();//inject new batch here

			//bind normal map to texture unit 1
			if ((boolean) WE.CVARS.get("LEnormalMapRendering").getValue()) {
				textureNormal.bind(1);
			}

			textureDiff.bind(0);

			view.getBatch().begin();
				AtlasRegion texture = getSprite(action, spriteNum);
				Sprite sprite = new Sprite(texture);
				sprite.setOrigin(
						texture.originalWidth/2 - texture.offsetX,
						VIEW_HEIGHT2 - texture.offsetY
					);
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

				//overlay
				if (loadAttack > LOAD_THRESHOLD || performingLoadAttack) {//loading or perfomring loadattack
					AtlasRegion overlayTexture;
					if (action=='i'){
						overlayTexture = getSprite('o', spriteNum);
					} else {
						overlayTexture = getSprite('s', spriteNumOverlay);
					}

					Sprite overlaySprite = new Sprite(overlayTexture);
					sprite.setOrigin(
						overlayTexture.originalWidth/2 - overlayTexture.offsetX,
						VIEW_HEIGHT2 - overlayTexture.offsetY
					);
					overlaySprite.scale(1f);
					overlaySprite.setColor(getColor());

					overlaySprite.setPosition(
						getPosition().getViewSpcX(view) + overlayTexture.offsetX - overlayTexture.originalWidth / 2,
						getPosition().getViewSpcY(view)//center
						- VIEW_HEIGHT2
						+ overlayTexture.offsetY
						+100 //offset for overlay
					);
					overlaySprite.draw(view.getBatch());
				}
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
	}

	/**
	 *interacts with the nearest thign if there is one
	 * @param view
	 */
	public void interactWithNearestThing(GameView view) {
		if (nearestEntity!=null)
			((Interactable) nearestEntity).interact(this, view);
		else {
			if (nearestInteractableBlock!=null)
				CavelandBlocks.interact(nearestInteractableBlock, this);
		}
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
			item.setPosition(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH * 1f));
			item.setHidden(false);
		}
	}
	
	/**
	 * drop an item by laying it down
	 */
	public void dropItem(){
		Collectible item = inventory.fetchFrontItem();
		if (item != null) {//throw is performed if there is an item to throw
			item.setPosition(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH * 0.1f));
			item.setMovement(Vector3.Zero.cpy());//throw with 3 m/s+current movement
			item.setHidden(false);
			item.preventPickup(this, 800);
		}
	}
	
	public void useItem(){
		inventory.action(this);
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
		if (timeTillImpact == Float.POSITIVE_INFINITY) {
			performingLoadAttack = false;
			if (action == 'l') {
				playAnimation('i');
			} else {
				if (loadAttack< LOADATTACKTIME || action != 'i')
					playAnimation('h');
			}

			Controller.getSoundEngine().play("sword");
			if (isOnGround())
				addToHor(13f);//add 13 m/s in move direction

			//start timer
			timeTillImpact = WE.CVARS.getValueF("PlayerTimeTillImpact");
			attackDamage = damage;

			if (!usedLoadAttackInAir){
				//set to 0 to indicate that it is active
				loadAttack = 0f;
			}
		}
	}
	
	private void attackImpact(){
		if (isSpawned()) {
			timeTillImpact = Float.POSITIVE_INFINITY;

			//from current position go 80px in aiming direction and get entities 80px around there
			ArrayList<AbstractEntity> entities = getPosition().cpy().addVector(getAiming().scl(160)).getEntitiesNearby(120);
			entities.addAll(getPosition().cpy().addVector(0, 0, GAME_EDGELENGTH2).getEntitiesNearby(39));//add entities under player, what if duplicate?
			//check hit
			for (AbstractEntity entity : entities) {
				if (entity instanceof MovableEntity && entity != this && !entity.isHidden()) {
					MovableEntity movable = (MovableEntity) entity;
					movable.damage(attackDamage);
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
				getCamera().shake(20, 50);
				byte id = aimCoord.getBlock().getId();
				if (!CavelandBlocks.hardMaterial( id )){
					//destructible by hand
					if (aimCoord.damage(attackDamage)) {
						if (id == 72)
							Controller.getSoundEngine().play("treehit");
						else
							Controller.getSoundEngine().play("impact");
						MovableEntity dirt = (MovableEntity) new BlockDirt().spawn(aimCoord.toPoint().cpy());
						dirt.addMovement(new Vector3((float) Math.random()-0.5f, (float) Math.random()-0.5f,(float) Math.random()*5f));
						dirt.setRotation((float) Math.random()*360);
					}
				} else {
					//indestructible by hand
					Controller.getSoundEngine().play("impact");//todo different sound
					//spawn particle
					Particle dirt = (Particle) new Particle((byte)22).spawn(aimCoord.toPoint().cpy().addVector(0, 0, 30));
					dirt.setTTL(400);
					dirt.addMovement(new Vector3(
						(float) (Math.random()-0.5f)*10.0f,
						(float) (Math.random()-0.5f)*10.0f,
						(float) Math.random()*2f)
					);
					dirt.setRotation((float) Math.random()*360);
				}
			}
		}
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
			if (!isOnGround()) {
				setFriction((float) WE.CVARS.get("playerfriction").getValue()/3f);
				addMovement(new Vector3(getOrientation().cpy().scl(30),-2f));
			} else {
				addToHor(40f);
			}
			attack((byte)100);
			performingLoadAttack=true;
			usedLoadAttackInAir = true;
		}

		loadAttack = Float.NEGATIVE_INFINITY;
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
	public void walk(boolean up, boolean down, boolean left, boolean right, float walkingspeed, float dt) {
		//if loading attack keep movement
		Vector2 movementBefore = getMovementHor().cpy();
		super.walk(up, down, left, right, walkingspeed, dt);
		if (loadAttack != Float.NEGATIVE_INFINITY || performingLoadAttack) {
			setMovement(movementBefore);
		} else {
			if (up || down || left || right){
				if (isOnGround() && getSpeedHor() > 0 && !playAnimation)
					playAnimation('w');
			}
		}
	}
	
	
	@Override
	public void step() {
		super.step();
		Particle dust = (Particle) new Particle(
			(byte) 22,
			1000f
		).spawn(getPosition().cpy().addVector((float) (40*Math.random()-20), (float) (40*Math.random()-20), 0));
		dust.setType(Particle.ParticleType.SMOKE);
		dust.setColor(new Color(0.2f, 0.25f, 0.05f, 1f));
		dust.addMovement(
			new Vector3(
				-getMovementHor().x*0.1f,
				-getMovementHor().y*0.1f,
				Block.GAME_EDGELENGTH / 500f
			)
		);
	}

	@Override
	public void onLand() {
		playAnimation('w');
		step();
	}

	@Override
	public void dispose() {
		Coordinate coord = getPosition().toCoord();
		WE.CVARS.getChildSystem().getChildSystem().get("PlayerLastSaveX").setValue(coord.getX());
		WE.CVARS.getChildSystem().getChildSystem().get("PlayerLastSaveY").setValue(coord.getY());
		WE.CVARS.getChildSystem().getChildSystem().get("PlayerLastSaveZ").setValue(coord.getZ());
		super.dispose();
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
		if (c == 'w') {
			performingLoadAttack=false;
		}
		updateSprite(false);
	}

	/**
	 * updates the number of the sprite using the cicle information
	 * @param overlay true if the overlay should  be updated
	 */
	private void updateSprite(boolean overlay) {
		if (overlay){
			//???
		} else {
			//cycle the cycle
			if (animationCycle >= 1000) {
				animationCycle %= 1000;
				if (action == 'h') {//play hit only once then continue with load
					action = 'l';
				}
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
			animationStep = getAnimationStep(6, overlay);

			if (!overlay) {
				//stop throwing anitmation if it is loaded
				if (prepareThrow && animationStep > 0) {
					playAnimation = false;
					animationStep = 1;
				} 
				if (animationStep >= 5){
					playAnimation = false;
				}
			}
		} else {
			animationStep = getAnimationStep(8, overlay);

			if (!overlay) {
				if (animationStep >= 7 && (action == 'l' || action == 'i')) {//animation to play only once
					playAnimation = false;//pause
				}

				if (action == 'j' && animationStep > 3) { //todo temporary fix to avoid landing animation
					animationStep = 3;
					playAnimation = false;
				}
			}
		}
		if (overlay){
			spriteNumOverlay = animationStart + animationStep;
		} else {
			spriteNum = animationStart + animationStep;
		}
	}
	
	private int getAnimationStep(int steps, boolean overlay){
		if (overlay) {
			if (loadAttack-LOAD_THRESHOLD >= LOADATTACKTIME)
				return 7;
			else
				return (int) ((loadAttack-LOAD_THRESHOLD) / (LOADATTACKTIME / (float) steps));//six sprites for each animation	
		} else
			return (int) (animationCycle / (1000 / (float) steps));//animation sprites with 8 steps
	}
	
	/**
	 * goes in idle position
	 */
	public void idle(){
		playAnimation('w');
		playAnimation = false;
	}
	
	/**
	 * display the interact button
	 * @param buttonID
	 * @param pos abot this position the button will appear
	 */
	public void showButton(byte buttonID, AbstractPosition pos) {
		if (interactButton == null) {
			interactButton = (SimpleEntity) new SimpleEntity((byte) 23, buttonID).spawn(pos.toPoint().cpy().addVector(0, 0, Block.GAME_EDGELENGTH)
			);
			addChild(interactButton);
			interactButton.setLightlevel(1);
			interactButton.setSaveToDisk(false);
		} else {
			interactButton.setPosition(pos.toPoint().cpy().addVector(0, 0, Block.GAME_EDGELENGTH));
		}
	}

	/**
	 * hide the interact button
	 */
	public void hideButton() {
		if (interactButton != null) {
			interactButton.dispose();
			interactButton = null;
		}
	}

	public int getPlayerNumber() {
		return playerNumber;
	}

}