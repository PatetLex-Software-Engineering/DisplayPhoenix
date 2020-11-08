package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.Pixel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.Setting;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.util.List;

public class PickerTool extends Tool {
    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/picker");
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
