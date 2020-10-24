package net.displayphoenix.bitly.ui;

import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.ui.widget.Toggle;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.PanelHelper;
import net.displayphoenix.web.Website;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class BitWidget {

    private BitWidgetStyle style;
    private String translationKey;
    private String flag;
    private String helpUrl;

    public BitWidget(BitWidgetStyle style, String flag, String translationKey) {
        this.style = style;
        this.translationKey = translationKey;
        this.flag = flag;
    }

    public BitWidgetStyle getStyle() {
        return style;
    }

    public String getFlag() {
        return flag;
    }

    public Component[] create() {
        JLabel label = new JLabel(Localizer.translate(this.translationKey));
        if (this.helpUrl != null) {
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getHelpWebsite().open();
                }
            });
        }
        ComponentHelper.themeComponent(label);
        ComponentHelper.deriveFont(label, 25F);
        switch (style) {
            case TOGGLE:
                Toggle toggle = new Toggle();
                toggle.setPreferredSize(new Dimension(150, 75));
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(toggle)), toggle};
            case TEXT_FIELD:
                TextField textField = new TextField();
                textField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        boolean flag = false;
                        try {
                            Float.parseFloat(Character.toString(c));
                        } catch (NumberFormatException ex) {
                            ex.printStackTrace();
                            flag = true;
                        }
                        if (!flag)
                            e.consume();
                    }
                });
                textField.setPreferredSize(new Dimension(150, 75));
                ComponentHelper.deriveFont(textField, 25);
                textField.setHorizontalAlignment(SwingConstants.CENTER);
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(textField)), textField};
            case NUMBER_FIELD:
                TextField numField = new TextField();
                numField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if ((c < '0' || c > '9') && (c != KeyEvent.VK_BACK_SPACE) && (c != '.')) {
                            e.consume();
                        }
                    }
                });
                numField.setPreferredSize(new Dimension(150, 75));
                ComponentHelper.deriveFont(numField, 25);
                numField.setHorizontalAlignment(SwingConstants.CENTER);
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(numField)), numField};
        }
        return null;
    }

    public Website getHelpWebsite() {
        return this.helpUrl != null ? new Website(this.helpUrl) : null;
    }
}
