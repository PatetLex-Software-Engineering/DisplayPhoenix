package net.displayphoenix.bitly.elements;

import net.displayphoenix.bitly.elements.workspace.ImplementedBit;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Bit {

    private List<Bit> plugins = new ArrayList<>();
    private List<BitWidget[]> widgets;
    private ImageIcon icon;
    private String plugin;
    private String pluginFlag;
    private String type;

    /**
     * Main element of Bitly, used to bridge UI to syntax code
     *
     * @param type  Type name of bit, identifier
     * @param plugin  Name of bit to insert
     * @param pluginFlag  Flag of code input
     * @param icon  Bit's icon
     * @param widgets  All widgets within bit
     */
    public Bit(String type, String plugin, String pluginFlag, ImageIcon icon, List<BitWidget[]> widgets) {
        this.widgets = widgets;
        this.icon = icon;
        this.pluginFlag = pluginFlag;
        this.type = type;
        this.plugin = plugin;
    }

    /**
     * @return  Type of widget
     */
    public String getType() {
        return type;
    }

    /**
     * @return  Name of bit to plugin
     */
    public String getPlugin() {
        return plugin;
    }

    /**
     * @return  Flag of code used in bit to insert
     */
    public String getPluginFlag() {
        return pluginFlag;
    }

    /**
     * @return  All plugins of bit
     */
    public List<Bit> getPlugins() {
        return plugins;
    }

    /**
     * @return  Icon of bit
     */
    public ImageIcon getIcon() {
        return this.icon;
    }

    /**
     * Adds plugin if not already, adds all widgets
     * 
     * @see net.displayphoenix.bitly.Bitly#registerBit(Bit)
     * 
     * @param bit  Plugin bit
     */
    public void addPlugin(Bit bit) {
        // Check for existing plugin
        if (!this.plugins.contains(bit)) {
            this.plugins.add(bit);

            // Add all pages from plugin bit to parent bit
            for (BitWidget[] page : bit.getBits()) {
                this.widgets.add(page);
            }
        }
    }

    /**
     * @return  Pages of bit object
     */
    public List<BitWidget[]> getBits() {
        return widgets;
    }

    /**
     * Creates bit panel and implemented bit
     *
     * @see BitWidget#create()
     * @see BitSave#BitSave(ImplementedBit, JPanel)
     * 
     * @param arguments  Arguments to pass to widgets
     * @return Save with implemented bit and respective panel
     *
     */
    public BitSave get(BitArgument... arguments) {
        ImplementedBit implementedBit = new ImplementedBit(this);
        List<Component> pageComponents = implementedBit.getPageComponents(arguments);
        JPanel componentPanel = PanelHelper.grid(2, pageComponents.toArray(new Component[pageComponents.size()]));

        JPanel pagePanel = PanelHelper.join();
        pagePanel.add(implementedBit.getPageWidgets(componentPanel));

        JPanel widgetPanel = PanelHelper.northAndCenterElements(componentPanel, pagePanel);

        return new BitSave(implementedBit, widgetPanel);
    }
}
