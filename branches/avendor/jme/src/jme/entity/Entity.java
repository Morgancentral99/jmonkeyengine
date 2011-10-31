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

package jme.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import jme.exception.MonkeyGLException;
import jme.exception.MonkeyRuntimeException;
import jme.geometry.Geometry;
import jme.geometry.bounding.BoundingVolume;
import jme.geometry.model.Model;
import jme.math.Vector;
import jme.physics.PhysicsModule;
import jme.entity.camera.Frustum;
import jme.utility.LoggingSystem;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.Window;

/**
 * <code>Entity</code> defines a game entity that consists of a piece of
 * geometry, a position and an orientation. An entity can be a collection of
 * children entities. An entity with multiple children create a tree structure,
 * as children can have children. Rendering the entity causes the parent
 * entity to render it's own geometry and then render each child.
 * 
 * If the parent of an entity is null, the parent is considered to be the 
 * <code>Locale</code>. The entities local coordinate system is relative to the
 * parent. That is, if a position of a entity is (0, 1, 0), it is one unit 
 * along the Y-Axis of the parent. 
 * 
 * An <code>Entity</code> is not required to maintain geometry. If there is
 * no geometry, it will simply not be rendered. This will allow for an
 * <code>Entity</code> to represent something abstract.
 * 
 * @author Mark Powell
 * @version $Id: Entity.java,v 1.1.1.1 2003-10-29 10:57:15 Anakan Exp $
 */
public class Entity implements EntityInterface {
    
    //The id of the entity
    private int id = 0;

    //The list of children this entity is the parent of.
    protected List children = null;

    //The geometry of the entity. Not required to be set, no
    //rendering will be done if it is null.
    protected Geometry geometry = null;
    
    //Volume that contains this entity.
    protected BoundingVolume boundingVolume = null;

    //Orientation and position of the entity.
    private Vector position = null;
    
    //Set the entities orientation
    private float yaw;
    private float roll;
    private float pitch;

    //visibility    
    private boolean hasMoved;
    private boolean isVisible = true;
   
    //physics
    private PhysicsModule physics;
    
    /**
     * Constructor initializes the entity. All attributes of the 
     * <code>Entity</code> are empty.
     * 
     * @param id the id of the entity
     * 
     * @throws MonkeyRuntimeException if the id is less than 1.
     */
    public Entity(int id) {
        if (id < 1) {
            throw new MonkeyRuntimeException("Entity id must be greater than 0");
        }
        children = new ArrayList();
        position = new Vector();

		if(!Window.isCreated()) {
			throw new MonkeyGLException("Window must be created before Entity.");
		}

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created a new entity");
    }

    /**
     * Constructor intializes the entity with a second entity as a child.
     * 
     * @param id the id of the entity
     * @param child the entities child node.
     *
     * @throws MonkeyRuntimeException if the id is less than 1.
     */
    public Entity(int id, Entity child) {
        if (id < 1) {
            throw new MonkeyRuntimeException("Entity id must be greater than 0");
        }
        children = new ArrayList();
        position = new Vector();
        
        children.add(child);

        LoggingSystem.getLoggingSystem().getLogger().log(
            Level.INFO,
            "Created a new entity");
    }

    /**
     * <code>addChild</code> adds an <code>Entity</code> to the entity with
     * this entity the parent. 
     * 
     * @param child the <code>Entity</code> to add to the children list.
     */
    public void addChild(Entity child) {
        children.add(child);
    }

    /**
     * <code>removeChild</code> removes the requested child from the list
     * of children.
     * 
     * @param child the child to remove from the list.
     */
    public void removeChild(Entity child) {
        children.remove(child);
    }
    
    /**
     * <code>setBoundingVolume</code> sets the volume that contains this
     * entity.
     * @param volume the volume that contains this entity.
     */
    public void setBoundingVolume(BoundingVolume volume) {
        this.boundingVolume = volume;
    }
    
    /**
     * <code>getBoundingVolume</code> returns the volume that contains this
     * entity.
     * @return the volume that contains this entity.
     */
    public BoundingVolume getBoundingVolume() {
        return boundingVolume;
    }
    
    /**
     * <code>hasCollision</code> determines if this entity is colliding with
     * a provided entity.
     * @param ent the entity to check.
     * @return true if a collision has occured, false otherwise.
     */
    public boolean hasCollision(Entity ent) {
        if(null != boundingVolume) {
            return boundingVolume.hasCollision(position, 
                    ent.getBoundingVolume(), ent.getPosition());
        } else {
            return false;
        }
    }
    
    /**
     * <code>distance</code> returns the distance between this entity and
     * a given entity. 
     * @param ent the entity to check.
     * @return the distance between this entity and another. -1 is returned
     *      if boundingVolume is not set.
     */
    public float distance(Entity ent) {
        if(null != boundingVolume) {
            return boundingVolume.distance(position, ent.getBoundingVolume(), 
                    ent.getPosition());
        } else {
            return -1.0f;
        }
    }

    /**
     * <code>setPosition</code> sets the position of this entity.
     * 
     * @param position the new position of this entity.
     */
    public void setPosition(Vector position) {
        this.position = position;
    }

