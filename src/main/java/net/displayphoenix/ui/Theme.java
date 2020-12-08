package net.displayphoenix.ui;

import net.displayphoenix.enums.WidgetStyle;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
public class Theme {

    private final ColorTheme colorTheme;
    private final WidgetStyle widgetStyle;
    private final Font font;
    private final boolean isResizeable;
    private int width;
    private int height;

    public Theme(ColorTheme colorTheme, WidgetStyle widgetStyle, Font font) {
        this(colorTheme, widgetStyle, font, 1200, 800, true);
    }

    public Theme(ColorTheme colorTheme, WidgetStyle widgetStyle, Font font, int width, int height) {
        this(colorTheme, widgetStyle, font, width, height, true);
    }

    private Theme(ColorTheme colorTheme, WidgetStyle widgetStyle, Font font, int width, int height, boolean resizeable) {
        this.colorTheme = colorTheme;
        this.widgetStyle = widgetStyle;
        this.font = font;
        this.width = width;
        this.height = height;
        this.isResizeable = resizeable;
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

    public boolean isResizeable() {
        return this.isResizeable;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
