package net.displayphoenix.file;

import java.io.File;

public class DetailedFile {

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
}
