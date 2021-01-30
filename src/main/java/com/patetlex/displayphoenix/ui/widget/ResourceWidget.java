package com.patetlex.displayphoenix.ui.widget;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.file.DetailedFile;
import com.patetlex.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

public class ResourceWidget extends JButton {

    private DetailedFile file;

    public ResourceWidget() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        setBorderPainted(true);
    }

    public DetailedFile getFile() {
        return file;
    }

    public void setFile(DetailedFile file) {
        this.file = file;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (isBorderPainted()) {
            g.setColor(getBackground());
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 2, 2);
        }
        ImageIcon docIcon = ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_doc");
        if (this.file != null && this.file.getFileExtension().equalsIgnoreCase("png")) {
            g.drawImage(ImageHelper.resize(new ImageIcon(this.file.getFile().getPath()), getWidth() - 2, getHeight() - 2).getImage(), 2, 2, this);
        }
        else {
            g.drawImage(ImageHelper.resize(docIcon, getWidth() - 8).getImage(), 4, 4, this);
        }
        int sqWidth = Math.round(getWidth() * 0.2F);
        g.drawImage(ImageHelper.resize(docIcon, sqWidth).getImage(), 0, getHeight() - sqWidth, this);
    }
}
