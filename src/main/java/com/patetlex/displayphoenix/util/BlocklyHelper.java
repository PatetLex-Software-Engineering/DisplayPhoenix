package com.patetlex.displayphoenix.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.blockly.Blockly;
import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import com.patetlex.displayphoenix.generation.Module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.function.Consumer;

public class BlocklyHelper {

    private static final Gson gson = new Gson();

    public static void loadBlockResource(String identifier) {
        loadBlockResource(identifier, identifier);
    }

    public static void loadBlockResource(String identifier, String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("blockly/blocks/" + path + ".json")));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            Blockly.loadBlock(identifier, output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Block getInstance(String type, String json) {
        JsonObject blockObject = gson.fromJson(json, JsonObject.class);

        Block block = new Block(type, blockObject.get("init") != null ? blockObject.get("init").getAsString() : null, blockObject);

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
                Module module = Module.getModuleFromName(codeKey);
                if (module != null) {
                    module.registerBlockCode(block, code.get(codeKey));
                }
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
        return block;
    }

    public static void loadCategoryResource(String identifier) {
        loadCategoryResource(identifier, identifier);
    }

    public static void loadCategoryResource(String identifier, String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("blockly/categories/" + path + ".json")));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            Blockly.loadCategory(identifier, output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void forEachBlock(Consumer<ImplementedBlock> forEachBlock, ImplementedBlock... blocks) {
        for (ImplementedBlock implementedBlock : blocks) {
            forEachBlock.accept(implementedBlock);
            for (String statement : implementedBlock.getStatementBlocks().keySet()) {
                forEachBlock(forEachBlock, implementedBlock.getStatementBlocks().get(statement).toArray(new ImplementedBlock[implementedBlock.getStatementBlocks().get(statement).size()]));
            }
            for (String value : implementedBlock.getValueBlocks().keySet()) {
                forEachBlock(forEachBlock, implementedBlock.getValueBlocks().get(value).toArray(new ImplementedBlock[implementedBlock.getValueBlocks().get(value).size()]));
            }
        }
    }
}
