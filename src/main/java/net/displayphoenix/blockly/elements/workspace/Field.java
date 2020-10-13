package net.displayphoenix.blockly.elements.workspace;

/**
 * @author TBroski
 */
public class Field {

    private String key;
    private String value;

    public Field(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
