package com.patetlex.displayphoenix.util;

import com.patetlex.displayphoenix.canvasly.effects.ImageEffect;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
        return fromResource("textures/" + identifier);
    }

    public static ImageIcon resize(Icon image, int width, int height) {
        return resize(convertIcon(image), width, height);
    }

    public static ImageIcon resize(Icon image, int sqWidth) {
        return resize(convertIcon(image), sqWidth);
    }

    public static ImageIcon resize(Image image, int width, int height) {
        return new ImageIcon(cover(image, new Dimension(width, height)));
    }

    public static ImageIcon resize(Image image, int sqWidth) {
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

    public static Color[][] getImagePixels(BufferedImage image) {
        Color[][] pixels = new Color[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                pixels[x][y] = new Color(image.getRGB(x, y));
            }
        }

        return pixels;
    }

    public static Image flip(Image image, int axis) {
        return new ImageEffect(image).flip(axis);
    }

    public static Image overlay(Image image, Color color, float opacity) {
        return new ImageEffect(image).overlay(color, opacity);
    }

    public static Image rotate(Image image, float angle) {
        return new ImageEffect(image).rotate(angle);
    }

    private static ImageIcon fromResource(String path) {
        if (CACHE.get(path) != null)
            return CACHE.get(path);
        else {
            ImageIcon newItem = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemClassLoader().getResource(path)));
            CACHE.put(path, newItem);
            return newItem;
        }
    }

    public static ImageIcon fromPath(String path) {
        return fromPath(new File(path));
    }

    public static ImageIcon fromPath(File path) {
        if (CACHE.get(path.getPath()) != null && CACHE.get(path.getPath()).getImage() != null)
            return CACHE.get(path);
        else {
            ImageIcon newItem = null;
            try {
                newItem = new ImageIcon(ImageIO.read(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            CACHE.put(path.getPath(), newItem);
            return newItem;
        }
    }

    public static Image convertIcon(Icon icon) {
        if (icon instanceof ImageIcon) {
            return ((ImageIcon) icon).getImage();
        }
        else {
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            BufferedImage image = gc.createCompatibleImage(icon.getIconWidth(), icon.getIconHeight());
            Graphics2D g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            return image;
        }
    }

    public static BufferedImage renderImage(Image image) {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }
}
