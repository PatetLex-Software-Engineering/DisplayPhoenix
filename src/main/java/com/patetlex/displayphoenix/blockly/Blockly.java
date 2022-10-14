package com.patetlex.displayphoenix.blockly;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.elements.Category;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.generation.Module;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.util.BlocklyHelper;

import java.io.*;
import java.util.*;

import static com.patetlex.displayphoenix.generation.Module.*;

/**
 * @author TBroski
 */
public class Blockly {

    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static final Map<Category, List<Block>> BLOCKS = new HashMap<>();
    private static List<Runnable> runOnLoad = new ArrayList<>();

    public static void load() {
        BLOCKS.clear();
        BlocklyHelper.loadCategoryResource("flow_control");
        BlocklyHelper.loadBlockResource("event_wrapper");
        Data.cache(null, "/blockly/");
        Data.cache(null, "/blockly/blocks/");
        Data.cache(null, "/blockly/categories/");
        Data.forCachedFile("/blockly/categories/", new FileIteration() {
            @Override
            public void iterate(File file) {
                registerCategory(new DetailedFile(file));
            }
        });
        Data.forCachedFile("/blockly/blocks/", new FileIteration() {
            @Override
            public void iterate(File file) {
                registerBlock(new DetailedFile(file));
            }
        });
        Blockly.getBlockFromType("event_wrapper").hide();
        for (Runnable runnable : runOnLoad) {
            runnable.run();
        }
    }

    /**
     * Parses JSON string, using Gson
     *
     * @param categoryJson JSON string of category
     */
    public static void loadCategory(String type, String categoryJson) {
        Data.cache(null, "/blockly/");
        Data.cache(null, "/blockly/categories/");
        Data.cache(categoryJson.getBytes(), "/blockly/categories/" + type + ".json");
    }

    /**
     * Parses JSON file, using Gson
     *
     * @param categoryJson JSON file of category
     * @see BlocklyPluginLoader#loadCategoriesFromDirectory(File)
     */
    public static void loadCategory(File categoryJson) {
        DetailedFile file = new DetailedFile(categoryJson);
        loadCategory(file.getFileName(), file.read());
    }

    /**
     * Parses JSON string, using Gson
     *
     * @param blockJson JSON string of block
     */
    public static void loadBlock(String type, String blockJson) {
        Data.cache(null, "/blockly/");
        Data.cache(null, "/blockly/blocks/");
        Data.cache(blockJson.getBytes(), "/blockly/blocks/" + type + ".json");
    }

    /**
     * Parses JSON file, using Gson
     *
     * @param blockJson JSON file of block
     * @see BlocklyPluginLoader#loadBlocksFromDirectory(File)
     */
    public static void loadBlock(File blockJson) {
        DetailedFile file = new DetailedFile(blockJson);
        loadBlock(file.getFileName(), file.read());
    }

/*    *//**
     * Unregisters a block from Blockly
     *
     * @param type Block type to remove
     *//*
    public static void unregisterBlock(String type) {
        Category categoryToRemoveFrom = null;
        Block blockToRemoveFrom = null;
        for (Category category : BLOCKS.keySet()) {
            boolean flag = false;
            for (Block block : BLOCKS.get(category)) {
                if (block.getType().equalsIgnoreCase(type)) {
                    categoryToRemoveFrom = category;
                    blockToRemoveFrom = block;
                    flag = true;
                    break;
                }
            }
            if (flag)
                break;
        }
        if (blockToRemoveFrom != null) {
            BLOCKS.get(categoryToRemoveFrom).remove(blockToRemoveFrom);
        }
    }*/

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
     * <p>
     * if extensions is null, no runtime options will be added
     *
     * @return HTML of blocks
     */
    public static String parseBlocksToJsonArray(Map<String, String[][]> extensions, Block... extra) {
        JsonArray array = new JsonArray();
        for (List<Block> blocks : BLOCKS.values()) {
            for (Block block : blocks) {
                if (block.isCustom()) {
                    array.add(getBlocklyObject(block, extensions));
                }
            }
            for (Block block : extra) {
                array.add(getBlocklyObject(block, extensions));
            }
        }
        return array.toString();
    }

    public static JsonObject getBlocklyObject(Block block, Map<String, String[][]> extensions) {
        JsonObject blockObject = block.getBlocklyJson().deepCopy();
        if (!blockObject.has("type"))
            blockObject.add("type", new JsonPrimitive(block.getType()));
        String translatedText = Localizer.translate("blockly.block." + block.getType() + ".text");
        if (!translatedText.equalsIgnoreCase("blockly.block." + block.getType() + ".text")) {
            blockObject.remove("message0");
            blockObject.add("message0", new JsonPrimitive(translatedText));
        }

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
                                String[][] options = gson.fromJson(inputObject.get("options"), new TypeToken<String[][]>() {
                                }.getType());
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
        return blockObject;
    }