    /**
     * <code>setYaw</code> sets the yaw angle of the entity. Where yaw is
     * defined as rotation about the local Y axis.
     * @param angle the angle of yaw.
     */
    public void setYaw(float angle) {
        this.yaw = angle;
    }
    
    /**
     * <code>setRoll</code> sets the roll angle of the entity. Where roll
     * is defined as rotation about the local Z axis.
     * @param angle the angle of roll.
     */
    public void setRoll(float angle) {
        this.roll = angle;
    }
    
    /**
     * <code>setPitch</code> sets the pitch angle of the entity. Where 
     * pitch is defined as rotation about the local x axis.
     * @param angle the angle of pitch.
     */
    public void setPitch(float angle) {
        this.pitch = angle;
    }

    /**
     * <code>setMoved</code> sets the moved flag.
     * @param hasMoved true if the entity has moved, false otherwise.
     */
    public void setMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * <code>setPhysicsModule</code> sets the module the defines how the
     * physics of the entity are handled. This allows the entity to 
     * interact with the world (what ever that may be) in a realistic and
     * appropriate manner.
     * @param physics the physics module for this entity.
     */
    public void setPhysicsModule(PhysicsModule physics) {
        this.physics = physics;
    }

    /**
     * <code>hasMoved</code> returns true if the entity has moved during the
     * last update, false otherwise.
     * @return true if the entity has moved, false if it hasn't.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * <code>setGeometry</code> sets the geometry of this entity. If the
     * geometry object is null, nothing will be rendered for this entity.
     * 
     * @param geometry the geometry to set for this entity.
     */
    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    /**
     * <code>getPosition</code> returns the position of the entity in 
     * three dimensional space.
     * 
     * @return the position of the entity.
     */
    public Vector getPosition() {
        return position;
    }
    
    /**
     * <code>getPhysics</code> returns the physics module of the particular
     * entity.
     * @return the physics module of the entity.
     */
    public PhysicsModule getPhysics() {
        return physics;
    }
    
    /**
     * <code>getId</code> returns the id of the entity.
     * @return the id of the entity.
     */
    public int getId() {
        return id;
    }

    /**
     * <code>update</code> updates the state of the entity. 
     */
    public void update(float time) {
        //if the geometry is of type model it should be updated.
        if(null != geometry) {
            if(geometry instanceof Model) {
                ((Model)geometry).update(time);
            }
        }
        if(null != physics) {
            physics.update(time);
            physics.updatePosition(position);
        }
    }

    /**
     * <code>render</code> translates and rotates the entity based on it's 
     * attributes. It then renders the geometry of the entity if there is any.
     * Each child is then rendered in turn.
     */
    public void render() {
        GL.glPushMatrix();
        GL.glEnable(GL.GL_DEPTH_TEST);
        GL.glTranslatef(position.x, position.y, position.z);
        GL.glRotatef(roll, 0, 0, 1);
        GL.glRotatef(yaw, 0, 1, 0);
        GL.glRotatef(pitch, 1, 0, 1);
        
        //no geometry, so don't render.
        if (null != geometry) {
            geometry.render();
        }

        //render each child.
        for (int i = 0; i < children.size(); i++) {
            ((Entity)children.get(i)).render();
        }
        GL.glDisable(GL.GL_DEPTH_TEST);
        GL.glPopMatrix();
    }

    /**
     * <code>isVisible</code> returns true if the entity is currently visible
     * and false if it is not.
     * 
     * @return true if the entity is visible, false otherwise.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * <code>checkVisibility</code> updates the visibility of this entity
     * based on the frustum levels.
     * @param frustum the camera's view frustum.
     */
    public void checkVisibility(Frustum frustum) {
        if(null != boundingVolume) {
            isVisible = boundingVolume.isVisible(position, frustum);
        } else {
            isVisible = true;
        }
    }

    /**
     * <code>toString</code> creates a string representation of the 
     * <code>Entity</code> object. The format is as follows:<br><br>
     * 
     * -----Entity: <br>
     * Position: {VECTOR POSITION}<br>
     *<br>
     * Child 0: <br>
     * -----Entity: <br>
     * Position: {VECTOR POSITION}<br>
     * <br>
     * Geometry: {GEOMETRY STRING}<br>
     * -----<br>
     *<br>
     * Geometry: {GEOMETRY STRING}<br>
     * -----<br>
     * 
     * @return the string representation of this object.
     */
    public String toString() {
        String string = "-----";
        string += "Entity: \nPosition: " + position.toString() + "\n";

        for (int i = 0; i < children.size(); i++) {
            string += ("\nChild " + i + ": \n");
            string += ((Entity)children.get(i)).toString() + "\n";
        }

        if(null != geometry) {
            string += "\nGeometry: " + geometry.toString() + "\n-----";
        }
        
        return string;
    }
	/**
     * <code>getGeometry</code> returns the geometry of this entity.
	 * @return the geometry of this entity.
	 */
	public Geometry getGeometry() {
		return geometry;
	}

}