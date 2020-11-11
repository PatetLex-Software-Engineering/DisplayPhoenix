package net.displayphoenix.ui.widget.color;

import net.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class BarPicker extends JPanel implements MouseListener, MouseMotionListener {

    private boolean isBackTransparent;
    private int pickerX;

    private Point cachedPoint;

    public BarPicker(boolean isBackTransparent) {
        setOpaque(false);
        this.isBackTransparent = isBackTransparent;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public float getValue() {
        return (float) this.pickerX / (float) this.getWidth();
    }

    public void setValue(float value) {
        this.pickerX = Math.round(value * (float) this.getWidth());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (!this.isBackTransparent) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        }
        else {
            for (int xp = 0; xp < getWidth(); xp++) {
                for (int yp = 0; yp < getHeight(); yp++) {
                    float v = xp + yp;
                    g.setColor(v % 2 == 0 ? Color.GRAY : Color.DARK_GRAY);
                    g.fillRect(xp, yp, 1, 1);
                }
            }
        }
        for (int i = 0; i < getWidth(); i++) {
            float r = (float) i / (float) getWidth();
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, r));
            ((Graphics2D) g).setColor(getForeground());
            ((Graphics2D) g).fillRect(getWidth() - i, 0, 1, getHeight());
        }
        g.setColor(Application.getTheme().getColorTheme().getSecondaryColor());
        g.fillRect(this.pickerX, 0, 1, getHeight());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.pickerX = e.getX();
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
        if (this.pickerX + dx >= 0 && this.pickerX + dx < this.getWidth()) {
            this.pickerX += dx;
        }
        this.cachedPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
