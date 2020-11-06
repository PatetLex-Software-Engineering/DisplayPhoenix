package net.displayphoenix.blockly.elements.workspace;

/**
 * @author TBroski
 */
public class Mutation {

    private String key;
    private int amount;

    /**
     * Advanced manipulator for blocks
     *
     * @param key  Mutation key
     * @param amount  Amount of mutations
     */
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
