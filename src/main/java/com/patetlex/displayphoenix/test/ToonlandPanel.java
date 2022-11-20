package com.patetlex.displayphoenix.test;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.ui.impl.GameGLFWPanel;

import java.awt.*;

public class ToonlandPanel extends GameGLFWPanel {

    public static Float MOUSE_SENSITIVITY = 0.5F;
    public static Float CAMERA_STEP = 0.5F;

    public ToonlandPanel(Dimension resolution, GameEngine engine) {
        super(resolution, engine);
    }

}
