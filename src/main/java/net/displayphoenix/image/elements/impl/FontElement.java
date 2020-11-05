package net.displayphoenix.image.elements.impl;

import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.ColorableElement;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FontElement extends ColorableElement implements KeyListener {

    private String text;
    private Font font = Font.getFont(Font.SANS_SERIF);

    public FontElement(String text, Color color, float scale) {
        this.text = text;
        setColor(color);
        setScaleFactor(scale);
    }
    public FontElement(String text) {
        this(text, Color.BLACK, 1F);
    }

    @Override
    public void draw(CanvasPanel canvas, Graphics g) {
        g.setColor(this.getColor());
        g.setFont(this.font);
        g.drawString(this.text, 0, getHeight(canvas, g));
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

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE && this.text.length() > 0) {
            this.text = this.text.substring(0, this.text.length() - 1);
        }
        else {
            this.text += e.getKeyChar();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
