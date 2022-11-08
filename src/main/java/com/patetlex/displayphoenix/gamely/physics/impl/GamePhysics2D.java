package com.patetlex.displayphoenix.gamely.physics.impl;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.obj.interfaces.ICollidable;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import org.joml.Vector3f;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class GamePhysics2D extends GamePhysics {

    protected static float COLLISION_VOID = 0.005F;

    @Override
    public void moveCamera(Camera camera) {
        float mx = camera.getMotion().x;
        float my = camera.getMotion().y;

        float tx = camera.getPosition().x + mx;
        float ty = camera.getPosition().y + my;

        mx *= camera.getDrag();
        my *= camera.getDrag();

        camera.getMotion().x = (mx);
        camera.getMotion().y = (my);

        moveObject(camera, tx, ty);
    }

    @Override
    public void moveObjects(Collection<GameObject> objs) {
        List<GameObject> list = new ArrayList<>(objs);
        for (int i = 0; i < list.size(); i++) {
            GameObject mObj = list.get(i);
            List<GameObject> cObjs = new ArrayList<>(objs);
            cObjs.remove(i);
/*            cObjs.sort(new Comparator<GameObject>() {
                @Override
                public int compare(GameObject o1, GameObject o2) {
                    float d1 = distance(mObj.getPosition().x - o1.getPosition().x, mObj.getPosition().y - o1.getPosition().y);
                    float d2 = distance(mObj.getPosition().x - o2.getPosition().x, mObj.getPosition().y - o2.getPosition().y);
                    if (d1 < d2) {
                        return -1;
                    } else if (d1 > d2) {
                        return 1;
                    }
                    return 0;
                }
            });*/
            if (Math.abs(mObj.getMotion().x) > 0.01F || Math.abs(mObj.getMotion().y) > 0.01F) {
                testMoveObject(mObj, cObjs);
            }
        }
    }

    protected void testMoveObject(GameObject gameObject, List<GameObject> gameObjects) {
        float mx = gameObject.getMotion().x;
        float my = gameObject.getMotion().y;

        float tx = gameObject.getPosition().x + mx;
        float ty = gameObject.getPosition().y + my;
        mx *= gameObject.getDrag();
        my *= gameObject.getDrag();
        gameObject.getMotion().x = (mx);
        gameObject.getMotion().y = (my);
        Rectangle entityBound = new Rectangle(0, 0, (int) gameObject.getBoundingSize().x, (int) gameObject.getBoundingSize().y);

        Line2D l1 = createLine(gameObject.getPosition().x + entityBound.x, -gameObject.getPosition().y - entityBound.y, tx + entityBound.x, -ty - entityBound.y);
        Line2D l2 = createLine(gameObject.getPosition().x + entityBound.x + (int) gameObject.getBoundingSize().x, -gameObject.getPosition().y - entityBound.y, tx + entityBound.x + (int) gameObject.getBoundingSize().x, -ty - entityBound.y);
        Line2D l3 = createLine(gameObject.getPosition().x + entityBound.x, -gameObject.getPosition().y - entityBound.y + (int) gameObject.getBoundingSize().y, tx + entityBound.x, -ty - entityBound.y + (int) gameObject.getBoundingSize().y);
        Line2D l4 = createLine(gameObject.getPosition().x + entityBound.x + (int) gameObject.getBoundingSize().x, -gameObject.getPosition().y - entityBound.y + (int) gameObject.getBoundingSize().y, tx + entityBound.x + (int) gameObject.getBoundingSize().x, -ty - entityBound.y + (int) gameObject.getBoundingSize().y);

        boolean flagX = false;
        boolean flagY = false;
        for (GameObject obj : gameObjects) {
            if (!(obj instanceof Camera)) {
                GameObject boundObj = obj;

                Rectangle bound = new Rectangle(0, 0, (int) boundObj.getBoundingSize().x, (int) boundObj.getBoundingSize().y);

                float absDx = Math.abs(tx - boundObj.getPosition().x);
                float absDy = Math.abs(ty - boundObj.getPosition().y);
                if (absDx > Math.max(entityBound.width, bound.width) + COLLISION_VOID) {
                    continue;
                }
                if (absDy > Math.max(entityBound.height, bound.height) + COLLISION_VOID) {
                    continue;
                }

                Line2D bt = createLine(boundObj.getPosition().x, -boundObj.getPosition().y, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y);
                Line2D bb = createLine(boundObj.getPosition().x, -boundObj.getPosition().y + bound.height, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y + bound.height);
                Line2D bl = createLine(boundObj.getPosition().x, -boundObj.getPosition().y, boundObj.getPosition().x, -bound.y - boundObj.getPosition().y + bound.height);
                Line2D br = createLine(boundObj.getPosition().x + bound.width, -boundObj.getPosition().y, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y + bound.height);

                if (l3.intersectsLine(bt) || l4.intersectsLine(bt)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagY = true;
                            gameObject.getPosition().y = (boundObj.getPosition().y + gameObject.getBoundingSize().y + COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l1.intersectsLine(bb) || l2.intersectsLine(bb)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagY = true;
                            gameObject.getPosition().y = (boundObj.getPosition().y - bound.height - COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l1.intersectsLine(br) || l3.intersectsLine(br)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagX = true;
                            gameObject.getPosition().x = (boundObj.getPosition().x + bound.width + COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l2.intersectsLine(bl) || l4.intersectsLine(bl)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagX = true;
                            gameObject.getPosition().x = (boundObj.getPosition().x - gameObject.getBoundingSize().x - COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }
                if (flagX || flagY)
                    break;
            }
        }
        moveObject(gameObject, flagX ? gameObject.getPosition().x : tx, flagY ? gameObject.getPosition().y : ty);
    }

    protected void moveObject(GameObject obj, float x, float y) {
        obj.getPosition().x = x;
        obj.getPosition().y = y;
    }

    protected Line2D createLine(float x1, float y1, float x2, float y2) {
        return new Line2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
    }

    protected float distance(float a, float b) {
        return (a * a) + (b * b);
    }

    @Override
    public List<GameObject> getCollidingObjects(GameObject gameObject, GameEngine engine) {
        Rectangle2D entityBound = new Rectangle2D.Float(gameObject.getPosition().x, -gameObject.getPosition().y, gameObject.getBoundingSize().x, gameObject.getBoundingSize().y);
        List<GameObject> bounds = new ArrayList<>();
        for (GameObject bound : engine.getGameObjects().values()) {
            if (bound.getId() != gameObject.getId()) {
                if (doesCollide(entityBound, bound)) {
                    bounds.add(bound);
                }
            }
        }
        return bounds;
    }

    @Override
    public GameObject rayTraceObject(Point onScreen, GameEngine engine) {
        Vector3f pointInGame = rayTracePoint(onScreen, engine);
        List<GameObject> objs = new ArrayList<>();
        for (GameObject obj : engine.getGameObjects().values()) {
            Rectangle2D boundingBox = new Rectangle2D.Float(obj.getPosition().x, -obj.getPosition().y, obj.getBoundingSize().x, obj.getBoundingSize().y);
            if (boundingBox.contains(pointInGame.x, -pointInGame.y)) {
                objs.add(obj);
            }
        }
        objs.sort(new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1.getPosition().z < o2.getPosition().z) {
                    return 1;
                } else if (o1.getPosition().z > o2.getPosition().z) {
                    return -1;
                }
                return 0;
            }
        });

        if (objs.size() == 0)
            return null;
        return objs.get(0);
    }

    @Override
    public Vector3f rayTracePoint(Point onScreen, GameEngine engine) {
        double r = engine.getPanel().getScalingFactor();
        Vector3f point = new Vector3f((float) onScreen.x, (float) onScreen.y,0);
        point.add(-(float) Math.round((engine.getPanel().getWidth() - engine.getPanel().getScaledResolution().getWidth()) / 2F), -(float) Math.round((engine.getPanel().getHeight() - engine.getPanel().getScaledResolution().getHeight()) / 2F), 0);
        point.set((float) (point.x / r), (float) (point.y / r), 0);
        point.set(point.x, -point.y, 0);
        int normX = Math.round((float) engine.getPanel().getResolution().getWidth() / 2F);
        int normY = Math.round((float) engine.getPanel().getResolution().getHeight() / 2F);
        point.add(-normX, normY, 0);
        point.add(engine.getCamera().getPosition().x, engine.getCamera().getPosition().y, 0);
        return point;
    }

    protected boolean doesCollide(Rectangle2D rectangle, GameObject bound) {
        Rectangle2D boundBound = new Rectangle2D.Float(bound.getPosition().x, -bound.getPosition().y, bound.getBoundingSize().x, bound.getBoundingSize().y);
        return rectangle.intersects(boundBound);
    }
}
