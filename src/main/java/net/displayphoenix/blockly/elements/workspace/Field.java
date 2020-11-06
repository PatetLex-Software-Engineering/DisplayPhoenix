package net.displayphoenix.blockly.elements.workspace;

/**
 * @author TBroski
 */
public class Field {

    private String key;
    private String value;

    /**
     * Blockly field
     *
     * https://developers.google.com/blockly/guides/create-custom-blocks/fields/built-in-fields/text-input
     *
     * @param key  Key of field
     * @param value  Value of field
     */
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
