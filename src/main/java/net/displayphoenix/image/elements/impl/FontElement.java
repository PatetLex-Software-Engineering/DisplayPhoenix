package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.ColorableElement;

import java.awt.*;

public class FontElement extends ColorableElement {

    private String text;
    private Font font = Font.getFont(Font.SANS_SERIF);

    public FontElement(String text) {
        this.text = text;
        setColor(Color.BLACK);
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.setColor(this.getColor());
        g.setFont(this.font);
        g.drawString(this.text, 0, 0);
    }

    public void font(Font font) {
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public int getWidth(CanvasPanel canvas, Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(this.text, g).getWidth();
    }

    @Override
    public int getHeight(CanvasPanel canvas, Graphics g) {
        return (int) g.getFontMetrics().getStringBounds(this.text, g).getHeight();
    }
}
