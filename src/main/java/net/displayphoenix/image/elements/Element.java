package net.displayphoenix.image.elements;

import net.displayphoenix.image.CanvasPanel;

import java.awt.*;

public abstract class Element implements Cloneable {

    private float scale = 1F;
    private boolean centered = true;

    public abstract void parse(CanvasPanel canvas, int offsetX, int offsetY);
    public abstract void draw(CanvasPanel canvas, Graphics g);
    public abstract int getWidth(CanvasPanel canvas, Graphics g);
    public abstract int getHeight(CanvasPanel canvas, Graphics g);

    public float getScaleFactor() {
        return this.scale;
    }

    public boolean isCentered() {
        return centered;
    }

    public void setScaleFactor(float scale) {
        this.scale = scale;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
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
