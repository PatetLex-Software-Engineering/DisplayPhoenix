package net.displayphoenix.blockly;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.Category;
import net.displayphoenix.generation.Module;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.util.BlocklyHelper;

import java.io.*;
import java.util.*;

import static net.displayphoenix.generation.Module.JAVA;
import static net.displayphoenix.generation.Module.JAVASCRIPT;

/**
 * @author TBroski
 */
public class Blockly {

    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static final Map<Category, List<Block>> BLOCKS = new HashMap<>();

    /**
     * Parses JSON string, using Gson
     *
     * @see Blockly#registerCategory(Category)
     *
     * @param categoryJson  JSON string of category
     */
    public static void registerCategory(String type, String categoryJson) {
        JsonObject categoryObject = gson.fromJson(categoryJson, JsonObject.class);
        registerCategory(new Category() {
            @Override
            public String getType() {
                return type;
            }

            @Override
            public String getName() {
                return categoryObject.get("name").getAsString();
            }

            @Override
            public String getColor() {
                return categoryObject.get("color").getAsString();
            }
        });
    }

    /**
     * Parses JSON file, using Gson
     *
     * @see Blockly#registerCategory(Category)
     * @see BlocklyPluginLoader#loadCategoriesFromDirectory(File)
     *
     * @param categoryJson  JSON file of category
     */
    public static void registerCategory(File categoryJson) {
        JsonObject categoryObject = null;
        try {
            // Parsing block object
            categoryObject = gson.fromJson(new FileReader(categoryJson), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonObject finalCategoryObject = categoryObject;
        registerCategory(new Category() {
            @Override
            public String getType() {
                return new DetailedFile(categoryJson).getFileName();
            }

            @Override
            public String getName() {
                return finalCategoryObject.get("name").getAsString();
            }

            @Override
            public String getColor() {
                return finalCategoryObject.get("color").getAsString();
            }
        });
    }

    /**
     * Registers a category with Blockly
     *
     * @param category  Category to register
     */
    public static void registerCategory(Category category) {
        if (!BLOCKS.containsKey(category) && getCategoryFromType(category.getType()) == null) {
            BLOCKS.put(category, new ArrayList<>());
        }
    }

    /**
     * Parses JSON string, using Gson
     *
     * @see Blockly#registerBlock(String, JsonObject, Category)
     *
     * @param blockJson  JSON string of block
     */
    public static void registerBlock(String type, String blockJson) {
        JsonObject blockObject = gson.fromJson(blockJson, JsonObject.class);
        Category category = getCategoryFromType(blockObject.get("category").getAsString());
        registerBlock(type, blockObject, category);
    }

    /**
     * Parses JSON file, using Gson
     *
     * @see Blockly#registerBlock(String, JsonObject, Category)
     * @see BlocklyPluginLoader#loadBlocksFromDirectory(File)
     *
     * @param blockJson  JSON file of block
     */
    public static void registerBlock(File blockJson) {
        JsonObject blockObject = null;
        try {
            // Parsing block object
            blockObject = gson.fromJson(new FileReader(blockJson), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Registering block
        Category category = getCategoryFromType(blockObject.get("category").getAsString());
        registerBlock(new DetailedFile(blockJson).getFileName(), blockObject, category);
    }

    /**
     * Registers a JsonObject
     *
     * @see Blockly#registerBlock(Block, Category)
     *
     * @param name  Type name of block
     * @param blockObject JSON object of block
     * @param category  Category to map
     */
    public static void registerBlock(String name, JsonObject blockObject, Category category) {
        // Creating block object
        Block block = new Block(name, blockObject.get("init") != null ? blockObject.get("init").getAsString() : null, blockObject);

        // Adding dependencies (if applicable)
        block.depend(blockObject.get("depend") != null ? blockObject.get("depend").getAsString() : null);

        // Adding provisions (if applicable)
        block.provide(blockObject.get("provide") != null ? blockObject.get("provide").getAsString() : null);

        // Adding field provisions (if applicable)
        if (blockObject.get("fieldProvides") != null) {
            Map<String, Map<String, String[]>> fieldProvides = gson.fromJson(blockObject.get("fieldProvides").toString(), new TypeToken<Map<String, Map<String, String[]>>>() {}.getType());
            for (String fieldKey : fieldProvides.keySet()) {
                block.fieldProvide(fieldKey, fieldProvides.get(fieldKey));
            }
        }

        // Registering code
        if (blockObject.get("code") != null) {
            Map<String, String> code = gson.fromJson(blockObject.get("code").toString(), new TypeToken<Map<String, String>>() {}.getType());
            for (String codeKey : code.keySet()) {
                Module.getModuleFromName(codeKey).registerBlockCode(block, code.get(codeKey));
            }
        }

        // Adding field manipulators (if applicable)
        if (blockObject.get("fieldManipulator") != null) {
            Map<String, Map<String, Map<String, String>>> fieldManipulator = gson.fromJson(blockObject.get("fieldManipulator").toString(), new TypeToken<Map<String, Map<String, Map<String, String>>>>() {
            }.getType());
            for (String moduleKey : fieldManipulator.keySet()) {
                Module.getModuleFromName(moduleKey).manipulateField(block, field -> {
                    for (String fieldKey : fieldManipulator.get(moduleKey).keySet()) {
                        if (field.getKey().equalsIgnoreCase(fieldKey)) {
                            for (String fieldValue : fieldManipulator.get(moduleKey).get(fieldKey).keySet()) {
                                if (field.getValue().equalsIgnoreCase(fieldValue)) {
                                    return fieldManipulator.get(moduleKey).get(fieldKey).get(fieldValue);
                                }
                            }
                            break;
                        }
                    }
                    return field.getValue();
                });
            }
        }

        // Does block escape syntax?
        if (blockObject.get("escape") != null) {
            Map<String, Boolean> escape = gson.fromJson(blockObject.get("escape").toString(), new TypeToken<Map<String, Boolean>>() {}.getType());
            for (String module : escape.keySet()) {
                if (escape.get(module))
                    Module.getModuleFromName(module).escapeSyntax(block);
            }
        }

        // Registering block object
        registerBlock(block, category);
    }

    /**
     * Registers a block object, unless it already exists <code>!BLOCKS.get(category).contains(bit)</code>.
     * Maps block with category
     *
     * @see Blockly#registerBlock(File)
     * @see Blockly#getBlockFromType(String)
     * @see BlocklyPluginLoader#loadBlocksFromDirectory(File)
     *
     * @param block  Block to register
     * @param category  Category to map
     */
    public static void registerBlock(Block block, Category category) {
        // Checking for category and block
        if (!BLOCKS.containsKey(category)) {
            BLOCKS.put(category, new ArrayList<>());
        } else if (BLOCKS.get(category).contains(block)) {
            return;
        }

        // Mapping category to block
        BLOCKS.get(category).add(block);

        // Log
        if (block.isCustom())
            System.out.println("[BLOCKLY] Registered block: " + block.getType() + ".");
    }

    /**
     * Get html of blocks
     *
     * @return HTML of blocks
     */
    public static String parseBlocksToJsonArray() {
        return parseBlocksToJsonArray(null);
    }

    /**
     * Get html of blocks
     *
     * if extensions is null, no runtime options will be added
     *
     * @return HTML of blocks
     */
    public static String parseBlocksToJsonArray(Map<String, String[][]> extensions) {
        JsonArray array = new JsonArray();
        for (List<Block> blocks : BLOCKS.values()) {
            for (Block block : blocks) {
                if (block.isCustom()) {
                    JsonObject blockObject = block.getBlocklyJson().deepCopy();
                    blockObject.add("type", new JsonPrimitive(block.getType()));

                    // Checking if block has arguments
                    if (blockObject.get("args0") != null) {
                        JsonArray inputs = blockObject.get("args0").getAsJsonArray();

                        // Iterating arguments
                        for (int i = 0; i < inputs.size(); i++) {
                            JsonObject inputObject = (JsonObject) inputs.get(i);

                            // Checking for runtime extension fields
                            if (inputObject.get("extend") != null && extensions != null) {
                                for (String extension : extensions.keySet()) {
                                    if (extension.equalsIgnoreCase(inputObject.get("extend").getAsString())) {

                                        // Adding new options
                                        List<String[]> newOptions = new ArrayList<>();
                                        for (String[] option : extensions.get(extension)) {
                                            newOptions.add(option);
                                        }
                                        if (inputObject.get("options") != null) {
                                            String[][] options = gson.fromJson(inputObject.get("options"), new TypeToken<String[][]>() {}.getType());
                                            for (String[] option : options) {
                                                newOptions.add(option);
                                            }
                                            inputObject.remove("options");
                                        }
                                        JsonArray options = new JsonArray();
                                        for (String[] option : newOptions) {
                                            JsonArray optionArray = new JsonArray();
                                            for (String element : option) {
                                                optionArray.add(element);
                                            }
                                            options.add(optionArray);
                                        }
                                        inputObject.add("options", options);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    array.add(blockObject);
                }
            }
        }
        return array.toString();
    }

    /**
     * Returns html of categories
     *
     * @param builder  String builder to append
     * @param categories  Categories to register
     */
    public static void appendCategories(StringBuilder builder, Category... categories) {
        for (Category category : categories) {
            builder.append("    <category name=\"" + category.getName() + "\" colour=\"" + category.getColor() + "\"> \n");
            for (Block block : BLOCKS.get(category)) {
                if (!block.isHidden()) {
                    builder.append("        <block type=\"" + block.getType() + "\"> \n");
                    if (block.getInit() != null) {
                        builder.append("            " + block.getInit() + "\n");
                    }
                    builder.append("        </block> \n");
                }
            }
            builder.append("    </category> \n");
        }
    }

    /**
     * @return All registered blockly categories
     */
    public static Category[] getBlocklyCategories() {
        Category[] categories = new Category[BLOCKS.keySet().size()];
        categories = BLOCKS.keySet().toArray(categories);
        return categories;
    }

    /**
     * @param category  Category of blocks
     * @return All blocks in category
     */
    public static Block[] getBlocksFromCategory(Category category) {
        Block[] blockArray = new Block[BLOCKS.get(category).size()];
        blockArray = BLOCKS.get(category).toArray(blockArray);
        return blockArray;
    }

    /**
     * Obtains a block from type name
     *
     * @see Blockly#registerBlock(Block, Category)
     *
     * @param type Type name of block
     *
     * @return Block object corresponding to type name
     */
    public static Block getBlockFromType(String type) {
        for (Category category : BLOCKS.keySet()) {
            for (Block block : BLOCKS.get(category)) {
                if (block.getType().equalsIgnoreCase(type)) {
                    return block;
                }
            }
        }
        return null;
    }

    /**
     * Obtains a category from type name
     *
     * @see Blockly#registerCategory(Category)
     *
     * @param type Type name of category
     *
     * @return Category object corresponding to type name
     */
    public static Category getCategoryFromType(String type) {
        for (Category category : BLOCKS.keySet()) {
            if (category.getType().equalsIgnoreCase(type)) {
                return category;
            }
        }
        return null;
    }

    /**
     * Register default Flow Control blocks
     */
    public static void queueFlowControl() {
        registerCategory("flow_control", BlocklyHelper.getCategoryJson("flow_control"));
        Category flow = getCategoryFromType("flow_control");
        Block ifBlock = new Block("controls_if");
        Blockly.registerBlock(ifBlock, flow);
        JAVA.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
        JAVA.escapeSyntax(ifBlock);
        JAVA.attachMutator(ifBlock, (mutation, index) -> {
            if (mutation.equalsIgnoreCase("elseif")) {
                return "else if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
            } else if (mutation.equalsIgnoreCase("else")) {
                return "else {\n$[statement%ELSE]\n}";
            }
            return null;
        });
        JAVASCRIPT.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
        JAVASCRIPT.escapeSyntax(ifBlock);
        JAVASCRIPT.attachMutator(ifBlock, (mutation, index) -> {
            if (mutation.equalsIgnoreCase("elseif")) {
                return "else if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
            } else if (mutation.equalsIgnoreCase("else")) {
                return "else {\n$[statement%ELSE]\n}";
            }
            return null;
        });

        Block repeatBlock = new Block("controls_repeat_ext");
        Blockly.registerBlock(repeatBlock, flow);
        JAVA.registerBlockCode(repeatBlock, "for (int $[increment%repeat] = 0; $[increment%repeat] < $[value%TIMES]; $[increment%repeat]++) {\n$[statement%DO]\n}");
        JAVA.escapeSyntax(repeatBlock);
        JAVASCRIPT.registerBlockCode(repeatBlock, "var $[increment%repeat]; for ($[increment%repeat] = 0; $[increment%repeat] < $[value%TIMES]; $[increment%repeat]++) {\n$[statement%DO]\n}");
        JAVASCRIPT.escapeSyntax(repeatBlock);
    }

    /**
     * Register default Logic blocks
     */
    public static void queueLogic() {
        registerCategory("logic", BlocklyHelper.getCategoryJson("logic"));
        Category logic = getCategoryFromType("logic");
        Block compareBlock = new Block("logic_compare");
        Blockly.registerBlock(compareBlock, logic);
        JAVA.registerBlockCode(compareBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVA.manipulateField(compareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "==";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!=";
                if (field.getValue().equalsIgnoreCase("LT")) return "<";
                if (field.getValue().equalsIgnoreCase("LTE")) return "<=";
                if (field.getValue().equalsIgnoreCase("GT")) return ">";
                if (field.getValue().equalsIgnoreCase("GTE")) return ">=";
            }
            return field.getValue();
        });
        JAVA.escapeSyntax(compareBlock);
        JAVASCRIPT.registerBlockCode(compareBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVASCRIPT.manipulateField(compareBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("EQ")) return "===";
                if (field.getValue().equalsIgnoreCase("NEQ")) return "!==";
                if (field.getValue().equalsIgnoreCase("LT")) return "<";
                if (field.getValue().equalsIgnoreCase("LTE")) return "<=";
                if (field.getValue().equalsIgnoreCase("GT")) return ">";
                if (field.getValue().equalsIgnoreCase("GTE")) return ">=";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(compareBlock);

        Block negateBlock = new Block("logic_negate");
        Blockly.registerBlock(negateBlock, logic);
        JAVA.registerBlockCode(negateBlock, "!($[value%BOOL])");
        JAVA.escapeSyntax(negateBlock);
        JAVASCRIPT.registerBlockCode(negateBlock, "!($[value%BOOL])");
        JAVASCRIPT.escapeSyntax(negateBlock);

        Block booleanBlock = new Block("logic_boolean");
        Blockly.registerBlock(booleanBlock, logic);
        JAVA.registerBlockCode(booleanBlock, "$[field%BOOL]");
        JAVA.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
        JAVA.escapeSyntax(booleanBlock);
        JAVASCRIPT.registerBlockCode(booleanBlock, "$[field%BOOL]");
        JAVASCRIPT.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
        JAVASCRIPT.escapeSyntax(booleanBlock);
    }

    /**
     * Register default Math blocks
     */
    public static void queueMath() {
        registerCategory("math", BlocklyHelper.getCategoryJson("math"));
        Category math = getCategoryFromType("math");
        Block numberBlock = new Block("math_number");
        Blockly.registerBlock(numberBlock, math);
        JAVA.registerBlockCode(numberBlock, "$[field%NUM]");
        JAVA.escapeSyntax(numberBlock);
        JAVASCRIPT.registerBlockCode(numberBlock, "$[field%NUM]");
        JAVASCRIPT.escapeSyntax(numberBlock);

        Blockly.registerBlock("math_arithmetic_custom", gson.fromJson(BlocklyHelper.getBlockJson("math_arithmetic_custom"), JsonObject.class), math);
        Block arithmeticBlock = Blockly.getBlockFromType("math_arithmetic_custom");
        JAVA.registerBlockCode(arithmeticBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVA.manipulateField(arithmeticBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("ADD")) return "+";
                if (field.getValue().equalsIgnoreCase("MINUS")) return "-";
                if (field.getValue().equalsIgnoreCase("MULTIPLY")) return "*";
                if (field.getValue().equalsIgnoreCase("DIVIDE")) return "/";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(arithmeticBlock);
        JAVASCRIPT.registerBlockCode(arithmeticBlock, "($[value%A] $[field%OP] $[value%B])");
        JAVASCRIPT.manipulateField(arithmeticBlock, field -> {
            if (field.getKey().equalsIgnoreCase("OP")) {
                if (field.getValue().equalsIgnoreCase("ADD")) return "+";
                if (field.getValue().equalsIgnoreCase("MINUS")) return "-";
                if (field.getValue().equalsIgnoreCase("MULTIPLY")) return "*";
                if (field.getValue().equalsIgnoreCase("DIVIDE")) return "/";
            }
            return field.getValue();
        });
        JAVASCRIPT.escapeSyntax(arithmeticBlock);
    }

    /**
     * Register default Text blocks
     */
    public static void queueText() {
        registerCategory("text", BlocklyHelper.getCategoryJson("text"));
        Category text = getCategoryFromType("text");
        Block textBlock = new Block("text");
        Blockly.registerBlock(textBlock, text);
        JAVA.registerBlockCode(textBlock, "\"$[field%TEXT]\"");
        JAVA.escapeSyntax(textBlock);
        JAVASCRIPT.registerBlockCode(textBlock, "'$[field%TEXT]'");
        JAVASCRIPT.escapeSyntax(textBlock);

        Block printBlock = new Block("text_print");
        Blockly.registerBlock(printBlock, text);
        JAVA.registerBlockCode(printBlock, "System.out.println($[value%TEXT])");
        JAVASCRIPT.registerBlockCode(printBlock, "console.log($[value%TEXT])");

        Blockly.registerBlock("string_length", gson.fromJson(BlocklyHelper.getBlockJson("string_length"), JsonObject.class), text);
        Blockly.registerBlock("text_arithmetic", gson.fromJson(BlocklyHelper.getBlockJson("text_arithmetic"), JsonObject.class), text);
        Blockly.registerBlock("text_substring", gson.fromJson(BlocklyHelper.getBlockJson("text_substring"), JsonObject.class), text);
    }
}
