package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.ColorableElement;

import java.awt.*;

public class BackgroundElement extends ColorableElement {

    public BackgroundElement(Color color) {
        this.setColor(color);
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.setColor(this.getColor());
        g.fillRect(0,0, getWidth(canvas, g), getHeight(canvas, g));
    }

    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return canvas.getCanvasWidth();
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return canvas.getCanvasHeight();
    }
}
