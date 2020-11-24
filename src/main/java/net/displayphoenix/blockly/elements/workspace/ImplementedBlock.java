package net.displayphoenix.blockly.elements.workspace;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.gen.BlocklyHtmlGenerator;
import net.displayphoenix.blockly.gen.BlocklyXmlParser;

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
    private boolean isDeletable;
    private boolean isMovable;

    private Map<String, List<ImplementedBlock>> innerBlocks = new HashMap<>();
    private Map<String, List<ImplementedBlock>> valueBlocks = new HashMap<>();
    private List<Mutation> mutations = new ArrayList<>();

    /**
     * Main element of blockly
     * Contains fields, statement blocks, value blocks, and mutations
     *
     * @param block  Block
     * @param x  X position
     * @param y  Y position
     * @param isDeletable  Is block deletable
     * @param isMovable  Is block movable
     * @param fields  Fields of block
     */
    public ImplementedBlock(Block block, int x, int y, boolean isDeletable, boolean isMovable, Field... fields) {
        this.type = block;
        this.x = x;
        this.y = y;
        this.isDeletable = isDeletable;
        this.isMovable = isMovable;
        this.fields = fields;
    }

    /**
     * Adds a statement block to block
     * For example, an if-statement
     *
     * @param statementKey  Key of statement
     * @param implementedBlock  Block to add
     */
    public void addStatementBlock(String statementKey, ImplementedBlock implementedBlock) {
        if (!this.innerBlocks.containsKey(statementKey)) {
            this.innerBlocks.put(statementKey, new ArrayList<>());
        }
        this.innerBlocks.get(statementKey).add(implementedBlock);
    }

    /**
     * Adds a value block
     *
     * For example, an print block with text value block of "Hello World!"
     *
     * @param valueKey  Key of value
     * @param implementedBlock  Block to add
     */
    public void addValueBlock(String valueKey, ImplementedBlock implementedBlock) {
        if (!this.valueBlocks.containsKey(valueKey)) {
            this.valueBlocks.put(valueKey, new ArrayList<>());
        }
        this.valueBlocks.get(valueKey).add(implementedBlock);
    }

    /**
     * Advanced mutation for blocks
     * For example, if-statement with else if and else
     *
     * @param mutation  Mutation to add
     */
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

    public boolean isDeletable() {
        return isDeletable;
    }

    public boolean isMovable() {
        return isMovable;
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

    /**
     * Parses block to xml
     * @return
     */
    @Override
    public String toString() {
        return BlocklyXmlParser.parseImplementedBlock(this);
    }

    /**
     * Checks if block equals other block
     *
     * @param obj  Other block
     * @return
     */
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
