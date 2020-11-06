package net.displayphoenix.blockly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
     * Iterates through all sub-files and registers each as JSON file
     *
     * @see Blockly#registerBlock(File, Category) 
     *
     * @param directory  Directory of blocks
     */
    public static void loadBlocksFromDirectory(File directory) {
        for (File subFile : directory.listFiles()) {
            if (subFile.isDirectory()) {
                loadBlocksFromDirectory(subFile);
            }
            else {
                try {
                    DetailedFile detailedFile = new DetailedFile(subFile);
                    JsonObject blockObject = gson.fromJson(new FileReader(subFile), JsonObject.class);
                    Blockly.registerBlock(detailedFile.getFile(), Blockly.getCategoryFromType(blockObject.get("category").getAsString()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
