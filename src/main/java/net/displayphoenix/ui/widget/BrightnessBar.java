package net.displayphoenix.ui.widget;

import net.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class BrightnessBar extends JPanel implements MouseListener, MouseMotionListener {

    private int bPickerX;

    private Point cachedPoint;

    public BrightnessBar() {
        setOpaque(false);
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public float getBrightness() {
        return (float) this.bPickerX / (float) this.getWidth();
    }

    public void setBrightness(float brightness) {
        this.bPickerX = Math.round(brightness * (float) this.getWidth());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float r = 1F / 255F;
        for (int i = 0; i < this.getWidth(); i++) {
            float c = r * (i + 1);
            g.setColor(new Color(c, c, c));
            g.fillRect(this.getWidth() - i, 0, 1, this.getHeight());
        }
        g.setColor(getForeground());
        g.fillRect(this.bPickerX, 0, 1, this.getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.bPickerX = e.getX();
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.cachedPoint = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        float dx = (float) (e.getX() - this.cachedPoint.getX());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (this.bPickerX + dx >= 0 && this.bPickerX + dx < this.getWidth()) {
            this.bPickerX += dx;
        }
        this.cachedPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
