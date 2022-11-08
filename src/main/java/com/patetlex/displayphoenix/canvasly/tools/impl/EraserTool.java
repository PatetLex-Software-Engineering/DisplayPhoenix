package com.patetlex.displayphoenix.canvasly.tools.impl;

import com.patetlex.displayphoenix.canvasly.CanvasPanel;
import com.patetlex.displayphoenix.canvasly.ToolPanel;
import com.patetlex.displayphoenix.canvasly.elements.StaticElement;
import com.patetlex.displayphoenix.canvasly.interfaces.ISettingComponent;
import com.patetlex.displayphoenix.canvasly.tools.Setting;
import com.patetlex.displayphoenix.canvasly.tools.Tool;
import com.patetlex.displayphoenix.canvasly.util.CanvasHelper;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

public class EraserTool extends Tool {

    @Override
    public String getName() {
        return "eraser";
    }

    @Override
    public ImageIcon getIcon() {
        return getImage("image/eraser");
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
        canvas.getStaticElementAt(x, y, new Consumer<StaticElement>() {
            @Override
            public void accept(StaticElement staticElement) {
                if (staticElement != null) {
                    canvas.removeStaticElement(staticElement);
                    canvas.repaint();
                }
            }
        });
        canvas.repaint();
    }

    @Override
    public List<Setting> getSettings() {
        return Tool.getPointSettings();
    }

}
