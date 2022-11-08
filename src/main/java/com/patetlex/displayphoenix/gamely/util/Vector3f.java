package com.patetlex.displayphoenix.gamely.util;

public class Vector3f {

    private float x;
    private float y;
    private float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void addX(float x) {
        this.x += x;
    }

    public void addY(float y) {
        this.y += y;
    }

    public void addZ(float z) {
        this.z += z;
    }

    public void set(Vector3f vector3f) {
        this.set(vector3f.x, vector3f.y, vector3f.z);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void add(Vector3f vector3f) {
        this.add(vector3f.x, vector3f.y, vector3f.z);
    }

    public void add(float x, float y, float z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public String toString() {
        return "{x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + "}";
    }
}
