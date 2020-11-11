package net.displayphoenix.canvasly.tools.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.elements.impl.FontElement;
import net.displayphoenix.canvasly.interfaces.ISettingComponent;
import net.displayphoenix.canvasly.tools.Setting;
import net.displayphoenix.canvasly.tools.Tool;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextTool extends Tool {

    @Override
    public ImageIcon getIcon() {
        return ImageHelper.getImage("image/text");
    }

    @Override
    public void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents) {
        canvas.setElement(canvas.getSelectedLayer(), new FontElement("Text", toolkit.getColorWheel().getColor(), (Font) settingComponents[0].getValue(), (int) settingComponents[1].getValue()), x, y);
    }

    @Override
    public List<Setting> getSettings() {
        List<Setting> settings = new ArrayList<>();
        for (Setting setting : Tool.getFontSettings()) {
            settings.add(setting);
        }
        for (Setting setting : Tool.getPointSettings()) {
            settings.add(setting);
        }
        return settings;
    }
}
