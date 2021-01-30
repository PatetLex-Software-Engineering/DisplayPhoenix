package com.patetlex.displayphoenix.blockly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.file.DetailedFile;

import java.io.File;

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
