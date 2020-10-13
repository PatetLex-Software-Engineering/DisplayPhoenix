package net.displayphoenix.screen;

import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
public class ImagePanel extends JPanel {

    private Image img;

    private boolean keepRatio = false;
    private boolean original = false;
    private boolean fitToWidth = false;

    private int offsetY = 0;

    public ImagePanel(Image img) {
        this.img = img;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void fitToImage() {
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
    }

    public void setImage(Image img) {
        this.img = img;
        repaint();
    }

    public void setRenderOriginal(boolean flag) {
        this.original = flag;
    }

    public void setKeepRatio(boolean flag) {
        this.keepRatio = flag;
    }

    public void setFitToWidth(boolean fitToWidth) {
        this.fitToWidth = fitToWidth;
    }

    @Override
    public void paintComponent(Graphics gg) {
        if (img != null) {
            if (original)
                gg.drawImage(img, 0, offsetY, this);
            else if (fitToWidth)
                gg.drawImage(img, 0, offsetY, getSize().width,
                        (int) ((float) getSize().width * ((float) img.getHeight(this) / (float) img.getWidth(this))),
                        this);
            else if (!keepRatio)
                gg.drawImage(img, 0, offsetY, getSize().width, getSize().height, this);
            else
                gg.drawImage(ImageHelper.cover(img, getSize()), 0, offsetY, this);

        }
    }
}
