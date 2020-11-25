package net.displayphoenix.bitly.ui;

import java.awt.*;

public class BitArgument {

    private String flag;
    private Object value;

    /**
     * Object holder of flag and value
     *
     * @see net.displayphoenix.bitly.elements.Bit#get(BitArgument...)
     * 
     * @param flag  Code flag of widget, identifier
     * @param value  Value to set
     */
    public BitArgument(String flag, Object value) {
        this.flag = flag;
        this.value = value;
    }

    /**
     *
     * @return  Flag of widget
     */
    public String getFlag() {
        return flag;
    }

    /**
     * @return  Raw value
     */
    public Object get() {
        return this.value;
    }

    /**
     * @return  Value to set as string
     */
    public String getAsString() {
        return (String) value;
    }

    /**
     * @return  Value to set as float
     */
    public float getAsFloat() {
        return (float) value;
    }

    /**
     * @return  Value to set as boolean
     */
    public boolean getAsBoolean() {
        return (boolean) value;
    }
}
