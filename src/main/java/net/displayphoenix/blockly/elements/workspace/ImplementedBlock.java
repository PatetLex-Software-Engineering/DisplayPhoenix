package net.displayphoenix.blockly.elements.workspace;

import net.displayphoenix.blockly.elements.Block;

import java.util.*;

/**
 * @author TBroski
 */
public class ImplementedBlock {

    public static final int INNER_BLOCK = -999;
    public static final int NEXT_BLOCK = -888;

    private Block type;
    private int x;
    private int y;
    private Field[] fields;

    private Map<String, List<ImplementedBlock>> innerBlocks = new HashMap<>();
    private Map<String, List<ImplementedBlock>> valueBlocks = new HashMap<>();
    private List<Mutation> mutations = new ArrayList<>();

    public ImplementedBlock(Block block, int x, int y, Field... fields) {
        this.type = block;
        this.x = x;
        this.y = y;
        this.fields = fields;
    }

    public void addStatementBlock(String statementKey, ImplementedBlock implementedBlock) {
        if (!this.innerBlocks.containsKey(statementKey)) {
            this.innerBlocks.put(statementKey, new ArrayList<>());
        }
        this.innerBlocks.get(statementKey).add(implementedBlock);
    }

    public void addValueBlock(String valueKey, ImplementedBlock implementedBlock) {
        if (!this.valueBlocks.containsKey(valueKey)) {
            this.valueBlocks.put(valueKey, new ArrayList<>());
        }
        this.valueBlocks.get(valueKey).add(implementedBlock);
    }

    public void addMutation(Mutation mutation) {
        this.mutations.add(mutation);
    }

    public Block getBlock() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Field[] getFields() {
        return fields;
    }
    public Map<String, List<ImplementedBlock>> getStatementBlocks() {
        return innerBlocks;
    }
    public Map<String, List<ImplementedBlock>> getValueBlocks() {
        return valueBlocks;
    }
    public List<Mutation> getMutations() {
        return mutations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImplementedBlock) {
            ImplementedBlock otherBlock = (ImplementedBlock) obj;

            // Default checks
            if (!otherBlock.getBlock().getType().equalsIgnoreCase(this.getBlock().getType()))
                return false;
            if (this.getX() != otherBlock.getX() || this.getY() != otherBlock.getY())
                return false;

            // Statement checks
            if (!otherBlock.getStatementBlocks().equals(this.getStatementBlocks()))
                return false;
/*            for (String statementType : otherBlock.getStatementBlocks().keySet()) {
                for (ImplementedBlock statementBlock : otherBlock.getStatementBlocks().get(statementType)) {
                    if (!this.getStatementBlocks().containsKey(statementType))
                        return false;
                    for (ImplementedBlock thisStatementBlock : this.getStatementBlocks().get(statementType)) {
                        if (!statementBlock.equals(thisStatementBlock))
                            return false;
                    }
                }
            }*/

            // Value checks
            if (!otherBlock.getValueBlocks().equals(this.getValueBlocks()))
                return false;
/*            for (String valueType : otherBlock.getValueBlocks().keySet()) {
                for (ImplementedBlock valueBlock : otherBlock.getValueBlocks().get(valueType)) {
                    if (!this.getValueBlocks().containsKey(valueType))
                        return false;
                    for (ImplementedBlock thisValueBlock : this.getStatementBlocks().get(valueType)) {
                        if (!valueBlock.equals(thisValueBlock))
                            return false;
                    }
                }
            }*/

            // Field checks
            if (!Arrays.deepEquals(otherBlock.getFields(), this.getFields()))
                return false;
/*            for (Field otherField : otherBlock.getFields()) {
                for (Field thisField : this.getFields()) {
                    if (otherField.getKey() != thisField.getKey())
                        return false;
                    if (otherField.getValue() != otherField.getKey())
                        return false;
                }
            }*/

            // Mutation checks
            if (!otherBlock.getMutations().equals(this.getMutations()))
                return false;
/*            for (Mutation otherMutation : otherBlock.getMutations()) {
                for (Mutation thisMutation : this.getMutations()) {
                    if (otherMutation.getKey() != thisMutation.getKey())
                        return false;
                    if (otherMutation.getAmount() != thisMutation.getAmount())
                        return false;
                }
            }*/

            return true;
        }
        return false;
    }
}
