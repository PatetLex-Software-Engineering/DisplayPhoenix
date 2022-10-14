package com.patetlex.displayphoenix.canvasly.effects;

import com.patetlex.displayphoenix.util.ImageHelper;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class ImageEffect {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private BufferedImage image;

    public ImageEffect(Image image) {
        this.image = ImageHelper.renderImage(image);
    }

    public BufferedImage draw() {
        return image;
    }

    public ImageEffect flip(int axis) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image, axis == HORIZONTAL ? this.image.getWidth(null) : 0, axis == VERTICAL ? this.image.getHeight(null) : 0, axis == HORIZONTAL ? -this.image.getWidth(null) : this.image.getWidth(null), axis == VERTICAL ? -this.image.getHeight(null) : this.image.getHeight(null), null);

        this.image = newImage;
        return this;
    }

    public ImageEffect overlay(Color color, float opacity) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image, 0, 0, this.image.getWidth(null), this.image.getHeight(null),null);

        Color[][] imagePixels = getImagePixels(newImage);
        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        int x = 0;
        for (Color[] px : imagePixels) {
            int y = 0;
            for (Color py : px) {
                if (py.getRed() > 0 && py.getGreen() > 0 && py.getBlue() > 0) {
                    graphics2D.setColor(color);
                    graphics2D.fillRect(x, y, 1, 1);
                }
                y++;
            }
            x++;
        }

        this.image = newImage;
        return this;
    }

    public ImageEffect overlay(Image image, float opacity) {
        return overlay(image, 0, 0, opacity);
    }

    public ImageEffect overlay(Image image, int x, int y, float opacity) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image, 0, 0, this.image.getWidth(null), this.image.getHeight(null),null);

        graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        graphics2D.drawImage(image, x, y, null);

        this.image = newImage;
        return this;
    }

    public ImageEffect rotate(float angle) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.setTransform(AffineTransform.getRotateInstance(Math.toRadians(angle), newImage.getWidth() / 2, newImage.getHeight() / 2));
        graphics2D.drawImage(this.image, 0, 0, newImage.getWidth(), newImage.getHeight(), null);

        this.image = newImage;
        return this;
    }

    public ImageEffect clip(int u1, int v1, int u2, int v2) {
        BufferedImage bufferImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = bufferImage.createGraphics();

        graphics2D.drawImage(this.image, 0, 0, this.image.getWidth(null), this.image.getHeight(null),null);

        BufferedImage newImage = new BufferedImage(u2 - u1, v2 - v1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D newGraphics = newImage.createGraphics();

        Color[][] imagePixels = getImagePixels(bufferImage);
        int x = 0;
        int tx = 0;
        for (Color[] px : imagePixels) {
            int y = 0;
            int ty = 0;
            if (x <= u2 && x >= u1) {
                for (Color py : px) {
                    if (y <= v2 && y >= v1) {
                        if (py.getRed() > 0 && py.getGreen() > 0 && py.getBlue() > 0) {
                            newGraphics.setColor(py);
                            newGraphics.fillRect(tx, ty, 1, 1);
                        }
                        ty++;
                    }
                    y++;
                }
                tx++;
            }
            x++;
        }

        this.image = newImage;
        return this;
    }

    public ImageEffect expandCanvas(int u, int v) {
        return expandCanvas(u, v, new Insets(0,0,0,0));
    }

    public ImageEffect expandCanvas(int u, int v, Insets hangingInsets) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null) + u, this.image.getHeight(null) + v, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        if (hangingInsets != null) {
            graphics2D.drawImage(this.image, hangingInsets.right > 0 ? newImage.getWidth() - this.image.getWidth(null) : 0, hangingInsets.bottom > 0 ? newImage.getHeight() - this.image.getHeight(null) : 0, this.image.getWidth(null), this.image.getHeight(null), null);
        } else {
            graphics2D.drawImage(this.image, Math.round((newImage.getWidth() - this.image.getWidth(null)) / 2F), Math.round((newImage.getHeight() - this.image.getHeight(null)) / 2F), this.image.getWidth(null), this.image.getHeight(null), null);
        }

        this.image = newImage;
        return this;
    }

    private static Color[][] getImagePixels(BufferedImage image) {
        Color[][] pixels = new Color[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x][y] = new Color(image.getRGB(x, y));
            }
        }

        return pixels;
    }
}
