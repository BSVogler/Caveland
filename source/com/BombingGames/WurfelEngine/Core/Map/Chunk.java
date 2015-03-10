/*
 * Copyright 2013 Benedikt Vogler.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of Bombing Games nor Benedikt Vogler nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.BombingGames.WurfelEngine.Core.Map;

import com.BombingGames.WurfelEngine.Core.CVar;
import com.BombingGames.WurfelEngine.Core.Controller;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractEntity;
import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.Core.Gameobjects.Block;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A Chunk is filled with many Blocks and is a part of the map.
 * @author Benedikt
 */
public class Chunk {
    /**The suffix of a chunk files.*/
    protected static final String CHUNKFILESUFFIX = "wec";
    /**The suffix of the metafile */
    protected static final String METAFILESUFFIX = "wem";

	private static int blocksX = 10;
    private static int blocksY = 40;//blocksY must be even number
    private static int blocksZ = 10;
	
	/**
	 * save file stuff
	 */
	private final static char SIGN_ENTITIES = '|';//124 OR 0x7c
	private final static char SIGN_STARTCOMMENTS = '{';//123 OR 0x7b
	private final static char SIGN_ENDCOMMENTS = '}';//125 OR 0x7d
	private final static char SIGN_EMPTYLAYER = '~';//126 OR 0x7e
	
    /**
     *
     * @param meta
     */
    protected static void setDimensions(MapMetaData meta) {
        blocksX = meta.getChunkBlocksX();
        blocksY = meta.getChunkBlocksY();
        blocksZ = meta.getChunkBlocksZ();
    }
	/**
	 * chunk coordinate
	 */
	private final int coordX, coordY;
    private final Block data[][][];
	private boolean modified;
	/**
	 * How many cameras are pointing at this chunk? If &lt;= 0 delete from memory.
	 */
	private int cameraAccessCounter = 0;
  
