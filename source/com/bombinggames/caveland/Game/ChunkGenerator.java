package com.bombinggames.caveland.Game;

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
				return 2;
			
			if (insideout==-1 && z<=5)//build stone for outside
				return 3;
			
			//floor
			if (z<=3)
				return 2;
			
		}
		return 0;
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return -1 outside, 0 middle, 1 inside
	 */
	public static int insideOutside(int x, int y, int z){
		final float g = 15;
		final float p = 5;
		float yStrech=0.5f;
		final float wallsize =1;
		final float roomWithPadding = g+p+wallsize;
		
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
			return 1;//still inside
		}
	}
	
}
