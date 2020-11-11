package net.displayphoenix.canvasly.elements.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.elements.Element;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageElement extends Element {

    private ImageIcon image;

    public ImageElement(ImageIcon image) {
        this.image = image;
    }

    @Override
    public void parse(CanvasPanel canvas, int offsetX, int offsetY) {
        ImageIcon newImage = ImageHelper.resize(this.image, Math.round(this.image.getIconWidth() * this.getScaleFactor()), Math.round(this.image.getIconHeight() * this.getScaleFactor()));
        for (int i = 0; i < newImage.getIconWidth(); i++) {
            for (int j = 0; j < newImage.getIconHeight(); j++) {
                if ((offsetX + i >= 0 && offsetX + i < canvas.getCanvasWidth()) && (offsetY + j >= 0 && offsetY + j < canvas.getCanvasHeight())) {
                    Color rgb = new Color(((BufferedImage) newImage.getImage()).getRGB(i, j));
                    int alpha = (((BufferedImage) newImage.getImage()).getRGB(i, j) >> 24) & 0xff;
                    rgb = new Color(rgb.getRed(), rgb.getBlue(), rgb.getGreen(), alpha);
                    canvas.setPixel(offsetX + i, offsetY + j, alpha > 0 ? new Pixel(rgb) : null);
                }
            }
        }
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
