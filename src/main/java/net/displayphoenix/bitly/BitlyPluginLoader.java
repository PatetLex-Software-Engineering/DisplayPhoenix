package net.displayphoenix.bitly;

import java.io.File;
public class BitlyPluginLoader {

    /**
     * Iterates through all sub-files and registers each as JSON object
     *
     * @see Bitly#registerBit(File)
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

                // Register JSON object
                Bitly.registerBit(subFile);
            }
        }
    }
}
