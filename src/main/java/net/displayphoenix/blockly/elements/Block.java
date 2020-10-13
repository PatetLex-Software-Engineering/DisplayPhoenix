package net.displayphoenix.blockly.elements;

import com.google.gson.JsonObject;
import net.displayphoenix.blockly.Blockly;

import java.util.HashMap;
import java.util.Map;

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

    public Block(String type) {
        this.type = type;
        this.isDefault = true;
    }
    public Block(String type, String init, JsonObject blocklyJson) {
        this.type = type;
        this.init = init;
        this.blocklyJson = blocklyJson;
    }

    public Block hide() {
        this.isHidden = true;
        return this;
    }

    public Block persist() {
        this.doesPersist = true;
        return this;
    }

    public Block depend(String type) {
        this.dependType = type;
        return this;
    }

    public Block provide(String type) {
        this.provideType = type;
        return this;
    }

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

    public Category getCategory() {
        for (Category category : Blockly.getBlocklyCategories()) {
            for (Block block : Blockly.getBlocksFromCategory(category)) {
                if (block.getType().equalsIgnoreCase(this.getType())) {
                    return category;
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
