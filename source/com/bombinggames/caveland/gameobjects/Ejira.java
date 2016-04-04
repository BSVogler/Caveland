package com.bombinggames.caveland.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.bombinggames.caveland.game.CLGameView;
import com.bombinggames.caveland.game.CavelandBlocks;
import com.bombinggames.caveland.game.ChunkGenerator;
import com.bombinggames.caveland.gameobjects.collectibles.Collectible;
import com.bombinggames.caveland.gameobjects.collectibles.CollectibleContainer;
import com.bombinggames.caveland.gameobjects.collectibles.Inventory;
import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.Camera;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.GameView;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractGameObject;
import com.bombinggames.wurfelengine.core.gameobjects.Controllable;
import com.bombinggames.wurfelengine.core.gameobjects.DestructionParticle;
import com.bombinggames.wurfelengine.core.gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Particle;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleEmitter;
import com.bombinggames.wurfelengine.core.gameobjects.ParticleType;
import com.bombinggames.wurfelengine.core.gameobjects.PointLightSource;
import com.bombinggames.wurfelengine.core.gameobjects.SimpleEntity;
import com.bombinggames.wurfelengine.core.map.AbstractBlockLogicExtension;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;
import com.bombinggames.wurfelengine.core.map.Position;
import com.bombinggames.wurfelengine.core.map.rendering.RenderCell;
import static com.bombinggames.wurfelengine.core.map.rendering.RenderCell.GAME_EDGELENGTH;
import static com.bombinggames.wurfelengine.core.map.rendering.RenderCell.GAME_EDGELENGTH2;
import static com.bombinggames.wurfelengine.core.map.rendering.RenderCell.VIEW_HEIGHT2;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The Ejira has two particle emitter attached via glue. Itself is not saved but it's position. The content of the backpack is dropped on disposing and saved separately.
 * @author Benedikt Vogler
 */
public class Ejira extends CLMovableEntity implements Controllable, HasTeam {

	/**
	 * Time till fully loaded attack.
	 */
	public static final float LOADATTACKTIME = 1000;
	private static final long serialVersionUID = 2L;

	private transient static final AtlasRegion[][] SPRITES = new AtlasRegion['z'][65];
	private transient static TextureAtlas spritesheet;
	private transient static Texture textureDiff;
	private transient static Texture textureNormal;
		
