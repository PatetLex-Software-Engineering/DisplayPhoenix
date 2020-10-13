package net.displayphoenix.ui.animation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Clipper implements ActionListener {

    private float crement;
    private float maxVal;
    private float minVal;
    private boolean isSmooth;

    private JComponent component;
    private Timer timer;
    private float currVal;

    private boolean increment;
    private boolean isRunning;

    public Clipper(JComponent component, float crement, float maxValue, float minValue) {
        this.timer = new Timer(1, this); //duration
        this.crement = crement;
        this.component = component;
        this.maxVal = maxValue;
        this.minVal = minValue;
        this.currVal = minValue;
    }

    public Clipper smooth() {
        this.isSmooth = true;
        return this;
    }

    public void increment() {
        if (!this.isSmooth)
            this.currVal = minVal;
        this.increment = true;
        this.isRunning = true;
        this.timer.start();
    }

    public void increment(float maxValue) {
        this.maxVal = maxValue;
        if (!this.isSmooth)
            this.currVal = minVal;
        this.increment = true;
        this.isRunning = true;
        this.timer.start();
    }

    public void decrement() {
        if (!this.isSmooth)
            this.currVal = maxVal;
        this.increment = false;
        this.isRunning = true;
        this.timer.start();
    }

    public void stop() {
        this.isRunning = false;
        this.timer.stop();
    }

    public void setCrement(float crement) {
        this.crement = crement;
    }

    public float getCurrentValue() {
        return currVal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (isRunning) {
            currVal += increment ? this.crement : -this.crement;
            if (currVal < minVal || currVal > maxVal) {
                currVal = increment ? maxVal : minVal;
                this.stop();
            }
            this.component.revalidate();
            this.component.repaint();
        }
    }
}