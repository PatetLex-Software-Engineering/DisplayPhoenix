package net.displayphoenix.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BitlyHelper {

    public static String getBitJson(String identifier) {
        try {
            if (!(identifier.endsWith(".json")))
                identifier += ".json";
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("bitly/" + identifier)));

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
