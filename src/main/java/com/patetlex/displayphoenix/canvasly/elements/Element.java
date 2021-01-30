package com.patetlex.displayphoenix.canvasly.elements;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;

import java.awt.*;

public abstract class Element implements Cloneable {

    private float scale = 1F;

    public abstract void parse(CanvasPanel canvas, int offsetX, int offsetY);
    public abstract void draw(CanvasPanel canvas, Graphics g);
    public abstract int getWidth(CanvasPanel canvas, Graphics g);
    public abstract int getHeight(CanvasPanel canvas, Graphics g);

    public float getScaleFactor() {
        return this.scale;
    }

    public void setScaleFactor(float scale) {
        this.scale = scale;
    }

    public int defaultOffsetX(CanvasPanel canvas, Graphics g) {
        return 0;
    }

    public int defaultOffsetY(CanvasPanel canvas, Graphics g) {
        return 0;
    }

    public String getType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    public Element clone() {
        try {
            return (Element) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
