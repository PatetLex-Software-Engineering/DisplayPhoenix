package net.displayphoenix.image;

import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
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
        float w = this.backgroundImage.getIconWidth();
        float h = this.backgroundImage.getIconHeight();
        ((Graphics2D) g).scale(r,r);
        g.drawImage(this.backgroundImage.getImage(), Math.round(cw), Math.round(ch), Math.round(w), Math.round(h), this);
        super.paintElements(g);
    }
}
