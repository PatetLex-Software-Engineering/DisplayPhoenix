package net.displayphoenix.canvasly.elements;

import java.awt.*;

public abstract class ColorableElement extends Element {

    private Color color;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
