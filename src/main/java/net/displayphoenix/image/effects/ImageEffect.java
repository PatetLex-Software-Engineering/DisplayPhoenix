package net.displayphoenix.image.effects;

import net.displayphoenix.util.ImageHelper;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class ImageEffect {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private Image image;

    public ImageEffect(Image image) {
        this.image = image;
    }

    public Image flip(int axis) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image, axis == HORIZONTAL ? this.image.getWidth(null) : 0, axis == VERTICAL ? this.image.getHeight(null) : 0, axis == HORIZONTAL ? -this.image.getWidth(null) : this.image.getWidth(null), axis == VERTICAL ? -this.image.getHeight(null) : this.image.getHeight(null), null);

        this.image = newImage;
        return newImage;
    }

    public Image overlay(Color color, float opacity) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image, 0, 0, this.image.getWidth(null), this.image.getHeight(null),null);

        Color[][] imagePixels = ImageHelper.getImagePixels(newImage);
        int x = 0;
        for (Color[] px : imagePixels) {
            int y = 0;
            for (Color py : px) {
                if (py.getRed() > 0 && py.getGreen() > 0 && py.getBlue() > 0) {
                    graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                    graphics2D.setColor(color);
                    graphics2D.fillRect(x, y, 1, 1);
                }
                y++;
            }
            x++;
        }

        this.image = newImage;
        return newImage;
    }

    public Image rotate(float angle) {
        BufferedImage newImage = new BufferedImage(this.image.getWidth(null), this.image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.setTransform(AffineTransform.getRotateInstance(Math.toRadians(angle), newImage.getWidth() / 2, newImage.getHeight() / 2));
        graphics2D.drawImage(this.image, 0, 0, newImage.getWidth(), newImage.getHeight(), null);

        this.image = newImage;
        return newImage;
    }
}
