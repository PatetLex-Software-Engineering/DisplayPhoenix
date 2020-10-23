package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.Element;

import javax.swing.*;
import java.awt.*;

public class ImageElement extends Element {

    private ImageIcon image;

    public ImageElement(ImageIcon image) {
        this.image = image;
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.drawImage(this.image.getImage(), getOffsetX(), getOffsetY(), canvas);
    }

    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return Math.round(canvas.convergeZoom(this.image.getImage().getWidth(canvas)));
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return Math.round(canvas.convergeZoom(this.image.getImage().getHeight(canvas)));
    }
}
