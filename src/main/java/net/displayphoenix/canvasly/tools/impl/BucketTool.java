package net.displayphoenix.canvasly.tools.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.interfaces.ISettingComponent;
import net.displayphoenix.canvasly.tools.Setting;
import net.displayphoenix.canvasly.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BucketTool extends Tool {

    @Override
    public String getName() {
        return "bucket";
    }

    @Override
    public ImageIcon getIcon() {
        return getImage("image/bucket");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        fill(canvas, x, y, getPixelColor(canvas, x, y), toolkit.getColorWheel().getColor(), (Integer) settingComponents[0].getValue());
        canvas.repaint();
    }

    private void fill(CanvasPanel canvas, int x, int y, Color key, Color fill, float tolerance) {
        for (int i = x; i < canvas.getCanvasWidth(); i++) {
            if (getPixelColor(canvas, i, y) != fill) {
                for (int j = y + 1; j < canvas.getCanvasHeight(); j++) {
                    if (getPixelColor(canvas, i, j) != fill) {
                        if (isColorTolerated(key, getPixelColor(canvas, i, j), tolerance)) {
                            canvas.setPixel(i, j, new Pixel(fill));
                            fill(canvas, i, j, key, fill, tolerance);
                        } else {
                            break;
                        }
                    }
                }
                if (isColorTolerated(key, getPixelColor(canvas, i, y), tolerance)) {
                    canvas.setPixel(i, y, new Pixel(fill));
                } else {
                    break;
                }
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            if (getPixelColor(canvas, i, y) != fill) {
                for (int j = y + 1; j < canvas.getCanvasHeight(); j++) {
                    if (getPixelColor(canvas, i, j) != fill) {
                        if (isColorTolerated(key, getPixelColor(canvas, i, j), tolerance)) {
                            canvas.setPixel(i, j, new Pixel(fill));
                            fill(canvas, i, j, key, fill, tolerance);
                        } else {
                            break;
                        }
                    }
                }
                if (isColorTolerated(key, getPixelColor(canvas, i, y), tolerance)) {
                    canvas.setPixel(i, y, new Pixel(fill));
                } else {
                    break;
                }
            }
        }
        for (int i = x; i < canvas.getCanvasWidth(); i++) {
            if (y - 1 < 0 || getPixelColor(canvas, i, y - 1) != fill) {
                for (int j = y - 1; j >= 0; j--) {
                    if (getPixelColor(canvas, i, j) != fill) {
                        if (isColorTolerated(key, getPixelColor(canvas, i, j), tolerance)) {
                            canvas.setPixel(i, j, new Pixel(fill));
                            fill(canvas, i, j, key, fill, tolerance);
                        } else {
                            break;
                        }
                    }
                }
                if (y - 1 >= 0 && isColorTolerated(key, getPixelColor(canvas, i, y - 1), tolerance)) {
                    canvas.setPixel(i, y - 1, new Pixel(fill));
                } else {
                    break;
                }
            }
        }
        for (int i = x - 1; i >= 0; i--) {
            if (y - 1 < 0 || getPixelColor(canvas, i, y - 1) != fill) {
                for (int j = y - 1; j >= 0; j--) {
                    if (getPixelColor(canvas, i, j) != fill) {
                        if (isColorTolerated(key, getPixelColor(canvas, i, j), tolerance)) {
                            canvas.setPixel(i, j, new Pixel(fill));
                            fill(canvas, i, j, key, fill, tolerance);
                        } else {
                            break;
                        }
                    }
                }
                if (y - 1 >= 0 && isColorTolerated(key, getPixelColor(canvas, i, y - 1), tolerance)) {
                    canvas.setPixel(i, y - 1, new Pixel(fill));
                } else {
                    break;
                }
            }
        }
    }


    @Override
    public List<Setting> getSettings() {
        return Tool.getToleranceSettings();
    }

    private Color getPixelColor(CanvasPanel canvas, int x, int y) {
        Pixel pixel = canvas.getLayers().get(canvas.getSelectedLayer())[x][y];
        return pixel != null ? pixel.getColor() : null;
    }

    private static boolean isColorTolerated(Color key, Color input, float tolerance) {
        float t = tolerance / 100;
        if (input == key)
            return true;
        if (input == null)
            return t >= 1 ? true : false;
        if (key == null)
            return false;
        float r = input.getRed();
        float g = input.getGreen();
        float b = input.getBlue();
        float kr = key.getRed();
        float kg = key.getGreen();
        float kb = key.getBlue();
        float raR = r - kr;
        float raG = g - kg;
        float raB = b - kb;
        if (raR < 0)
            raR *= -1;
        if (raG < 0)
            raG *= -1;
        if (raB < 0)
            raB *= -1;
        return ((raR / 255F) <= t) && ((raG / 255F) <= t) && ((raB / 255F) <= t);
    }
}
