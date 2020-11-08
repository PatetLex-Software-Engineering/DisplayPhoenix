package net.displayphoenix.image.tools.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.elements.impl.FontElement;
import net.displayphoenix.image.interfaces.ISettingComponent;
import net.displayphoenix.image.tools.Setting;
import net.displayphoenix.image.tools.Tool;
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
