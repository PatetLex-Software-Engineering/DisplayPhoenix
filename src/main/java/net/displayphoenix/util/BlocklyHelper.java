package net.displayphoenix.util;

import net.displayphoenix.blockly.Blockly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlocklyHelper {

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
}
