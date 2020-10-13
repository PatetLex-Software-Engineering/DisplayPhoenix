package net.displayphoenix.ui.constructor;

import net.displayphoenix.lang.Localizer;

import java.awt.*;

public class ConstructElement {

    private boolean localized;

    private String name;
    private Color color;

    public ConstructElement(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public ConstructElement setLocalized() {
        this.localized = true;
        return this;
    }

    public String getName() {
        if (this.localized)
            return Localizer.translate(this.name);
        return name;
    }

    public Color getColor() {
        return color;
    }
}
