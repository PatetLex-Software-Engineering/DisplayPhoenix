package net.displayphoenix.ui.widget;

import net.displayphoenix.Application;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.animation.Clipper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author TBroski
 */
public class Toggle extends JButton {

    private Clipper slideClipper = new Clipper(this, 0.005F, 1F, 0F).smooth(); //0.005F
    private Clipper colorClipper = new Clipper(this, 0.005F, 1F, 0F).smooth(); //0.005F

    private ColorTheme colorTheme;
    private Color ballColor;
    private Color toggledColor;
    private Color normalColor;
    private Color borderColor;
    private int borderWidth;

    private boolean toggled;

    public Toggle() {
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggled = !toggled;
                if (toggled) {
                    colorClipper.increment();
                    slideClipper.increment();
                }
                else {
                    colorClipper.decrement();
                    slideClipper.decrement();
                }
            }
        });
        this.colorTheme = Application.getTheme().getColorTheme();
        this.ballColor = this.colorTheme.getAccentColor();
        this.borderColor = this.colorTheme.getAccentColor().darker().darker().darker().darker().darker();
        this.toggledColor = this.colorTheme.getSecondaryColor();
        this.normalColor = this.colorTheme.getAccentColor().darker().darker();
        this.borderWidth = 4;
    }

    public Toggle setBallColor(Color color) {
        this.ballColor = color;
        return this;
    }
    public Toggle setToggleColor(Color color) {
        this.toggledColor = color;
        return this;
    }
    public Toggle setNormalColor(Color color) {
        this.normalColor = color;
        return this;
    }
    public Toggle setBorder(Color color) {
        this.borderColor = color;
        return this;
    }
    public Toggle setBorder(Color color, int borderWidth) {
        this.borderColor = color;
        this.borderWidth = borderWidth;
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(this.borderColor);
        g.fillRoundRect(0,0,this.getWidth(), this.getHeight(), 45,45);
        g.setColor(this.normalColor);
        g.fillRoundRect(this.borderWidth / 2,this.borderWidth / 2, this.getWidth() - this.borderWidth, this.getHeight() - this.borderWidth, 45,45);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, colorClipper.getCurrentValue()));
        g.setColor(this.toggledColor);
        g.fillRoundRect(this.borderWidth / 2,this.borderWidth / 2, this.getWidth() - this.borderWidth, this.getHeight() - this.borderWidth, 45,45);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));
        g.setColor(this.ballColor);
        g.fillOval(Math.round((this.getWidth() - this.getHeight()) * this.slideClipper.getCurrentValue()) + Math.round(this.borderWidth / 2F), this.borderWidth / 2, this.getHeight() - this.borderWidth, this.getHeight() - this.borderWidth);
    }

    public boolean isToggled() {
        return toggled;
    }
}
