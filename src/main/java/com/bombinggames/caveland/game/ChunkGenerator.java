package com.bombinggames.caveland.game;

import com.bombinggames.caveland.gameobjects.ExitPortal;
import com.bombinggames.wurfelengine.core.map.Coordinate;
import com.bombinggames.wurfelengine.core.map.Generator;

/**
 *
 * @author Benedikt Vogler
 */
public class ChunkGenerator implements Generator {
	/**
	 * every block below this border is a cave
	 */
	public static final int CAVESBORDER = 1000;
	
	/**
	 * every block below this border is a generated cave
	 */
	public static final int GENERATORBORDER = 1200;
	
	/**width of a room */
	static final float g = 21;
	/**
	 * padding
	 */
	static final float p = 5;
	static final float yStrech = 0.5f;
	/**
	 * how thick is the wall
	 */
	static final float wallsize =1;
	static final float roomWithPadding = g+p+wallsize;
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		for (int y = GENERATORBORDER; y < GENERATORBORDER+100; y++) {
			for (int x = 0; x < 100; x++) {
				int result = insideOutside(x,y,4);
				switch (result) {
					case 1:
						System.out.print(". ");
						break;
					case 0:
						System.out.print("# ");
						break;
					case -1:
						System.out.print("_ ");
						break;
					case 2:
						System.out.print("i ");
						break;
					case 3:
						System.out.print("o ");
						break;
					default:
						break;
				}
			}
			System.out.println("");
		}
	}
	
	@Override
	public int generate(int x, int y, int z) {
		if (y<CAVESBORDER) {//overworld
			
			//floor
			if (z<3) return 2;
			if (z==3) return 1;
		} else {
			if (y < GENERATORBORDER)
				return 0;
			
			//underworld
			int insideout = insideOutside(x, y, z);
			
//			if (insideout==2)
//				return RenderBlock.getInstance(CavelandBlocks.CLBlocks.ENTRY.getSpriteId());
			
			if (insideout==2)
				return CavelandBlocks.CLBlocks.ENTRY.getId();
				
			//walls
			if (insideout==0) {//build a wall
				if (z<=4)
					return CavelandBlocks.CLBlocks.INDESTRUCTIBLEOBSTACLE.getId()+(1<<8);
			}
			
			if (insideout==-1)//build air for outside
				return 0;	
			
			if (z==3)
				if ((x*y*2+x+y*3+500) % 8==y % 7)
					if (x%2==0 && y%2==0){
						if (y%5==0)
							return CavelandBlocks.CLBlocks.SULFUR.getId();
						else
							return CavelandBlocks.CLBlocks.COAL.getId();
					} else {
						if (y%8==0)
							return CavelandBlocks.CLBlocks.IRONORE.getId();
						else
							return 2;
					}
			
			//floor
			if (z<=2)
				return (byte)2;
			
		}
		return 0;
	}
	
	/**
	 * the the entities which should be spawned at this coordiante
	 * @param x
	 * @param y
	 * @param z
	 */
	@Override
	public void spawnEntities(int x, int y, int z){
		if (y > GENERATORBORDER) {
			//apply p
			float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
			float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;

			//loch in der Decke
			if (xRoom==g-5 && yRoom==p+2 && z == 4) {
				ExitPortal portal = (ExitPortal) new ExitPortal().spawn(new Coordinate(x, y, z).toPoint());
				portal.enableEnemySpawner();
				if (getCaveNumber(x, y, z)==0) {
					//exit to surface
					portal.setTarget(new Coordinate(0, 0, 5));
				} else {
					portal.setTarget(getCaveDown(getCaveNumber(x, y, z)-1));
				}
				//portal.setSpriteValue((byte) 1);
				//portal.enableEnemySpawner();
			}
		}
	}
	
	/**
	 * Get the numbers of the cave. The entry cave is cave number 0.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return -1 if not in any cave
	 */
	public static int getCaveNumber(int x, int y, int z){
		if (y < GENERATORBORDER )
			return -1;
		return (int) Math.floor(x / roomWithPadding);
	}
	
	/**
	 * Get the numbers of the cave. The entry cave is cave number 0.
	 * 
	 * @param coord
	 * @return -1 if not in any cave
	 */
	public static int getCaveNumber(Coordinate coord){
		return getCaveNumber(coord.getX(), coord.getY(), coord.getZ());
	}
	
	/**
	 * copy safe
	 * @param caveNumber start with 0
	 * @return 
	 */
	public static Coordinate getCaveUp(int caveNumber){
		return new Coordinate(
			(int) (roomWithPadding*caveNumber+g),
			GENERATORBORDER+66,
			7
		);
	}
	
	/**
	 * copy safe
	 * @param caveNumber
	 * @return 
	 */
	public static Coordinate getCaveDown(int caveNumber){
		return new Coordinate(
			(int) (roomWithPadding*caveNumber+11),
			GENERATORBORDER+77,
			4
		);
	}
	
	/**
	 * copy safe
	 * @param caveNumber
	 * @return 
	 */
	public static Coordinate getCaveCenter(int caveNumber){
		return new Coordinate(
			(int) (roomWithPadding*caveNumber+p+wallsize+g/2),
			(int) (GENERATORBORDER+20+(p+g)/yStrech),
			4
		);
	}
	
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return -1 outside, 0 middle, 1 inside, 2 entry, 3 exit
	 */
	public static int insideOutside(int x, int y, int z){
		if (y>=GENERATORBORDER) {
			//apply p
			float xRoom = (((x) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;
			float yRoom = (((y*yStrech) % roomWithPadding) + roomWithPadding) % roomWithPadding-p;

			if (xRoom<0) return -1;
			if (yRoom<0) return -1;
			//yRoom*=yStrech;//strech;

			boolean firstCheckInside = true;//standard firstCheckInside is inside

			//fix/hack for even walls and staggered map
			if (xRoom < g/2 && y%2 == 1)
				xRoom++;

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
					if (xRoom==6 && yRoom==g-8 && z==2)
						return 2;
					if (xRoom==g-7 && yRoom==6 && z==4)
						return 3;
					return 1;//still inside
				}
			}
		} else {
			return -1;
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
