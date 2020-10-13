package net.displayphoenix.blockly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.file.DetailedFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class BlocklyPluginLoader {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
