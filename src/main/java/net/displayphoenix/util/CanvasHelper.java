package net.displayphoenix.util;

import net.displayphoenix.canvasly.CanvasPanel;

public class CanvasHelper {

    public static boolean isPointInBounds(CanvasPanel canvas, int x, int y) {
        return (x >= 0 && x < canvas.getCanvasWidth()) && (y >= 0 && y < canvas.getCanvasHeight());
    }
}
