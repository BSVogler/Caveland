/*
 * If this software is used for a game the official „Wurfel Engine“ logo or its name must be visible in an intro screen or main menu.
 *
 * Copyright 2016 Benedikt Vogler.
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
 * * Neither the name of Benedikt Vogler nor the names of its contributors 
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
package com.bombinggames.wurfelengine.core.map.rendering;

import com.bombinggames.wurfelengine.WE;
import com.bombinggames.wurfelengine.core.WorkingDirectory;
import com.bombinggames.wurfelengine.core.map.Chunk;
import com.bombinggames.wurfelengine.core.map.Generators.FullMapGenerator;
import com.bombinggames.wurfelengine.core.map.Iterators.DataIterator;
import com.bombinggames.wurfelengine.core.map.Map;
import java.io.File;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Benedikt Vogler
 */
public class RenderChunkTest {

	private static RenderChunk instance;
	
	public RenderChunkTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
		try {
			WE.launch("test", new String[]{"--windowed"});
			Map map = new Map(new File(WorkingDirectory.getMapsFolder()+"/"+"test"), 0);
			instance = new RenderChunk(new Chunk(map,0, 0));
		} catch (IOException ex) {
			fail("Map could not be loaded.");
		}
		
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of clearPool method, of class RenderChunk.
	 */
	@Test
	public void testClearPool() {
		System.out.println("clearPool");
		RenderChunk.clearPool();
		//can not be tested
	}

	/**
	 * Test of initData method, of class RenderChunk.
	 */
	@Test
	public void testInitData() {
		System.out.println("initData");
		RenderChunk instance = RenderChunkTest.instance;
		instance.initData();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getCell method, of class RenderChunk.
	 */
	@Test
	public void testGetCell() {
		System.out.println("getCell");
		int x = 0;
		int y = 0;
		int z = 0;
		RenderCell expResult = RenderChunk.NULLPOINTEROBJECT;
		RenderCell result = instance.getCell(x, y, z);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getData method, of class RenderChunk.
	 */
	@Test
	public void testGetData() {
		System.out.println("getData");
		RenderCell[][][] result = instance.getData();
		Assert.assertNotNull(result);
		Assert.assertEquals(RenderChunk.NULLPOINTEROBJECT, result[0][0][0]);
	}

	/**
	 * Test of resetClipping method, of class RenderChunk.
	 * @throws java.io.IOException
	 */
	@Test
	public void testResetClipping() throws IOException {
		System.out.println("resetClipping");
		Map map = new Map(new File(WorkingDirectory.getMapsFolder()+"/"+"test"), 0);
		Chunk chunk = new Chunk(map,0, 0);
		chunk.fill(new FullMapGenerator((byte) 1));
		instance = new RenderChunk(chunk);
		instance.getData()[0][0][0].setClippedLeft();
		instance.getData()[0][0][0].setClippedTop();
		instance.getData()[0][0][0].setClippedRight();
		Assert.assertTrue(instance.getData()[0][0][0].isFullyClipped());
		instance.resetClipping();
		Assert.assertFalse(instance.getData()[0][0][0].isFullyClipped());
	}

	/**
	 * Test of resetShadingFor method, of class RenderChunk.
	 */
	@Test
	public void testResetShadingFor() {
		System.out.println("resetShadingFor");
		int idexX = 0;
		int idexY = 0;
		int idexZ = 0;
		instance.resetShadingFor(idexX, idexY, idexZ);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getTopLeftCoordinateX method, of class RenderChunk.
	 */
	@Test
	public void testGetTopLeftCoordinateX() {
		System.out.println("getTopLeftCoordinateX");
		int expResult = 0;
		int result = instance.getTopLeftCoordinateX();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getTopLeftCoordinateY method, of class RenderChunk.
	 */
	@Test
	public void testGetTopLeftCoordinateY() {
		System.out.println("getTopLeftCoordinateY");
		int expResult = 0;
		int result = instance.getTopLeftCoordinateY();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getIterator method, of class RenderChunk.
	 */
	@Test
	public void testGetIterator() {
		System.out.println("getIterator");
		int startingZ = 0;
		int limitZ = 0;
		DataIterator<RenderCell> result = instance.getIterator(startingZ, limitZ);
		if (!(result instanceof DataIterator))
			fail("Iterator is not an iterator");
		if (!result.hasNext())
			fail("Iterator not ready");
	}

	/**
	 * Test of getChunkX method, of class RenderChunk.
	 */
	@Test
	public void testGetChunkX() {
		System.out.println("getChunkX");
		int expResult = 0;
		int result = instance.getChunkX();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getChunkY method, of class RenderChunk.
	 */
	@Test
	public void testGetChunkY() {
		System.out.println("getChunkY");
		int expResult = 0;
		int result = instance.getChunkY();
		assertEquals(expResult, result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getBlockByIndex method, of class RenderChunk.
	 */
	@Test
	public void testGetCellByIndex() {
		System.out.println("getCellByIndex");
		int x = 0;
		int y = 0;
		int z = 0;
		RenderCell expResult = RenderChunk.NULLPOINTEROBJECT;
		RenderCell result = instance.getCellByIndex(x, y, z);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getCameraAccess method, of class RenderChunk.
	 */
	@Test
	public void testCameraAccess() {
		System.out.println("cameraAccess");
		boolean expResult = false;//"Camera access should be by default false."
		boolean result = instance.getCameraAccess();
		assertEquals(expResult, result);
	}

	/**
	 * Test of setCameraAccess method, of class RenderChunk.
	 */
	@Test
	public void testSetCameraAccess() {
		System.out.println("setCameraAccess");
		instance.setCameraAccess(false);
		if (instance.getCameraAccess()){
			fail("Set-get fail.");
		}
		instance.setCameraAccess(true);
		if (!instance.getCameraAccess()){
			fail("Set-get fail.");
		}
	}

	/**
	 * Test of dispose method, of class RenderChunk.
	 */
	@Test
	public void testDispose() {
		System.out.println("dispose");
		instance.dispose();
	}
	
}
