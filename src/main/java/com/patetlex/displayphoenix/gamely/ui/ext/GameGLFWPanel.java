package com.patetlex.displayphoenix.gamely.ui.ext;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.engine.impl.GameEngine3D;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWNativeWin32;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLX13;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.jawt.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.nio.IntBuffer;

public class GameGLFWPanel extends GamePanel {

    private GLFWCanvas canvas;

    public GameGLFWPanel(Dimension resolution, GameEngine engine) {
        super(resolution, engine);
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }
        this.canvas = new GLFWCanvas(this, this, this, this);
        this.setLayout(new BorderLayout());
        this.add(this.canvas, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.canvas.paint(g);
        super.paintComponent(g);
    }

    @Override
    protected void renderEngine(Graphics g) {
        if (!this.getEngine().isRunning()) {
            this.getEngine().start(this);
        }
    }

    public boolean isVSync() {
        return false;
    }

    public long getContext() {
        return this.canvas.context;
    }

    public void destroy() {
        this.canvas.destroy();
    }

    private static class GLFWCanvas extends Canvas {

        private final JAWT awt;

        private JAWTDrawingSurface ds;
        //private GLXGears gears;
        private GLCapabilities caps;
        private long context;

        private final GameGLFWPanel panel;

        public GLFWCanvas(GameGLFWPanel panel, MouseListener mouse, KeyListener key, MouseMotionListener motion) {
            this.panel = panel;
            this.awt = JAWT.calloc();
            this.awt.version(JAWTFunctions.JAWT_VERSION_1_4);
            if (!JAWTFunctions.JAWT_GetAWT(awt)) {
                throw new IllegalStateException("GetAWT failed");
            }

            this.addMouseListener(mouse);
            this.addKeyListener(key);
            this.addMouseMotionListener(motion);
        }

        @Override
        public void paint(Graphics g) {
            if (this.ds == null) {
                this.ds = JAWTFunctions.JAWT_GetDrawingSurface(this, awt.GetDrawingSurface());
                if (this.ds == null) {
                    throw new IllegalStateException("awt->GetDrawingSurface() failed");
                }
            }

            int lock = JAWTFunctions.JAWT_DrawingSurface_Lock(this.ds, this.ds.Lock());
            if ((lock & JAWTFunctions.JAWT_LOCK_ERROR) != 0) {
                throw new IllegalStateException("ds->Lock() failed");
            }

            try {
                JAWTDrawingSurfaceInfo dsi = JAWTFunctions.JAWT_DrawingSurface_GetDrawingSurfaceInfo(this.ds, this.ds.GetDrawingSurfaceInfo());
                if (dsi == null) {
                    throw new IllegalStateException("ds->GetDrawingSurfaceInfo() failed");
                }
                try {
                    JAWTWin32DrawingSurfaceInfo dsi_win = JAWTWin32DrawingSurfaceInfo.create(dsi.platformInfo());

                    long hdc = dsi_win.hdc();
                    if (hdc == MemoryUtil.NULL) {
                        return;
                    }

                    // The render method is invoked in the EDT
                    if (this.context == MemoryUtil.NULL) {
                        createContextGLFW(dsi_win);
                    } else {
                        GLFW.glfwMakeContextCurrent(context);
                        GL.setCapabilities(caps);
                    }

                    try (MemoryStack stack = MemoryStack.stackPush()) {
                        IntBuffer pw = stack.mallocInt(1);
                        IntBuffer ph = stack.mallocInt(1);

                        GLFW.glfwGetFramebufferSize(context, pw, ph);
                        this.panel.getEngine().render((Graphics2D) g);
                    }

                    GLFW.glfwSwapBuffers(this.context);
                    GLFW.glfwPollEvents();


                    GLFW.glfwMakeContextCurrent(MemoryUtil.NULL);
                    GL.setCapabilities(null);
                } finally {
                    // Free the drawing surface info
                    JAWTFunctions.JAWT_DrawingSurface_FreeDrawingSurfaceInfo(dsi, ds.FreeDrawingSurfaceInfo());
                }
            } finally {
                // Unlock the drawing surface
                JAWTFunctions.JAWT_DrawingSurface_Unlock(ds, ds.Unlock());
            }
        }

        private void createContextGLFW(JAWTWin32DrawingSurfaceInfo dsi_win) {
            this.context = GLFWNativeWin32.glfwAttachWin32Window(dsi_win.hwnd(), MemoryUtil.NULL);
            if (this.context == MemoryUtil.NULL) {
                throw new IllegalStateException("Failed to attach win32 window.");
            }

            GLFW.glfwMakeContextCurrent(this.context);
            this.caps = GL.createCapabilities();
            ((GameEngine3D) this.panel.getEngine()).setupGLFW(this.panel);
        }

        public void destroy() {
            JAWTFunctions.JAWT_FreeDrawingSurface(this.ds, this.awt.FreeDrawingSurface());
            this.awt.free();
            if (this.context != MemoryUtil.NULL) {
                GLFW.glfwDestroyWindow(this.context);
            }
        }
    }
}
