package com.bombinggames.caveland.Game;

import com.bombinggames.caveland.GameObjects.Portal;
import com.bombinggames.wurfelengine.core.Gameobjects.AbstractEntity;
import com.bombinggames.wurfelengine.core.Map.Coordinate;
import com.bombinggames.wurfelengine.core.Map.Generator;

/**
 *
 * @author Benedikt Vogler
 */
public class ChunkGenerator implements Generator {
	/**
	 * every block below this border is  a cave
	 */
	public static final int CAVESBORDER = 1000;
	
	static final float g = 15;
	static final float p = 5;
	static final float yStrech = 0.5f;
	static final float wallsize =1;
	static final float roomWithPadding = g+p+wallsize;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		for (int y = -50; y < 50; y++) {
			for (int x = -50; x < 50; x++) {
				int result = insideOutside(x,y,4);
				if (result ==1)
					System.out.print(".");
				else if (result ==0)
					System.out.print("#");
				else if (result ==-1)
					System.out.print("_");
				else if (result ==2)
					System.out.print("i");
				else if (result ==3)
					System.out.print("o");
			}
			System.out.println("");
		}
	}
	
	@Override
	public byte generate(int x, int y, int z) {
		if (y<CAVESBORDER) {//overworld
			
			//floor
			if (z<3) return 2;
			if (z==3) return 1;
		} else {
			//underworld
			int insideout = insideOutside(x, y, z);
			
			//walls
			if (insideout==0 && z<=5)//build a wall
				return 3;
			
			if (insideout==-1 && z<=5)//build air for outside
				return 0;
			
			//floor
			if (z<=3)
				return 2;
			
		}
		return 0;
	}
	
	/**
	 * the the entities which should be spawned at this coordiante
	 * @param x
	 * @param y
	 * @param z
	 * @return 
	 */
	@Override
	public AbstractEntity[] generateEntities(int x, int y, int z){
		//apply p
		float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		
		//entry
		if (xRoom==5 && yRoom==g-5 && z == 8)
			return new AbstractEntity[]{new Portal(new Coordinate((int) (x-roomWithPadding), y, z))};
		
		//exit
		if (xRoom==g-5 && yRoom==5 && z == 8){
			Portal portal = new Portal(new Coordinate((int) (x+roomWithPadding), y, z));
			portal.setValue((byte) 1);
			return new AbstractEntity[]{portal};
		}
		return null;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return -1 outside, 0 middle, 1 inside, 2 entry, 3 exit
	 */
	public static int insideOutside(int x, int y, int z){
		//apply p
		float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
		
		if (xRoom<0) return -1;
		if (yRoom<0) return -1;
		//yRoom*=yStrech;//strech;

		boolean firstCheckInside = true;//standard firstCheckInside is inside
		
		if (
			   xRoom + yRoom <= g/2.0f//top left
			|| xRoom - yRoom >= g/2.0f//top right
			|| xRoom + yRoom >= 3 * g/2.0f//bottom right
			|| yRoom - xRoom >= g/2.0f//bottom left
		) {
			firstCheckInside=false;//is outside
		}
		
		//check again with checking walls
		if (
			   xRoom + yRoom <= g/2.0f-wallsize
			|| xRoom - yRoom >= g/2.0f+wallsize
			|| xRoom + yRoom >= 3 * g/2.0f+wallsize
			|| yRoom - xRoom >= g/2.0f+wallsize
		) {
			return -1; //if outside and still outside inside definetely outside
		} else {
			if (firstCheckInside==false)
				return 0;//must be in middle if was outside and now inside
			else {
				if (xRoom==5 && yRoom==g-5)
					return 2;
				if (xRoom==g-5 && yRoom==5)
					return 3;
				return 1;//still inside
			}
		}
	}
	
	/**
	 * 
	 * @param coord
	 * @return 
	 * @see #insideOutside(int, int, int) 
	 */
	public static int insideOutside(Coordinate coord){
		return insideOutside(coord.getX(), coord.getY(), coord.getZ());
	}
}
