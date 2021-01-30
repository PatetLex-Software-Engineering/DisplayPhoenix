package com.patetlex.displayphoenix.canvasly;

import java.awt.*;

public class Pixel {

    private Color color;

    public Pixel(Color color) {
        this.color = color;
    }

    public void draw(Graphics g) {
        g.setColor(getColor());
        g.fillRect(0, 0, 1, 1);
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
