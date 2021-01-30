package com.patetlex.displayphoenix.canvasly.tools.impl;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.ToolPanel;
import com.patetlex.displayphoenix.canvasly.elements.impl.FontElement;
import com.patetlex.displayphoenix.canvasly.interfaces.ISettingComponent;
import com.patetlex.displayphoenix.canvasly.tools.Setting;
import com.patetlex.displayphoenix.canvasly.tools.Tool;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextTool extends Tool {

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public ImageIcon getIcon() {
        return getImage("image/text");
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
