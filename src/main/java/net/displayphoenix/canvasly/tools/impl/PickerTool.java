package net.displayphoenix.canvasly.tools.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.interfaces.ISettingComponent;
import net.displayphoenix.canvasly.tools.Setting;
import net.displayphoenix.canvasly.tools.Tool;

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
