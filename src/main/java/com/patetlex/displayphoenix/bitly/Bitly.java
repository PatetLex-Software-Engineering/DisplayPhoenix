package com.patetlex.displayphoenix.bitly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.bitly.elements.Bit;
import com.patetlex.displayphoenix.bitly.elements.BitWidgetStyle;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.generation.Module;
import com.patetlex.displayphoenix.interfaces.FileIteration;
import com.patetlex.displayphoenix.util.FileHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author TBroski
 */
public class Bitly {

    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static List<Bit> BITS = new ArrayList<>();
    private static List<Bit> BIT_PLUGINS = new ArrayList<>();
    private static List<BitWidgetStyle> WIDGET_STYLES = new ArrayList<>();

    public static void load() {
        BITS.clear();
        BIT_PLUGINS.clear();
        Data.cache(null, "/bitly/");
        Data.cache(null, "/bitly/elements/");
        Data.forCachedFile("/bitly/", new FileIteration() {
            @Override
            public void iterate(File file) {
                if (file.getName().endsWith(".json")) {
                    registerBit(new DetailedFile(file));
                }
            }
        });
        for (Bit bit : BITS) {
            Object o = new Object() {
                @Override
                public boolean equals(Object obj) {
                    Bit bit1 = (Bit) obj;
                    for (BitWidget[] page : bit1.getBits()) {
                        for (BitWidget widget : page) {
                            Map<String, byte[]> externalFiles = widget.getExternalFiles(bit1.sourceFile() == null, bit1.sourceFile() != null ? bit1.sourceFile().getParentFile() : null);
                            for (String name : externalFiles.keySet()) {
                                Data.cache(externalFiles.get(name), "/bitly/elements/" + name);
                            }
                        }
                    }
                    return false;
                }
            };
            o.equals(bit);
            for (Bit plugin : bit.getPlugins()) {
                o.equals(plugin);
            }
        }
    }

    /**
     * Parses a JSON file
     *
     * @param file File to bit
     * @see Bitly#loadBit(String, String, RenderedImage)
     */
    public static void loadBit(File file) {
        DetailedFile detailedFile = new DetailedFile(file);
        JsonObject bitObject = detailedFile.readAsJson();
        bitObject.addProperty("source_file", file.getPath());
        try {
            loadBit(detailedFile.getFileName(), gson.toJson(bitObject), ImageIO.read(new File(file.getParentFile().getPath() + "\\" + detailedFile.getFileName() + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**s
     * Parses a JSON string, using GSON
     *
     * @param type Type of bit
     * @param json Json of bit
     */
    public static void loadBit(String type, String json, RenderedImage iconImage) {
        Data.cache(null, "/bitly/");
        Data.cache(json.getBytes(), "/bitly/" + type + ".json");
        File png = Data.cache(null, "/bitly/" + type + ".png");
        try {
            ImageIO.write(iconImage, "PNG", png);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtains a bit from type name, will not return a plugin
     *
     * @param type Type name of bit
     * @return Bit object corresponding to type name
     *
     * @see BitlyPluginLoader#loadBitsFromDirectory(File)
     */
    public static Bit getBitFromType(String type) {
        // Iterating registered bits
        for (Bit bit : BITS) {

            // Checking for type name
            if (bit.getType().equalsIgnoreCase(type)) {
                return bit;
            }
        }
        return null;
    }

    public static BitWidgetStyle getStyleFromName(String name) {
        for (BitWidgetStyle style : WIDGET_STYLES) {
            if (style.getName().equalsIgnoreCase(name)) {
                return style;
            }
        }
        return null;
    }

    /**
     * Returns all registered bits, unmodifiable
     * <p>
     * To add a bit
     *
     * @return All registered bits
     * @see Bitly#loadBit(File)
     * @see BitlyPluginLoader#loadBitsFromDirectory(File)
     */
    public static List<Bit> getRegisteredBits() {
        return Collections.unmodifiableList(BITS);
    }

    public static List<BitWidgetStyle> getRegisteredWidgetStyles() {
        return Collections.unmodifiableList(WIDGET_STYLES);
    }

    public static void registerWidgetStyle(BitWidgetStyle style) {
        if (WIDGET_STYLES.contains(style)) {
            WIDGET_STYLES.remove(style);
        }
        WIDGET_STYLES.add(style);
    }

    private static void registerBit(DetailedFile file) {
        String json = file.read();
        String type = file.getFileName();
        ImageIcon icon = null;
        try {
            icon = new ImageIcon(ImageIO.read(new File(file.getFile().getParentFile().getPath() + "/" + type + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Obtaining JSON object
        JsonObject bitObject = gson.fromJson(json, JsonObject.class);

        // Creating Bit object
        Bit bit = new Bit(type, bitObject.get("plugin") != null ? bitObject.get("plugin").getAsString() : null, bitObject.get("pluginFlag") != null ? bitObject.get("pluginFlag").getAsString() : null, icon, gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>() {
        }.getType()), bitObject.get("script") != null ? bitObject.get("script").getAsString() : null, bitObject.get("source_file") != null ? new File(bitObject.get("source_file").getAsString()) : null);

        // Registering code
        if (bitObject.get("code") != null) {
            Map<String, String> moduleToCode = gson.fromJson(bitObject.get("code").toString(), new TypeToken<Map<String, String>>() {
            }.getType());
            for (String module : moduleToCode.keySet()) {
                Module.getModuleFromName(module).registerBitCode(bit, moduleToCode.get(module));
            }
        }

        // Registering bit
        // Checking if bit is a plugin
        if (bit.getPlugin() != null) {

            // Iterating BITS
            for (Bit addedBit : BITS) {

                // Checking if a bit is the parent of the plugin
                if (addedBit.getType().equalsIgnoreCase(bit.getPlugin())) {
                    addedBit.addPlugin(bit);

                    // Exit method, plugin added
                    return;
                }
            }

            // Adding to plugin queue
            BIT_PLUGINS.add(bit);
            return;
        }

        // Checking if bit exists
        if (!BITS.contains(bit)) {
            BITS.add(bit);

            // Checking if it is parent of queued plugins
            for (Bit pluginBit : BIT_PLUGINS) {

                // Checking if the bit is the parent of a plugin
                if (bit.getType().equalsIgnoreCase(pluginBit.getPlugin())) {
                    bit.addPlugin(pluginBit);
                }
            }
        }
        System.out.println("[BITLY] Registered bit: " + type + ".");
    }
}
