/*
 * Copyright (c) 2003, jMonkeyEngine - Mojo Monkey Coding
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer. 
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution. 
 * 
 * Neither the name of the Mojo Monkey Coding, jME, jMonkey Engine, nor the 
 * names of its contributors may be used to endorse or promote products derived 
 * from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package jme.geometry.model;

/**
 * A Material is a texture map in MS3D.  Currently, as of version 0.1 of
 * najgl, only bitmap (*.bmp) textures are supported.
 *
 * @author naj
 * @version 0.1
 */
public class Material {

    /**
     * The filename of the bitmap.  Must be relative to the loaded model file.
     */
    public String name;

    /**
     * The filename of the color map.  Must be relative to the loaded model file.
     */
    public String colorMap;

    /**
     * The filename of the alpah map.  Must be relative to the loaded model file.
     */
    public String alphaMap;

    /**
     * A pointer to the memory address of the texture in opengl memory.
     */
    public int glTextureAddress;

    /**
     * An array of (r,g,b,a) color values for natural light.
     */
    public float[] ambient;

    /**
     * An array of (r,g,b,a) color values for indirect light.
     */
    public float[] diffuse;

    /**
     * An array of (r,g,b,a) color values for direct light.
     */
    public float[] specular;

    /**
     * An array of (r,g,b,a) color values for projected light.
     */
    public float[] emissive;

    /**
     * The ammount of light to be reflected off of the texture.
     */
    public float shininess;

    /**
     * The ammount of light to pass through the texture.
     */
    public float transparency;

}