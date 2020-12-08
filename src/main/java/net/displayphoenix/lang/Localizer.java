package net.displayphoenix.lang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.Application;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TBroski
 */
public class Localizer {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Map<Local, Map<String, String>> TRANSLATED_VALUES_LOCAL = new HashMap<>();

    public static String translate(String key, Object... arguments) {
        return translate(key, Application.getSelectedLocal(), arguments);
    }

    public static String translate(String key, Local local, Object... arguments) {
        String translatedText = TRANSLATED_VALUES_LOCAL.get(local).getOrDefault(key, key);
        for (int i = 1; i < arguments.length; i++) {
            translatedText = translatedText.replaceAll("%" + i, String.valueOf(arguments[i]));
        }
        return translatedText;
    }

    public static void create() {
        loadValues();
        loadDefaultValues();
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
        for (Local local : Local.values()) {
            InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream("lang/" + local.getTag() + ".json");
            if (inputStream == null) {
                File file = new File("src/main/resources/lang/" + local.getTag() + ".json");
                try {
                    File langDir = new File("src/main/resources/lang");
                    langDir.mkdir();
                    langDir.createNewFile();
                    if (file.createNewFile()) {
                        FileWriter writer = new FileWriter(file);
                        Map<String, String> example = new HashMap<>();
                        example.put("key.example.test", "Example");
                        TRANSLATED_VALUES_LOCAL.put(local, example);

                        String json = gson.toJson(TRANSLATED_VALUES_LOCAL.get(local));

                        writer.write(json);

                        writer.flush();
                        writer.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            loadStream(local, inputStream);
        }
    }

    private static void loadDefaultValues() {
        for (Local local : Local.values()) {
            loadStream(local, ClassLoader.getSystemClassLoader().getResourceAsStream("def_lang/" + local.getTag() + ".json"));
        }
    }

    private static void loadStream(Local local, InputStream inputStream) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            if (TRANSLATED_VALUES_LOCAL.containsKey(local)) {
                Map<String, String> translations = gson.fromJson(output.toString(), new TypeToken<Map<String, String>>() {}.getType());
                for (String translationKey : translations.keySet()) {
                    if (!TRANSLATED_VALUES_LOCAL.get(local).keySet().contains(translationKey)) {
                        TRANSLATED_VALUES_LOCAL.get(local).put(translationKey, translations.get(translationKey));
                    }
                }

                return;
            }
            TRANSLATED_VALUES_LOCAL.put(local, gson.fromJson(output.toString(), new TypeToken<Map<String, String>>() {}.getType()));
            if (TRANSLATED_VALUES_LOCAL.get(local) == null)
                TRANSLATED_VALUES_LOCAL.put(local, new HashMap<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
