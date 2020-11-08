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
        Color key = toolkit.getColorWheel().getColor();
        for (int i = 0; i < canvas.getCanvasWidth(); i++) {
            for (int j = 0; j < canvas.getCanvasWidth(); j++) {
                if (ColorHelper.isColorTolerated(key, getPixelColor(pixels, i, j), (int) settingComponents[0].getValue())) {
                    pixels[i][j] = new Pixel(key);
                }
            }
        }
        canvas.repaint();
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
