package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.Pixel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.Setting;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.CanvasHelper;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.util.List;

public class EraserTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/eraser");
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
                    canvas.setPixel(Math.round(x + dx), Math.round(y + dy), null);
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
