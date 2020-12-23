package net.displayphoenix.file.indexly.ui;

import net.displayphoenix.Application;
import net.displayphoenix.canvasly.effects.ImageEffect;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.widget.FadeOnHoverWidget;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class FileOptionsPanel extends JPanel {

    private FileBrowserPanel browser;
    private JLabel pathLabel;

    public FileOptionsPanel(FileBrowserPanel browserPanel) {
        this.browser = browserPanel;
        Image arrow = ImageHelper.flip(ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow"), 50, 50).getImage(), ImageEffect.HORIZONTAL);
        FadeOnHoverWidget backWidget = new FadeOnHoverWidget(new ImageIcon(ImageHelper.overlay(arrow, Application.getTheme().getColorTheme().getSecondaryColor(), 1F)), new ImageIcon(ImageHelper.overlay(arrow, Application.getTheme().getColorTheme().getAccentColor(), 1F)), 0.01F);
        backWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                File parentDirectory = browser.getCurrentDirectory().getParentFile();
                if (parentDirectory != null && parentDirectory.exists()) {
                    browser.setCurrentDirectory(parentDirectory);
                }
            }
        });
        backWidget.setToolTipText(Localizer.translate("file.back.text"));
        net.displayphoenix.ui.widget.TextField searchField = new TextField("Search");
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                browserPanel.reload();
            }
        });
        browserPanel.addMasterFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.getName().startsWith(searchField.getText()) || searchField.getText().isEmpty() || searchField.getText().equalsIgnoreCase("Search")) {
                    return true;
                }
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });
        searchField.setPreferredSize(new Dimension(300, 40));
        JLabel currentPathLabel = new JLabel(browserPanel.getCurrentDirectory().getPath());
        browserPanel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("Search");
                currentPathLabel.setText(browserPanel.getCurrentDirectory().getPath());
                browserPanel.reload();
            }
        });
        ComponentHelper.themeComponent(currentPathLabel);
        ComponentHelper.deriveFont(currentPathLabel, 15);
        add(PanelHelper.grid(3, PanelHelper.join(backWidget), PanelHelper.join(searchField), PanelHelper.join(currentPathLabel)));
        this.pathLabel = currentPathLabel;
    }

    public JLabel getPathLabel() {
        return pathLabel;
    }
}
