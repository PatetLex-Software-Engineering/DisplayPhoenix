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
public class OverlayOnHoverWidget extends JButton implements MouseListener {

    private Clipper clipper; //0.005F

    private Image normalIcon;
    private Color overlayColor;

    private boolean isColored;
    private Color normalColor;

    public OverlayOnHoverWidget(ImageIcon normalImage, Color overlayColor, float maxTransparency, float crement) {
        this.normalIcon = normalImage.getImage();
        this.overlayColor = overlayColor;

        this.clipper = new Clipper(crement, maxTransparency, 0F).smooth(); //0.005F
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

    public OverlayOnHoverWidget(Color normalColor, Color overlayColor, float maxTransparency, float crement) {
        this.isColored = true;
        this.normalColor = normalColor;
        this.overlayColor = overlayColor;

        this.clipper = new Clipper(crement, maxTransparency, 0F).smooth(); //0.005F
        this.clipper.addListener(() -> {
            repaint();
        });
        this.addMouseListener(this);

        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!this.isColored) {
            ImageIcon nIcon = new ImageIcon(ImageHelper.cover(this.normalIcon, this.getSize()));
            nIcon.paintIcon(this, g, 0, 0);
            Graphics2D g2d = (Graphics2D) g;
            if (clipper.getCurrentValue() != 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clipper.getCurrentValue()));
                g.setColor(this.overlayColor);
                g.fillRect(0, 0, nIcon.getIconWidth(), nIcon.getIconHeight());
            }
        }
        else {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(this.normalColor);
            g.fillRect(0,0,this.getWidth(), this.getHeight());
            if (clipper.getCurrentValue() != 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clipper.getCurrentValue()));
                g.setColor(this.overlayColor);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
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
