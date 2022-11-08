package com.patetlex.displayphoenix.gamely.engine.impl;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.gamely.physics.GamePhysics;
import com.patetlex.displayphoenix.gamely.ui.GamePanel;
import com.patetlex.displayphoenix.gamely.ui.ext.GameGLFWPanel;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GameEngine3D extends GameEngine {

    public static final float FOV = (float) Math.toRadians(60);
    public static final float Z_NEAR = 0.01F;
    public static final float Z_FAR = 1000F;

    private final Matrix4f projectionMatrix;

    public GameEngine3D(GamePhysics physics, int tickRate) {
        super(physics, tickRate);

        this.projectionMatrix = new Matrix4f();
    }

    @Override
    public void start(GamePanel panel) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!(panel instanceof GameGLFWPanel)) {
            throw new IllegalArgumentException("Use GameGLFWPanel.");
        }

        super.start(panel);

    }

    public void setupGLFW(GameGLFWPanel panel) {

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);

        GL11.glViewport(0, 0, panel.getWidth(), panel.getHeight());

        if (panel.isVSync()) {
            GLFW.glfwSwapInterval(1);
        }
    }

    @Override
    public void render(Graphics2D g) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

    }

    @Override
    public void tick() {

    }

    @Override
    protected void end() {
        ((GameGLFWPanel) this.getPanel()).destroy();
    }

    @Override
    public boolean shouldBreak() {
        return GLFW.glfwWindowShouldClose(((GameGLFWPanel) this.getPanel()).getContext());
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix4f, int width, int height) {
        float aspectRatio = (float) width / height;
        return matrix4f.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix() {
        return this.updateProjectionMatrix(this.projectionMatrix, this.getPanel().getWidth(), this.getPanel().getHeight());
    }
}
