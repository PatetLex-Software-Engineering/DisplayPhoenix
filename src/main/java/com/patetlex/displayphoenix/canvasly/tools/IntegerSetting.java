package com.patetlex.displayphoenix.canvasly.tools;

public class IntegerSetting extends Setting {

    private int defaultValue;
    private int minValue;
    private int maxValue;

    public IntegerSetting(String translationKey, int defaultValue, int minimumValue, int maxValue) {
        super(translationKey);
        this.defaultValue = defaultValue;
        this.minValue = minimumValue;
        this.maxValue = maxValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public int getDefaultValue() {
        return defaultValue;
    }

    public int getMinValue() {
        return minValue;
    }
}
