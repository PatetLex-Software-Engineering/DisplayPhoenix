package net.displayphoenix.image.tools;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.interfaces.ISettingComponent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Tool {

    public abstract ImageIcon getIcon();

    public abstract void onLeftClick(ToolPanel toolkit, CanvasPanel canvas, int x, int y, ISettingComponent[] settingComponents);
    public List<Setting> getSettings() {
        return null;
    }

    public static List<Setting> getToleranceSettings() {
        List<Setting> settings = new ArrayList<>();
        settings.add(new IntegerSetting("canvas.tolerance.setting.text", 75, 0, 100));
        return settings;
    }

    public static List<Setting> getPointSettings() {
        List<Setting> settings = new ArrayList<>();
        settings.add(new IntegerSetting("canvas.point.setting.text", 1, 1, 50));
        return settings;
    }

    public static List<Setting> getFontSettings() {
        List<Setting> settings = new ArrayList<>();
        settings.add(new FontSetting("canvas.font.setting.text"));
        return settings;
    }
}
