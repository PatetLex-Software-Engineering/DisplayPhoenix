package com.patetlex.displayphoenix.canvasly.util;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;

import java.awt.*;

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
}
