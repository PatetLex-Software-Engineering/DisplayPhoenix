package com.patetlex.displayphoenix.gamely.engine.impl;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameEngine2D extends GameEngine {

    public GameEngine2D(GamePhysics physics, int tickRate) {
        super(physics, tickRate);
    }

    @Override
    public void render(Graphics2D g) {
        AffineTransform t = g.getTransform();
        List<GameObject> raw = new ArrayList<>();
        for (GameObject obj : this.objs.values()) {
<<<<<<< HEAD
            Rectangle2D cameraView = new Rectangle2D.Float(this.getCamera().getPosition().x - ((float) this.getPanel().getResolution().getWidth() / 2F), -(this.getCamera().getPosition().y + ((float) this.getPanel().getResolution().getHeight() / 2F)), (float) this.getPanel().getResolution().getWidth(), (float) this.getPanel().getResolution().getHeight());
            if (obj != null) {
                Rectangle2D objR = new Rectangle2D.Float(obj.getPosition().x, -obj.getPosition().y, obj.getBoundingSize().x, obj.getBoundingSize().y);
=======
            Rectangle2D cameraView = new Rectangle2D.Float(this.getCamera().getPosition().getX() - ((float) this.getPanel().getResolution().getWidth() / 2F), -(this.getCamera().getPosition().getY() + ((float) this.getPanel().getResolution().getHeight() / 2F)), (float) this.getPanel().getResolution().getWidth(), (float) this.getPanel().getResolution().getHeight());
            if (obj != null) {
                Rectangle2D objR = new Rectangle2D.Float(obj.getPosition().getX(), -obj.getPosition().getY(), obj.getBoundingSize().getX(), obj.getBoundingSize().getY());
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

                if (!cameraView.intersects(objR))
                    continue;

                raw.add(obj);
            }
        }
        raw.sort(new Comparator<GameObject>() {
            @Override
            public int compare(GameObject o1, GameObject o2) {
<<<<<<< HEAD
                if (o1.getPosition().z < o2.getPosition().z) {
                    return -1;
                } else if (o1.getPosition().z > o2.getPosition().z) {
=======
                if (o1.getPosition().getZ() < o2.getPosition().getZ()) {
                    return -1;
                } else if (o1.getPosition().getZ() > o2.getPosition().getZ()) {
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
                    return 1;
                }
                return 0;
            }
        });
        renderObjects(g, raw);
        g.setTransform(t);
    }

    protected void renderObjects(Graphics2D g, List<GameObject> objects) {
        AffineTransform t = g.getTransform();
        int normX = Math.round((float) this.getPanel().getResolution().getWidth() / 2F);
        int normY = Math.round((float) this.getPanel().getResolution().getHeight() / 2F);
        g.translate(normX, normY);
<<<<<<< HEAD
        g.translate(-this.getCamera().getPosition().x, this.getCamera().getPosition().y);
=======
        g.translate(-this.getCamera().getPosition().getX(), this.getCamera().getPosition().getY());
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
        for (GameObject gameObject : objects) {
            renderObject(g, gameObject);
        }
        g.setTransform(t);
    }

    protected void renderObject(Graphics2D g, GameObject gameObject) {
        //renderRayTraceLines(g, gameObject);
        //renderDebugBox(g, gameObject);
        //renderPoint(g, gameObject);
        AffineTransform t = g.getTransform();
<<<<<<< HEAD
        g.translate(gameObject.getPosition().x, -gameObject.getPosition().y);
=======
        g.translate(gameObject.getPosition().getX(), -gameObject.getPosition().getY());
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
        gameObject.render(g);
        g.setTransform(t);
    }

    protected void renderDebugBox(Graphics2D g, GameObject gameObject) {
        GameObject boundObj = gameObject;

<<<<<<< HEAD
        Rectangle bound = new Rectangle(0, 0, (int) boundObj.getBoundingSize().x, (int) boundObj.getBoundingSize().y);
        Line2D bt = createLine(boundObj.getPosition().x, -boundObj.getPosition().y, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y);
        Line2D bb = createLine(boundObj.getPosition().x, -boundObj.getPosition().y + bound.height, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y + bound.height);
        Line2D bl = createLine(boundObj.getPosition().x, -boundObj.getPosition().y, boundObj.getPosition().x, -bound.y - boundObj.getPosition().y + bound.height);
        Line2D br = createLine(boundObj.getPosition().x + bound.width, -boundObj.getPosition().y, boundObj.getPosition().x + bound.width, -boundObj.getPosition().y + bound.height);
=======
        Rectangle bound = new Rectangle(0, 0, (int) boundObj.getBoundingSize().getX(), (int) boundObj.getBoundingSize().getY());
        Line2D bt = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY(), boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY());
        Line2D bb = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY() + bound.height, boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY() + bound.height);
        Line2D bl = createLine(boundObj.getPosition().getX(), -boundObj.getPosition().getY(), boundObj.getPosition().getX(), -bound.y - boundObj.getPosition().getY() + bound.height);
        Line2D br = createLine(boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY(), boundObj.getPosition().getX() + bound.width, -boundObj.getPosition().getY() + bound.height);
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
        g.setColor(Color.MAGENTA);
        g.setStroke(new BasicStroke(0.5F));
        g.draw(bt);
        g.draw(bb);
        g.draw(bl);
        g.draw(br);
    }

    protected void renderPoint(Graphics2D g, GameObject gameObject) {
<<<<<<< HEAD
        Point2D point = new Point2D.Float(Math.round(gameObject.getPosition().x), -Math.round(gameObject.getPosition().y));
=======
        Point2D point = new Point2D.Float(Math.round(gameObject.getPosition().getX()), -Math.round(gameObject.getPosition().getY()));
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

        g.setColor(Color.MAGENTA);

        g.scale(0.3, 0.3);
        g.fillRect((int) point.getX(), (int) point.getY(), 1, 1);
        g.scale(1 / 0.3, 1 / 0.3);
    }

    protected void renderRayTraceLines(Graphics2D g, GameObject gameObject) {
<<<<<<< HEAD
        Rectangle entityBound = new Rectangle(0, 0, (int) gameObject.getBoundingSize().x, (int) gameObject.getBoundingSize().y);

        int tx = (int) (gameObject.getPosition().x + 1);
        int ty = (int) (gameObject.getPosition().y + 1);
        Line2D l1 = createLine(gameObject.getPosition().x + entityBound.x, -gameObject.getPosition().y - entityBound.y, tx + entityBound.x, -ty - entityBound.y);
        Line2D l2 = createLine(gameObject.getPosition().x + entityBound.x + (int) gameObject.getBoundingSize().x, -gameObject.getPosition().y - entityBound.y, tx + entityBound.x + (int) gameObject.getBoundingSize().x, -ty - entityBound.y);
        Line2D l3 = createLine(gameObject.getPosition().x + entityBound.x, -gameObject.getPosition().y - entityBound.y + (int) gameObject.getBoundingSize().y, tx + entityBound.x, -ty - entityBound.y + (int) gameObject.getBoundingSize().y);
        Line2D l4 = createLine(gameObject.getPosition().x + entityBound.x + (int) gameObject.getBoundingSize().x, -gameObject.getPosition().y - entityBound.y + (int) gameObject.getBoundingSize().y, tx + entityBound.x + (int) gameObject.getBoundingSize().x, -ty - entityBound.y + (int) gameObject.getBoundingSize().y);
=======
        Rectangle entityBound = new Rectangle(0, 0, (int) gameObject.getBoundingSize().getX(), (int) gameObject.getBoundingSize().getY());

        int tx = (int) (gameObject.getPosition().getX() + 1);
        int ty = (int) (gameObject.getPosition().getY() + 1);
        Line2D l1 = createLine(gameObject.getPosition().getX() + entityBound.x, -gameObject.getPosition().getY() - entityBound.y, tx + entityBound.x, -ty - entityBound.y);
        Line2D l2 = createLine(gameObject.getPosition().getX() + entityBound.x + (int) gameObject.getBoundingSize().getX(), -gameObject.getPosition().getY() - entityBound.y, tx + entityBound.x + (int) gameObject.getBoundingSize().getX(), -ty - entityBound.y);
        Line2D l3 = createLine(gameObject.getPosition().getX() + entityBound.x, -gameObject.getPosition().getY() - entityBound.y + (int) gameObject.getBoundingSize().getY(), tx + entityBound.x, -ty - entityBound.y + (int) gameObject.getBoundingSize().getY());
        Line2D l4 = createLine(gameObject.getPosition().getX() + entityBound.x + (int) gameObject.getBoundingSize().getX(), -gameObject.getPosition().getY() - entityBound.y + (int) gameObject.getBoundingSize().getY(), tx + entityBound.x + (int) gameObject.getBoundingSize().getX(), -ty - entityBound.y + (int) gameObject.getBoundingSize().getY());
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

        g.setColor(Color.LIGHT_GRAY);
        g.setStroke(new BasicStroke(0.5F));
        g.draw(l1);
        g.draw(l2);
        g.draw(l3);
        g.draw(l4);
    }

    @Override
    public void tick() {
        this.tickObjects();
    }

    public void tickObjects() {
        for (GameObject object : this.objs.values()) {
            object.tick();
        }
    }

    protected Line2D createLine(float x1, float y1, float x2, float y2) {
        return new Line2D.Float(x1, y1, x2, y2);
    }
}
