package net.displayphoenix.file.indexly;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Indexly {

    private static final Map<String, Icon> extToIcon = new HashMap<>();
    private static Icon fileIcon = null;

    public static JPanel getOpenPanel(Consumer<File> fileConsumer, String... extensions) {
        return null;
    }

    public static void registerExtension(String extension, Icon icon) {
        if (!extension.startsWith(".")) {
            extension = "." + extension;
        }
        extToIcon.put(extension, icon);
    }

    public static Icon getIconOfFile(File file) {
        for (String ext : extToIcon.keySet()) {
            if (file.getName().endsWith(ext)) {
                return extToIcon.get(ext);
            }
        }
        return FileSystemView.getFileSystemView().getSystemIcon(file);
    }
}
