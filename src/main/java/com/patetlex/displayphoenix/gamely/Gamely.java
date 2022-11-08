package com.patetlex.displayphoenix.gamely;

import com.google.gson.Gson;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.gamely.engine.GameEngine;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class Gamely {

    private static final Map<String, Save> GAME_SAVES = new HashMap<>();

    public static void load() {
        GAME_SAVES.clear();
        Data.markPathAsStorage("/gamely/saves/");
        Data.cache(null, "/gamely/");
        Data.cache(null, "/gamely/saves/");
        Data.forCachedFile("/gamely/saves/", new FileIteration() {
            @Override
            public void iterate(File file) {
                if (file.isFile()) {
                    try {
                        FileInputStream s = new FileInputStream(file);
                        byte[] b = FileHelper.readAllBytesFromStream(s);
                        s.close();
                        GAME_SAVES.put(new DetailedFile(file).getFileName(), Save.read(b));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static Map<String, Save> getGameSaves() {
        return Collections.unmodifiableMap(GAME_SAVES);
    }

    /**
     * @param name  Identifier of game save
     *
     * @return False if save is not found
     */
    public static boolean removeSave(String name) {
        return Data.delete("/gamely/saves/" + name + ".save");
    }

    /**
     * Note: To convert GameEngine to Gamely.Save use GameEngine#write()
     *
     * @param name  Identifier of game save
     * @param gameSave  Save of game
     */
    public static void saveGame(String name, Save gameSave) {
        Data.cache(gameSave.write(), "/gamely/saves/" + name + ".save");
    }

    public static class Save {

        private static final Gson gson = new Gson();

        private Map<String, Save> attributes = new HashMap<>();
        private Map<String, String> strings = new HashMap<>();
        private Map<String, Float> floats = new HashMap<>();
        private Map<String, Integer> ints = new HashMap<>();
        private Map<String, Boolean> bools = new HashMap<>();

        public void put(String name, Save save) {
            this.attributes.put(name, save);
        }

        public void putString(String name, String save) {
            this.strings.put(name, save);
        }

        public void putFloat(String name, float save) {
            this.floats.put(name, save);
        }

        public void putInteger(String name, int save) {
            this.ints.put(name, save);
        }

        public void putBoolean(String name, boolean save) {
            this.bools.put(name, save);
        }

        public Save get(String name) {
            return this.attributes.get(name);
        }

        public String getString(String name) {
            return this.strings.get(name);
        }

        public float getFloat(String name) {
            Float f = this.floats.get(name);
            if (f == null)
                return 0;
            return f;
        }

        public int getInteger(String name) {
            Integer i = this.ints.get(name);
            if (i == null)
                return 0;
            return i;
        }

        public boolean getBoolean(String name) {
            Boolean b = this.bools.get(name);
            if (b == null)
                return false;
            return b;
        }

        public List<String> keys() {
            List<String> keys = new ArrayList<>();
            for (String s : this.attributes.keySet()) {
                keys.add(s);
            }
            for (String s : this.strings.keySet()) {
                keys.add(s);
            }
            for (String s : this.floats.keySet()) {
                keys.add(s);
            }
            for (String s : this.ints.keySet()) {
                keys.add(s);
            }
            for (String s : this.bools.keySet()) {
                keys.add(s);
            }
            return keys;
        }

        public byte[] write() {
            String json = gson.toJson(this);
            return Base64.getEncoder().encode(json.getBytes(StandardCharsets.UTF_8));
        }

        public static Save read(byte[] bytes) {
            byte[] decode = Base64.getDecoder().decode(bytes);
            return gson.fromJson(new String(decode), Save.class);
        }
    }
}
