package com.patetlex.displayphoenix.bitly.ui;

import com.patetlex.displayphoenix.bitly.Bitly;
import com.patetlex.displayphoenix.bitly.elements.Bit;
import com.patetlex.displayphoenix.bitly.elements.BitWidgetStyle;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.util.*;
import com.patetlex.displayphoenix.system.web.Website;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.List;

public class BitWidget {

    private transient BitWidgetStyle styleCache;

    private String style;
    private String flag;
    private String helpUrl;
    public  String[] provisions;
    public String[] options;
    public String path;
    public String headBlock;
    public int width;
    public int height;
    public String[] tools;
    public List<CanvasElement> canvasElements;
    private String script;

    /**
     * Main component of Bitly, used as widgets for main panel
     *
     * @param style Style of widget
     * @param flag  Flag for code
     * @see BitWidgetStyle
     */
    public BitWidget(BitWidgetStyle style, String flag) {
        this.style = style.getName();
        this.styleCache = style;
        this.flag = flag;
    }

    /**
     * Returns style of widget
     *
     * @return Style of widget
     * @see BitWidgetStyle
     */
    public BitWidgetStyle getStyle() {
        if (this.styleCache == null)
            this.styleCache = Bitly.getStyleFromName(this.style);
        return this.styleCache;
    }

    /**
     * Returns code flag of widget
     *
     * @return Flag of bit
     */
    public String getFlag() {
        return flag;
    }

    /**
     * JavaScript of widget
     *
     * @return
     */
    public String getScript() {
        return script;
    }

    /**
     * Creates the main component and panel component
     *
     * @return Component array of both panel and component
     */
    public Component[] create() {
        // Widget comment
        JLabel label = new JLabel(Localizer.translate("bitly.widget." + flag.toLowerCase() + ".text"));

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

        Component component = this.getStyle().create(this);

        return new Component[]{PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(component)), component};
    }

    /**
     * Sets the value of a widget to argument
     *
     * @param component Component to set
     * @param argument  Argument to set to
     * @see Bit#get(BitArgument...)
     */
    public void setValue(Component component, BitArgument argument) {
        this.getStyle().setValue(component, argument);
        component.repaint();
    }

    /**
     * Returns the value of a component
     *
     * @param component Component to check
     */
    public Object getValue(Component component) {
        return this.getStyle().getValue(component);
    }

    /**
     * Returns website of <code>helpUrl</code>
     *
     * @return Website of help url
     * @see BitWidget#create()
     */
    public Website getHelpWebsite() {
        return this.helpUrl != null ? new Website(this.helpUrl) : null;
    }

    public Map<String, byte[]> getExternalFiles(boolean isNative, File relativePath) {
        Map<String, byte[]> nameToContent = new HashMap<>();
        Map<String, byte[]> styleFiles = getStyle().loadExternalFiles(this, isNative, relativePath);
        for (String name : styleFiles.keySet()) {
            nameToContent.put(name, styleFiles.get(name));
        }
        return nameToContent;
    }

    public static class CanvasElement {

        public String type;
        public String defaultValue;
        public int scale = 1;
        public int r;
        public int g;
        public int b;
        public int a = 255;
        public boolean parse;
        public boolean overlay;

    }
}
