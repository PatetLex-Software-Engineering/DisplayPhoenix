package com.patetlex.displayphoenix.file;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.patetlex.displayphoenix.util.FileHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author TBroski
 */
public class DetailedFile {

    private static transient final Gson gson = new Gson();

    private String fileName;
    private File file;
    private String ext;

    public DetailedFile(File file) {
        this.file = file;
        this.ext = getExtensionOfFile(file);
        this.fileName = getNameOfFile(file);
    }

    public File getFile() {
        return file;
    }

    public String getFileExtension() {
        return ext;
    }

    public String getFileName() {
        return fileName;
    }

    public String read() {
        return FileHelper.readAllLines(this.getFile());
    }

    public JsonObject readAsJson() {
        return gson.fromJson(read(), JsonObject.class);
    }

    public void write(String s) {
        write(s.getBytes());
    }

    public void write(byte[] b) {
        try {
            FileOutputStream outputStream = new FileOutputStream(this.getFile());
            outputStream.write(b);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getExtensionOfFile(File file) {
        String ext = "";
        String fileName = file.getName();
        if(fileName.contains(".") && fileName.lastIndexOf(".") != 0) {
            ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        return ext;
    }

    private static String getNameOfFile(File file) {
        String fileName = file.getName();
        if(fileName.contains(".") && fileName.lastIndexOf(".") != 0) {
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        }
        return fileName;
    }

    @Override
    public String toString() {
        return this.getFile().toString();
    }
}
