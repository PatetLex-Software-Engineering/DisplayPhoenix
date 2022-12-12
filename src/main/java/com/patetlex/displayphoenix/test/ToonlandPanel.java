package com.patetlex.displayphoenix.test;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.obj.GameObject;
import com.patetlex.displayphoenix.gamely.ui.impl.GameGLFWPanel;
import com.patetlex.displayphoenix.test.obj.BlockObject;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ToonlandPanel extends GameGLFWPanel {

    public static Float MOUSE_SENSITIVITY = 0.5F;
    public static Float CAMERA_STEP = 0.5F;

    public ToonlandPanel(Dimension resolution, GameEngine engine) {
        super(resolution, engine);
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        List<GameObject> objectsToCheck = new ArrayList<>();
        for (GameObject gameObject : this.getEngine().getGameObjects().values()) {
            if (gameObject instanceof BlockObject) {
                objectsToCheck.add(gameObject);
            }
        }
        GameObject object = this.getEngine().getPhysics().rayTraceObject(objectsToCheck, e.getPoint(), this.getEngine());
        if (object != null)
            System.out.println(this.getEngine().getTicksExisted() + " - " + object.getId());
    }

}
