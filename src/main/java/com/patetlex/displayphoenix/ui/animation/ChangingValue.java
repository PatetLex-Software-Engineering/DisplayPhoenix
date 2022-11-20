package com.patetlex.displayphoenix.ui.animation;

import com.patetlex.displayphoenix.gamely.engine.GameEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangingValue {

    public static int TICK_RATE = 20;
    private static boolean SHOULD_RUN = true;
    private static boolean RUNNING;
    private static final Map<ChangingValue, PairedValue> VALUES_TO_CHANGE = new HashMap<>();

    private float originalValue;

    private float value;

    public ChangingValue(float originalValue) {
        this.originalValue = originalValue;
        this.value = originalValue;
        if (!RUNNING)
            startLoop();
    }

    public float getValue() {
        return value;
    }

    public float getOriginalValue() {
        return originalValue;
    }

    public void set(float value) {
        this.value = value;
    }

    public void reset() {
        this.value = originalValue;
    }


    public void moveToValue(float newValue, int ticksToChange) {
        VALUES_TO_CHANGE.remove(this);
        this.originalValue = this.getValue();
        VALUES_TO_CHANGE.put(this, new PairedValue(newValue, ticksToChange));
    }

    public static void stopAnimationThread() {
        SHOULD_RUN = false;
    }

    /**
     * Start animation thread
     */
    private static void startLoop() {
        RUNNING = true;
        new Thread(() -> {
            long lastTime = System.nanoTime();
            final double ns = 1000000000D / TICK_RATE;
            double delta = 0;
            while (SHOULD_RUN) {
                long now = System.nanoTime();
                delta += (now - lastTime) / ns;
                lastTime = now;
                while (delta >= 1) {

                    // Change values
                    List<ChangingValue> changedValues = new ArrayList<>();
                    for (ChangingValue value : VALUES_TO_CHANGE.keySet()) {
                        PairedValue pair = VALUES_TO_CHANGE.get(value);
                        float d = pair.newValue - value.getOriginalValue();
                        float c = d / pair.ticksToChange;
                        float currentVal = value.getValue() + c;
                        if (d < 0 && currentVal < pair.newValue) {
                            currentVal = pair.newValue;
                            changedValues.add(value);
                        } else if (d > 0 && currentVal > pair.newValue) {
                            currentVal = pair.newValue;
                            changedValues.add(value);
                        }
                        value.set(currentVal);

                    }
                    for (ChangingValue changedValue : changedValues) {
                        VALUES_TO_CHANGE.remove(changedValue);
                    }

                    delta--;
                }
            }
        }).start();
    }

    private static class PairedValue {
        public final float newValue;
        public final int ticksToChange;
        public PairedValue(float newValue, int ticksToChange) {
            this.newValue = newValue;
            this.ticksToChange = ticksToChange;
        }
    }
}
