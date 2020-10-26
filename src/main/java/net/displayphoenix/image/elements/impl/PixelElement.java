package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.ColorableElement;

import java.awt.*;

public class PixelElement extends ColorableElement {

    public PixelElement() {
        setCentered(false);
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.setColor(getColor());
        g.fillRect(0, 0,1,1);
    }

    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return 1;
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return 1;
    }
}
