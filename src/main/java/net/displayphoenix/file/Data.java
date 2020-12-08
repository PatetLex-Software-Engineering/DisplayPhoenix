package net.displayphoenix.file;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.Application;
import net.displayphoenix.lang.Local;
import net.displayphoenix.util.FileHelper;
import net.displayphoenix.util.StringHelper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author TBroski
 */
public class Data {

    private static final Gson gson = new Gson();

    private static Map<String, Object> data;

    /**
     * Store a JSON parsable object
     *
     * @param key  Key to object
     * @param value  Object value
     */
    public static void store(String key, Object value) {
        data.put(key, value);
    }

    /**
     * Get a saved object
     *
     * @param key  Key to object
     * @return
     */
    public static Object get(String key) {
        return data.get(key);
    }

    /**
     * Removed saved object
     *
     * @param key  Key to object
     */
    public static void remove(String key) {
        data.remove(key);
    }

    public static boolean has(String key) {
        return data.containsKey(key);
    }

    public static void create() {
        try {
            File dir = new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()));
            dir.mkdir();
            dir.createNewFile();
            File dataFile = new File(dir.getPath() + "/data.json");
            dataFile.createNewFile();
            Map<String, DataObject> dataObjectMap = gson.fromJson(FileHelper.readAllLines(dataFile), new TypeToken<Map<String, DataObject>>() {}.getType());
            data = new HashMap<>();
            if (dataObjectMap != null) {
                for (String key : dataObjectMap.keySet()) {
                    data.put(key, gson.fromJson(dataObjectMap.get(key).object, Class.forName(dataObjectMap.get(key).objectClass)));
                }
            }
            Local local = (Local) get("local");
            Application.setLocal(local != null ? local : Local.EN_US);
            String prevDir = (String) get("prev_dir");
            if (prevDir != null) {
                FileDialog.PREVIOUS_DIRECTORY = new File(prevDir);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try {
            store("local", Application.getSelectedLocal());
            store("prev_dir", FileDialog.PREVIOUS_DIRECTORY.getPath());
            File dir = new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()));
            dir.mkdir();
            dir.createNewFile();
            File dataFile = new File(dir.getPath() + "/data.json");
            dataFile.createNewFile();
            Map<String, DataObject> dataObjectMap = new HashMap<>();
            for (String key : data.keySet()) {
                dataObjectMap.put(key, new DataObject(data.get(key)));
            }
            FileWriter writer = new FileWriter(dataFile);
            writer.write(gson.toJson(dataObjectMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class DataObject {
        public String object;
        public String objectClass;

        public DataObject(Object object) {
            this.object = gson.toJson(object);
            this.objectClass = object.getClass().getTypeName();
        }
    }
}
