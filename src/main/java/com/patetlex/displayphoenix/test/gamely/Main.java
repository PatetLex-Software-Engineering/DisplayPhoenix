package com.patetlex.displayphoenix.test.gamely;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.enums.WidgetStyle;
import com.patetlex.displayphoenix.gamely.physics.impl.GamePhysics3D;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import com.patetlex.displayphoenix.test.gamely.obj.BlockObject;
import com.patetlex.displayphoenix.ui.ColorTheme;
import com.patetlex.displayphoenix.ui.Theme;
import com.patetlex.displayphoenix.util.ImageHelper;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

import static com.patetlex.displayphoenix.test.gamely.ToonlandEngine.CHUNK_RENDER_DISTANCE;

public class Main {

    /**
     * Testing/Creating new Gamely lib
     *
     * @param args
     */
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(Color.GRAY, Color.WHITE, Color.BLACK), WidgetStyle.POPPING, Font.getFont(Font.SERIF));
        ImageIcon icon = ImageHelper.getImage("popping_warning");

        Application.create(Application.class, icon, theme);

        GLFWErrorCallback.createPrint(System.err).set();

        Random random = new Random();

        long terrainSeed = random.nextLong();
        long temperatureSeed = random.nextLong();
        long humiditySeed = random.nextLong();

/*        BufferedImage atlas = ImageHelper.loadImagesToAtlas("gamely/toonland/");

        Application.openWindow(parentFrame -> {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.drawImage(atlas, 0, 0, this);
                }
            };
            panel.setPreferredSize(parentFrame.getPreferredSize());
            parentFrame.add(panel);
        });*/

        Application.openWindow(parentFrame -> {
            ToonlandEngine engine = new ToonlandEngine(new GamePhysics3D(), 20, terrainSeed, temperatureSeed, humiditySeed);

            engine.getCamera().getPosition().set(0, 45, 0);
            engine.getCamera().setLight(new Vector4f(1.0F, 1.0F, 1.0F, 0.2F), new Vector3f(1, 0.003F, 0.005F));

            for (int i = -CHUNK_RENDER_DISTANCE; i < CHUNK_RENDER_DISTANCE; i++) {
                for (int j = -CHUNK_RENDER_DISTANCE; j < CHUNK_RENDER_DISTANCE; j++) {
                    engine.loadChunk(i, j);
                }
            }

            for (int i = 0; i < 3; i++) {
                BlockObject blockObject = new BlockObject(new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), "cubemap");
                blockObject.getPosition().set(i * 15, 25, i * 15);
                engine.addGameObject(blockObject);
            }

            GamePanel panel = new ToonlandPanel(new Dimension(100, 100), engine);
            parentFrame.add(panel);
        });
    }
}
