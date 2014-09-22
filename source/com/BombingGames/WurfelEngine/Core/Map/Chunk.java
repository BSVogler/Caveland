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

import com.BombingGames.WurfelEngine.Core.Gameobjects.AbstractGameObject;
import com.BombingGames.WurfelEngine.WE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

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

    protected static void setDimensions(MapMetaData meta) {
        blocksX = meta.getChunkBlocksX();
        blocksY = meta.getChunkBlocksY();
        blocksZ = meta.getChunkBlocksZ();
    }
    
    private Cell data[][][];
  
    /**
     * Creates a Chunk filled with empty cells (likely air).
     */
    public Chunk() {
        data = new Cell[blocksX][blocksY][blocksZ];
        
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++)
                    data[x][y][z] = new Cell();
    }
    
    /**
    *Creates a chunk by trying to load and if this fails it generates a new one.
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
     * @param mapname filename
     * @param generator
    */
    public Chunk(final String mapname, final int coordX, final int coordY, final Generator generator){
        this();
        Gdx.app.debug("Chunk", "Creating chunk"+coordX+","+coordY);
        if (!load(mapname, coordX, coordY))
            fill(coordX, coordY, generator);
    }
    
    /**
    *Creates a chunk by generating a new one.
    * @param coordX the chunk coordinate
    * @param coordY the chunk coordinate
    * @param generator
    */
    public Chunk(final int coordX, final int coordY, final Generator generator){
        this();
        fill(coordX, coordY, generator);
    }
    
    /**
     * 
     * @param coordX
     * @param coordY
     * @param generator 
     */
    private void fill(final int coordX, final int coordY, final Generator generator){
        WE.getConsole().add("Creating new chunk: "+coordX+", "+ coordY);
        for (int x=0; x < blocksX; x++)
            for (int y=0; y < blocksY; y++)
                for (int z=0; z < blocksZ; z++)
                    data[x][y][z] = new Cell(
                        generator.generate(blocksX*coordX+x, blocksY*coordY+y, z),
                        0,
                        new Coordinate(blocksX*coordX+x, blocksY*coordY+y, z, false)
                    );
    }
    
    /**
     * Tries to load a chunk from disk.
     */
    private boolean load(final String fileName, int coordX, int coordY){
        //Reading map files test
        try {
            //FileHandle path = Gdx.files.internal("/map/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
            FileHandle path = new FileHandle(
                WE.getWorkingDirectory().getAbsolutePath()
                    + "/maps/"+fileName+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX
            );
            
            Gdx.app.debug("Chunk","Loading Chunk: "+ coordX + ", "+ coordY + "\"");
            
            if (path.exists()) {
                //FileReader input = new FileReader("map/chunk"+coordX+","+coordY+".otmc");
                //BufferedReader bufRead = new BufferedReader(input);
                BufferedReader bufRead = path.reader(30000);//normal chunk file is around 17.000byte

                StringBuilder line;
                //jump over first line to prevent problems with length byte
                bufRead.readLine();


                int z = 0;
                int x;
                int y;
                String lastline;

                //finish a layer
                do {
                    line = new StringBuilder(1);
                    line.append(bufRead.readLine());
                    
                    if ((line.charAt(1) == '/') && (line.charAt(2) == '/')){//jump over optional comment line
                        line = new StringBuilder(1);
                        line.append(bufRead.readLine());
                    }

                    //Ebene
                    y = 0;
                    do{
                        x = 0;

                        do{
                            int posdots = line.indexOf(":");

                            int posend = 1;
                            while ((posend < -1+line.length()) && (line.charAt(posend)!= ' '))  {
                                posend++;
                            }

                            data[x][y][z] = new Cell(
                                Integer.parseInt(line.substring(0,posdots)),
                                Integer.parseInt(line.substring(posdots+1, posend)),
                                new Coordinate(blocksX*coordX+x, blocksY*coordY+y, z, false)
                            );
                            x++;
                            line.delete(0,posend+1);
                        } while (x < blocksX);

                        line = new StringBuilder(1);
                        line.append(bufRead.readLine());

                        y++;
                    } while (y < blocksY);
                    lastline = bufRead.readLine();
                    z++;
                } while (lastline != null);
                return true;
            } else {
                Gdx.app.log("Chunk",coordX + ","+ coordY +" could not be found.");
            }
        } catch (IOException ex) {
            Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed: "+ex);
        } catch (StringIndexOutOfBoundsException ex) {
            Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed. Map file corrupt: "+ex);
        } catch (ArrayIndexOutOfBoundsException ex){
            Gdx.app.error("Chunk","Loading of chunk "+coordX+","+coordY + " failed.Chunk or meta file corrupt: "+ex);
        }
        
        return false;
    }
    
    /**
     * 
     * @param fileName
     * @param coordX
     * @param coordY
     * @return 
     * @throws java.io.IOException 
     */
    public boolean save(String fileName, int coordX, int coordY) throws IOException {
        if ("".equals(fileName)) return false;
        Gdx.app.log("Chunk","Saving "+coordX + ","+ coordY +".");
        FileHandle path = new FileHandle(WE.getWorkingDirectory().getAbsolutePath() + "/maps/"+fileName+"/chunk"+coordX+","+coordY+"."+CHUNKFILESUFFIX);
        String lineFeed = System.getProperty("line.separator");
        
        path.file().createNewFile();
        try (Writer writer = path.writer(false, "UTF8")) {
            for (int z = 0; z < blocksZ; z++) {
                writer.write("//"+z+lineFeed);
                for (int y = 0; y < blocksY; y++) {
                    String line = "";
                    for (int x = 0; x < blocksX; x++) {
                        line +=data[x][y][z].getBlock().getId()+":"+data[x][y][z].getBlock().getValue()+" ";  
                    }
                    writer.write(line+lineFeed);
                }
                writer.write(lineFeed);
            }
        } catch (IOException ex){
            throw ex;
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
    public Cell[][][] getData() {
        return data;
    }

    /**
     * 
     * @param data
     */
    public void setData(Cell[][][] data) {
        this.data = data;
    }

    /**
     *
     * @return
     */
    public static int getScreenWidth(){
        return blocksX*AbstractGameObject.SCREEN_WIDTH;
    }
    
    /**
     *
     * @return
     */
    public static int getScreenDepth() {
        return blocksY*AbstractGameObject.SCREEN_DEPTH/4;
    }
    
    /**
     *
     * @return
     */
    public static int getGameWidth(){
        return blocksX*AbstractGameObject.GAME_DIAGLENGTH;
    }
    
    /**
     *
     * @return
     */
    public static int getGameDepth() {
        return blocksY*AbstractGameObject.GAME_DIAGLENGTH2;
    }
    
        /**
     * The height of the map.
     * @return in game size
     */
    public static int getGameHeight(){
        return blocksZ*AbstractGameObject.GAME_EDGELENGTH;
    }
}