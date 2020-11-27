package net.displayphoenix.canvasly.elements.impl;

import net.displayphoenix.canvasly.CanvasPanel;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.elements.ColorableElement;
import net.displayphoenix.canvasly.util.CanvasHelper;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

public class FontElement extends ColorableElement implements KeyListener {

    private String text;
    private Font font;

    public FontElement(String text) {
        this(text, Color.BLACK, 1F);
    }
    public FontElement(String text, Color color, float scale) {
        this(text, color, new Font(Font.SANS_SERIF, Font.PLAIN, 1), scale);
    }
    public FontElement(String text, Color color, Font font, float scale) {
        this.text = text;
        setColor(color);
        setScaleFactor(scale);
        this.font = font.deriveFont(12F);
    }

    @Override
    public void parse(CanvasPanel canvas, int offsetX, int offsetY) {
        BufferedImage image = new BufferedImage(Math.round(canvas.getCanvasWidth() * this.getScaleFactor()), Math.round(canvas.getCanvasHeight() * this.getScaleFactor()), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();

        graphics2D.scale(this.getScaleFactor(), this.getScaleFactor());
        graphics2D.setColor(this.getColor());
        graphics2D.setFont(this.getFont());
        graphics2D.drawString(this.text, 0, (image.getHeight() / this.getScaleFactor()) - getHeight(canvas, graphics2D));

        float height = getHeight(canvas, graphics2D) * this.getScaleFactor();
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = image.getHeight() - 1; j >= 0; j--) {
                Color rgb = new Color(image.getRGB(i, j));
                int alpha = (image.getRGB(i, j) >> 24) & 0xff;
                int oj = j - image.getHeight() + Math.round(height);
                if (alpha > 0 && CanvasHelper.isPointInBounds(canvas, offsetX + i, offsetY + oj + Math.round(height))) {
                    canvas.setPixel(offsetX + i, offsetY + oj + Math.round(height), new Pixel(rgb));
                }
            }
        }
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

    public String getText() {
        return text;
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
    public int defaultOffsetY(CanvasPanel canvas, Graphics g) {
        return getHeight(canvas, g);
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
