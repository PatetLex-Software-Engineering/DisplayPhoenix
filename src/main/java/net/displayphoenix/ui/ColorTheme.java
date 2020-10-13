package net.displayphoenix.ui;

import java.awt.*;

public class ColorTheme {

    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    private Color textColor;

    public ColorTheme(Color primaryColor, Color secondaryColor, Color accentColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.accentColor = accentColor;
    }

    public ColorTheme(Color primaryColor, Color secondaryColor, Color accentColor, Color textColor) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.accentColor = accentColor;
        this.textColor = textColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getTextColor() {
        return textColor != null ? textColor : accentColor;
    }
}
