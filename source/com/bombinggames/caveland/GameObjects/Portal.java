package com.bombinggames.caveland.GameObjects;

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.bombinggames.caveland.GameObjects.logicblocks.LiftLogic;
import com.bombinggames.wurfelengine.core.Events;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Gameobjects.MovableEntity;
import com.bombinggames.wurfelengine.core.Map.Chunk;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.extension.AimBand;
import java.util.ArrayList;

/**
 * Teleports every object in this cell.
 *
 * @author Benedikt Vogler
 */
public class Portal extends AbstractEntity implements Telegraph {

	private static final long serialVersionUID = 2L;
	private Coordinate target = new Coordinate(0, 0, Chunk.getBlocksZ() - 1);
	/**
	 * indicates whether the portal is open or not
	 */
	private transient boolean active = true;
	private transient AimBand particleBand;
	/**
	 * if true checks if at the target there is an exit pointing to this entry
	 */
	private boolean verifiedExit;
	private transient ExitPortal exitPortal;

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default<br>
	 * Invisible by default.
	 */
	public Portal() {
		super((byte) 0);
		setIndestructible(true);
		setName("Portal");
	}

	/**
	 * teleports to 0 0 Chunk.getBlocksZ()-1 by default
	 *
	 * @param id
	 */
	public Portal(byte id) {
		super(id);
		setIndestructible(true);
		setName("Portal");
	}

	/**
	 * copy safe
	 *
	 * @return
	 */
	public Coordinate getTarget() {
		if (target == null) {
			return null;
		} else {
			return target.cpy();
		}
	}

	/**
	 * Set the coordiante where the portal teleports to.
	 *
	 * @param target
	 */
	public void setTarget(Coordinate target) {
		this.target = target;
	}

	@Override
	public void update(float dt) {
		if (hasPosition() && active && getTarget() != null) {
			if (particleBand != null) {
				particleBand.update();
			}

			//move things in the portal
			getPosition().toCoord().getEntitiesInside(MovableEntity.class)
				.forEach((AbstractEntity e) -> {
					if (e.getPosition().getZ() <= getPosition().toPoint().getZ() + 10//must be in the first part of the block
						&& e != this //don't teleport itself
						&& ((MovableEntity) e).isColiding()//only teleport things which collide
					) {
						teleport(e);
					}
				}
				);
		}
	}
	
	/**
	 *
	 * @param e
	 */
	public void teleport(AbstractEntity e){
		if (verifiedExit) {
			if (getPosition().toCoord().add(0, 0, 1).getLogic() instanceof LiftLogic) {
				//teleport in front of lift
				MessageManager.getInstance().dispatchMessage(
					this,
					e,
					Events.teleport.getId(),
					getExitPortal().getGround().goToNeighbour(6).toPoint()
				);
				return;
			}
		}
		MessageManager.getInstance().dispatchMessage(
			this,
			e,
			Events.teleport.getId(),
			target.toPoint()
		);
	}

	/**
	 * Verifies and repairs if needed so that at the target there is an exitPortal pointing back to this portal.
	 * @return 
	 */	
	public ExitPortal getExitPortal(){
		ArrayList<ExitPortal> exitPortalList = target.getEntitiesInside(ExitPortal.class);
		if (exitPortalList.isEmpty()) {
			//spawn new exitportal if missing
			exitPortal = (ExitPortal) new ExitPortal().spawn(target.toPoint());
		} else {
			exitPortal = exitPortalList.get(0);
		}
		
		//check target of exitportal
		Coordinate exitTarget = getPosition().toCoord().goToNeighbour(5).add(0, 0, 1);//target is left from portal
		if (!exitPortal.getTarget().equals(exitTarget)) {
			exitPortal.setTarget(exitTarget);
		}
		return exitPortal;
	}

	/**
	 * Set the portal to "open" or "closed"
	 *
	 * @param b
	 */
	public void setActive(boolean b) {
		active = b;
	}

	/**
	 * indicates whether the portal is open or not
	 *
	 * @return
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * if true checks that at the target is an exitPortal
	 * @param verifyExit 
	 */
	public void setVerifyExit(boolean verifyExit) {
		this.verifiedExit = verifyExit;
	}
	
	/**
	 * if at the target there is an exit portal returns it.
	 * @return 
	 */
	public ExitPortal getCorrespondingExitPortal(){
		return exitPortal;
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		 if (msg.message == Events.deselectInEditor.getId()){
			if (particleBand != null) {
				particleBand.dispose();
				particleBand = null;
			}
		} else if (msg.message == Events.selectInEditor.getId()){
			if (particleBand == null) {
				particleBand = new AimBand(this, target);
			} else {
				particleBand.setTarget(target);
			}
		}
		return true;
	}
	
}
