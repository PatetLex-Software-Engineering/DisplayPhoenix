package com.patetlex.displayphoenix.gamely.obj;

import com.patetlex.displayphoenix.gamely.Gamely;

public class Camera extends GameObject {

    private float drag;

    public Camera() {
        super();
        this.drag = 0;
    }

    public void setDrag(float drag) {
        this.drag = drag;
    }

    @Override
    public float getDrag() {
        return this.drag;
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
