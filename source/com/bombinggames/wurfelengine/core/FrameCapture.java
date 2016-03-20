/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bombinggames.wurfelengine.core;

/**
 *
 * @author Micahel McMahon
 */
public class FrameCapture  {
    
    
    private int[][] pixelData;


    private int width;
    private int heightl;
    
    /**
     * Creates a new instance of a frame capture;
     * @param width the width of the resulting capture
     * @param height the height of the resulting capture 
     */
    public FrameCapture(int width, int height)
    {
        this.heightl = height;
        this.width = width;
    }
    
    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeightl() {
        return heightl;
    }

    public void setHeightl(int heightl) {
        this.heightl = heightl;
    }
    
    public void capture()
    {
        
    }
    
    private void clear()
    {
        
    }
    
    public void saveCaptureToDisk(String fileName, String path)
    {
        
    }
    
    
}
