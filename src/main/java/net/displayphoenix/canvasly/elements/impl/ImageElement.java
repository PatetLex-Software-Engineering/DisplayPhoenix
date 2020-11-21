package net.displayphoenix.canvasly.elements.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.elements.Element;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageElement extends Element {

    private Image image;

    public ImageElement(Image image) {
        this.image = image;
    }

    @Override
    public void parse(CanvasPanel canvas, int offsetX, int offsetY) {
        Image newImage = resize(this.image, Math.round(this.image.getWidth(canvas) * this.getScaleFactor()), Math.round(this.image.getHeight(canvas) * this.getScaleFactor()));
        for (int i = 0; i < newImage.getWidth(canvas); i++) {
            for (int j = 0; j < newImage.getHeight(canvas); j++) {
                if ((offsetX + i >= 0 && offsetX + i < canvas.getCanvasWidth()) && (offsetY + j >= 0 && offsetY + j < canvas.getCanvasHeight())) {
                    Color rgb = new Color(((BufferedImage) newImage).getRGB(i, j));
                    int alpha = (((BufferedImage) newImage).getRGB(i, j) >> 24) & 0xff;
                    rgb = new Color(rgb.getRed(), rgb.getBlue(), rgb.getGreen(), alpha);
                    canvas.setPixel(offsetX + i, offsetY + j, alpha > 0 ? new Pixel(rgb) : null);
                }
            }
        }
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.drawImage(this.image, 0, 0, getWidth(canvas, g), getHeight(canvas, g), canvas);
    }

    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return this.image.getWidth(canvas);
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return this.image.getHeight(canvas);
    }

    private static Image resize(Image image, int width, int height) {
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        float imgRatio = (float) image.getHeight(null) / (float) image.getWidth(null);
        float panelRatio = (float) height / (float) width;

        int w, h, x, y;
        if (panelRatio > imgRatio) {
            h = height;
            w = (int) ((float) height / imgRatio);
        } else {
            w = width;
            h = (int) ((float) height * imgRatio);
        }

        x = (width - w) / 2;
        y = (height - h) / 2;

        buf.getGraphics().drawImage(image, x, y, w, h, null);
        return buf;
    }
}
