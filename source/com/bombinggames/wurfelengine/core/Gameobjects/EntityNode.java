/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core.Gameobjects;

import com.bombinggames.wurfelengine.core.Map.Point;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author Benedikt Vogler
 */
public class EntityNode extends AbstractEntity {
	private static final long serialVersionUID = 1L;
	/**
	 * Spawning and disposing the parent also calls the children.
	 */
	private transient ArrayList<AbstractEntity> children = new ArrayList<>(0);

	public EntityNode() {
		super((byte) 0);
	}

	@Override
	public AbstractEntity spawn(Point point) {
		super.spawn(point);
		for (AbstractEntity child : children) {
			child.spawn(point);
		}
		return this;
	}

	@Override
	public void disposeFromMap() {
		super.disposeFromMap();
		for (AbstractEntity child : children) {
			child.disposeFromMap();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		for (AbstractEntity child : children) {
			child.dispose();
		}
	}

	@Override
	public void setSaveToDisk(boolean saveToDisk) {
		super.setSaveToDisk(saveToDisk);
		for (AbstractEntity child : children) {
			child.setSaveToDisk(saveToDisk);
		}
	}

	@Override
	public void setIndestructible(boolean indestructible) {
		super.setIndestructible(indestructible);
		for (AbstractEntity child : children) {
			child.setIndestructible(indestructible);
		}
	}

	@Override
	public void setHidden(boolean hidden) {
		super.setHidden(hidden);
		for (AbstractEntity child : children) {
			child.setHidden(hidden);
		}
	}
	
	/**
	 * Children linkes objects together so that they inherit some properties. Spawning and disposing the parent also calls the children.
	 * @param child 
	 */
	public void addChild(AbstractEntity child){
		children.add(child);	
	}

	/**
	 * Children linked objects together so that they inherit some properties. Spawning and disposing the parent also calls the children.
	 * @return 
	 */
	public ArrayList<AbstractEntity> getChildren() {
		return children;
	}
	
	private void writeObject(ObjectOutputStream out)throws IOException {
		out.defaultWriteObject();
		//don't save objects which are flagged to be ignored during save
		children.removeIf(ent -> !ent.isGettingSaved());
		out.writeObject(children);
	}
	
	@SuppressWarnings({"unchecked"})
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
		children = (ArrayList<AbstractEntity>) in.readObject();
		enableShadow(); 
    }
}
