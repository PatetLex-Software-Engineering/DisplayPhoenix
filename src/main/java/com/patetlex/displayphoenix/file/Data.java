package com.patetlex.displayphoenix.file;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.lang.Local;
import com.patetlex.displayphoenix.ui.ColorTheme;
import com.patetlex.displayphoenix.util.FileHelper;
import com.patetlex.displayphoenix.util.StringHelper;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author TBroski
 */
public class Data {

    private static final Gson gson = new Gson();

    private static boolean created;
    private static Map<String, Object> data;

    private static List<String> markedStorage = new ArrayList<>();

    /**
     * Store a JSON parsable object
     *
     * @param key  Key to object
     * @param value  Object value
     */
    public static void store(String key, Object value) {
        if (value != null)
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

    /**
     * Is key stored
     *
     * @param key  Key to object
     */
    public static boolean has(String key) {
        return data.containsKey(key);
    }

    /**
     * Clear cache
     */
    public static void clearCache() {
        File directory = new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()));
        File data = new File(directory.getPath() + "/data.json");
        data.delete();

        FileHelper.forEachSubFile(new File(directory.getPath() + "\\cache\\"), new FileIteration() {
            @Override
            public void iterate(File file) {
                boolean flag = false;
                for (String filePath : markedStorage) {
                    if (file.getPath().contains(filePath))
                        flag = true;
                }
                if (!flag)
                    file.delete();
            }
        });
        create();
    }

    /**
     * Caches a file
     *
     * @param content  Content of file
     * @param identifier  Identifier of file
     */
    public static File cache(byte[] content, String identifier) {
        try {
            String home = System.getProperty("user.home");
            if (home != null) {
                File dir = new File(home + "/." + StringHelper.id(Application.getTitle()));
                dir.mkdir();
                dir.createNewFile();
                File cacheDir = new File(dir.getPath() + "/cache");
                cacheDir.mkdir();
                cacheDir.createNewFile();
                File file = new File(home + "/." + StringHelper.id(Application.getTitle()) + "/cache/" + identifier);
                if (content == null)
                    file.mkdir();
                file.createNewFile();
                if (content != null) {
                    OutputStream writer = new FileOutputStream(file);
                    writer.write(content);
                    writer.flush();
                    writer.close();
                }
                return file;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static File cache(File file) {
        try {
            return cache(Files.readAllBytes(file.toPath()), new DetailedFile(file).getFileName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean delete(String identifier) {
        File file = new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()) + "\\cache\\" + identifier);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * Files marked as storage will NOT be cleared as cache. See Data#clearCache()
     *
     * @param filePath  Path to file/folder
     */
    public static void markPathAsStorage(String filePath) {
        markedStorage.add(filePath);
    }

    /**
     * Finds a file
     *
     * @param identifier  Identifier of file
     *
     * @return  File from name
     */
    public static File find(String identifier) {
        return new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()) + "/cache/" + identifier);
    }

    /**
     * Iterates every cached file
     *
     * @param fileIterator  Iterator interface
     */
    public static void forCachedFile(FileIteration fileIterator) {
        forCachedFile("", fileIterator);
    }

    /**
     * Iterates every cached file in relative path
     *
     * @param extraPath  Relative path
     * @param fileIterator  Iterator interface
     */
    public static void forCachedFile(String extraPath, FileIteration fileIterator) {
        FileHelper.forEachSubFile(new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()) + "/cache/" + extraPath), fileIterator);
    }

    public static int create() {
        created = true;
        try {
            File dir = new File(System.getProperty("user.home") + "/." + StringHelper.id(Application.getTitle()));
            dir.mkdir();
            dir.createNewFile();
            File cacheDir = new File(dir.getPath() + "/cache/");
            cacheDir.mkdir();
            cacheDir.createNewFile();
            File dataFile = new File(dir.getPath() + "/data.json");
            dataFile.createNewFile();
            Map<String, DataObject> dataObjectMap = gson.fromJson(FileHelper.readAllLines(dataFile), new TypeToken<Map<String, DataObject>>() {}.getType());
            data = new HashMap<>();
            if (dataObjectMap != null) {
                for (String key : dataObjectMap.keySet()) {
                    try {
                        data.put(key, gson.fromJson(dataObjectMap.get(key).object, Class.forName(dataObjectMap.get(key).objectClass)));
                    } catch (ClassNotFoundException e) {
                    }
                }
            }
            int session = 0;
            if (has("session")) {
                session = (int) get("session");
                if (session < 0) {
                    session = 0;
                }
            }
            if (!has("app_version") || !((String) get("app_version")).equalsIgnoreCase(Application.getVersion())) {
                for (File subFile : dir.listFiles()) {
                    if (!subFile.isDirectory()) {
                        subFile.delete();
                    }
                }
                FileHelper.forEachSubFile(cacheDir, new FileIteration() {
                    @Override
                    public void iterate(File file) {
                        file.delete();
                    }
                });
                store("app_version", Application.getVersion());
                Application.restart();
                return session;
            }
            Local local = (Local) get("local");
            Application.setLocal(local != null ? local : Local.EN_US);
            String prevDir = (String) get("prev_dir");
            if (prevDir != null) {
                FileDialog.PREVIOUS_DIRECTORY = new File(prevDir);
            }
            ColorTheme theme = (ColorTheme) get("color_theme");
            if (theme != null) {
                Application.switchTheme(theme);
            }
            return session;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void save() {
        try {
            store("local", Application.getSelectedLocal());
            store("prev_dir", FileDialog.PREVIOUS_DIRECTORY.getPath());
            store("color_theme", Application.getTheme().getColorTheme());
            store("session", Application.getSession() + 1);
            store("app_version", Application.getVersion());
            String home = System.getProperty("user.home");
            if (home != null) {
                File dir = new File(home + "/." + StringHelper.id(Application.getTitle()));
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
            }
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
