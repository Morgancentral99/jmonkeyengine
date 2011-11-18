package com.g3d.collision;

import com.g3d.math.Vector3f;

public interface MotionAllowedListener {

    /**
     * Check if motion allowed. Modify position and velocity vectors
     * appropriately if not allowed..
     * 
     * @param position
     * @param velocity
     */
    public void checkMotionAllowed(Vector3f position, Vector3f velocity);

}