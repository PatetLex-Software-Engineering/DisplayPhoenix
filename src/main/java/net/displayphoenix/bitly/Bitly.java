package net.displayphoenix.bitly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.generation.Module;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

    /**
     * Registers a bit object, unless it already exists <code>!BITS.contains(bit)</code>.
     * <p>
     * If the bit is a plugin it checks for parent, if none is found it adds to plugin
     * queue.
     *
     * @param bit Bit to register
     * @see Bitly#registerBit(File)
     * @see Bitly#getBitFromType(String)
     * @see BitlyPluginLoader#loadBitsFromDirectory(File)
     */
    public static void registerBit(Bit bit) {
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
    }

    /**
     * Parses a JSON file
     *
     * @param file File to bit
     * @see Bitly#registerBit(String, String)
     */
    public static void registerBit(File file) {
        DetailedFile detailedFile = new DetailedFile(file);
        registerBit(detailedFile.getFileName(), detailedFile.getFileContents());
    }

    /**
     * Parses a JSON string, using GSON
     *
     * @param type Type of bit
     * @param json Json of bit
     * @see Bitly#registerBit(Bit)
     */
    public static void registerBit(String type, String json) {
        // Obtaining JSON object
        JsonObject bitObject = gson.fromJson(json, JsonObject.class);

        ImageIcon icon;
        if (bitObject.get("iconPath") != null) {
            icon = ImageHelper.fromPath(bitObject.get("iconPath").getAsString());
        } else {
            icon = ImageHelper.getImage("bitly/" + type);
        }


        // Creating Bit object
        Bit bit = new Bit(type, bitObject.get("plugin") != null ? bitObject.get("plugin").getAsString() : null, bitObject.get("pluginFlag") != null ? bitObject.get("pluginFlag").getAsString() : null, icon, gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>() {
        }.getType()));

        // Registering Bit object
        registerBit(bit);

        // Registering code
        if (bitObject.get("code") != null) {
            Map<String, String> moduleToCode = gson.fromJson(bitObject.get("code").toString(), new TypeToken<Map<String, String>>() {
            }.getType());
            for (String module : moduleToCode.keySet()) {
                Module.getModuleFromName(module).registerBitCode(bit, moduleToCode.get(module));
            }
        }
        System.out.println("[BITLY] Registered bit: " + type + ".");
    }

    /**
     * Obtains a bit from type name, will not return a plugin
     *
     * @param type Type name of bit
     * @return Bit object corresponding to type name
     * @see Bitly#registerBit(Bit)
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
     * @see Bitly#registerBit(Bit)
     * @see Bitly#registerBit(File)
     * @see BitlyPluginLoader#loadBitsFromDirectory(File)
     */
    public static List<Bit> getRegisteredBits() {
        return Collections.unmodifiableList(BITS);
    }
}
