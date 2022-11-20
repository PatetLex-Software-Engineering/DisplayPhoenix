package com.patetlex.displayphoenix.gamely.obj;

import com.patetlex.displayphoenix.gamely.Gamely;
import org.joml.Matrix4f;

public class Camera extends GameObject {

    public static final float Z_NEAR = 0.01F;
    public static final float Z_FAR = 1000F;

    public static float FOV = (float) Math.toRadians(60);

    private final Matrix4f projectionMatrix;

    private float drag;

    public Camera() {
        super();
        this.drag = 0;

        this.projectionMatrix = new Matrix4f();
    }

    public void move(float x, float y, float z) {
        if (z != 0) {
            this.getPosition().x += (float) Math.sin(Math.toRadians(this.getRotation().y)) * -1.0F * z;
            this.getPosition().z += (float) Math.cos(Math.toRadians(this.getRotation().y)) * z;
        }
        if (x != 0) {
            this.getPosition().x += (float) Math.sin(Math.toRadians(this.getRotation().y - 90)) * -1.0F * x;
            this.getPosition().z += (float) Math.cos(Math.toRadians(this.getRotation().y - 90)) * x;
        }
        this.getPosition().y += y;
    }

    public void setDrag(float drag) {
        this.drag = drag;
    }

    @Override
    public float getDrag() {
        return this.drag;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix4f, int width, int height) {
        float aspectRatio = (float) width / height;
        return matrix4f.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f updateProjectionMatrix() {
        if (this.getEngine().getPanel() != null)
            return this.updateProjectionMatrix(this.projectionMatrix, this.getEngine().getPanel().getWidth(), this.getEngine().getPanel().getHeight());
        return new Matrix4f();
    }

    @Override
    public void write(Gamely.Save save) {
        super.write(save);
        save.putFloat("drag", this.drag);
    }

    @Override
    public void read(Gamely.Save save) {
        super.read(save);
        this.drag = save.getFloat("drag");
    }
}
