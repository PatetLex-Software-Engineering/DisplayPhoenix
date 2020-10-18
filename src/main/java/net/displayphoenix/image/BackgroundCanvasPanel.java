package net.displayphoenix.image;

import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

public class BackgroundCanvasPanel extends CanvasPanel {

    private ImageIcon backgroundImage;

    public BackgroundCanvasPanel(ImageIcon backgroundImage) {
        setPreferredSize(new Dimension(backgroundImage.getIconWidth(), backgroundImage.getIconHeight()));
        setBounds(0, 0,backgroundImage.getIconWidth(), backgroundImage.getIconHeight());
        this.backgroundImage = new ImageIcon(ImageHelper.cover(backgroundImage.getImage(), getSize()));
    }

    @Override
    protected void paintElements(Graphics g) {
        float r = (this.mouseRotation + 100) / 100;
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        float w = this.backgroundImage.getIconWidth() * r;
        float h = this.backgroundImage.getIconHeight() * r;
        g.drawImage(this.backgroundImage.getImage(), Math.round(cw - (w / 2F)), Math.round(ch - (h / 2F)), Math.round(w), Math.round(h), this);
        super.paintElements(g);
    }
}
