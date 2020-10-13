package net.displayphoenix.file;

import net.displayphoenix.Application;
import net.displayphoenix.exception.AppNotCreatedException;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.util.Locale;

public class FileDialog {

    private static File previousDirectory = new File(System.getProperty("user.home"));

    public static DetailedFile openFile(Window parentWindow, String... extensions) {
        File[] files = getBasicFileDialog(parentWindow, getFileFiltersForStringArray(extensions), true, false);
        if (files != null) {
            return convert(files)[0];
        }
        return null;
    }

    public static DetailedFile[] openFiles(Window parentWindow, String... extensions) {
        File[] files = getBasicFileDialog(parentWindow, getFileFiltersForStringArray(extensions), true, true);
        if (files != null) {
            return convert(files);
        }
        return null;
    }

    public static DetailedFile saveFile(Window parentWindow, String... extensions) {
        File[] files = getBasicFileDialog(parentWindow, getFileFiltersForStringArray(extensions), false, false);
        if (files != null) {
            return convert(files)[0];
        }
        return null;
    }

    public static File getFileDirectory(Window parentWindow) {
        return getWorkspaceDirectorySelectDialog(parentWindow);
    }

    private static File getWorkspaceDirectorySelectDialog(Window f) {
        JFileChooser fc = new JFileChooser();
        fc.setPreferredSize(new Dimension(720, 420));

        fc.setCurrentDirectory(previousDirectory);

        fc.setFileFilter(new FileFilter() {
            @Override public boolean accept(File file) {
                return file.isDirectory();
            }

            @Override public String getDescription() {
                return "Directories";
            }
        });

        fc.setDialogTitle("Select directory for the bot");
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int response = fc.showOpenDialog(f);
        if (response == JFileChooser.APPROVE_OPTION)
            return fc.getSelectedFile();
        return null;
    }

    private static java.io.File[] getBasicFileDialog(Window parent, FileFilter[] filters, boolean open, boolean multiSelect) {
        JFileChooser fileGetter = new JFileChooser();
        if (filters != null) {
            for (FileFilter filter : filters) {
                if (filter != null)
                    fileGetter.addChoosableFileFilter(filter);
            }
        }
        fileGetter.setPreferredSize(new Dimension(720, 420));
        fileGetter.setCurrentDirectory(previousDirectory);
        fileGetter.setMultiSelectionEnabled(multiSelect);
        fileGetter.setAcceptAllFileFilterUsed(false);
        fileGetter.setFileView(new FileView() {
            final FileSystemView fileSystemView = FileSystemView.getFileSystemView();
            @Override
            public Icon getIcon(File f) {
                if (f.getName().endsWith("." + Application.getTitle().toLowerCase())) {
                    return ImageHelper.resize(Application.getIcon(), 16);
                }
                return fileSystemView.getSystemIcon(f);
            }
        });
        int response = open ? fileGetter.showOpenDialog(parent) : fileGetter.showSaveDialog(parent);
        previousDirectory = fileGetter.getCurrentDirectory();
        if (response == JFileChooser.APPROVE_OPTION) {
            if (multiSelect) {
                File[] files = fileGetter.getSelectedFiles();
                if (files != null && files.length > 0)
                    return files;
            } else
                return new File[] { fileGetter.getSelectedFile() };
        }
        return null;
    }

    private static FileFilter[] getFileFiltersForStringArray(String[] filters) {
        FileFilter[] fileFilters = new FileFilter[filters.length];
        int idx = 0;
        for (String extension : filters) {
            extension = extension.toLowerCase(Locale.ENGLISH);

            if (extension.startsWith("."))
                extension = extension.replaceFirst("\\.", "");

            String finalExtension = extension;
            fileFilters[idx] = new FileFilter() {
                @Override public boolean accept(File f) {
                    return f.getName().toLowerCase(Locale.ENGLISH).endsWith("." + finalExtension) || f.isDirectory();
                }

                @Override public String getDescription() {
                    return finalExtension.toUpperCase(Locale.ENGLISH) + " files (*." + finalExtension
                            .toLowerCase(Locale.ENGLISH) + ")";
                }
            };
            idx++;
        }
        return fileFilters;
    }

    private static DetailedFile[] convert(File[] files) {
        if (files != null) {
            DetailedFile[] dFiles = new DetailedFile[files.length];
            for (int i = 0; i < files.length; i++) {
                dFiles[i] = new DetailedFile(files[i]);
            }
            return dFiles;
        }
        return null;
    }
}
