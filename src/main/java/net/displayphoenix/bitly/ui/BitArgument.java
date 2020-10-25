package net.displayphoenix.bitly.ui;

public class BitArgument {

    private String flag;
    private Object value;

    public BitArgument(String flag, Object value) {
        this.flag = flag;
        this.value = value;
    }

    public String getFlag() {
        return flag;
    }

    public String getAsString() {
        return (String) value;
    }

    public float getAsFloat() {
        return (float) value;
    }

    public boolean getAsBoolean() {
        return (boolean) value;
    }
}
