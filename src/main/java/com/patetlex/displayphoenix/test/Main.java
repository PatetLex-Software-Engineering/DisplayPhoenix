package com.patetlex.displayphoenix.test;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.enums.WidgetStyle;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.physics.impl.GamePhysics2D;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import com.patetlex.displayphoenix.gamely.ui.ext.GameGLFWPanel;
import com.patetlex.displayphoenix.ui.ColorTheme;
import com.patetlex.displayphoenix.ui.Theme;
import com.patetlex.displayphoenix.util.ImageHelper;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class Main {

    public static void main(String[] args) {
       // title = "App";
        //theme = new Theme(new ColorTheme(Color.GRAY, Color.WHITE, Color.BLACK), WidgetStyle.POPPING, Font.getFont(Font.SERIF));
        //icon = ImageHelper.getImage("popping_warning");


        //create(Application.class, icon, theme);

        //Maps.loadApi("hvKEubROLS0focJTKrMOcwD5AOhZHNOH");
        GLFWErrorCallback.createPrint(System.err).set();
        Application.openWindow(parentFrame -> {
            GameEngine engine = new GameEngine3D(new GamePhysics2D(), 20) {
                @Override
                public void setupGLFW(GameGLFWPanel panel) {
                    super.setupGLFW(panel);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_STENCIL_TEST);
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glCullFace(GL11.GL_BACK);
                }

                @Override
                public void render(Graphics2D g) {
                    super.render(g);
                }
            };
            GamePanel panel = new GameGLFWPanel(new Dimension(100, 100), engine);
            parentFrame.add(panel);
        });
    }
}
