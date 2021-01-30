package com.patetlex.displayphoenix.ui.widget;

import com.patetlex.displayphoenix.canvasly.elements.CanvasSave;
import com.patetlex.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

public class CanvasWidget extends JPanel {

    private CanvasSave save;

    public CanvasWidget() {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public CanvasSave getSave() {
        return save;
    }

    public void setSave(CanvasSave save) {
        this.save = save;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int j = 0;
        for (int xp = 0; xp < getWidth(); xp ++) {
            int k = 0;
            for (int yp = 0; yp < getHeight(); yp ++) {
                float v = k + j;
                g.setColor(v % 2 == 0 ? Color.WHITE : Color.GRAY);
                g.fillRect(xp, yp, 1, 1);
                k++;
            }
            j++;
        }
        ImageIcon pencilIcon = ImageHelper.resize(ImageHelper.getImage("image/pencil"), Math.round(getWidth() * 0.15F));
        g.drawImage(pencilIcon.getImage(), getWidth() - pencilIcon.getIconWidth(), getHeight() - pencilIcon.getIconHeight(), this);
    }
}
