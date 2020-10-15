package net.displayphoenix.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author TBroski
 */
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

    public static String readAllLines(File file) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader(file));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
