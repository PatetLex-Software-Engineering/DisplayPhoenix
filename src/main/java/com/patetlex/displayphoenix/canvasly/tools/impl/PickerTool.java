package com.patetlex.displayphoenix.canvasly.tools.impl;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.Pixel;
import com.patetlex.displayphoenix.canvasly.ToolPanel;
import com.patetlex.displayphoenix.canvasly.interfaces.ISettingComponent;
import com.patetlex.displayphoenix.canvasly.tools.Setting;
import com.patetlex.displayphoenix.canvasly.tools.Tool;

import javax.swing.*;
import java.util.List;

public class PickerTool extends Tool {

    @Override
    public String getName() {
        return "picker";
    }

    @Override
    public ImageIcon getIcon() {
        return getImage("image/picker");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        Pixel pixel = canvas.getLayers().get(canvas.getSelectedLayer())[x][y];
        if (pixel != null) {
            toolkit.getColorWheel().setColor(pixel.getColor(), 1F);
        }
    }

    @Override
    public List<Setting> getSettings() {
        return null;
    }


}
