package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.ColorableElement;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class BackgroundElement extends ColorableElement {

    public BackgroundElement(Color color) {
        this.setColor(color);
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        float cw = canvas.getWidth() / 2F;
        float ch = canvas.getHeight() / 2F;
        ((Graphics2D) g).setTransform(new AffineTransform());
        ((Graphics2D) g).scale(canvas.convergeZoom(getScaleFactor()), canvas.convergeZoom(getScaleFactor()));
        g.translate(Math.round((cw - canvas.convergeZoom(canvas.getCanvasWidth()) / 2F) + canvas.convergeZoom(canvas.getCanvasX())), Math.round((ch - canvas.convergeZoom(canvas.getCanvasHeight()) / 2F) + canvas.convergeZoom(canvas.getCanvasY())));
        g.setColor(this.getColor());
        g.fillRect(getOffsetX(), getOffsetY(), getWidth(canvas, g), getHeight(canvas, g));
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
