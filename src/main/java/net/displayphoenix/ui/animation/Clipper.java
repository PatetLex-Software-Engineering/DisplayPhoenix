package net.displayphoenix.ui.animation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author TBroski
 */
public class Clipper implements Runnable {

    private List<Runnable> runnableListeners = new ArrayList<>();

    private float crement;
    private float maxVal;
    private float minVal;
    private boolean isSmooth;

    private Timer timer;
    private float currVal;

    private boolean increment;

    public Clipper(float crement, float maxValue, float minValue) {
        this(crement, maxValue, minValue, minValue);
    }

    public Clipper(float crement, float maxValue, float minValue, float currValue) {
        this.timer = new Timer(this); //duration
        this.crement = crement;
        this.maxVal = maxValue;
        this.minVal = minValue;
        this.currVal = currValue;
    }

    public Clipper smooth() {
        this.isSmooth = true;
        return this;
    }

    public void increment() {
        if (!this.isSmooth)
            this.currVal = minVal;
        this.increment = true;
        this.timer.start();
    }

    public void increment(float maxValue) {
        this.maxVal = maxValue;
        if (!this.isSmooth)
            this.currVal = minVal;
        this.increment = true;
        this.timer.start();
    }

    public void decrement() {
        if (!this.isSmooth)
            this.currVal = maxVal;
        this.increment = false;
        this.timer.start();
    }

    public void stop() {
        this.timer.stop();
    }

    public void setCrement(float crement) {
        this.crement = crement;
    }

    public float getCrement() {
        return this.crement;
    }

    public float getCurrentValue() {
        return currVal;
    }

    public boolean isIncrementing() {
        return increment;
    }

    public void addListener(Runnable runnable) {
        this.runnableListeners.add(runnable);
    }

    @Override
    public void run() {
        currVal += increment ? this.crement : -this.crement;
        if (currVal < minVal || currVal > maxVal) {
            currVal = increment ? maxVal : minVal;
            this.stop();
        }
        for (Runnable runnable : this.runnableListeners) {
            runnable.run();
        }
    }

    private static class Timer {

        private boolean isRunning;
        private Runnable listener;
        private Thread currentThread;

        public Timer(Runnable listener) {
            this.listener = listener;
        }

        public void stop() {
            this.isRunning = false;
        }

        public void start() {
            this.isRunning = true;
            this.currentThread = new Thread(() -> {
                while (this.isRunning) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    listener.run();
                }
            });
            this.currentThread.start();
        }
    }
}