package net.displayphoenix.ui.widget;

import net.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
public class TextField extends JTextField {
    public TextField() {
        super();
        this.setFont(Application.getTheme().getFont());
        this.setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.setForeground(Application.getTheme().getColorTheme().getTextColor());
        this.setBorder(BorderFactory.createMatteBorder(2,2,2,2, Application.getTheme().getColorTheme().getAccentColor().darker().darker().darker().darker()));
        this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    }

    public TextField(String text) {
        super(text);
        this.setFont(Application.getTheme().getFont());
        this.setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.setForeground(Application.getTheme().getColorTheme().getTextColor());
        this.setBorder(BorderFactory.createMatteBorder(2,2,2,2, Application.getTheme().getColorTheme().getAccentColor().darker().darker().darker().darker()));
        this.setCursor(new Cursor(Cursor.TEXT_CURSOR));
    }
}
