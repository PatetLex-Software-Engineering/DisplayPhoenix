package net.displayphoenix.bitly.ui;

import net.displayphoenix.Application;
import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.js.BlocklyJS;
import net.displayphoenix.blockly.ui.BlocklyDependencyPanel;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.widget.ProvisionWidget;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.ui.widget.Toggle;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.PanelHelper;
import net.displayphoenix.web.Website;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class BitWidget {

    private BitWidgetStyle style;
    private String translationKey;
    private String flag;
    private String helpUrl;
    private String[] provisions;

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
        System.out.println(flag);
        switch (style) {
            case TOGGLE:
                Toggle toggle = new Toggle();
                toggle.setPreferredSize(new Dimension(150, 75));
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(toggle)), toggle};
            case TEXT:
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
            case NUMBER:
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
            case BLOCKLY:
                ProvisionWidget provisionWidget = new ProvisionWidget(this.provisions);
                provisionWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            Application.openWindow(JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                                BlocklyDependencyPanel dependencyPanel = new BlocklyDependencyPanel();
                                for (String provision : provisions) {
                                    dependencyPanel.addProvision(provision);
                                }
                                if (provisionWidget.getXml() != null) {
                                    dependencyPanel.getBlocklyPanel().setWorkspace(provisionWidget.getXml());
                                } else {
                                    Block event = Blockly.getBlockFromType("event_wrapper");
                                    if (event == null) {
                                        Blockly.registerBlock(new File(BlocklyJS.getDefaultBlock("event_wrapper")), Blockly.FLOW);
                                        event = Blockly.getBlockFromType("event_wrapper");
                                        event.persist();
                                        event.hide();
                                    }
                                    dependencyPanel.getBlocklyPanel().setWorkspace(new ImplementedBlock(event, 50, 50));
                                }
                                parentFrame.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosing(WindowEvent e) {
                                        provisionWidget.setXml(dependencyPanel.getBlocklyPanel().getRawWorkspace());
                                    }
                                });
                                parentFrame.add(dependencyPanel);
                            });
                        }
                    }
                });
                ComponentHelper.deriveFont(provisionWidget, 25);
                provisionWidget.setPreferredSize(new Dimension(150, 75));
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(provisionWidget)), provisionWidget};
        }
        return null;
    }

    public Website getHelpWebsite() {
        return this.helpUrl != null ? new Website(this.helpUrl) : null;
    }
}
