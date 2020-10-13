package net.displayphoenix.blockly.elements.workspace;

/**
 * @author TBroski
 */
public class Mutation {

    private String key;
    private int amount;

    public Mutation(String key, int amount) {
        this.key = key;
        this.amount = amount;
    }

    public String getKey() {
        return key;
    }

    public int getAmount() {
        return amount;
    }
}
