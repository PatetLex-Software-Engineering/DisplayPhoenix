package net.displayphoenix.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.Application;
import net.displayphoenix.util.StringHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TBroski
 */
public class Localizer {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<Local, Map<String, String>> TRANSLATED_VALUES_LOCAL = new HashMap<>();

    public static String translate(String key, Object... arguments) {
        String translatedText = TRANSLATED_VALUES_LOCAL.get(Application.getSelectedLocal()).getOrDefault(key, key);
        for (int i = 1; i < arguments.length; i++) {
            translatedText = translatedText.replaceAll("%" + i, String.valueOf(arguments[i]));
        }
        return translatedText;
    }

    public static void create() {
        loadValues();
    }

    public static void loadLangFromDirectory(File directory) {
        try {
            for (Local local : Local.values()) {
                File file = new File(directory.getPath() + "/" + local.getTag() + ".json");

                if (!file.createNewFile()) {
                    FileReader reader = new FileReader(file);

                    TRANSLATED_VALUES_LOCAL.put(local, gson.fromJson(reader, new TypeToken<Map<String, String>>() {}.getType()));
                    if (TRANSLATED_VALUES_LOCAL.get(local) == null)
                        TRANSLATED_VALUES_LOCAL.put(local, new HashMap<>());

                    reader.close();
                }
                else {
                    FileWriter writer = new FileWriter(file);
                    Map<String, String> example = new HashMap<>();
                    example.put("key.example.test", "Example");
                    TRANSLATED_VALUES_LOCAL.put(local, example);

                    String json = gson.toJson(TRANSLATED_VALUES_LOCAL.get(local));

                    writer.write(json);

                    writer.flush();
                    writer.close();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadValues() {
        File langDir = new File("lang");
        langDir.mkdir();
        loadLangFromDirectory(langDir);
    }
}
