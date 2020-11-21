package net.displayphoenix.canvasly.components;

import net.displayphoenix.canvasly.interfaces.ISettingComponent;
import net.displayphoenix.canvasly.tools.FontSetting;
import net.displayphoenix.canvasly.tools.Setting;

import javax.swing.*;
import java.awt.*;

public class FontSettingComponent extends JPanel implements ISettingComponent<Font> {

    private JComboBox<String> fontList;
    private FontSetting setting;

    public FontSettingComponent(FontSetting setting) {
        this.setting = setting;
        String[] fontNames = new String[setting.getFonts().length];
        int arialIndex = 0;
        for (int i = 0; i < setting.getFonts().length; i++) {
            String name = setting.getFonts()[i].getFontName();
            fontNames[i] = name;
            if (name.equalsIgnoreCase("Arial")) {
                arialIndex = i;
            }
        }
        this.fontList = new JComboBox(fontNames);
        this.fontList.setSelectedIndex(arialIndex);
        add(this.fontList);
        setOpaque(false);
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
        if (this.fontList != null)
            this.fontList.setForeground(fg);
    }

    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        if (this.fontList != null)
            this.fontList.setBackground(bg);
    }

    @Override
    public Font getValue() {
        for (Font font : this.setting.getFonts()) {
            if (font.getFontName().equalsIgnoreCase((String) this.fontList.getSelectedItem())) {
                return font;
            }
        }
        return null;
    }

    @Override
    public Setting getSetting() {
        return this.setting;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        if (this.fontList != null)
            this.fontList.setPreferredSize(new Dimension(Math.round(preferredSize.width * 0.9F), Math.round(preferredSize.height * 0.9F)));
    }
}
