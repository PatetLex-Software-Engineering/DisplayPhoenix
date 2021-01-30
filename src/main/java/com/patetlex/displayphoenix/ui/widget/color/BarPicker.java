package com.patetlex.displayphoenix.ui.widget.color;

import com.patetlex.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public abstract class BarPicker extends JPanel implements MouseListener, MouseMotionListener {

    private boolean isBackTransparent;
    protected int pickerX;

    private Point cachedPoint;

    protected List<ActionListener> actionListeners = new ArrayList<>();

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
        this.setValue(Math.round(value * (float) this.getWidth()));
    }

    protected void setValue(int x) {
        this.pickerX = x;
        for (ActionListener actionListener : this.actionListeners) {
            actionListener.actionPerformed(new ActionEvent(this, 0, "setValue"));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintBack(g);
        g.setColor(Application.getTheme().getColorTheme().getSecondaryColor());
        g.fillRect(this.pickerX, 0, 1, getHeight());
    }

    protected void paintBack(Graphics g) {
        if (!this.isBackTransparent) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
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
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.setValue(e.getX());
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
            this.setValue(Math.round(this.pickerX + dx));
        }
        this.cachedPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
