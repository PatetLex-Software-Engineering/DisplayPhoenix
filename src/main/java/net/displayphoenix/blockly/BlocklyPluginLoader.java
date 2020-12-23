package net.displayphoenix.blockly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.displayphoenix.Application;
import net.displayphoenix.bitly.Bitly;
import net.displayphoenix.blockly.elements.Category;
import net.displayphoenix.file.DetailedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author TBroski
 */
public class BlocklyPluginLoader {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Iterates through all sub-files and loads each as JSON file
     *
     * @see Blockly#loadBlock(File)
     *
     * @param directory  Directory of blocks
     */
    public static void loadBlocksFromDirectory(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                loadBlocksFromDirectory(subFile);
            }
            else {
                // Check if file is a JSON
                if (new DetailedFile(subFile).getFileExtension().equalsIgnoreCase("json")) {
                    // Register JSON file
                    Blockly.loadBlock(subFile);
                }
            }
        }
        if (Application.isCreated()) {
            Blockly.load();
        }
    }

    /**
     * Iterates through all sub-files and registers each as JSON file
     *
     * @see Blockly#loadCategory(File) 
     *
     * @param directory  Directory of categories
     */
    public static void loadCategoriesFromDirectory(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                loadBlocksFromDirectory(subFile);
            }
            else {
                Blockly.loadCategory(subFile);
            }
        }
        if (Application.isCreated()) {
            Blockly.load();
        }
    }
}
