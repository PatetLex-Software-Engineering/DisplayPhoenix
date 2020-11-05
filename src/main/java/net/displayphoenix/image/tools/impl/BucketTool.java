package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.Pixel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.CanvasHelper;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

public class BucketTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/bucket");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {
        Pixel clickedPixel = canvas.getLayers().get(canvas.getSelectedLayer())[x][y];
        Color pixelColor = clickedPixel != null ? clickedPixel.getColor() : null;
        fillColor(canvas, toolkit.getColorWheel().getColor(), pixelColor, x, y);
    }

    @Override
    public void onRightClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y) {

    }


    private void fillColor(CanvasPanel canvas, Color fillColor, Color keyColor, int x, int y) {
        canvas.setPixel(x, y, new Pixel(fillColor));
        if (CanvasHelper.isPointInBounds(canvas, x + 1, y) && getPixelColor(canvas, x + 1, y) == keyColor && getPixelColor(canvas, x + 1, y) != fillColor) {
            fillColor(canvas, fillColor, keyColor, x + 1, y);
        }
        if (CanvasHelper.isPointInBounds(canvas, x - 1, y) && getPixelColor(canvas, x - 1, y) == keyColor && getPixelColor(canvas, x - 1, y) != fillColor) {
            fillColor(canvas, fillColor, keyColor, x - 1, y);
        }
        if (CanvasHelper.isPointInBounds(canvas, x, y + 1) && getPixelColor(canvas, x, y + 1) == keyColor && getPixelColor(canvas, x, y + 1) != fillColor) {
            fillColor(canvas, fillColor, keyColor, x, y + 1);
        }
        if (CanvasHelper.isPointInBounds(canvas, x, y - 1) && getPixelColor(canvas, x, y - 1) == keyColor && getPixelColor(canvas, x, y - 1) != fillColor) {
            fillColor(canvas, fillColor, keyColor, x, y - 1);
        }
    }

    private Color getPixelColor(CanvasPanel canvas, int x, int y) {
        Pixel pixel = canvas.getLayers().get(canvas.getSelectedLayer())[x][y];
        return pixel != null ? pixel.getColor() : null;
    }
}
