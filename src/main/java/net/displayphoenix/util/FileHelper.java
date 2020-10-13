package net.displayphoenix.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileHelper {
    public static void deleteFolder(File directory) {
        for (File subFile : directory.listFiles()) {
            if(subFile.isDirectory()) {
                deleteFolder(subFile);
            }
            else {
                subFile.delete();
            }
        }
        directory.delete();
    }

    public static String readAllLines(URI filePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath), Charset.defaultCharset());

            String out = "";
            for (String line : lines) {
                out += line;
            }
            return out;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
