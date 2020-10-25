package net.displayphoenix.util;

import net.displayphoenix.image.effects.ImageEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author TBroski
 */
public class ImageHelper {

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public static ImageIcon getImage(String identifier) {
        if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
            identifier += ".png";
        return fromPath("textures/" + identifier);
    }

    public static ImageIcon resize(ImageIcon image, int width, int height) {
        return new ImageIcon(cover(image.getImage(), new Dimension(width, height)));
    }
    public static ImageIcon resize(ImageIcon image, int sqWidth) {
        return resize(image, sqWidth, sqWidth);
    }

    public static Image cover(Image image, Dimension dimension) {
        BufferedImage buf = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);

        float imgRatio = (float) image.getHeight(null) / (float) image.getWidth(null);
        float panelRatio = (float) dimension.height / (float) dimension.width;

        int w, h, x, y;
        if (panelRatio > imgRatio) {
            h = dimension.height;
            w = (int) ((float) dimension.height / imgRatio);
        } else {
            w = dimension.width;
            h = (int) ((float) dimension.width * imgRatio);
        }

        x = (dimension.width - w) / 2;
        y = (dimension.height - h) / 2;

        buf.getGraphics().drawImage(image, x, y, w, h, null);
        return buf;
    }

    public static Image flip(ImageIcon image, int axis) {
        return new ImageEffect(image).flip(axis);
    }

    private static ImageIcon fromPath(String path) {
        if (CACHE.get(path) != null)
            return CACHE.get(path);
        else {
            ImageIcon newItem = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemClassLoader().getResource(path)));
            CACHE.put(path, newItem);
            return newItem;
        }
    }
}
