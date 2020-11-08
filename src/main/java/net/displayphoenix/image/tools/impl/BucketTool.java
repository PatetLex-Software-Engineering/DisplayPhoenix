package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.Pixel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.Setting;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.CanvasHelper;
import net.displayphoenix.util.ColorHelper;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BucketTool extends Tool {

    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/bucket");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        Pixel[][] pixels = canvas.getLayers().get(canvas.getSelectedLayer());
        float tolerance = (int) settingComponents[0].getValue();
        Color key = getPixelColor(pixels, x, y);
        Color fill = toolkit.getColorWheel().getColor();
        fillCanvas(canvas, pixels, x, y, fill, key, tolerance);
        canvas.repaint();
    }

    private void fillCanvas(CanvasPanel canvas, Pixel[][] pixels, int x, int y, Color fill, Color key, float tolerance) {
        for (int i = x; i < canvas.getCanvasWidth(); i++) {
            for (int j = y; j < canvas.getCanvasHeight(); j++) {
                if (ColorHelper.isColorTolerated(key, getPixelColor(pixels, i, j), tolerance)) {
                    pixels[i][j] = new Pixel(fill);
                }
                else {
                    checkCanvas(canvas, pixels, i, j, fill, key, tolerance);
                    break;
                }
            }
            for (int j = y - 1; j >= 0; j--) {
                if (ColorHelper.isColorTolerated(key, getPixelColor(pixels, i, j), tolerance)) {
                    pixels[i][j] = new Pixel(fill);
                }
                else {
                    checkCanvas(canvas, pixels, i, j, fill, key, tolerance);
                    break;
                }
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            for (int j = y; j < canvas.getCanvasHeight(); j++) {
                if (ColorHelper.isColorTolerated(key, getPixelColor(pixels, i, j), tolerance)) {
                    pixels[i][j] = new Pixel(fill);
                }
                else {
                    checkCanvas(canvas, pixels, i, j, fill, key, tolerance);
                    break;
                }
            }
            for (int j = y - 1; j >= 0; j--) {
                if (ColorHelper.isColorTolerated(key, getPixelColor(pixels, i, j), tolerance)) {
                    pixels[i][j] = new Pixel(fill);
                }
                else {
                    checkCanvas(canvas, pixels, i, j, fill, key, tolerance);
                    break;
                }
            }
        }
    }

    private void checkCanvas(CanvasPanel canvas, Pixel[][] pixels, int x, int y, Color fill, Color key, float tolerance) {
        if (CanvasHelper.isPointInBounds(canvas, x + 1, y) && ColorHelper.isColorTolerated(key, getPixelColor(pixels, x + 1, y), tolerance) && getPixelColor(pixels, x + 1, y) != fill) {
            fillCanvas(canvas, pixels, x + 1, y, fill, key, tolerance);
        }
        else if (CanvasHelper.isPointInBounds(canvas, x - 1, y) && ColorHelper.isColorTolerated(key, getPixelColor(pixels, x - 1, y), tolerance) && getPixelColor(pixels, x - 1, y) != fill) {
            fillCanvas(canvas, pixels, x - 1, y, fill, key, tolerance);
        }
        else if (CanvasHelper.isPointInBounds(canvas, x, y + 1) && ColorHelper.isColorTolerated(key, getPixelColor(pixels, x, y + 1), tolerance) && getPixelColor(pixels, x, y + 1) != fill) {
            fillCanvas(canvas, pixels, x, y + 1, fill, key, tolerance);
        }
        else if (CanvasHelper.isPointInBounds(canvas, x, y - 1) && ColorHelper.isColorTolerated(key, getPixelColor(pixels, x, y - 1), tolerance) && getPixelColor(pixels, x, y - 1) != fill) {
            fillCanvas(canvas, pixels, x, y - 1, fill, key, tolerance);
        }
    }

    @Override
    public List<Setting> getSettings() {
        return Tool.getToleranceSettings();
    }

    private Color getPixelColor(Pixel[][] pArr, int x, int y) {
        Pixel pixel = pArr[x][y];
        return pixel != null ? pixel.getColor() : null;
    }
}
