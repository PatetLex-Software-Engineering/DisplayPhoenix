package net.displayphoenix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BlocklyHelper {

    public static String getBlockJson(String identifier) {
        try {
            if (!(identifier.endsWith(".json")))
                identifier += ".json";
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("blockly/blocks/" + identifier)));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCategoryJson(String identifier) {
        try {
            if (!(identifier.endsWith(".json")))
                identifier += ".json";
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("blockly/categories/" + identifier)));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
