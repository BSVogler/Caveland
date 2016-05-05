/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.mapeditor;

import com.bombinggames.wurfelengine.Command;
import com.bombinggames.wurfelengine.core.Controller;
import com.bombinggames.wurfelengine.core.gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.gameobjects.Cursor;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Point;

/**
 * a enum listing the available tools
 */
public enum Tool {
	/**
	 * tool to draw blocks
	 */
	DRAW(0, "draw_button", true, false, true), /**
	 * tool to cover an area with blocks
	 */ BUCKET(1, "bucket_button", true, false, false), /**
	 * "repaints" blocks
	 */ REPLACE(2, "replace_button", true, false, false), /**
	 * select and move entities
	 */ SELECT(3, "pointer_button", false, false, false), /**
	 * spawn new entities
	 */ SPAWN(4, "entity_button", false, true, true), /**
	 * replace blocks with air
	 */ ERASE(5, "eraser_button", false, false, false);
	final int id;
	final String name;
	final boolean selectFromBlocks;
	final boolean selectFromEntities;
	public final boolean showNormal;

	private Tool(int id, String name, boolean worksOnBlocks, boolean worksOnEntities, boolean showNormal) {
		this.id = id;
		this.name = name;
		this.selectFromBlocks = worksOnBlocks;
		this.selectFromEntities = worksOnEntities;
		this.showNormal = showNormal;
	}

	/**
	 *
	 * @return
	 */
	public int getId() {
		return id;
	}

	/**
	 *
	 * @return
	 */
	public boolean selectFromBlocks() {
		return selectFromBlocks;
	}

	/**
	 *
	 * @return
	 */
	public boolean selectFromEntities() {
		return selectFromEntities;
	}

	/**
	 *
	 *
	 * @param cursor
	 * @param placableTable
	 * @return
	 */
	public Command getCommand(Cursor cursor, AbstractPlacableTable placableTable) {
		switch (this) {
			case DRAW:
				return new Command() {
					private Coordinate coord;
					private int previous;
					private int block;

					@Override
					public void execute() {
						if (coord == null) {
							coord = cursor.getCoordInNormalDirection();
							block = ((BlockTable) placableTable).getSelectedBlock();
							previous = coord.getBlock();
						}
						Controller.getMap().setBlock(coord, block);
					}

					@Override
					public void undo() {
						Controller.getMap().setBlock(coord, previous);
					}
				};
			case REPLACE:
				return new Command() {
					private Coordinate coord;
					private int previous;
					private int block;

					@Override
					public void execute() {
						if (coord == null) {
							coord = cursor.getPosition().toCoord();
							block = ((BlockTable) placableTable).getSelectedBlock();
							previous = coord.getBlock();
						}
						Controller.getMap().setBlock(coord, block);
					}

					@Override
					public void undo() {
						Controller.getMap().setBlock(coord, previous);
					}
				};
			case SPAWN:
				return new Command() {
					private AbstractEntity ent = null;
					private Point point;

					@Override
					public void execute() {
						if (point == null) {
							point = cursor.getNormal().getPosition();
							ent = ((EntityTable) placableTable).getEntity();
						}
						if (ent != null) {
							ent = ((EntityTable) placableTable).getEntity();
							ent.spawn(point.cpy());
						}
					}

					@Override
					public void undo() {
						ent.dispose();
					}
				};
			case ERASE:
			default:
				//erase
				return new Command() {
					private int previous;
					private Coordinate coord;

					@Override
					public void execute() {
						if (coord == null) {
							coord = cursor.getPosition().toCoord();
							previous = coord.getBlock();
						}
						Controller.getMap().setBlock(coord, (byte) 0);
					}

					@Override
					public void undo() {
						Controller.getMap().setBlock(coord, previous);
					}
				};
		}
	}
	
}
