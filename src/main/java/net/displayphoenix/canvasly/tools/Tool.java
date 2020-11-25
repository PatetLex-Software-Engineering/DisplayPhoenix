package net.displayphoenix.canvasly.tools;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.ToolPanel;
import net.displayphoenix.canvasly.interfaces.ISettingComponent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Tool {

    public static final List<Tool> REGISTERED_TOOLS = new ArrayList<>();

    private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

    public abstract String getName();
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

    protected static ImageIcon getImage(String identifier) {
        if (!(identifier.endsWith(".png") || identifier.endsWith(".gif")))
            identifier += ".png";
        String path = "textures/" + identifier;
        if (CACHE.get(path) != null)
            return CACHE.get(path);
        else {
            ImageIcon newItem = new ImageIcon(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemClassLoader().getResource(path)));
            CACHE.put(path, newItem);
            return newItem;
        }
    }
}