	/**
	 * loads the spritesheets for the custom player
	 */
	public static void loadSheet() {
		if (spritesheet == null) {
			spritesheet = WE.getAsset("com/bombinggames/caveland/playerSheet.txt");
		}
		textureDiff = spritesheet.getTextures().first();
		if (WE.getCVars().getValueB("LEnormalMapRendering")) {
			textureNormal = WE.getAsset("com/bombinggames/caveland/playerSheetNormal.png");
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
		if (SPRITES[category][value] == null) { //load if not already loaded

			AtlasRegion sprite = spritesheet.findRegion("diff/"+category + "/" + Integer.toString(value));
			if (sprite == null) { //if there is no sprite show the default "sprite not found sprite" for this category
				Gdx.app.debug("Player animation", category + Integer.toString(value) + " not found");
				return null;
			}
			SPRITES[category][value] = sprite;
			return sprite;
		} else {
			return SPRITES[category][value];
		}
	}
	
	private transient final int playerNumber;

	private transient boolean canPlayLoadingSound = false;

	private transient int timeSinceDamage;

	private transient Inventory inventory;

	/**
	 * true if last jump was airjump.
	 */
	private transient float jetPackTime = 0;

	private transient Camera camera;

	/**
	 * time of loading
	 */
	private transient float loadAttack = Float.NEGATIVE_INFINITY;
	private transient final float LOAD_THRESHOLD = 300;//300ms until loading starts
	private transient Interactable nearestInteractable;

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
	private transient boolean prepareThrow;
	private transient boolean bunnyHopForced;
	private transient boolean usedLoadAttackInAir;
	private transient SimpleEntity interactButton = null;
	private transient int spriteNumOverlay;
	/**
	 * true if attack in loadattackMode
	 */
	private transient boolean performingPowerAttack = false;
	
	/**
	 * timer for delaying impact
	 */
	private transient float timeTillImpact; 
	/**
	 * save the damage until it is used on the impact
	 */
	private transient byte attackDamage;
	
	private transient final ParticleEmitter emitter;
	private transient final ParticleEmitter emitter2;
	private PointLightSource lightsource;
	private transient AimBand interactionAimband;
	/**
	 * Orientation of the sprite. Does not turn isntantly like the orientation.
	 */
	private final Vector2 spriteOrientation = new Vector2(1, 0);
	private boolean jetpackOn;
	
	/**
	 * creates a new Ejira
	 * @param number should start by 1
	 */
	public Ejira(int number) {
		super((byte) 30, 0);
		playerNumber = number;
		setName("Ejira");
		setStepSound1Grass("step");
		setJumpingSound("urfJump");
		setFriction((float) WE.getCVars().get("playerfriction").getValue());
		setDimensionZ((int) (RenderCell.GAME_EDGELENGTH*1.4f));
		setObstacle(true);
		setMass(60f);
		
		Particle particle = new Particle((byte) 22, 800);
		particle.setColor(new Color(1.0f, 0.8f, 0.2f, 1f));
		particle.setType(ParticleType.FIRE);
		particle.setTTL(1800f);
		emitter = new ParticleEmitter(80);
		emitter.setParticleDelay(7);
		emitter.setPrototype(particle);
		//emitter.setBrightness(3.1f);
		emitter.setActive(false);
		emitter.setHidden(true);
		emitter.setSaveToDisk(false);
		emitter.setParticleSpread(new Vector3(1.6f, 0.6f, 0.5f));
		
		emitter2 = new ParticleEmitter(80);
		emitter2.setParticleDelay(7);
		emitter2.setPrototype(particle);
		emitter2.setBrightness(10.1f);
		emitter2.setActive(false);
		emitter2.setHidden(true);
		emitter2.setSaveToDisk(false);
		emitter2.setParticleSpread(new Vector3(0.6f, 0.6f, 0.5f));
		
		setSaveToDisk(false);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		inventory = (Inventory) new Inventory(this).spawn();
		
		emitter.spawn(point.cpy());
		emitter2.spawn(point.cpy());
		
		lightsource = new PointLightSource(Color.MAGENTA.cpy(), 2, 10, WE.getGameplay().getView());
		lightsource.setSaveToDisk(false);
		lightsource.spawn(getPosition().cpy().add(0, 0, RenderCell.GAME_EDGELENGTH2));
		return this;
	}
	
	@Override
	public void dispose() {
		emitter.dispose();
		emitter2.dispose();
		lightsource.dispose();
		super.dispose();
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
		
		if (hasPosition()) {
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
					setFriction((float) WE.getCVars().get("playerfriction").getValue());
				usedLoadAttackInAir = false;
			}

			//if in cave force in it
			if ( getPosition().toCoord().getY() > ChunkGenerator.CAVESBORDER ){
				if (pos.getZ()>Chunk.getGameHeight()-RenderCell.GAME_EDGELENGTH)
					pos.setZ(Chunk.getGameHeight()-RenderCell.GAME_EDGELENGTH);
			}

			/*ANIMATION*/
			{
				//some redundant code from movable to have a custom animation
				if (playAnimation) {
					if (action == 'w') {
						animationCycle += dt * getSpeed() * (float) WE.getCVars().get("walkingAnimationSpeedCorrection").getValue();//multiply by factor to make the animation fit the movement speed
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
			if (performingPowerAttack && getSpeedHor() < 0.1) {
				performingPowerAttack = false;
			}

			//detect button hold
			if (loadAttack != Float.NEGATIVE_INFINITY) {
				loadAttack += dt;


				//loading attack
				if (loadAttack > LOAD_THRESHOLD) {//time till registered as a "hold"
					if (action!='l' && action!='i' && !playAnimation)
						playAnimation('l');
					if (!canPlayLoadingSound) {
						WE.SOUND.play("loadAttack");
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



			//get nearby mine cart
			ArrayList<MineCart> nearbyMc = getCollidingEntities(MineCart.class);

			if (!nearbyMc.isEmpty()) {
				Iterator<MineCart> it = nearbyMc.iterator();
				while (it.hasNext()) {
					MineCart minecart = it.next();
					//if contact with lore and it has no passenger
					if (minecart.getPassenger() == null
						&& pos.getZ() > (minecart.getPosition().getZ() + RenderCell.GAME_EDGELENGTH2/2)
					) {
						//enter chu chu
						minecart.setPassanger(this);
						break;
					}
				}
			}
			
			//collect collectibles
			ArrayList<Collectible> collectibles = getCollidingEntities(Collectible.class);
			boolean playCollectSound = false;
			for (Collectible collectible : collectibles) {
				if (collectible.canBePickedByParent(this) && inventory.add(collectible)) {
					playCollectSound = true;
				}
			}
			if (playCollectSound) {
				WE.SOUND.play("collect");
			}
			
			ArrayList<Money> money = getCollidingEntities(Money.class);
			money.forEach(m -> m.dispose());
			
			if (!money.isEmpty()) {
				WE.SOUND.play("moneyPickup");
			}
			
			//increase money
			WE.getCVarsSave().get("money").setValue(WE.getCVarsSave().getValueI("money")+money.size());

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
			
			//check nearby blocks
			final Coordinate origin = getPosition().toCoord();
			final Coordinate tmp = getPosition().toCoord();
			for (int x = -2; x < 2; x++) {
				for (int y = -2; y < 2; y++) {
					for (int z = -2; z < 2; z++) {
						AbstractBlockLogicExtension logic = tmp.set(origin).add(x, y, z).getLogic();
						if (
							logic != null
							&& logic instanceof Interactable
							&& tmp.distanceTo(getPosition()) <= GAME_EDGELENGTH * 2
						) {
							nearbyInteractable.add((Interactable) logic);
						}
					}
				}
			}
			
			//remove not interactable objects
			nearbyInteractable.removeIf((Interactable t) -> !t.interactable() || t.interactableOnlyWithPickup());

			//sort to find nearest object
			if (!nearbyInteractable.isEmpty()) {
				nearbyInteractable.sort((Interactable o1, Interactable o2) -> {
					if (o1.getPosition().distanceTo(getPosition()) < o2.getPosition().distanceTo(getPosition()))
						return -1;
					else if (o1.getPosition().distanceTo(getPosition()) == o2.getPosition().distanceTo(getPosition()))
						return 0;
					else
						return 1;
				});
				
				//check if a different one
				nearestInteractable = nearbyInteractable.get(0);
				showInteractButton(Interactable.RT, nearestInteractable.getPosition());
			} else if (nearestInteractable != null) {
				hideInteractButton();
				nearestInteractable = null;
			}

			//if collecting a backpack
			ArrayList<CollectibleContainer> backpacksOnCoord = origin.getEntitiesInside(CollectibleContainer.class);
			CollectibleContainer backpack=null;
			if (backpacksOnCoord.size()>0)
				backpack = backpacksOnCoord.get(0);
			if (backpack != null && !backpack.isHidden()){
				for (int i = 0; i < backpack.size(); i++) {
					backpack.retrieveCollectible(i);
				}
				backpack.dispose();
			}

			//play walking animation
	//		if (isOnGround() && getSpeedHor() > 0 && !playAnimation && loadAttack==Float.NEGATIVE_INFINITY) {
	//			if (action!='i')
	//				playAnimation('w');
	//		}

			//update attached objects position
			if (lightsource.hasPosition())
				lightsource.getPosition().set(getPosition()).add(0, 0, RenderCell.GAME_EDGELENGTH2);
			
			Vector3 vecToJetpack = new Vector3(getOrientation().scl(-20), 0);
			float angleOrient = (float) Math.acos(getOrientation().y);
			Vector3 vecToEmitter = new Vector3(-25, 0, RenderCell.GAME_EDGELENGTH2).rotateRad(new Vector3(0, 0, 1), angleOrient);
			if (emitter.hasPosition())
				emitter.getPosition().set(getPosition()).add(vecToEmitter).add(vecToJetpack);
			vecToEmitter = new Vector3(25, 0, RenderCell.GAME_EDGELENGTH2).rotateRad(new Vector3(0, 0, 1), angleOrient);
			if (emitter2.hasPosition())
				emitter2.getPosition().set(getPosition()).add(vecToEmitter).add(vecToJetpack);
			
			if (jetpackOn){
				jetPackTime -= dt;
			}
			
			if (jetpackOn && jetPackTime <= 0) {
				extinguishJetpack();
			}
			
			//refill
			if ( isOnGround() && jetPackTime <= 0){
				jetPackTime = WE.getCVars().getValueF("jetpackMaxTime");
			}

			//update emitter
			if (jetpackOn) {
				//limit speed to prevent mega jumps
				if (getMovement().z < WE.getCVars().getValueF("jetpackMaxSpeed")) {
					addMovement(new Vector3(0, 0, dt * WE.getCVars().getValueF("jetpackPower")));
				}
				emitter.setActive(true);
				emitter2.setActive(true);
				emitter.setParticleStartMovement(new Vector3(0, 0, -getMovement().z*1.5f));
				emitter2.setParticleStartMovement(new Vector3(0, 0, -getMovement().z*1.5f));
			} else {
				emitter.setActive(false);
				emitter2.setActive(false);
			}
			
			if (interactionAimband != null) {
				interactionAimband.update();
			}
			
			float turnSpeed = getSpeedHor()*dt*0.002f;
			slerp(spriteOrientation,getOrientation(),turnSpeed);
		}
	}

	/**
	 * rotation interpolation
	 * @param start
	 * @param end
	 * @param percent
	 * @return 
	 */
	private Vector2 slerp(Vector2 start, Vector2 end, float percent) {
		// Dot product - the cosine of the angle between 2 vectors.
		float dot = start.dot(end);
		if (dot < -1) {
			dot = -1;
		}
		if (dot > 1) {
			dot = 1;
		}
		// Acos(dot) returns the angle between start and end,
		// And multiplying that by percent returns the angle between
		// start and the final result.
		float theta = (float) (Math.acos(dot) * percent);
		Vector2 relVec = end.sub(start.cpy().scl(dot));
		relVec.nor();
		// The final result.
		return ((start.scl((float) Math.cos(theta)).add((relVec.scl((float) Math.sin(theta))))));
	}
	@Override
	public void setHidden(boolean hidden) {
		super.setHidden(hidden);
	}

	@Override
	public void render(GameView view, Camera camera) {
		if (!WE.getCVars().getValueB("ignorePlayer") && textureNormal != null) {
			view.getSpriteBatch().end();//inject new batch here

			//bind normal map to texture unit 1
			if ((boolean) WE.getCVars().get("LEnormalMapRendering").getValue()) {
				textureNormal.bind(1);
			}

			textureDiff.bind(0);

			view.getSpriteBatch().begin();
				AtlasRegion texture = getSprite(action, spriteNum);
				Sprite sprite = new Sprite(texture);
				sprite.setOrigin(
					texture.originalWidth/2 - texture.offsetX,
					VIEW_HEIGHT2 - texture.offsetY
				);
				//sprite.scale(get);
				sprite.setColor(getColor());

				sprite.setPosition(
					getPosition().getViewSpcX() + texture.offsetX - texture.originalWidth / 2,
					getPosition().getViewSpcY()//center
					- VIEW_HEIGHT2
					+ texture.offsetY
					- 50 //only this player sprite has an offset because it has overize
				);
				sprite.draw(view.getSpriteBatch());

				//overlay
				if (loadAttack > LOAD_THRESHOLD || performingPowerAttack) {//loading or perfomring loadattack
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
					overlaySprite.scale(1f);//saved at half size, so must scale by 2 to fit
					overlaySprite.setColor(new Color(0.8f, 0.8f, 0.8f, 1f));//a bit brigher then default

					overlaySprite.setPosition(
						getPosition().getViewSpcX() + overlayTexture.offsetX - overlayTexture.originalWidth / 2,
						getPosition().getViewSpcY()//center
						- VIEW_HEIGHT2
						+ overlayTexture.offsetY
						+100 //offset for overlay
					);
					overlaySprite.draw(view.getSpriteBatch());
				}
			view.getSpriteBatch().end();

			//bind normal map to texture unit 1
			if ((boolean) WE.getCVars().get("LEnormalMapRendering").getValue()) {
				AbstractGameObject.getTextureNormal().bind(1);
			}

			//bind diffuse color to texture unit 0
			//important that we specify 0 otherwise we'll still be bound to glActiveTexture(GL_TEXTURE1)
			AbstractGameObject.getTextureDiffuse().bind(0);
			view.getSpriteBatch().begin();
		}
	}

	/**
	 * interacts with the nearest thign if there is one
	 *
	 * @param view
	 */
	public void interactWithNearestThing(CLGameView view) {
		if (nearestInteractable != null) {
			nearestInteractable.interact(view, this);
		}
	}

	/**
	 * creates an aimband
	 * @param ent 
	 */
	public void startInteraction(AbstractEntity ent) {
		interactionAimband = new AimBand(this, ent);
	}

	/**
	 * creates an aimband
	 * @param coord 
	 */
	public void startInteraction(Coordinate coord) {
		interactionAimband = new AimBand(this, coord);
	}

	/**
	 * removed the aimband
	 */
	public void endInteraction() {
		if (interactionAimband != null) {
			interactionAimband.dispose();
		}
		interactionAimband = null;
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
	 * try throwing an item from inventory. Throw must be prepared beforehand
	 */
	public void throwItem() {
		Collectible item = inventory.retrieveFrontItem();
		if (item != null && prepareThrow) {//throw is performed if there is an item to throw
			//play animation
			if (action != 't') {//check if not in loaded position
				playAnimation('t');
			}
			playAnimation = true;
			prepareThrow = false;

			item.setMovement(getMovement().cpy().add(getAiming().scl(3f)));//throw with 3 m/s+current movement
			item.preventPickup(this, 400);
			item.getPosition().set(getPosition()).add(0, 0, GAME_EDGELENGTH * 1f);
			item.setHidden(false);
		} else {
			WE.SOUND.play("interactionFail");
		}
	}

	@Override
	public Vector3 getAiming() {
		return new Vector3(spriteOrientation, 0);
	}
	
	/**
	 * drop an item by laying it down
	 */
	public void dropItem(){
		prepareThrow = false;
		Collectible item = inventory.retrieveFrontItem();
		if (item != null) {//throw is performed if there is an item to throw
			item.setPosition(getPosition().cpy().add(0, 0, GAME_EDGELENGTH * 0.1f));
			item.setMovement(Vector3.Zero.cpy());//throw with 3 m/s+current movement
			item.setHidden(false);
			item.preventPickup(this, 800);
		}
		playAnimation('w');
	}
	
	/**
	 *
	 * @param view
	 */
	public void useItem(CLGameView view){
		inventory.action(view, this);
	}

	/**
	 * Does an attack move.
	 */
	public void attack(){
		attack((byte)50);
	}
	
	/**
	 * does an attack move and starts the loading of the power attack	
	 *
	 * @param damage custom damage
	 */
	public void attack(byte damage) {
		if (timeTillImpact == Float.POSITIVE_INFINITY) {
			performingPowerAttack = false;
			if (action == 'l') {
				playAnimation('i');
			} else {
				if (loadAttack< LOADATTACKTIME || action != 'i')
					playAnimation('h');
			}

			WE.SOUND.play("sword");
			if (isOnGround())
				addToHor(13f);//add 13 m/s in move direction

			//start timer
			timeTillImpact = WE.getCVars().getValueF("PlayerTimeTillImpact");
			attackDamage = damage;

			if (!usedLoadAttackInAir){
				//set to 0 to indicate that it is active
				loadAttack = 0f;
			}
		}
	}
	
	private void attackImpact(){
		if (hasPosition()) {
			timeTillImpact = Float.POSITIVE_INFINITY;

			//from current position go 80px in aiming direction and get nearbyEntities 80px around there
			ArrayList<AbstractEntity> nearbyEntities = getPosition().cpy().add(getAiming().scl(160)).getEntitiesNearby(120);
			nearbyEntities.addAll(getPosition().cpy().add(0, 0, GAME_EDGELENGTH2).getEntitiesNearby(39));//add nearbyEntities under player, what if duplicate?
			//check hit
			for (AbstractEntity entity : nearbyEntities) {
				if ( entity != this && !entity.isHidden()) {
					MessageManager.getInstance().dispatchMessage(
						this,
						entity,
						Events.damage.getId(),
						attackDamage
					);
					getCamera().shake(20, 50);
				
					if (entity instanceof MovableEntity) {
						((MovableEntity) entity).setMovement(
							new Vector3(
								(float) (getAiming().x + Math.random() * 0.5f - 0.25f),
								(float) (getAiming().y + Math.random() * 0.5f - 0.25f),
								(float) Math.random()
							)
						);
						((MovableEntity) entity).setSpeedHorizontal(2);
					}
				}
			}

			//damage blocks
			Coordinate aimCoord = getPosition().cpy().add(0, 0, GAME_EDGELENGTH2).add(getAiming().scl(80)).toCoord();
			//check if the player can damage the blocks
			if (!RenderCell.isLiquid(aimCoord.getBlock())) {
				getCamera().shake(20, 50);
				byte id = aimCoord.getBlockId();
				if (!CavelandBlocks.hardMaterial( id )){
					//destructible by hand
					if (aimCoord.damage(attackDamage)) {
						if (id == 72)
							WE.SOUND.play("treehit");
						else
							WE.SOUND.play("impact");
						//one particle
						new DestructionParticle((byte) 44).spawn(aimCoord.toPoint());
					}
				} else {
					//indestructible by hand
					WE.SOUND.play("impact");//todo different sound
					//spawn particle
					Particle dirt = (Particle) new Particle((byte)22).spawn(aimCoord.toPoint().add(0, 0, 30));
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
	public void takeDamage(byte value) {
		if (!WE.getCVars().getValueB("godmode")) {
			super.takeDamage(value);
			WE.SOUND.play("urfHurt");
			if (getCamera() != null) {
				getCamera().setDamageoverlayOpacity(1f - getHealth() / 100f);
			}
			timeSinceDamage = 0;
		}
	}

	@Override
	public void heal(byte value) {
		super.heal(value);
		if (getCamera() != null) {
			getCamera().setDamageoverlayOpacity(1f - getHealth() / 100f);
		}
	}

	@Override
	public void jump() {
		//check if an arijump can be performed
		if (hasPosition()) {
			if (canJumpFromGround()) {
				if (!isOnGround()) {//must perform a bunnyhop b/c not on ground
					MessageManager.getInstance().dispatchMessage(this, Events.landed.getId());
				}
				//cancel z movement
				Vector3 resetZ = getMovement().cpy();
				resetZ.z = 0;
				setMovement(resetZ);
				playAnimation('j');
				jump(4.7f, true);
			} else {
				fireJetpack();
			}
		}
	}
	
	public void fireJetpack(){
		if (jetPackTime>0) {
			if (!jetpackOn){
				WE.SOUND.play("jetpack");
			}
			jetpackOn = true;
		}
	}
	
	public void extinguishJetpack(){
		jetpackOn = false;
	}

	/**
	 * Checks if the ground is near. Allows jumping without touching the floor.
	 *
	 * @return ture if a bit above groudn or at ground
	 * @see AbstractEntity#isOnGround()
	 */
	private boolean canJumpFromGround() {
		if (bunnyHopForced) {
			bunnyHopForced = false;
			return true;
		}
		//check if the ground is below
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
			WE.SOUND.play("release");
			if (!isOnGround()) {
				setFriction((float) WE.getCVars().get("playerfriction").getValue()/3f);
				addMovement(new Vector3(getOrientation().cpy().scl(30),-2f));
			} else {
				addToHor(40f);
			}
			attack((byte)100);
			performingPowerAttack = true;
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
		
		//update the direction vector
		Vector2 dir = new Vector2(left ? -1 : (right ? 1 : 0f), up ? -1 : (down ? 1 : 0f));
		dir.nor();
		if (dir.len2()>0)
			setOrientation(dir);
		
		boolean standingStill = false;
		if (getSpeedHor() < 0.1f)
			standingStill = true;
		
		//if loading attack keep movement
		if (up || down || left || right) {
			if (loadAttack == Float.NEGATIVE_INFINITY && !performingPowerAttack && !prepareThrow) {
			//set speed to 0 if at max allowed speed for accelaration and moving in movement direction
			//in order to find out, add movement dir and current movement dir together and if len(vector) > len(currentdir)*sqrt(2) then added speed=0
			//			float accelaration =30;//in m/s^2
			//			dir.scl(accelaration*dt/1000f);//in m/s
			//check if will reach max velocity
			//			Vector3 res = getMovement().add(dir.cpy());
			//			res.z=0;
			//			if (res.len() > walkingspeed){
			//				//scale that it will not exceed the walkingspeed
			//				dir.nor().scl((walkingspeed-res.len()));
			//			}
			//			addMovement(dir);
			//repalce horizontal movement if walking

				setHorMovement(dir.scl(walkingspeed));
				if (isOnGround() && getSpeedHor() > 0.1f && !playAnimation) {
					playAnimation('w');
				}
			}
			if (standingStill) {
				float turnSpeed = dt*0.008f;
				if (spriteOrientation.cpy().sub(getOrientation()).len() < 1.5f)
					turnSpeed *= 4;
				slerp(spriteOrientation, getOrientation(), turnSpeed);
			}
		}
	}
	
	
	@Override
	public void step() {
		super.step();
		Particle dust = (Particle) new Particle(
			(byte) 22,
			700f
		).spawn(
			getPosition().cpy().add(
				(float) (40*Math.random()-20),
				(float) (40*Math.random()-20),
				0
			)
		);
		dust.setType(ParticleType.SMOKE);
		dust.setColor(new Color(0.2f, 0.25f, 0.05f, 0.8f));
		dust.addMovement(new Vector3(
				-getMovementHor().x*0.1f,
				-getMovementHor().y*0.1f,
				RenderCell.GAME_EDGELENGTH / 500f
			)
		);
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
			performingPowerAttack  = false;
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
		if (spriteOrientation.x < -Math.sin(Math.PI / 3)) {
			animationStart = 6;//west
		} else {
			if (spriteOrientation.x < -0.5) {
				//y
				if (spriteOrientation.y < 0) {
					animationStart = 5;//north-west
				} else {
					animationStart = 7;//south-east
				}
			} else {
				if (spriteOrientation.x < 0.5) {
					//y
					if (spriteOrientation.y < 0) {
						animationStart = 4;//north
					} else {
						animationStart = 0;//south
					}
				} else {
					if (spriteOrientation.x < Math.sin(Math.PI / 3)) {
						//y
						if (spriteOrientation.y < 0) {
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

				if (action == 'j' && !isOnGround() && animationStep > 3) { //todo temporary fix to avoid landing animation
					animationStep = 3;
					playAnimation = false;
				}
				if (action == 'j' && isOnGround()){
					playAnimation = true;
				}
				if (action == 'j' && animationStep > 6){
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
	
	private int getAnimationStep(int steps, boolean overlay) {
		if (overlay) {
			if (loadAttack - LOAD_THRESHOLD >= LOADATTACKTIME) {
				return 7;
			} else {
				return (int) ((loadAttack - LOAD_THRESHOLD) / (LOADATTACKTIME / steps));//six sprites for each animation	
			}
		} else {
			return (int) (animationCycle / (1000 / (float) steps));//animation sprites with 8 steps
		}
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
	public void showInteractButton(byte buttonID, Position pos) {
		if (interactButton == null) {
			interactButton = (SimpleEntity) new SimpleEntity((byte) 23, buttonID).spawn(pos.toPoint().add(0, 0, RenderCell.GAME_EDGELENGTH)
			);
			interactButton.setName("Interact Button");
			interactButton.setCategory('i');
			interactButton.setLightlevel(1);
			interactButton.setSaveToDisk(false);
		} else {
			interactButton.setPosition(pos.toPoint().add(0, 0, RenderCell.GAME_EDGELENGTH));
		}
	}

	/**
	 * hide the interact button
	 */
	public void hideInteractButton() {
		if (interactButton != null) {
			interactButton.dispose();
			interactButton = null;
		}
	}

	/**
	 * starts with 1
	 *
	 * @return
	 */
	public int getPlayerNumber() {
		return playerNumber;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		super.handleMessage(msg);
		if (msg.message == Events.damage.getId()) {
			byte damage = ((Byte) msg.extraInfo);
			if (!WE.getCVars().getValueB("godmode"))
				takeDamage(damage);
			if (getHealth() <= 0) {
				die();
			}
			return true;
		}
		
		return false;
	}

	@Override
	public int getTeamId() {
		return 2;
	}

	private void die() {
		//respawn
		heal((byte) 100);
		Coordinate respawn = new Coordinate(
			WE.getCVarsSave().getValueI("respawnX"),
			WE.getCVarsSave().getValueI("respawnY"),
			WE.getCVarsSave().getValueI("respawnZ")
		);
		respawn.add(0, 1, 0);
		
		setPosition(respawn);
	}
	
}