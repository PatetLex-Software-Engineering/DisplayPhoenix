package com.patetlex.displayphoenix.ui.widget.color;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.ui.interfaces.ColorListener;
import com.patetlex.displayphoenix.util.ColorHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class ColorWheel extends JPanel implements MouseListener, MouseMotionListener {

    private List<ColorListener> colorListeners = new ArrayList<>();

    private Color[][] cachedColors;

    private int rgbPickerX;
    private int rgbPickerY;

    private float brightness;
    private float alpha;

    private Point cachedPoint;

    public ColorWheel() {
        setOpaque(false);
        addMouseListener(this);
        addMouseMotionListener(this);
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.brightness = 1F;
        this.alpha = 1F;
    }

    public Color getColor() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Color color = this.cachedColors[centerX - this.rgbPickerX][centerY - this.rgbPickerY];
        if (color != null)
            color = new Color(Math.round(color.getRed() * this.brightness), Math.round(color.getGreen() * this.brightness), Math.round(color.getBlue() * this.brightness), Math.round(this.alpha * 255));
        return color;
    }

    public void setColor(Color key, float tolerance) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Color fullBrightKey = ColorHelper.max(key);
        int j = 0;
        for (Color[] xColors : this.cachedColors) {
            int k = 0;
            for (Color yColor : xColors) {
                if (yColor != null && ColorHelper.isColorTolerated(fullBrightKey, yColor, tolerance)) {
                    this.rgbPickerX = centerX - j;
                    this.rgbPickerY = centerY - k;
                    for (ColorListener colorListener : this.colorListeners) {
                        colorListener.onColorSet(key);
                    }
                    repaint();
                    break;
                }
                k++;
            }
            j++;
        }
    }

    protected void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    protected void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.cachedColors = new Color[getWidth()][getHeight()];
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = (getWidth() / 2) * (getWidth() / 2);
        int redX = getWidth();
        int redY = getHeight() / 2;
        int redRad = getWidth() * getWidth();
        int greenX = 0;
        int greenY = getHeight() / 2;
        int greenRad = getWidth() * getWidth();
        int blueX = getWidth() / 2;
        int blueY = getHeight();
        int blueRad = getWidth() * getWidth();
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                int a = x - centerX;
                int b = y - centerY;
                int distance = a * a + b * b;
                if (distance < radius) {
                    int rdx = x - redX;
                    int rdy = y - redY;
                    int redDist = (rdx * rdx + rdy * rdy);
                    int redVal = (int) (255 - ((redDist / (float) redRad) * 256));
                    int gdx = x - greenX;
                    int gdy = y - greenY;
                    int greenDist = (gdx * gdx + gdy * gdy);
                    int greenVal = (int) (255 - ((greenDist / (float) greenRad) * 256));
                    int bdx = x - blueX;
                    int bdy = y - blueY;
                    int blueDist = (bdx * bdx + bdy * bdy);
                    int blueVal = (int) (255 - ((blueDist / (float) blueRad) * 256));

                    Color c = new Color(redVal, greenVal, blueVal);
                    float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                    Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], 1);

                    g.setColor(highlight);
                    g.fillRect(x, y, 1, 1);
                    this.cachedColors[x][y] = highlight;
                }
            }
            float rgbPickerWidth = getWidth() * 0.1F;
            for (int rgbPX = 0; rgbPX < rgbPickerWidth; rgbPX++) {
                for (int rgbPY = 0; rgbPY < rgbPickerWidth; rgbPY++) {
                    float r = rgbPickerWidth / 2;
                    float dx = rgbPX - r;
                    float dy = rgbPY - r;
                    if (dy < 0)
                        dy *= -1;
                    if (dx < 0)
                        dx *= -1;
                    float d = (float) Math.sqrt((dx * dx) + (dy * dy));
                    if (d < r && d > r - 2.5F) {
                        g.setColor(getForeground());
                        g.fillRect(centerX - this.rgbPickerX + rgbPX - Math.round(r), centerY - this.rgbPickerY + rgbPY - Math.round(r), 1, 1);
                    }
                }
            }
            int swatchSize = Math.round(getWidth() * 0.2F);
            if (this.cachedColors != null) {
                for (int xp = 0; xp < swatchSize; xp++) {
                    for (int yp = 0; yp < swatchSize; yp++) {
                        float v = xp + yp;
                        g.setColor(v % 2 == 0 ? Color.WHITE : Color.GRAY);
                        g.fillRect(xp, getHeight() - yp, 1, 1);
                    }
                }
                g.setColor(this.getColor());
                g.fillRect(0, getHeight() - swatchSize, swatchSize, swatchSize);
            }
        }
    }

    public void addColorListener(ColorListener colorListener) {
        this.colorListeners.add(colorListener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int cw = getWidth() / 2;
        int ch = getHeight() / 2;
        float dx = e.getX() - cw;
        float dy = e.getY() - ch;
        if (isPointInWheel(e.getPoint())) {
            this.rgbPickerX = Math.round(-dx);
            this.rgbPickerY = Math.round(-dy);
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.cachedPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int cw = getWidth() / 2;
        int ch = getHeight() / 2;
        if (isPointInWheel(new Point(cw + this.rgbPickerX, ch + this.rgbPickerY))) {
            float dx = (float) (e.getX() - this.cachedPoint.getX());
            float dy = (float) (e.getY() - this.cachedPoint.getY());
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (isPointInWheel(new Point(Math.round(cw + this.rgbPickerX - dx), Math.round(ch + this.rgbPickerY - dy)))) {
                this.rgbPickerX -= dx;
                this.rgbPickerY -= dy;
            }
            this.cachedPoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    private boolean isPointInWheel(Point point) {
        float r = getWidth() / 2;
        int cw = getWidth() / 2;
        int ch = getHeight() / 2;
        float dx = (float) (point.getX() - cw);
        float dy = (float) (point.getY() - ch);
        float d = (float) Math.sqrt((dx * dx) + (dy * dy));
        return d < r;
    }
}
