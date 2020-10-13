package net.displayphoenix.ui;

import net.displayphoenix.enums.WidgetStyle;

import java.awt.*;

public class Theme {

    private final ColorTheme colorTheme;
    private final WidgetStyle widgetStyle;
    private final Font font;
    private final int width;
    private final int height;

    public Theme(ColorTheme colorTheme, WidgetStyle widgetStyle, Font font, int width, int height) {
        this.colorTheme = colorTheme;
        this.widgetStyle = widgetStyle;
        this.font = font;
        this.width = width;
        this.height = height;
    }

    public ColorTheme getColorTheme() {
        return colorTheme;
    }

    public WidgetStyle getWidgetStyle() {
        return widgetStyle;
    }

    public Font getFont() {
        return font;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
