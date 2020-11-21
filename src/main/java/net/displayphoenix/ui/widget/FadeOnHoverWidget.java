package net.displayphoenix.ui.widget;

import net.displayphoenix.ui.animation.Clipper;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * @author TBroski
 */
public class FadeOnHoverWidget extends JButton implements MouseListener {

    private Clipper clipper; //0.005F

    private Image normalIcon;
    private Image hoverIcon;

    public FadeOnHoverWidget(ImageIcon normalImage, ImageIcon hoverImage, float crement) {
        this.normalIcon = normalImage.getImage();
        this.hoverIcon = hoverImage.getImage();

        this.clipper = new Clipper(crement, 1F, 0F).smooth(); //0.005F
        this.clipper.addListener(() -> {
            repaint();
        });
        this.addMouseListener(this);

        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        this.setBounds(0,0, normalImage.getIconWidth(), normalImage.getIconHeight());
        this.setPreferredSize(new Dimension(normalImage.getIconWidth(), normalImage.getIconHeight()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        ImageIcon nIcon = new ImageIcon(ImageHelper.cover(this.normalIcon, this.getSize()));
        ImageIcon hIcon = new ImageIcon(ImageHelper.cover(this.hoverIcon, this.getSize()));
        nIcon.paintIcon(this, g, 0,0);
        Graphics2D g2d = (Graphics2D) g;
        if (clipper.getCurrentValue() != 0) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clipper.getCurrentValue()));
            g2d.drawImage(hIcon.getImage(), 0, 0, null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        clipper.increment();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        clipper.decrement();
    }
}
