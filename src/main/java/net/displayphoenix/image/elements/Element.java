package net.displayphoenix.image.elements;

import net.displayphoenix.image.CanvasPanel;

import java.awt.*;

public abstract class Element {

    private float scale = 1F;
    private int offX;
    private int offY;
    private boolean centered = true;

    public abstract void draw(CanvasPanel canvas, Graphics g);
    public abstract int getWidth(CanvasPanel canvas, Graphics g);
    public abstract int getHeight(CanvasPanel canvas, Graphics g);

    public int getOffsetX() {
        return offX;
    }

    public int getOffsetY() {
        return offY;
    }

    public void setOffsetX(int offX) {
        this.offX = offX;
    }

    public void setOffsetY(int offY) {
        this.offY = offY;
    }

    public float getScaleFactor() {
        return this.scale;
    }

    public boolean isCentered() {
        return centered;
    }

    public void parse(CanvasPanel canvas, Graphics2D g2d) {
        draw(canvas, g2d);
    }

    public void setScaleFactor(float scale) {
        this.scale = scale;
    }

    public void setCentered(boolean centered) {
        this.centered = centered;
    }
}
