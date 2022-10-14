package com.patetlex.displayphoenix.gamely.obj.interfaces;

import com.patetlex.displayphoenix.gamely.obj.GameObject;

public interface ICollidable {
    /**
     * @param bound  Colliding entity
     * @return True to stop colliding object's motion
     */
    boolean onCollision(GameObject bound);
}
