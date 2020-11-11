package net.displayphoenix.canvasly.tools.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.interfaces.ISettingComponent;
import net.displayphoenix.canvasly.tools.Setting;
import net.displayphoenix.canvasly.tools.Tool;
import net.displayphoenix.util.CanvasHelper;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.util.List;

public class PencilTool extends Tool {

    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/pencil");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        int pointVal = (int) settingComponents[0].getValue();
        for (int i = 0; i < pointVal * 2; i++) {
            for (int j = 0; j < pointVal * 2; j++) {
                float dx = i - pointVal;
                float dy = j - pointVal;
                float d = (float) Math.sqrt((dx * dx) + (dy * dy));
                if (d < pointVal && CanvasHelper.isPointInBounds(canvas, Math.round(x + dx), Math.round(y + dy))) {
                    canvas.setPixel(Math.round(x + dx), Math.round(y + dy), new Pixel(toolkit.getColorWheel().getColor()));
                }
            }
        }
        canvas.repaint();
    }

    @Override
    public List<Setting> getSettings() {
        return Tool.getPointSettings();
    }
}
