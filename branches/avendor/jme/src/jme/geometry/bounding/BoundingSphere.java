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

package jme.geometry.bounding;

import jme.entity.camera.Frustum;
import jme.math.Distance;
import jme.math.Vector;

/**
 * <code>BoundingSphere</code> defines a sphere that defines a container 
 * for a group of vertices of a particular piece of geometry. This sphere 
 * defines a radius and a center. This origin is translated from the containing 
 * entity's position.
 * <br><br>
 * A typical usage is to allow the class define the center and radius
 * by calling either <code>containAABB</code> or <code>averagePoints</code>.
 * 
 * 
 * @author Mark Powell
 * @version $Id: BoundingSphere.java,v 1.1.1.1 2003-10-29 10:57:31 Anakan Exp $
 */
public class BoundingSphere implements BoundingVolume {
	private float radius;
	private Vector center;
    private float collisionBuffer;
    
	/**
	 * Default contstructor instantiates a new <code>BoundingSphere</code>
	 * object. 
	 */
	public BoundingSphere() {
		center = new Vector();
	}

	/**
	 * Constructor instantiates a new <code>BoundingSphere</code> object.
	 * @param radius the radius of the sphere.
	 * @param center the center of the sphere.
	 */
	public BoundingSphere(float radius, Vector center) {
		if (null == center) {
			this.center = new Vector();
		} else {
			this.center = center;
		}
		this.radius = radius;
	}

	/**
	 * <code>getRadius</code> returns the radius of the bounding sphere.
	 * @return the radius of the bounding sphere.
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * <code>getCenter</code> returns the center of the bounding sphere.
	 * @return the center of the bounding sphere.
	 */
	public Vector getCenter() {
		return center;
	}

	/**
	 * <code>setRadius</code> sets the radius of this bounding sphere.
	 * @param radius the new radius of the bounding sphere.
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * <code>setCenter</code> sets the center of the bounding sphere.
	 * @param center the new center of the bounding sphere.
	 */
	public void setCenter(Vector center) {
		this.center = center;
	}

	/**
	 * <code>containAABB</code> creates a minimum-volume axis-aligned
	 * bounding box of the points, then selects the smallest 
	 * enclosing sphere of the box with the sphere centered at the
	 * boxes center.
	 * @param points the list of points.
	 */
	public void containAABB(Vector[] points) {
		Vector min = new Vector(points[0].x,points[0].y,points[0].z);
        Vector max = new Vector(min.x, min.y, min.z);
		for (int i = 1; i < points.length; i++) {
			if (points[i].x < min.x)
				min.x = points[i].x;
			else if (points[i].x > max.x)
				max.x = points[i].x;

			if (points[i].y < min.y)
				min.y = points[i].y;
			else if (points[i].y > max.y)
				max.y = points[i].y;

			if (points[i].z < min.z)
				min.z = points[i].z;
			else if (points[i].z > max.z)
				max.z = points[i].z;
		}

		center = max.add(min);
		center = center.mult(0.5f);
		Vector halfDiagonal = max.subtract(min);
		halfDiagonal = halfDiagonal.mult(0.5f);
		radius = halfDiagonal.length();
	}

	/**
	 * <code>averagePoints</code> selects the sphere center to be
	 * the average of the points and the sphere radius to be the 
	 * smallest value to enclose all points.
	 * @param points the list of points to contain.
	 */
	public void averagePoints(Vector[] points) {
		center = points[0];
		
		for (int i = 1; i < points.length; i++)
			center = center.add(points[i]);
		float quantity = 1.0f / points.length;
		center = center.mult(quantity);

		float maxRadiusSqr = 0;
		for (int i = 0; i < points.length; i++) {
			Vector diff = points[i].subtract(center);
			float radiusSqr = diff.lengthSquared();
			if (radiusSqr > maxRadiusSqr)
				maxRadiusSqr = radiusSqr;
		}

		radius = (float)Math.sqrt(maxRadiusSqr);

	}
    
    /**
     * <code>setCollisionBuffer</code> sets the value that must be reached to
     * consider bounding volumes colliding. By default this value is 0.
     * @param buffer the collision buffer.
     */
    public void setCollisionBuffer(float buffer) {
        collisionBuffer = buffer;
    }
    
    /**
     * <code>hasCollision</code> returns true if an collision is occuring with
     * the given bounding volume and this volume. Offsets are given to allow
     * for positional representations of the volumes.
     * @param sourceOffset defines the position of the entity containing
     *      this volume, if null it is ignored.
     * @param volume the bounding volume to compare.
     * @param targetOffset defines the position of the entity containing
     *      the target volume, if null it is ignored.
     * @return true if a collision has occured, false otherwise.
     */
    public boolean hasCollision(Vector sourceOffset, BoundingVolume volume, 
            Vector targetOffset) {
                
        float distance = distance(sourceOffset, volume, targetOffset);
        System.out.println(distance);
        if(distance <= collisionBuffer && distance != -1.0f) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <code>distance</code> returns the distance between this volume and 
     * a given volume. Offsets are used to represent positions of the
     * entities that may be contained by the volume.
     * @param sourceOffset defines the position of the entity containing
     *      this volume, if null it is ignored.
     * @param volume the bounding volume to compare.
     * @param targetOffset defines the position of the entity containing
     *      the target volume, if null it is ignored.
     * @return the distance between the two bounding volumes. -1 if there is
     *      a problem.
     */
    public float distance(Vector sourceOffset, BoundingVolume volume, 
            Vector targetOffset) {
        float rad = ((BoundingSphere)volume).radius + radius;
        System.out.println(((BoundingSphere)volume).radius + " + " + radius + " = " + rad);
        float dis =  Distance.distancePointPoint(sourceOffset, targetOffset);
        if(dis < 0) {
            return 0;
        } else {
            return dis;
        }
    }
    
    /**
     * <code>isVisible</code> calculates whether or not this bounding volume
     * is within a view frustum. The location of the sphere can be modified 
     * with an offset position to allow for movement of any entity that the
     * volume is attached to.
     * @param offsetPosition the offset for the center of the volume.
     * @param frustum the view frustum to check against.
     * @return true if the volume is in the volume, false otherwise.
     */
    public boolean isVisible(Vector offsetPosition, Frustum frustum) {
        if(null != frustum) {
            if(offsetPosition != null) {
                Vector finalCenter = offsetPosition.add(center);
                return frustum.containsSphere(finalCenter.x, finalCenter.y, 
                        finalCenter.z, radius);
            } else {
                return frustum.containsSphere(center.x, center.y, center.z, 
                        radius);
            }
        } else {
            return true;
        }
    }
}