package net.displayphoenix.bitly.ui;

import net.displayphoenix.Application;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.js.BlocklyJS;
import net.displayphoenix.blockly.ui.BlocklyDependencyPanel;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.ui.widget.ProvisionWidget;
import net.displayphoenix.ui.widget.ResourceWidget;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.ui.widget.Toggle;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ImageHelper;
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
    private String[] extensions;
    private String path;
    private int width;
    private int height;


    private Object value;

    /**
     * Main component of Bitly, used as widgets for main panel
     *
     * @see BitWidgetStyle
     *
     * @param style  Style of widget
     * @param flag  Flag for code
     * @param translationKey  Translation key for Bit comment
     */
    public BitWidget(BitWidgetStyle style, String flag, String translationKey) {
        this.style = style;
        this.translationKey = translationKey;
        this.flag = flag;
    }

    /**
     * Returns style of widget
     *
     * @see BitWidgetStyle
     *
     * @return Style of widget
     */
    public BitWidgetStyle getStyle() {
        return style;
    }

    /**
     * Returns code flag of widget
     * 
     * @see net.displayphoenix.generation.Module#getCodeFromBit(Bit)
     *
     * @return  Flag of bit
     */
    public String getFlag() {
        return flag;
    }

    /**
     *
     * Creates the main component and panel component
     *
     * @see FileDialog#openFile(Window, String...)
     * 
     * @param parentFrame  Can be null, used for file dialog
     * 
     * @return  Component array of both panel and component
     */
    public Component[] create(Window parentFrame) {
        // Widget comment
        JLabel label = new JLabel(Localizer.translate(this.translationKey));

        // Creates help website if applicable
        if (this.helpUrl != null) {
            label.setCursor(new Cursor(Cursor.HAND_CURSOR));
            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    getHelpWebsite().open();
                }
            });
        }

        // Themes label
        ComponentHelper.themeComponent(label);
        ComponentHelper.deriveFont(label, 25F);

        // Checks for each individual style
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
            case RESOURCE:
                ResourceWidget resourceWidget = new ResourceWidget();
                resourceWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        resourceWidget.setFile(FileDialog.openFile(parentFrame, extensions));
                    }
                });
                resourceWidget.setPreferredSize(new Dimension(150, 150));
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(resourceWidget)), resourceWidget};
            case IMAGE:
                JLabel label1 = new JLabel(ImageHelper.resize(ImageHelper.fromPath(this.path), this.width > 0 ? this.width : 150, this.height > 0 ? this.height : 150));
                label1.setPreferredSize(new Dimension(this.width > 0 ? this.width : 150, this.height > 0 ? this.height : 150));
                return new Component[] {PanelHelper.northAndCenterElements(PanelHelper.join(label1), PanelHelper.join(label1)), label1};
        }
        return null;
    }

    /**
     * Sets the value of a widget to argument
     *
     * @see Bit#open(Window, BitArgument...) 
     * 
     * @param component Component to set
     * @param argument Argument to set to
     */
    public void setValue(Component component, BitArgument argument) {

        // Switch for each style
        switch (style) {
            case TOGGLE:
                ((Toggle) component).setToggle(argument.getAsBoolean());
                break;
            case TEXT:
            case NUMBER:
                ((TextField) component).setText(argument.getAsString());
                break;
            case BLOCKLY:
                ((ProvisionWidget) component).setXml(argument.getAsString());
                break;
            case RESOURCE:
                ((ResourceWidget) component).setFile(new DetailedFile(new File(argument.getAsString())));
                break;
        }
        component.repaint();
    }

    /**
     * Returns website of <code>helpUrl</code>
     *
     * @see BitWidget#create(Window)
     *
     * @return Website of help url
     */
    public Website getHelpWebsite() {
        return this.helpUrl != null ? new Website(this.helpUrl) : null;
    }
}