    /**
     * Returns html of categories
     *
     * @param builder    String builder to append
     * @param categories Categories to register
     */
    public static void appendCategories(StringBuilder builder, Category... categories) {
        for (Category category : categories) {
            boolean flag = false;
            for (Block block : BLOCKS.get(category)) {
                if (!block.isHidden()) {
                    flag = true;
                }
            }
            if (flag) {
                builder.append("    <category name=\"" + Localizer.translate("blockly.category." + category.getType() + ".text") + "\" colour=\"" + category.getColor() + "\"> \n");
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
     * @param category Category of blocks
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
     * @param type Type name of block
     * @return Block object corresponding to type name
     * @see Blockly#loadBlock(String, String)
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
     * @param type Type name of category
     * @return Category object corresponding to type name
     * @see Blockly#loadCategory(String, String)
     */
    public static Category getCategoryFromType(String type) {
        for (Category category : BLOCKS.keySet()) {
            if (category.getType().equalsIgnoreCase(type)) {
                return category;
            }
        }
        return null;
    }

    private static void registerBlock(DetailedFile file) {
        JsonObject blockObject = file.readAsJson();

        // Registering block
        Category category = getCategoryFromType(blockObject.get("category").getAsString());

        // Creating block object
        Block block = new Block(file.getFileName(), blockObject.get("init") != null ? blockObject.get("init").getAsString() : null, blockObject);

        // Adding dependencies (if applicable)
        if (blockObject.get("dependencies") != null) {
            JsonArray dependenciesArray = blockObject.get("dependencies").getAsJsonArray();
            String[] dependencies = new String[dependenciesArray.size()];
            for (int i = 0; i < dependenciesArray.size(); i++) {
                dependencies[i] = dependenciesArray.get(i).getAsString();
            }
            block.setLocalDependencies(dependencies);
        }

        // Adding statement dependencies (if applicable)
        if (blockObject.get("statement_dependencies") != null) {
            String[] dependencies = gson.fromJson(blockObject.get("statement_dependencies"), new TypeToken<String[]>() {
            }.getType());
            block.addStatementDependencies(dependencies);
        }

        // Adding provisions (if applicable)
        if (blockObject.get("provisions") != null) {
            JsonArray provisionsArray = blockObject.get("provisions").getAsJsonArray();
            String[] provisions = new String[provisionsArray.size()];
            for (int i = 0; i < provisionsArray.size(); i++) {
                provisions[i] = provisionsArray.get(i).getAsString();
            }
            block.setLocalProvisions(provisions);
        }

        // Registering code
        if (blockObject.get("code") != null) {
            Map<String, String> code = gson.fromJson(blockObject.get("code").toString(), new TypeToken<Map<String, String>>() {
            }.getType());
            for (String codeKey : code.keySet()) {
                Module.getModuleFromName(codeKey).registerBlockCode(block, code.get(codeKey));
            }
        }

        // Adding field manipulators (if applicable)
        if (blockObject.get("field_manipulator") != null) {
            Map<String, Map<String, Map<String, String>>> fieldManipulator = gson.fromJson(blockObject.get("field_manipulator").toString(), new TypeToken<Map<String, Map<String, Map<String, String>>>>() {
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

            // Escaping modules marked
            Map<String, Boolean> escape = gson.fromJson(blockObject.get("escape").toString(), new TypeToken<Map<String, Boolean>>() {
            }.getType());
            for (String module : escape.keySet()) {
                if (escape.get(module))
                    Module.getModuleFromName(module).escapeSyntax(block);
            }
        }

        // Does block have args?
        if (blockObject.get("args0") != null) {
            JsonArray inputs = blockObject.get("args0").getAsJsonArray();

            // Iterating inputs
            for (int i = 0; i < inputs.size(); i++) {
                JsonObject inputObject = (JsonObject) inputs.get(i);

                if (inputObject.get("type") != null) {

                    // Is arg a field dropdown?
                    if (inputObject.get("type").getAsString().equalsIgnoreCase("field_dropdown")) {
                        if (inputObject.get("dependencies") != null) {
                            Map<String, String[]> dependencies = gson.fromJson(inputObject.get("dependencies"), new TypeToken<Map<String, String[]>>() {
                            }.getType());
                            block.addFieldDependencies(inputObject.get("name").getAsString(), dependencies);
                        }
                        if (inputObject.get("provisions") != null) {
                            Map<String, String[]> provisions = gson.fromJson(inputObject.get("provisions"), new TypeToken<Map<String, String[]>>() {
                            }.getType());
                            block.addFieldProvisions(inputObject.get("name").getAsString(), provisions);
                        }
                    }

                    // Is arg a statement?
                    else if (inputObject.get("type").getAsString().equalsIgnoreCase("input_statement")) {
                        if (inputObject.get("provisions") != null) {
                            String[] provisions = gson.fromJson(inputObject.get("provisions"), new TypeToken<String[]>() {
                            }.getType());
                            block.addStatementProvisions(inputObject.get("name").getAsString(), provisions);
                        }
                    }
                }
            }
        }

        // Registering block object
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

    private static void registerCategory(DetailedFile file) {
        File categoryJson = file.getFile();
        JsonObject categoryObject = null;
        try {
            // Parsing block object
            categoryObject = gson.fromJson(new FileReader(categoryJson), JsonObject.class);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        JsonObject finalCategoryObject = categoryObject;
        Category category = new Category() {
            @Override
            public String getType() {
                return new DetailedFile(categoryJson).getFileName();
            }

            @Override
            public String getColor() {
                return finalCategoryObject.get("color").getAsString();
            }
        };

        if (!BLOCKS.containsKey(category) && getCategoryFromType(category.getType()) == null) {
            BLOCKS.put(category, new ArrayList<>());
        }
    }

    /**
     * Register default Flow Control blocks
     */
    public static void queueFlowControl() {
        BlocklyHelper.loadCategoryResource("flow_control");
        runOnLoad.add(() -> {
            Category flow = getCategoryFromType("flow_control");
            Block ifBlock = new Block("controls_if");
            BLOCKS.get(flow).add(ifBlock);
            JAVA.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
            JAVA.escapeSyntax(ifBlock);
            JAVA.attachMutator(ifBlock, (mutation, index) -> {
                System.out.println(mutation);
                if (mutation.equalsIgnoreCase("elseif")) {
                    return "\nelse if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
                } else if (mutation.equalsIgnoreCase("else")) {
                    return "\nelse {\n$[statement%ELSE]\n}";
                }
                return null;
            });
            JAVASCRIPT.registerBlockCode(ifBlock, "if ($[value%IF0]) {\n$[statement%DO0]\n}");
            JAVASCRIPT.escapeSyntax(ifBlock);
            JAVASCRIPT.attachMutator(ifBlock, (mutation, index) -> {
                if (mutation.equalsIgnoreCase("elseif")) {
                    return "\nelse if ($[value%IF" + index + "]) {\n$[statement%DO" + index + "]\n}";
                } else if (mutation.equalsIgnoreCase("else")) {
                    return "\nelse {\n$[statement%ELSE]\n}";
                }
                return null;
            });
            LUA.registerBlockCode(ifBlock, "if ($[value%IF0]) then \n$[statement%DO0]");
            LUA.escapeSyntax(ifBlock);
            LUA.attachMutator(ifBlock, (mutation, index) -> {
                if (mutation.equalsIgnoreCase("elseif")) {
                    return "\nelseif ($[value%IF" + index + "]) then \n$[statement%DO" + index + "]";
                } else if (mutation.equalsIgnoreCase("else")) {
                    return "\nelse \n$[statement%ELSE]";
                }
                return null;
            });
        });

        //Blockly.registerBlock("repeat", gson.fromJson(BlocklyHelper.getBlockJson("repeat"), JsonObject.class), flow);
        BlocklyHelper.loadBlockResource("repeat");
    }

    /**
     * Register default Logic blocks
     */
    public static void queueLogic() {
        //registerCategory("logic", BlocklyHelper.getCategoryJson("logic"));
        BlocklyHelper.loadCategoryResource("logic");
        runOnLoad.add(() -> {
            Category logic = getCategoryFromType("logic");
            Block compareBlock = new Block("logic_compare");
            BLOCKS.get(logic).add(compareBlock);
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
            LUA.registerBlockCode(compareBlock, "($[value%A] $[field%OP] $[value%B])");
            LUA.manipulateField(compareBlock, field -> {
                if (field.getKey().equalsIgnoreCase("OP")) {
                    if (field.getValue().equalsIgnoreCase("EQ")) return "==";
                    if (field.getValue().equalsIgnoreCase("NEQ")) return "~=";
                    if (field.getValue().equalsIgnoreCase("LT")) return "<";
                    if (field.getValue().equalsIgnoreCase("LTE")) return "<=";
                    if (field.getValue().equalsIgnoreCase("GT")) return ">";
                    if (field.getValue().equalsIgnoreCase("GTE")) return ">=";
                }
                return field.getValue();
            });
            LUA.escapeSyntax(compareBlock);

            Block booleanBlock = new Block("logic_boolean");
            BLOCKS.get(logic).add(booleanBlock);
            JAVA.registerBlockCode(booleanBlock, "$[field%BOOL]");
            JAVA.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
            JAVA.escapeSyntax(booleanBlock);
            JAVASCRIPT.registerBlockCode(booleanBlock, "$[field%BOOL]");
            JAVASCRIPT.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
            JAVASCRIPT.escapeSyntax(booleanBlock);
            LUA.registerBlockCode(booleanBlock, "$[field%BOOL]");
            LUA.manipulateField(booleanBlock, field -> field.getValue().toLowerCase());
            LUA.escapeSyntax(booleanBlock);
        });

        //Blockly.registerBlock("negate_logic", gson.fromJson(BlocklyHelper.getBlockJson("negate_logic"), JsonObject.class), logic);
        //Blockly.registerBlock("null", gson.fromJson(BlocklyHelper.getBlockJson("null"), JsonObject.class), logic);
        BlocklyHelper.loadBlockResource("negate_logic");
        BlocklyHelper.loadBlockResource("null");
    }

    /**
     * Register default Math blocks
     */
    public static void queueMath() {
        //registerCategory("math", BlocklyHelper.getCategoryJson("math"));
        BlocklyHelper.loadCategoryResource("math");
        runOnLoad.add(() -> {
            Category math = getCategoryFromType("math");
            Block numberBlock = new Block("math_number");
            BLOCKS.get(math).add(numberBlock);
            JAVA.registerBlockCode(numberBlock, "$[field%NUM]");
            JAVA.escapeSyntax(numberBlock);
            JAVASCRIPT.registerBlockCode(numberBlock, "$[field%NUM]");
            JAVASCRIPT.escapeSyntax(numberBlock);
            LUA.registerBlockCode(numberBlock, "$[field%NUM]");
            LUA.escapeSyntax(numberBlock);

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
            JAVA.escapeSyntax(arithmeticBlock);
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
            LUA.registerBlockCode(arithmeticBlock, "($[value%A] $[field%OP] $[value%B])");
            LUA.manipulateField(arithmeticBlock, field -> {
                if (field.getKey().equalsIgnoreCase("OP")) {
                    if (field.getValue().equalsIgnoreCase("ADD")) return "+";
                    if (field.getValue().equalsIgnoreCase("MINUS")) return "-";
                    if (field.getValue().equalsIgnoreCase("MULTIPLY")) return "*";
                    if (field.getValue().equalsIgnoreCase("DIVIDE")) return "/";
                }
                return field.getValue();
            });
        });

        //Blockly.registerBlock("math_arithmetic_custom", gson.fromJson(BlocklyHelper.getBlockJson("math_arithmetic_custom"), JsonObject.class), math);
        BlocklyHelper.loadBlockResource("math_arithmetic_custom");
    }

    /**
     * Register default Text blocks
     */
    public static void queueText() {
        //registerCategory("text", BlocklyHelper.getCategoryJson("text"));
        BlocklyHelper.loadCategoryResource("text");
        runOnLoad.add(() -> {
            Category text = getCategoryFromType("text");
            Block textBlock = new Block("text");
            BLOCKS.get(text).add(textBlock);
            JAVA.registerBlockCode(textBlock, "\"$[field%TEXT]\"");
            JAVA.escapeSyntax(textBlock);
            JAVASCRIPT.registerBlockCode(textBlock, "'$[field%TEXT]'");
            JAVASCRIPT.escapeSyntax(textBlock);
            LUA.registerBlockCode(textBlock, "\"$[field%TEXT]\"");
            LUA.escapeSyntax(textBlock);
            JAVASCRIPT.manipulateField(textBlock, field -> {
                if (field.getKey().equalsIgnoreCase("TEXT")) {
                    return field.getValue().replaceAll("'", "\\\\'");
                }
                return null;
            });
            JAVA.manipulateField(textBlock, field -> {
                if (field.getKey().equalsIgnoreCase("TEXT")) {
                    return field.getValue().replaceAll("\"", "\\\"").replaceAll("'", "\\\\'");
                }
                return null;
            });
            LUA.manipulateField(textBlock, field -> {
                if (field.getKey().equalsIgnoreCase("TEXT")) {
                    return field.getValue().replace("\"", "\\\"").replaceAll("'", "\\\\'");
                }
                return null;
            });
        });

        //Blockly.registerBlock("print_text", gson.fromJson(BlocklyHelper.getBlockJson("print_text"), JsonObject.class), text);
        //Blockly.registerBlock("string_length", gson.fromJson(BlocklyHelper.getBlockJson("string_length"), JsonObject.class), text);
        //Blockly.registerBlock("text_arithmetic", gson.fromJson(BlocklyHelper.getBlockJson("text_arithmetic"), JsonObject.class), text);
        //Blockly.registerBlock("text_substring", gson.fromJson(BlocklyHelper.getBlockJson("text_substring"), JsonObject.class), text);
        BlocklyHelper.loadBlockResource("print_text");
        BlocklyHelper.loadBlockResource("string_length");
        BlocklyHelper.loadBlockResource("text_arithmetic");
        BlocklyHelper.loadBlockResource("text_substring");
    }
}
