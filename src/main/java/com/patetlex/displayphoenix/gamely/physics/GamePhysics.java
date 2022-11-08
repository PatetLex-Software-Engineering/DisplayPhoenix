package com.patetlex.displayphoenix.gamely.physics;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
<<<<<<< HEAD
import org.joml.Vector3f;
=======
import com.patetlex.displayphoenix.gamely.util.Vector3f;
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

import java.awt.*;
import java.util.Collection;
import java.util.List;

public abstract class GamePhysics {
    public abstract void moveCamera(Camera camera);
    public abstract void moveObjects(Collection<GameObject> objs);
    public abstract List<GameObject> getCollidingObjects(GameObject obj, GameEngine engine);
    public abstract GameObject rayTraceObject(Point onScreen, GameEngine engine);
    public abstract Vector3f rayTracePoint(Point onScreen, GameEngine engine);
}
