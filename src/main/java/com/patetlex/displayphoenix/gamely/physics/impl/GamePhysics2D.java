package com.patetlex.displayphoenix.gamely.physics.impl;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.Camera;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.obj.interfaces.ICollidable;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import com.patetlex.displayphoenix.gamely.util.Vector3f;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class GamePhysics2D extends GamePhysics {

    protected static float COLLISION_VOID = 0.005F;

    @Override
    public void moveCamera(Camera camera) {
        float mx = camera.getMotion().getX();
        float my = camera.getMotion().getY();

        float tx = camera.getPosition().getX() + mx;
        float ty = camera.getPosition().getY() + my;

        mx *= camera.getDrag();
        my *= camera.getDrag();

        camera.getMotion().setX(mx);
        camera.getMotion().setY(my);

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
                    float d1 = distance(mObj.getPosition().getX() - o1.getPosition().getX(), mObj.getPosition().getY() - o1.getPosition().getY());
                    float d2 = distance(mObj.getPosition().getX() - o2.getPosition().getX(), mObj.getPosition().getY() - o2.getPosition().getY());
                    if (d1 < d2) {
                        return -1;
                    } else if (d1 > d2) {
                        return 1;
                    }
                    return 0;
                }
            });*/
            if (Math.abs(mObj.getMotion().getX()) > 0.01F || Math.abs(mObj.getMotion().getY()) > 0.01F) {
                testMoveObject(mObj, cObjs);
            }
        }
    }

    protected void testMoveObject(GameObject gameObject, List<GameObject> gameObjects) {
        float mx = gameObject.getMotion().getX();
        float my = gameObject.getMotion().getY();

        float tx = gameObject.getPosition().getX() + mx;
        float ty = gameObject.getPosition().getY() + my;
        mx *= gameObject.getDrag();
        my *= gameObject.getDrag();
        gameObject.getMotion().setX(mx);
        gameObject.getMotion().setY(my);
        Rectangle entityBound = new Rectangle(0, 0, (int) gameObject.getBoundingSize().getX(), (int) gameObject.getBoundingSize().getY());

        Line2D l1 = createLine(gameObject.getPosition().getX() + entityBound.x, -gameObject.getPosition().getY() - entityBound.y, tx + entityBound.x, -ty - entityBound.y);
        Line2D l2 = createLine(gameObject.getPosition().getX() + entityBound.x + (int) gameObject.getBoundingSize().getX(), -gameObject.getPosition().getY() - entityBound.y, tx + entityBound.x + (int) gameObject.getBoundingSize().getX(), -ty - entityBound.y);
        Line2D l3 = createLine(gameObject.getPosition().getX() + entityBound.x, -gameObject.getPosition().getY() - entityBound.y + (int) gameObject.getBoundingSize().getY(), tx + entityBound.x, -ty - entityBound.y + (int) gameObject.getBoundingSize().getY());
        Line2D l4 = createLine(gameObject.getPosition().getX() + entityBound.x + (int) gameObject.getBoundingSize().getX(), -gameObject.getPosition().getY() - entityBound.y + (int) gameObject.getBoundingSize().getY(), tx + entityBound.x + (int) gameObject.getBoundingSize().getX(), -ty - entityBound.y + (int) gameObject.getBoundingSize().getY());

        boolean flagX = false;
        boolean flagY = false;
        for (GameObject obj : gameObjects) {
            if (!(obj instanceof Camera)) {
                GameObject boundObj = obj;

                Rectangle bound = new Rectangle(0, 0, (int) boundObj.getBoundingSize().getX(), (int) boundObj.getBoundingSize().getY());

                float absDx = Math.abs(tx - boundObj.getPosition().getX());
                float absDy = Math.abs(ty - boundObj.getPosition().getY());
                if (absDx > Math.max(entityBound.width, bound.width) + COLLISION_VOID) {
                    continue;
                }
                if (absDy > Math.max(entityBound.height, bound.height) + COLLISION_VOID) {
                    continue;
                }

                Line2D bt = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY(), boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY());
                Line2D bb = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY() + bound.height, boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY() + bound.height);
                Line2D bl = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY(), boundObj.getPosition().getX(), -bound.y - boundObj.getPosition().getY() + bound.height);
                Line2D br = createLine(boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY(), boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY() + bound.height);

                if (l3.intersectsLine(bt) || l4.intersectsLine(bt)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagY = true;
                            gameObject.getPosition().setY(boundObj.getPosition().getY() + gameObject.getBoundingSize().getY() + COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l1.intersectsLine(bb) || l2.intersectsLine(bb)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagY = true;
                            gameObject.getPosition().setY(boundObj.getPosition().getY() - bound.height - COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l1.intersectsLine(br) || l3.intersectsLine(br)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagX = true;
                            gameObject.getPosition().setX(boundObj.getPosition().getX() + bound.width + COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }

                if (l2.intersectsLine(bl) || l4.intersectsLine(bl)) {
                    if (boundObj instanceof ICollidable) {
                        if (((ICollidable) boundObj).onCollision(gameObject)) {
                            flagX = true;
                            gameObject.getPosition().setX(boundObj.getPosition().getX() - gameObject.getBoundingSize().getX() - COLLISION_VOID);
                        }
                    }
                    if (gameObject instanceof ICollidable)
                        ((ICollidable) gameObject).onCollision(boundObj);
                }
                if (flagX || flagY)
                    break;
            }
        }
        moveObject(gameObject, flagX ? gameObject.getPosition().getX() : tx, flagY ? gameObject.getPosition().getY() : ty);
    }

    protected void moveObject(GameObject obj, float x, float y) {
        obj.getPosition().setX(x);
        obj.getPosition().setY(y);
    }

    protected Line2D createLine(float x1, float y1, float x2, float y2) {
        return new Line2D.Float(Math.min(x1, x2), Math.min(y1, y2), Math.max(x1, x2), Math.max(y1, y2));
    }

    protected float distance(float a, float b) {
        return (a * a) + (b * b);
    }

    @Override
    public List<GameObject> getCollidingObjects(GameObject gameObject, GameEngine engine) {
        Rectangle2D entityBound = new Rectangle2D.Float(gameObject.getPosition().getX(), -gameObject.getPosition().getY(), gameObject.getBoundingSize().getX(), gameObject.getBoundingSize().getY());
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
            Rectangle2D boundingBox = new Rectangle2D.Float(obj.getPosition().getX(), -obj.getPosition().getY(), obj.getBoundingSize().getX(), obj.getBoundingSize().getY());
            if (boundingBox.contains(pointInGame.getX(), -pointInGame.getY())) {
                objs.add(obj);
            }
        }
        objs.sort(new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
                if (o1.getPosition().getZ() < o2.getPosition().getZ()) {
                    return 1;
                } else if (o1.getPosition().getZ() > o2.getPosition().getZ()) {
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
        Vector3f point = new Vector3f((float) onScreen.getX(), (float) onScreen.getY(),0);
        point.add(-(float) Math.round((engine.getPanel().getWidth() - engine.getPanel().getScaledResolution().getWidth()) / 2F), -(float) Math.round((engine.getPanel().getHeight() - engine.getPanel().getScaledResolution().getHeight()) / 2F), 0);
        point.set((float) (point.getX() / r), (float) (point.getY() / r), 0);
        point.set(point.getX(), -point.getY(), 0);
        int normX = Math.round((float) engine.getPanel().getResolution().getWidth() / 2F);
        int normY = Math.round((float) engine.getPanel().getResolution().getHeight() / 2F);
        point.add(-normX, normY, 0);
        point.add(engine.getCamera().getPosition().getX(), engine.getCamera().getPosition().getY(), 0);
        return point;
    }

    protected boolean doesCollide(Rectangle2D rectangle, GameObject bound) {
        Rectangle2D boundBound = new Rectangle2D.Float(bound.getPosition().getX(), -bound.getPosition().getY(), bound.getBoundingSize().getX(), bound.getBoundingSize().getY());
        return rectangle.intersects(boundBound);
    }
}
