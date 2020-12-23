package net.displayphoenix.bitly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.file.Data;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.generation.Module;
import net.displayphoenix.interfaces.FileIteration;
import net.displayphoenix.util.FileHelper;
import net.displayphoenix.util.ImageHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    public static void load() {
        Data.cache(null, "/bitly/");
        Data.forCachedFile("/bitly/", new FileIteration() {
            @Override
            public void iterate(File file) {
                if (file.getName().endsWith(".json")) {
                    registerBit(new DetailedFile(file));
                }
            }
        });
    }

    /**
     * Parses a JSON file
     *
     * @param file File to bit
     * @see Bitly#loadBit(String, String, RenderedImage)
     */
    public static void loadBit(File file) {
        DetailedFile detailedFile = new DetailedFile(file);
        try {
            loadBit(detailedFile.getFileName(), detailedFile.getFileContents(), ImageIO.read(new File(file.getParentFile().getPath() + "\\" + detailedFile.getFileName() + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Data.cache(null, "/bitly/elements/");
        JsonObject bitObject = gson.fromJson(FileHelper.readAllLines(file), JsonObject.class);
        List<BitWidget[]> widgets = gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>() {}.getType());
        for (BitWidget[] page : widgets) {
            for (BitWidget widget : page) {
                if (widget.getStyle() == BitWidgetStyle.CANVAS) {
                    for (String fileName : widget.getExternalFiles(false, file.getParentFile()).keySet()) {
                        Data.cache(widget.getExternalFiles(false, file.getParentFile()).get(fileName), "/bitly/elements/" + fileName);
                    }
                }
            }
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

    private static void registerBit(DetailedFile file) {
        String json = file.getFileContents();
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
        }.getType()));

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
