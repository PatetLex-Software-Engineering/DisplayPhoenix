package net.displayphoenix.bitly;

import net.displayphoenix.file.DetailedFile;

import java.io.File;
public class BitlyPluginLoader {

    public static void loadBitsFromDirectory(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                loadBitsFromDirectory(subFile);
            }
            else {
                DetailedFile detailedFile = new DetailedFile(subFile);
                Bitly.registerBit(detailedFile.getFile());
            }
        }
    }
}
