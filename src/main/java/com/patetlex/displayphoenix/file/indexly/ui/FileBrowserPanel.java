package com.patetlex.displayphoenix.file.indexly.ui;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.file.indexly.Indexly;
import com.patetlex.displayphoenix.util.ImageHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileBrowserPanel extends JPanel {

    private List<FileFilter> masterFileFilters = new ArrayList<>();
    private List<FileFilter> fileFilters = new ArrayList<>();
    private List<File> directories = new ArrayList<>();
    private List<File> files = new ArrayList<>();
    private JList<File> filesList;
    private File currentDirectory;
    private boolean multiselectEnabled;

    private List<ActionListener> actionListeners = new ArrayList<>();

    public FileBrowserPanel(File directory) {
        this(false, directory);
    }

    public FileBrowserPanel(boolean multiselect, File directory) {
        super(new BorderLayout());
        init(multiselect, directory);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void setMultiselectEnabled(boolean enabled) {
        this.multiselectEnabled = enabled;
        reload();
    }

    public File[] getSelectedFiles() {
        if (this.filesList.getSelectedIndices().length == 0) {
            return null;
        }
        int[] indices = this.filesList.getSelectedIndices();
        File[] files = new File[indices.length];
        for (int i = 0; i < indices.length; i++) {
            files[i] = this.filesList.getModel().getElementAt(indices[i]);
        }
        return files;
    }

    public File getCurrentDirectory() {
        return this.currentDirectory;
    }

    public void addFileFilter(FileFilter fileFilter) {
        this.fileFilters.add(fileFilter);
    }

    protected void addMasterFilter(FileFilter fileFilter) {
        this.masterFileFilters.add(fileFilter);
    }

    protected void setCurrentDirectory(File directory) {
        File prevDir = getCurrentDirectory();
        removeAll();
        this.directories.clear();
        this.files.clear();
        init(this.multiselectEnabled, directory);
        revalidate();
        if (!prevDir.equals(directory)) {
            for (ActionListener actionListener : this.actionListeners) {
                actionListener.actionPerformed(new ActionEvent(this, 0, "directory changed"));
            }
        }
    }

    protected List<File> getDirectories() {
        return this.directories;
    }

    protected List<File> getFiles() {
        return this.files;
    }

    protected JList<File> getJList() {
        return this.filesList;
    }

    @Override
    public void reshape(int x, int y, int w, int h) {
        super.reshape(x, y, w, h);
        this.filesList.setFixedCellHeight(Math.round(h * 0.065F));
    }

    public void reload() {
        setCurrentDirectory(getCurrentDirectory());
    }

    public void addActionListener(ActionListener actionListener) {
        this.actionListeners.add(actionListener);
    }

    private void init(boolean multiselect, File directory) {
        this.multiselectEnabled = multiselect;
        this.currentDirectory = directory;
        File[] startingFiles = directory.listFiles();
        Arrays.sort(startingFiles, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        DefaultListModel<File> listModel = new DefaultListModel<>();
        for (File file : startingFiles) {
            if (file.isDirectory()) {
                boolean flag = false;
                for (FileFilter fileFilter : this.masterFileFilters) {
                    if (!fileFilter.accept(file)) {
                        flag = true;
                    }
                }
                if (!flag)
                    this.directories.add(file);
            } else {
                boolean flag = this.fileFilters.size() == 0;
                for (FileFilter fileFilter : this.fileFilters) {
                    if (fileFilter.accept(file)) {
                        flag = true;
                    }
                }
                for (FileFilter fileFilter : this.masterFileFilters) {
                    if (!fileFilter.accept(file)) {
                        flag = false;
                    }
                }
                if (flag) {
                    this.files.add(file);
                }
            }
        }
        for (File dir : this.directories) {
            if (!dir.isHidden()) {
                listModel.addElement(dir);
            }
        }
        for (File file : this.files) {
            if (!file.isHidden()) {
                listModel.addElement(file);
            }
        }
        JList<File> list = new JList<>(listModel);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0) {
                    File file = list.getModel().getElementAt(index);
                    if (e.getClickCount() == 2) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            if (file.isDirectory()) {
                                setCurrentDirectory(file);
                            } else if (file.canExecute()) {
                                try {
                                    Application.getSystemProcessor().executeFile(file);
                                } catch (IOException ex) {
                                }
                            }
                        }
                    }
                }
            }
        });
        list.setSelectionMode(multiselect ? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION : ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new FileRenderer());
        list.setOpaque(false);
        this.filesList = list;
        JScrollPane scrollBar = new JScrollPane(this.filesList);
        scrollBar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollBar, BorderLayout.CENTER);
    }

    private static class FileRenderer extends JLabel implements ListCellRenderer<File> {
        @Override
        public Component getListCellRendererComponent(JList<? extends File> list, File value, int index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(isSelected);
            setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
            setForeground(isSelected ? Application.getTheme().getColorTheme().getPrimaryColor() : Application.getTheme().getColorTheme().getTextColor());
            setIcon(Indexly.getIconOfFile(value));
            if (list.getFixedCellHeight() > 0) {
                setIcon(ImageHelper.resize(Indexly.getIconOfFile(value), list.getFixedCellHeight()));
            }
            setText(value.getName());
            setToolTipText(value.getPath());
            return this;
        }
    }
}
