package com.patetlex.displayphoenix.gamely.physics.impl;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Collection;
import java.util.List;

public class GamePhysics3D extends GamePhysics {
    @Override
    public void moveCamera(Camera camera) {

    }

    @Override
    public void moveObjects(Collection<GameObject> objs) {

    }

    @Override
    public List<GameObject> getCollidingObjects(GameObject obj, GameEngine engine) {
        return null;
    }

    @Override
    public GameObject rayTraceObject(Point onScreen, GameEngine engine) {
        return null;
    }

    @Override
    public Vector3f rayTracePoint(Point onScreen, GameEngine engine) {
        return null;
    }
}
