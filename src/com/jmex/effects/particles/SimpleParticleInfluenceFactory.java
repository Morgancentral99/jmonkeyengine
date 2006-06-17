/*
 * Copyright (c) 2003-2005 jMonkeyEngine
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

package com.jmex.effects.particles;

import java.io.IOException;

import com.jme.math.FastMath;
import com.jme.math.Line;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * <code>SimpleParticleForceFactory</code>
 * @author Joshua Slack
 * @version $Id: SimpleParticleInfluenceFactory.java,v 1.2 2006-06-17 15:04:21 renanse Exp $
 */
public final class SimpleParticleInfluenceFactory {

    public static class BasicWind extends ParticleInfluence {
        private float strength;
        private Vector3f windDirection;
        private boolean random;
        
        public BasicWind() {
        }
        
        public BasicWind(float windStr, Vector3f windDir, boolean addRandom) {
            strength = windStr;
            windDirection = windDir;
            random = addRandom;
        }
        
        public float getStrength() {
            return strength;
        }
        
        public void setStrength(float windStr) {
            strength = windStr;
        }
        
        public Vector3f getWindDirection() {
            return windDirection;
        }
        
        public void setWindDirection(Vector3f windDir) {
            windDirection = windDir;
        }

        public boolean isRandom() {
            return random;
        }
        
        public void setRandom(boolean addRandom) {
            random = addRandom;
        }
        
        public void apply(float dt, Particle p) {
            float tStr = (random ? FastMath.nextRandomFloat() * strength : strength);
            p.getVelocity().addLocal(windDirection.x * tStr * dt,
                                     windDirection.y * tStr * dt,
                                     windDirection.z * tStr * dt);
        }
        
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(strength, "strength", 1f);
            capsule.write(windDirection, "windDirection", Vector3f.UNIT_X);
            capsule.write(random, "random", false);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            strength = capsule.readFloat("strength", 1f);
            windDirection = (Vector3f)capsule.readSavable("windDirection",
                new Vector3f(Vector3f.UNIT_X));
            random = capsule.readBoolean("random", false);
        }

        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicGravity extends ParticleInfluence {
        private Vector3f gravity;
        
        public BasicGravity() {
        }
        
        public BasicGravity(Vector3f gravForce) {
            gravity = new Vector3f(gravForce);
        }
        
        public Vector3f getGravityForce() {
            return gravity;
        }
        
        public void setGravityForce(Vector3f gravForce) {
            gravity = gravForce;
        }
        
        public void apply(float dt, Particle p) {
            p.getVelocity().addLocal(gravity.x * dt, gravity.y * dt,
                                     gravity.z * dt);
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(gravity, "gravity", Vector3f.ZERO);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            gravity = (Vector3f)capsule.readSavable("gravity",
                new Vector3f(Vector3f.ZERO));
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicDrag extends ParticleInfluence {
        private Vector3f velocity = new Vector3f();
        private float dragCoefficient;
        
        public BasicDrag() {
        }
        
        public BasicDrag(float dragCoef) {
            dragCoefficient = dragCoef;
        }
        
        public float getDragCoefficient() {
            return dragCoefficient;
        }
        
        public void setDragCoefficient(float dragCoef) {
            dragCoefficient = dragCoef;
        }
        
        public void apply(float dt, Particle p) {
            // viscous drag
            velocity.set(p.getVelocity());
            p.getVelocity().addLocal(velocity.multLocal(-dragCoefficient * dt * p.getInvMass()));
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(dragCoefficient, "dragCoefficient", 1f);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            dragCoefficient = capsule.readFloat("dragCoefficient", 1f);
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    public static class BasicVortex extends ParticleInfluence {
        private float strength, divergence;
        private Line axis;
        private boolean random;
        private Vector3f v1 = new Vector3f(), v2 = new Vector3f();
        private Quaternion rot = new Quaternion();
        
        public BasicVortex() {
        }
        
        public BasicVortex(float strength, float divergence, Line axis,
            boolean random) {
            this.strength = strength;
            this.axis = axis;
            this.random = random;
            setDivergence(divergence);
        }
        
        public float getStrength() {
            return strength;
        }
        
        public void setStrength(float strength) {
            this.strength = strength;
        }
        
        public float getDivergence() {
            return divergence;
        }
        
        public void setDivergence(float divergence) {
            this.divergence = divergence;
            rot.fromAngleAxis(-divergence, axis.getDirection());
        }
        
        public Line getAxis() {
            return axis;
        }
        
        public void setAxis(Line axis) {
            this.axis = axis;
        }
        
        public boolean isRandom() {
            return random;
        }
        
        public void setRandom(boolean random) {
            this.random = random;
        }
        
        public void apply(float dt, Particle p) {
            p.getPosition().subtract(axis.getOrigin(), v1);
            axis.getDirection().cross(v1, v2);
            if (v2.length() == 0) {
                return; // particle is on the axis
            }
            v2.normalizeLocal();
            rot.multLocal(v2);
            float tStr = (random ? FastMath.nextRandomFloat() * strength : strength);
            p.getVelocity().addLocal(v2.x * tStr * dt,
                                     v2.y * tStr * dt,
                                     v2.z * tStr * dt);
        }
    
        public void write(JMEExporter e) throws IOException {
            super.write(e);
            OutputCapsule capsule = e.getCapsule(this);
            capsule.write(strength, "strength", 1f);
            capsule.write(divergence, "divergence", 0f);
            capsule.write(axis, "axis", new Line(new Vector3f(),
                new Vector3f(Vector3f.UNIT_Y)));
            capsule.write(random, "random", false);
        }

        public void read(JMEImporter e) throws IOException {
            super.read(e);
            InputCapsule capsule = e.getCapsule(this);
            strength = capsule.readFloat("strength", 1f);
            axis = (Line)capsule.readSavable("axis", new Line(new Vector3f(),
                new Vector3f(Vector3f.UNIT_Y)));
            random = capsule.readBoolean("random", false);
            setDivergence(capsule.readFloat("divergence", 0f));
        }
        
        public Class getClassTag() {
            return this.getClass();
        }
    }
    
    /**
     * Not used.
     */
    private SimpleParticleInfluenceFactory() {
    }

    /**
     * Creates a basic wind that always blows in a single direction.
     *
     * @param windStr Max strength of wind.
     * @param windDir Direction wind should blow.
     * @param addRandom randomly alter the strength of the wind by 0-100%
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicWind(float windStr, Vector3f windDir, boolean addRandom) {
        return new BasicWind(windStr, windDir, addRandom);
    }

    /**
     * Create a basic gravitational force.
     *
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicGravity(Vector3f gravForce) {
        return new BasicGravity(gravForce);
    }

    /**
     * Create a basic drag force that will use the given drag coefficient.
     * Drag is determined by figuring the current velocity and reversing it, then
     * multiplying by the drag coefficient and dividing by the particle mass.
     *
     * @param dragCoef Should be positive.  Larger values mean more drag but possibly more instability.
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicDrag(float dragCoef) {
        return new BasicDrag(dragCoef);
    }
    
    /**
     * Creates a basic vortex.
     *
     * @param strength Max strength of vortex.
     * @param divergence The divergence in radians from the tangent vector
     * @param axis The center of the vortex.
     * @param random randomly alter the strength of the vortex by 0-100%
     * @return ParticleInfluence
     */
    public static ParticleInfluence createBasicVortex(float strength,
        float divergence, Line axis, boolean random) {
        return new BasicVortex(strength, divergence, axis, random);
    }
}
