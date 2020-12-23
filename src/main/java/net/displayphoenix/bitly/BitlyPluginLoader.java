package net.displayphoenix.bitly;

import net.displayphoenix.Application;
import net.displayphoenix.file.DetailedFile;

import java.io.File;

public class BitlyPluginLoader {

    /**
     * Iterates through all sub-files and loads each as JSON object
     *
     * @see Bitly#loadBit(File)
     *
     * @param directory  Directory of bits
     */
    public static void loadBitsFromDirectory(File directory) {
        // Iterating all sub files
        for (File subFile : directory.listFiles()) {

            // Checking for directory
            if (subFile.isDirectory()) {

                // Iteraring sub directory
                loadBitsFromDirectory(subFile);
            }
            else {
                // Check if the file is a JSON
                if (subFile.getName().endsWith(".json")) {
                    // Register JSON file
                    Bitly.loadBit(subFile);
                }
            }
        }
        if (Application.isCreated()) {
            Bitly.load();
        }
    }
}
