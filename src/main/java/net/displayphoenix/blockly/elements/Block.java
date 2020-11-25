package net.displayphoenix.blockly.elements;

import com.google.gson.JsonObject;
import net.displayphoenix.blockly.Blockly;

import java.util.HashMap;
import java.util.Map;

/**
 * @author TBroski
 */
public class Block {

    private JsonObject blocklyJson;

    private boolean isDefault;
    private boolean isHidden;
    private boolean doesPersist;
    private String[] localDependencies;
    private String[] statementDependencies;
    private Map<String, Map<String, String[]>> fieldDependencies = new HashMap<>();
    private String[] localProvisions;
    private Map<String, String[]> statementProvisions = new HashMap<>();
    private Map<String, Map<String, String[]>> fieldProvisions = new HashMap<>();

    private String type;
    private String init;

    /**
     * Base element of Blockly, static data of block
     *
     * For custom blocks use,
     * @see Block#Block(String, String, JsonObject) 
     *
     * @param type  Block type, identifier
     */
    public Block(String type) {
        this.type = type;
        this.isDefault = true;
    }

    /**
     * Custom block, has blockly json and init
     *
     * @param type  Block type, identifier
     * @param init  Init json
     * @param blocklyJson  Blockly json
     */
    public Block(String type, String init, JsonObject blocklyJson) {
        this.type = type;
        this.init = init;
        this.blocklyJson = blocklyJson;
    }

    /**
     * Hides block in categories
     * @return
     */
    public Block hide() {
        this.isHidden = true;
        return this;
    }

    /**
     * Persists block, block cannot be deleted
     * @return
     */
    public Block persist() {
        this.doesPersist = true;
        return this;
    }

    /**
     * Sets local provisions of block
     *
     * @param provisions  Provisions array
     * @return
     */
    public void setLocalProvisions(String[] provisions) {
        this.localProvisions = provisions;
    }

    /**
     * Adds statement provisions
     *
     * @param statementKey  Statement key
     * @param provisions  Provisions array
     */
    public void addStatementProvisions(String statementKey, String[] provisions) {
        this.statementProvisions.put(statementKey, provisions);
    }

    /**
     * Adds field provisions
     *
     * @param fieldKey  Field key
     * @param fieldValueToProvisions  Field value to provisions
     */
    public void addFieldProvisions(String fieldKey, Map<String, String[]> fieldValueToProvisions) {
        this.fieldProvisions.put(fieldKey, fieldValueToProvisions);
    }

    /**
     * Sets local dependencies of block
     *
     * @param dependencies  Dependencies array
     * @return
     */
    public void setLocalDependencies(String[] dependencies) {
        this.localDependencies = dependencies;
    }

    /**
     * Adds statement dependencies
     *
     * @param dependencies  Dependencies array
     */
    public void addStatementDependencies(String[] dependencies) {
        this.statementDependencies = dependencies;
    }

    /**
     * Adds field dependencies
     *
     * @param fieldKey  Field key
     * @param fieldValueToDependencies  Field value to dependencies
     */
    public void addFieldDependencies(String fieldKey, Map<String, String[]> fieldValueToDependencies) {
        this.fieldDependencies.put(fieldKey, fieldValueToDependencies);
    }

    public boolean isCustom() {
        return !isDefault;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public boolean doesPersist() {
        return doesPersist;
    }

    public String getType() {
        return type;
    }

    public String[] getLocalProvisions() {
        return this.localProvisions;
    }

    public String[] getLocalDependencies() {
        return this.localDependencies;
    }

    public String[] getStatementProvisions(String statementKey) {
        return this.statementProvisions.get(statementKey);
    }

    public String[] getStatementDependencies() {
        return this.statementDependencies;
    }

    public Map<String, String[]> getFieldProvisions(String fieldKey) {
        return this.fieldProvisions.get(fieldKey);
    }

    public Map<String, String[]> getFieldDependencies(String fieldKey) {
        return this.fieldDependencies.get(fieldKey);
    }

    /**
     * Get category of block
     * @return
     */
    public Category getCategory() {
        // Iterating each registered category
        for (Category category : Blockly.getBlocklyCategories()) {

            // Iterating block of category
            for (Block block : Blockly.getBlocksFromCategory(category)) {

                // Checking if block is not hidden
                if (!block.isHidden) {

                    // Checking block type
                    if (block.getType().equalsIgnoreCase(this.getType())) {
                        return category;
                    }
                }
            }
        }
        return null;
    }

    public String getInit() {
        return init;
    }

    public JsonObject getBlocklyJson() {
        return blocklyJson;
    }
}
