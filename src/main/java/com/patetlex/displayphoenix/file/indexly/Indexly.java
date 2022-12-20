package com.patetlex.displayphoenix.file.indexly;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.file.indexly.ui.FileBrowserPanel;
import com.patetlex.displayphoenix.file.indexly.ui.FileOptionsPanel;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.ui.ApplicationFrame;
import com.patetlex.displayphoenix.ui.widget.RoundedButton;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.util.PanelHelper;
import com.patetlex.displayphoenix.util.StringHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class Indexly {

    public static File PREVIOUS_DIRECTORY = new File(System.getProperty("user.home"));
    private static final Map<String, Icon> extToIcon = new HashMap<>();

    public static void openFile(Consumer<DetailedFile> callback, String... extensions) {
        getBasicFileDialog(getFileFiltersForStringArray(extensions), new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) {
                DetailedFile file = convert((File[]) objects[0])[0];
                boolean flag = false;
                if (extensions == null || extensions.length == 0)
                    flag = true;
                for (String ext : extensions) {
                    if (ext.equalsIgnoreCase(file.getFileExtension())) {
                        flag = true;
                    }
                }
                if (flag) {
                    ((ApplicationFrame) objects[1]).dispose();
                    callback.accept(file);
                } else {
                    Application.prompt(Localizer.translate("file.file_dialog.title", "Open"), Localizer.translate("file.file_dialog.message.not_valid_file"), true);
                }
            }
        }, true, false);
    }

    public static void openFiles(Consumer<DetailedFile[]> callback, String... extensions) {
        getBasicFileDialog(getFileFiltersForStringArray(extensions), new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) {
                DetailedFile[] files = convert((File[]) objects[0]);
                boolean flag = true;
                for (DetailedFile detailedFile : files) {
                    boolean accepted = false;
                    if (extensions == null || extensions.length == 0)
                        accepted = true;
                    for (String ext : extensions) {
                        if (ext.equalsIgnoreCase(detailedFile.getFileExtension())) {
                            accepted = true;
                        }
                    }
                    if (!accepted)
                        flag = false;
                }
                if (flag) {
                    ((ApplicationFrame) objects[1]).dispose();
                    callback.accept(files);
                } else {
                    Application.prompt(Localizer.translate("file.file_dialog.title", "Open Multiple"), Localizer.translate("file.file_dialog.message.not_valid_file"), true);
                }
            }
        }, true, true);
    }

    public static void saveFile(Consumer<DetailedFile> callback, String... extensions) {
        getBasicFileDialog(getFileFiltersForStringArray(extensions), new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) {
                DetailedFile saveFile = convert((File[]) objects[0])[0];
                boolean flag = false;
                if (extensions == null || extensions.length == 0)
                    flag = true;
                for (String ext : extensions) {
                    if (ext.equalsIgnoreCase(saveFile.getFileExtension())) {
                        flag = true;
                    }
                }
                if (flag) {
                    ((ApplicationFrame) objects[1]).dispose();
                    callback.accept(saveFile);
                } else {
                    Application.prompt(Localizer.translate("file.file_dialog.title", "Save"), Localizer.translate("file.file_dialog.message.not_valid_file"), true);
                }
            }
        }, false, false);
    }

    public static void getFileDirectory(Consumer<File> callback) {
        getDirectorySelectDialog(new Consumer<Object[]>() {
            @Override
            public void accept(Object[] objects) {
                callback.accept((File) objects[0]);
                ((ApplicationFrame) objects[1]).dispose();
            }
        });
    }

    private static void getDirectorySelectDialog(Consumer<Object[]> callback) {
        if (!PREVIOUS_DIRECTORY.exists())
            PREVIOUS_DIRECTORY = new File(System.getProperty("user.home"));
        FileBrowserPanel browserPanel = new FileBrowserPanel(false, PREVIOUS_DIRECTORY);
        browserPanel.addFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        FileOptionsPanel optionsPanel = new FileOptionsPanel(browserPanel);
        RoundedButton button = new RoundedButton(Localizer.translate("file.open.text"));
        String[] descriptions = new String[]{"Directories Only"};
        JComboBox descriptionBox = new JComboBox(descriptions);
        Application.openWindow(Localizer.translate("file.file_dialog.title", Localizer.translate("file.open.text")), JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
            JPanel concludingOptions = PanelHelper.grid(2, PanelHelper.join(descriptionBox), PanelHelper.join(button));
            parentFrame.add(PanelHelper.northAndCenterElements(optionsPanel, PanelHelper.centerAndSouthElements(browserPanel, concludingOptions)));
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File[] files = browserPanel.getSelectedFiles();
                    if (files == null || files.length == 0) {
                        callback.accept(new Object[]{browserPanel.getCurrentDirectory(), parentFrame});
                        return;
                    }
                    if (!files[0].isDirectory()) {
                        Application.prompt(Localizer.translate("file.file_dialog.title", ""), Localizer.translate("file.file_dialog.message.not_valid_file"), true);
                        return;
                    }
                    callback.accept(new Object[]{files[0], parentFrame});
                }
            });
        });
    }

    private static void getBasicFileDialog(FileFilter[] filters, Consumer<Object[]> callback, boolean open, boolean multiSelect) {
        if (!PREVIOUS_DIRECTORY.exists())
            PREVIOUS_DIRECTORY = new File(System.getProperty("user.home"));
        FileBrowserPanel browserPanel = new FileBrowserPanel(multiSelect, PREVIOUS_DIRECTORY);
        for (FileFilter fileFilter : filters) {
            browserPanel.addFileFilter(fileFilter);
        }
        FileOptionsPanel optionsPanel = new FileOptionsPanel(browserPanel);
        RoundedButton button = new RoundedButton(open ? Localizer.translate("file.open.text") : Localizer.translate("file.save.text"));
        String[] descriptions = new String[filters.length];
        for (int i = 0; i < filters.length; i++) {
            descriptions[i] = filters[i].getDescription();
        }
        JComboBox descriptionBox = new JComboBox(descriptions);
        Application.openWindow(Localizer.translate("file.file_dialog.title", open ? Localizer.translate("file.open.text") : Localizer.translate("file.save.text")), JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
            TextField saveField = null;
            if (open) {
                JPanel concludingOptions = PanelHelper.grid(2, PanelHelper.join(descriptionBox), PanelHelper.join(button));
                parentFrame.add(PanelHelper.northAndCenterElements(optionsPanel, PanelHelper.centerAndSouthElements(browserPanel, concludingOptions)));
            } else {
                saveField = new TextField(filters.length > 0 ? "Example." + getExtensionFromFilter(filters[0]) : "Example");
                optionsPanel.getPathLabel().setText(browserPanel.getCurrentDirectory().getPath() + "\\" + saveField.getText());
                saveField.setPreferredSize(new Dimension(200, 30));
                saveField.setHorizontalAlignment(JTextField.CENTER);
                saveField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        super.keyTyped(e);
                        optionsPanel.getPathLabel().setText(browserPanel.getCurrentDirectory().getPath() + "\\" + ((TextField) e.getComponent()).getText());
                    }
                });
                JPanel concludingOptions = PanelHelper.grid(3, PanelHelper.join(descriptionBox), PanelHelper.join(saveField), PanelHelper.join(button));
                parentFrame.add(PanelHelper.northAndCenterElements(optionsPanel, PanelHelper.centerAndSouthElements(browserPanel, concludingOptions)));
                TextField finalSaveField = saveField;
                descriptionBox.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String[] prefices = finalSaveField.getText().split("\\.");
                        if (prefices.length > 0) {
                            finalSaveField.setText(prefices[0] + "." + StringHelper.substringsBetween((String) descriptionBox.getSelectedItem(), "(*.", ")")[0]);
                        } else {
                            finalSaveField.setText(finalSaveField.getText() + "." + StringHelper.substringsBetween((String) descriptionBox.getSelectedItem(), "(*.", ")")[0]);
                        }
                        optionsPanel.getPathLabel().setText(browserPanel.getCurrentDirectory().getPath() + "\\" + finalSaveField.getText());
                    }
                });
                browserPanel.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        optionsPanel.getPathLabel().setText(browserPanel.getCurrentDirectory().getPath() + "\\" + finalSaveField.getText());
                    }
                });
            }
            TextField finalSaveField = saveField;
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File[] files = browserPanel.getSelectedFiles();
                    if ((files == null || files.length == 0) && open) {
                        Application.prompt(Localizer.translate("file.file_dialog.title", ""), Localizer.translate("file.file_dialog.message.not_valid_file"), true);
                        return;
                    }
                    if (!open) {
                        String path = browserPanel.getCurrentDirectory().getPath();
                        if (files != null && files.length > 0) {
                            path = files[0].getPath();
                        }
                        files = new File[]{new File(path + "\\" + finalSaveField.getText())};
                    }
                    callback.accept(new Object[]{files, parentFrame});
                }
            });
        });
    }

    private static FileFilter[] getFileFiltersForStringArray(String[] filters) {
        if (filters != null && filters.length > 0) {
            FileFilter[] fileFilters = new FileFilter[filters.length];
            int idx = 0;
            for (String extension : filters) {
                extension = extension.toLowerCase(Locale.ENGLISH);

                if (extension.startsWith("."))
                    extension = extension.replaceFirst("\\.", "");

                String finalExtension = extension;
                fileFilters[idx] = new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return f.getName().toLowerCase(Locale.ENGLISH).endsWith("." + finalExtension) || f.isDirectory();
                    }

                    @Override
                    public String getDescription() {
                        return finalExtension.toUpperCase(Locale.ENGLISH) + " files (*." + finalExtension.toLowerCase(Locale.ENGLISH) + ")";
                    }
                };
                idx++;
            }
            return fileFilters;
        }
        return new FileFilter[]{new FileFilter() {
            @Override
            public boolean accept(File f) {
                return true;
            }

            @Override
            public String getDescription() {
                return "Any file";
            }
        }};
    }

    private static String getExtensionFromFilter(FileFilter fileFilter) {
        return StringHelper.substringsBetween(fileFilter.getDescription(), "(*.", ")")[0];
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
