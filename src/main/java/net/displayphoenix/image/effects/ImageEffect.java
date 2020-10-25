package net.displayphoenix.image.effects;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageEffect {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private ImageIcon image;

    public ImageEffect(ImageIcon image) {
        this.image = image;
    }

    public Image flip(int axis) {
        BufferedImage newImage = new BufferedImage(this.image.getIconWidth(), this.image.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = newImage.createGraphics();

        graphics2D.drawImage(this.image.getImage(), axis == HORIZONTAL ? this.image.getIconWidth() : 0, axis == VERTICAL ? this.image.getIconHeight() : 0, axis == HORIZONTAL ? -this.image.getIconWidth() : this.image.getIconWidth(), axis == VERTICAL ? -this.image.getIconHeight() : this.image.getIconHeight(), null);

        return newImage;
    }
}
