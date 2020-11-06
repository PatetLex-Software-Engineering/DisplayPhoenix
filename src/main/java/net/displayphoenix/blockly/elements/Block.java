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

    private Map<String, Map<String, String[]>> fieldProvides = new HashMap<>();
    private boolean isDefault;
    private boolean isHidden;
    private boolean doesPersist;
    private String dependType;
    private String provideType;

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
     * Sets dependency of block
     * @param type  Dependency
     * @return
     */
    public Block depend(String type) {
        this.dependType = type;
        return this;
    }

    /**
     * Sets provision of block
     * @param type  Provision
     * @return
     */
    public Block provide(String type) {
        this.provideType = type;
        return this;
    }

    /**
     * Add field provisions
     * @param fieldKey  Field key
     * @param valToType  Field value to provision type
     */
    public void fieldProvide(String fieldKey, Map<String, String[]> valToType) {
        this.fieldProvides.put(fieldKey, valToType);
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

    public String getDependency() {
        return dependType;
    }

    public String getType() {
        return type;
    }

    public String getProvision() {
        return this.provideType;
    }

    public Map<String, String[]> getProvisionsFromField(String fieldKey) {
        return this.fieldProvides.get(fieldKey);
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