    /**
     * Creates a Chunk filled with empty cells (likely air).
	 * @param coordX
	 * @param coordY
     */
    public Chunk(final int coordX, final int coordY) {
        this.coordX = coordX;
		this.coordY = coordY;
		
		data = new Block[blocksX][blocksY][blocksZ];
        
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++)
                    data[x][y][z] = Block.getInstance(0);
		modified = true;

    }
    
    /**
    *Creates a chunk by trying to load and if this fails it generates a new one.
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
     * @param mapname filename
     * @param generator
    */
    public Chunk(final String mapname, final int coordX, final int coordY, final Generator generator){
        this(coordX,coordY);
        Gdx.app.debug("Chunk", "Creating chunk "+coordX+","+coordY);
		if (CVar.get("shouldLoadMap").getValueb()){
			if (!load(mapname, coordX, coordY))
				fill(coordX, coordY, generator);
		} else fill(coordX, coordY, generator);
		increaseCameraAccesCounter();
    }
    
    /**
    *Creates a chunk by generating a new one.
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
    * @param generator
    */
    public Chunk(final int coordX, final int coordY, final Generator generator){
        this(coordX,coordY);
        fill(coordX, coordY, generator);
    }
	
	public void update(float dt){
		for (Block[][] x : data) {
			for (Block[] y : x) {
				for (Block z : y) {
					z.update(dt);
				}
			}
		}
		
		if (modified){
			modified=false;
			Controller.getMap().modified();
			for (LinkedWithMap object : Controller.getMap().getLinkedObjects()){
				object.onChunkChange(this);
			}
		}
	}
    
    /**
     * Fills the chunk using a generator.
     * @param chunkCoordX
     * @param chunkCoordY
     * @param generator 
     */
    private void fill(final int chunkCoordX, final int chunkCoordY, final Generator generator){
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++){
					Block block = Block.getInstance(
						generator.generate(blocksX*chunkCoordX+x, blocksY*chunkCoordY+y, z),
						0
					);
					block.setPosition(new Coordinate(blocksX*chunkCoordX+x, blocksY*chunkCoordY+y, z));
                    data[x][y][z] = block;//relative to chunk to map absolute
				}
		modified = true;
    }
    
    /**
     * Tries to load a chunk from disk.
     */
    private boolean load(final String fileName, int coordX, int coordY){

		//FileHandle path = Gdx.files.internal("/map/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
		FileHandle path = Gdx.files.absolute(
			WE.getWorkingDirectory().getAbsolutePath()
				+ "/maps/"+fileName+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX
		);


		if (path.exists()) {
			Gdx.app.debug("Chunk","Loading Chunk: "+ coordX + ", "+ coordY);
			//Reading map files test
			try (FileInputStream fis = new FileInputStream(path.file())) {
				int z = 0;
				int x;
				int y;

				int bufChar = fis.read();

				//read a line
				while (bufChar != -1 && bufChar != SIGN_ENTITIES) {//read while not eof and not at entity part
					if (bufChar == 0x0A) //skip line breaks
						bufChar = fis.read();

					if (bufChar != 0x0A){

						//jump over optional comment line
						if (bufChar == SIGN_STARTCOMMENTS){
							bufChar = fis.read();
							while (bufChar != SIGN_ENDCOMMENTS){
								bufChar = fis.read();
							}
							bufChar = fis.read();
							if (bufChar== 0x0A)//if following is a line break also skip it again
								bufChar = fis.read();
						}

						//if layer is empty, fill with air
						if (bufChar == SIGN_EMPTYLAYER ){
							for (x = 0; x < blocksX; x++) {
								for (y = 0; y < blocksY; y++) {
									data[x][y][z] = Block.getInstance(0);
									data[x][y][z].setPosition(
										new Coordinate(
											coordX*blocksX+x,
											coordY*blocksY+y,
											z
										)
									);
								}
							}
						} else {
							//fill layer block by block
							y = 0;
							do {
								x = 0;
								do {

									int id; //already got first one
									if (y==0 && x==0)
										 id = bufChar;
									else 
										id = fis.read();
									int value = fis.read();
									data[x][y][z] = Block.getInstance(id, value);
									data[x][y][z].setPosition(
										new Coordinate(
											coordX*blocksX+x,
											coordY*blocksY+y,
											z
										)
									);
									x++;
								} while (x < blocksX);
								y++;
							} while (y < blocksY);
						}
						z++;
					}
					//read next line
					bufChar = fis.read();
				}

				if (CVar.get("loadEntities").getValueb()) {
					//loading entities
					if (bufChar==SIGN_ENTITIES){
						int length = fis.read(); //amount of entities
						Gdx.app.debug("Chunk", "Loading " + length+" entities");

						try (ObjectInputStream objectIn = new ObjectInputStream(fis)) {
							AbstractEntity object;
							for (int i = 0; i < length; i++) {
								object = (AbstractEntity) objectIn.readObject();
								Controller.getMap().getEntitys().add(object);
								Gdx.app.debug("Chunk", "Loaded entity: "+object.getName());
							}
						} catch (ClassNotFoundException ex) {
							Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
				modified = true;
				return true;
			} catch (IOException ex) {
				Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed: "+ex);
			} catch (StringIndexOutOfBoundsException | NumberFormatException ex) {
				Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed. Map file corrupt: "+ex);
			} catch (ArrayIndexOutOfBoundsException ex){
				Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed.Chunk or meta file corrupt: "+ex);
			}
		} else {
			Gdx.app.log("Chunk",coordX + ","+ coordY +" could not be found on disk.");
		}
		
        return false;
    }
    
    /**
     * 
     * @param fileName the map name on disk

     * @return 
     * @throws java.io.IOException 
     */
    public boolean save(String fileName) throws IOException {
        if ("".equals(fileName)) return false;
        Gdx.app.log("Chunk","Saving "+coordX + ","+ coordY +".");
        FileHandle path = new FileHandle(WE.getWorkingDirectory().getAbsolutePath() + "/maps/"+fileName+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
        
        path.file().createNewFile();
        try (FileOutputStream fileOut = new FileOutputStream(path.file())) {		
			try {
				for (int z = 0; z < blocksZ; z++) {
					//check if layer is empty
					boolean dirty = false;
					for (int x = 0; x < blocksX; x++) {
						for (int y = 0; y < blocksY; y++) {
							if (data[x][y][z].getId() != 0)
								dirty=true;
						}
					}
					fileOut.write(SIGN_STARTCOMMENTS);
					fileOut.write(z);
					fileOut.write(SIGN_ENDCOMMENTS);
					if (dirty)
						for (int y = 0; y < blocksY; y++) {
							for (int x = 0; x < blocksX; x++) {
								fileOut.write(data[x][y][z].getId());
								fileOut.write(data[x][y][z].getValue());
							}
						}
					else {
						fileOut.write(SIGN_EMPTYLAYER);
					}
				}
				
				//save entities
				ArrayList<AbstractEntity> entities = Controller.getMap().getEntitysOnChunkWhichShouldBeSaved(coordX, coordY);
				if (entities.size() > 0) {
					fileOut.write(SIGN_ENTITIES);
					fileOut.write(entities.size());
					try (ObjectOutputStream outStream = new ObjectOutputStream(fileOut)) {
						for (AbstractEntity ent : entities){
							Gdx.app.debug("Chunk", "Saving entity:"+ent.getId());
							outStream.writeObject(ent);
						}
						outStream.close();
					}
				}
			} catch (IOException ex){
				throw ex;
			}
		}
		return true;
    }
        /**
     * The amount of blocks in X direction
     * @return 
     */
    public static int getBlocksX() {
        return blocksX;
    }

    /**
     * The amount of blocks in Y direction
     * @return 
     */
    public static int getBlocksY() {
        return blocksY;
    }

   /**
     * The amount of blocks in Z direction
     * @return 
     */
    public static int getBlocksZ() {
        return blocksZ;
    }
    

    /**
     * Returns the data of the chunk
     * @return 
     */
    public Block[][][] getData() {
        return data;
    }

    /**
     *Not scaled.
     * @return
     */
    public static int getViewWidth(){
        return blocksX*AbstractGameObject.VIEW_WIDTH;
    }
    
    /**
     *Not scaled.
     * @return
     */
    public static int getViewDepth() {
        return blocksY*AbstractGameObject.VIEW_DEPTH2;// Divided by 2 because of shifted each second row.
    }
    
    /**
     *x axis
     * @return
     */
    public static int getGameWidth(){
        return blocksX*AbstractGameObject.GAME_DIAGLENGTH;
    }
    
    /**
     *y axis
     * @return
     */
    public static int getGameDepth() {
        return blocksY*AbstractGameObject.GAME_DIAGLENGTH2;
    }
    
        /**
     * The height of the map. z axis
     * @return in game size
     */
    public static int getGameHeight(){
        return blocksZ*AbstractGameObject.GAME_EDGELENGTH;
    }
	
	
	/**
	 * Check if the coordinate has the coordinate inside.
	 * @param coord the coordinate to be checked
	 * @return true if coord is inside.
	 */
	public boolean hasCoord(Coordinate coord){
		int x = coord.getX();
		int y = coord.getY();
		int left = data[0][0][0].getPosition().getX();
		int top = data[0][0][0].getPosition().getY();
		return (x >= left
				&& x < left + blocksX
				&& y >= top
				&& y < top + blocksY
		);
	}
	
		/**
	 * Check if the coordinate has the coordinate inside.
	 * @param point the coordinate to be checked
	 * @return true if coord is inside.
	 */
	public boolean hasPoint(Point point){
		float x = point.getX();
		float y = point.getY();
		float left = getTopLeftCoordinate().getX();
		float top = getTopLeftCoordinate().getY();
		return (x >= left
				&& x < left + getGameWidth()
				&& y >= top
				&& y < top + getGameDepth()
		);
	}
	
	/**
	 * print the chunk to console
	 * @return 
	 */
	@Override
	public String toString() {
		String strg = null;
		for (int z = 0; z < blocksZ; z++) {
			for (int y = 0; y < blocksY; y++) {
				for (int x = 0; x < blocksX; x++) {
					if (data[x][y][z].getId()==0)
						strg += "  ";
					else
						strg += data[x][y][z].getId() + " ";
				}
				strg += "\n";
			}
			strg += "\n\n";
		}
		return strg;
	}
	
	
	public ChunkIterator getIterator(final int startingZ, final int limitZ){
		return new ChunkIterator(this, startingZ, limitZ);
	}

	public int getChunkX() {
		return coordX;
	}

	public int getChunkY() {
		return coordY;
	}
	
	public Coordinate getTopLeftCoordinate(){
		return data[0][0][0].getPosition();
	}

	/**
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param z coordinate
	 * @return 
	 */
	public Block getBlock(int x, int y, int z) {
		int xIndex = x-data[0][0][0].getPosition().getX();
		int yIndex = y-data[0][0][0].getPosition().getY();
		return data[xIndex][yIndex][z];
	}

	/**
	 * sets a block in the map. if position is under the map does nothing.
	 * @param block 
	 */
	public void setBlock(Block block) {
		int xIndex = block.getPosition().getX()-data[0][0][0].getPosition().getX();
		int yIndex = block.getPosition().getY()-data[0][0][0].getPosition().getY();
		int z = block.getPosition().getZ();
		if (z >= 0){
			data[xIndex][yIndex][z] = block;
			modified = true;
		}
	}
	
	public void resetCameraAccesCounter(){
		cameraAccessCounter=0;
	}
	
	public final void increaseCameraAccesCounter(){
		cameraAccessCounter++;
	}

	/**
	 * Can this can be removed from memory?
	 * @return true if no camera is rendering this chunk
	 */
	boolean shouldBeRemoved() {
		return cameraAccessCounter <= 0;
	}
	
	/**
	 * disposes the chunk
	 * @param saveFileName if null, does not save the file
	 */
	public void dispose(String saveFileName){
		if (saveFileName != null)
			try {
				save(saveFileName);
			} catch (IOException ex) {
				Logger.getLogger(Chunk.class.getName()).log(Level.SEVERE, null, ex);
			}
		ArrayList<AbstractEntity> entities = Controller.getMap().getEntitysOnChunk(coordX, coordY);
		for (int i = 0; i < entities.size(); i++) {
			entities.get(i).dispose();
        }
	}
	
}