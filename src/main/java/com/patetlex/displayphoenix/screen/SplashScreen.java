package com.patetlex.displayphoenix.screen;

import com.patetlex.displayphoenix.init.ColorInit;
import com.patetlex.displayphoenix.widgets.ProgressWidget;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
public class SplashScreen extends JWindow {

    protected ProgressWidget progressWidget;

    public SplashScreen(ProgressWidget progressWidget, Image panelImage, int width, int height) {
        this.progressWidget = progressWidget;
        JPanel imagePanel = new ImagePanel(panelImage);
        imagePanel.setLayout(null);
        imagePanel.setOpaque(false);

        progressWidget.setOpaque(false);
        progressWidget.repaint();
        progressWidget.setBounds(0,0,width,height);


        imagePanel.add(progressWidget);

        setBackground(ColorInit.TRANSPARENT);
        add(imagePanel);
        setSize(width, height);
        setLocationRelativeTo(null);
        setVisible(true);
        requestFocus();
        requestFocusInWindow();
        setAlwaysOnTop(true);
        setOpacity(1);
        toFront();
    }

    public void setLoadingProgress(float loadingProgress) {
        this.progressWidget.setLoadingProgress(loadingProgress);
        if (this.progressWidget.getLoadingProgress() == this.progressWidget.getMaxProgress()) {
            this.setVisible(false);
        }
    }
}
