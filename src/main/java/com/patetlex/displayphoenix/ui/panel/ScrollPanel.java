package com.patetlex.displayphoenix.ui.panel;

import com.patetlex.displayphoenix.ui.animation.Clipper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ScrollPanel extends JPanel implements MouseWheelListener, MouseListener {

    public static final float SCROLL_SENSITIVITY = 50F;

    private Map<Component, Boolean> movedComponents = new HashMap<>();

    private int maxScroll;
    private int minScroll;
    private float scroll;
    private float scrollCrement;

    public ScrollPanel() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, 3F);
    }

    public ScrollPanel(int minScroll, int maxScroll, float scrollCrement) {
        this.minScroll = minScroll;
        this.maxScroll = maxScroll;
        this.scrollCrement = scrollCrement;
        setOpaque(false);
        addMouseWheelListener(this);
        addMouseListener(this);
    }

    public float getScroll() {
        return scroll;
    }

    public void scroll(float scroll) {
        scroll /= SCROLL_SENSITIVITY;
        if (scroll == 0)
            return;
        boolean flag = this.scroll + scroll > this.scroll;
        float maxVal = flag ? scroll : 0;
        float minVal = flag ? 0 : scroll;
        float currVal = flag ? minVal : maxVal;
        Clipper clipper = new Clipper(this.scrollCrement, maxVal, minVal, currVal);
        if (flag) {
            clipper.increment();
        }
        else {
            clipper.decrement();
        }
        AtomicInteger i = new AtomicInteger();
        clipper.addListener(() -> {
            for (Component component : this.getComponents()) {
                float ty = component.getY() + (clipper.isIncrementing() ? clipper.getCrement() : - clipper.getCrement());
                int y;
                if (clipper.isIncrementing()) {
                    y = Math.round((float) Math.ceil(ty));
                }
                else {
                    y = Math.round((float) Math.floor(ty));
                }
                component.setLocation(component.getX(), y);
            }
            i.getAndIncrement();
        });
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float amount = (float) e.getPreciseWheelRotation() * SCROLL_SENSITIVITY;
        if (this.scroll + amount > this.minScroll && this.scroll + amount < this.maxScroll) {
            this.scroll += amount;
            for (Component component : this.getComponents()) {
                component.setLocation(component.getX(), Math.round(component.getY() + amount));
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            float scroll = (getHeight() / 2F) - e.getY();
            scroll *= SCROLL_SENSITIVITY;
            scroll(scroll);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

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
}
