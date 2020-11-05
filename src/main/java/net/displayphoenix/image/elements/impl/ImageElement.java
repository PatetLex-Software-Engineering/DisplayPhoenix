package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

public class ImageElement extends Element {

    private ImageIcon image;

    public ImageElement(ImageIcon image) {
        this.image = image;
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.drawImage(this.image.getImage(), 0, 0, getWidth(canvas, g), getHeight(canvas, g), canvas);
    }
    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return this.image.getIconWidth();
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return this.image.getIconHeight();
    }
}
