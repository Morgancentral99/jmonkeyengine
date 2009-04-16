/*
 * Copyright (c) 2003-2009 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jmex.terrain.util;

import java.util.logging.Logger;

import com.jme.math.FastMath;
import com.jme.system.JmeException;

/**
 * <code>MidPointHeightMap</code> creates a heightmap based on the
 * Midpoint Displacement fractal generation algorithm based on Jason Shankel's
 * paper from "Game Programming Gems". Terrain is generated by displacing the
 * center of a "square" multiple times. Displacing the center creates for
 * new squares, these new squares are then treated the same way, until the level
 * of detail has been reached.
 *
 * It is important to note that the size of the terrain MUST be a power of
 * two.
 *
 * @author Mark Powell
 * @version $Id$
 */
public class MidPointHeightMap extends AbstractHeightMap {
    private static final Logger logger = Logger
            .getLogger(MidPointHeightMap.class.getName());
    
    private float roughness;

	/**
	 * Constructor builds a new heightmap using the midpoint displacement
	 * algorithm. Roughness determines how chaotic the terrain will be.
	 * Where 1 is perfectly self-similar, > 1 early iterations have a
	 * disproportionately large effect creating smooth terrain, and < 1
	 * late iteraions have a disproportionately large effect creating
	 * chaotic terrain.
	 *
	 * @param size the size of the terrain, must be a power of 2.
	 * @param roughness how chaotic to make the terrain.
	 *
	 * @throws JmeException if size is less than or equal to
	 * 		zero or roughtness is less than 0.
	 */
    public MidPointHeightMap(int size, float roughness) {
        if(!FastMath.isPowerOfTwo(size)) {
            throw new JmeException("Size must be (2^N) sized.");
        }
        if (roughness < 0 || size <= 0) {
            throw new JmeException("size and roughness must be " +
            	"greater than 0");
        }
        this.roughness = roughness;
        this.size = size;

        load();
    }

	/**
     * <code>load</code> generates the heightfield using the Midpoint Displacement
     * algorithm. <code>load</code> uses the latest attributes, so a call
     * to <code>load</code> is recommended if attributes have changed using
     * the set methods.
     */
    public boolean load() {
        float height;
        double heightReducer;
        float[][] tempBuffer;

		//holds the points of the square.
        int ni, nj;
        int mi, mj;
        int pmi, pmj;

        if (null != heightData) {
            unloadHeightMap();
        }

        height = size / 2f;
        heightReducer = Math.pow(2, -1 * roughness);

        heightData = new float[size*size];
        tempBuffer = new float[size][size];

        int counter = size;
        while (counter > 0) {
            //displace the center of the square.
            for (int i = 0; i < size; i += counter) {
                for (int j = 0; j < size; j += counter) {
                	//(0,0) point of the local square
                    ni = (i + counter) % size;
                    nj = (j + counter) % size;
					//middle point of the local square
                    mi = (i + counter / 2);
                    mj = (j + counter / 2);

					//displayce the middle point by the average of the
					//corners, and a random value.
                    tempBuffer[mi][mj] =
                        (float) ((tempBuffer[i][j]
                            + tempBuffer[ni][j]
                            + tempBuffer[i][nj]
                            + tempBuffer[ni][nj])
                            / 4
                            + (Math.random() * height - (height / 2)));
                }
            }

			//next calculate the new midpoints of the line segments.
            for (int i = 0; i < size; i += counter) {
                for (int j = 0; j < size; j += counter) {
                	//(0,0) of the local square
                    ni = (i + counter) % size;
                    nj = (j + counter) % size;

					//middle point of the local square.
                    mi = (i + counter / 2);
                    mj = (j + counter / 2);

					//middle point on the line in the x-axis direction.
                    pmi = (i - counter / 2 + size) % size;
                    //middle point on the line in the y-axis direction.
                    pmj = (j - counter / 2 + size) % size;

                    //Calculate the square value for the top side of the rectangle
                    tempBuffer[mi][j] =
                        (float) ((tempBuffer[i][j]
                            + tempBuffer[ni][j]
                            + tempBuffer[mi][pmj]
                            + tempBuffer[mi][mj])
                            / 4
                            + (Math.random() * height - (height / 2)));

                    //Calculate the square value for the left side of the rectangle
                    tempBuffer[i][mj] =
                        (float) ((tempBuffer[i][j]
                            + tempBuffer[i][nj]
                            + tempBuffer[pmi][mj]
                            + tempBuffer[mi][mj])
                            / 4
                            + (Math.random() * height - (height / 2)));

                }
            }

            counter /= 2;
            height *= heightReducer;
        }

        normalizeTerrain(tempBuffer);

        //transfer the new terrain into the height map.
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                setHeightAtPoint((float)tempBuffer[i][j], i, j);
            }
        }

        logger.info("Created Heightmap using Mid Point");
        return true;
    }

	/**
	 * <code>setRoughness</code> sets the new roughness value of the
	 * heightmap. Roughness determines how chaotic the terrain will be.
	 * Where 1 is perfectly self-similar, > 1 early iterations have a
	 * disproportionately large effect creating smooth terrain, and < 1
	 * late iteraions have a disproportionately large effect creating
	 * chaotic terrain.
	 *
	 * @param roughness how chaotic will the heightmap be.
	 */
    public void setRoughness(float roughness) {
        if (roughness < 0) {
            throw new JmeException("roughness must be greater than 0");
        }
        this.roughness = roughness;
    }


}
