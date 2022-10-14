package com.patetlex.displayphoenix.canvasly.util;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.Pixel;
import com.patetlex.displayphoenix.canvasly.elements.Layer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class CanvasHelper {

    public static boolean isPointInBounds(CanvasPanel canvas, Point point) {
        return isPointInBounds(canvas, point.x, point.y);
    }

    public static boolean isPointInBounds(CanvasPanel canvas, int x, int y) {
        return (x >= 0 && x < canvas.getCanvasWidth()) && (y >= 0 && y < canvas.getCanvasHeight());
    }

    public static Point getCanvasPixelFromPoint(CanvasPanel canvas, Point point) {
        return getCanvasPixelFromPoint(canvas, point.x, point.y);
    }

    public static Point getCanvasPixelFromPoint(CanvasPanel canvas, int x, int y) {
        int px = Math.round(x - (canvas.getWidth() / 2F));
        int py = Math.round(y - (canvas.getHeight() / 2F));
        px = Math.round((float) Math.floor(((px - canvas.getCanvasX()) / canvas.convergeZoom(1)) + (canvas.getCanvasWidth() / 2)));
        py = Math.round((float) Math.floor(((py - canvas.getCanvasY()) / canvas.convergeZoom(1)) + (canvas.getCanvasHeight() / 2)));
        return new Point(px, py);
    }

    public static BufferedImage draw(Pixel[][] pixels) {
        Map<Layer, Pixel[][]> map = new HashMap<>();
        map.put(new Layer(0), pixels);
        return draw(map);
    }

    public static BufferedImage draw(Map<Layer, Pixel[][]> pixels) {
        int width = 0;
        int height = 0;
        for (Layer layer : pixels.keySet()) {
            width = pixels.get(layer).length;;
            height = pixels.get(layer)[0].length;
            break;
        }
        return draw(pixels, width, height);
    }

    public static BufferedImage draw(Map<Layer, Pixel[][]> pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();

        List<Layer> layers = Arrays.asList(pixels.keySet().toArray(new Layer[pixels.keySet().size()]));
        Collections.sort(layers, Comparator.comparingInt(Layer::getIndex));
        for (Layer layer : layers) {
            if (!layer.isHidden()) {
                for (Pixel[] x : pixels.get(layer)) {
                    for (Pixel y : x) {
                        if (y != null) {
                            y.draw(graphics2D);
                        }
                        graphics2D.translate(0, 1);
                    }
                    graphics2D.translate(1, -height);
                }
                graphics2D.translate(-width, 0);
            }
        }

        return image;
    }
}
