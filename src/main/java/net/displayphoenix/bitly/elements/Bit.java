package net.displayphoenix.bitly.elements;

import net.displayphoenix.Application;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.canvasly.effects.ImageEffect;
import net.displayphoenix.ui.widget.*;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bit {

    private transient List<Bit> plugins = new ArrayList<>();
    private transient Map<BitWidget, Component[]> widgetComponentMap = new HashMap<>();
    private String plugin;
    private String pluginFlag;
    private String type;
    private List<BitWidget[]> widgets;
    private transient int currentPage;

    /**
     * Main element of Bitly, used to bridge UI to syntax code
     *
     * @param type  Type name of bit, identifier
     * @param plugin  Name of bit to insert
     * @param pluginFlag  Flag of code input
     * @param widgets  All widgets within bit
     */
    public Bit(String type, String plugin, String pluginFlag, List<BitWidget[]> widgets) {
        this.widgets = widgets;
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
     * Get widget components
     *
     * @return
     */
    public Map<BitWidget, Component[]> getWidgetComponentMap() {
        return widgetComponentMap;
    }

    /**
     * Adds plugin if not already, adds all widgets
     * 
     * @see net.displayphoenix.bitly.Bitly#registerBit(Bit)
     * @see Bit#getWidgetComponentMap()
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
     * Creates bit panel
     *
     * @see BitWidget#create()
     * 
     * @param arguments  Arguments to pass to widgets
     * @return Panel of representative Bit
     *
     */
    public JPanel open(BitArgument... arguments) {
        List<Component> pageComponents = getPageComponents(arguments);
        JPanel componentPanel = PanelHelper.grid(2, pageComponents.toArray(new Component[pageComponents.size()]));

        JPanel pagePanel = PanelHelper.join();
        pagePanel.add(getPageWidgets(componentPanel));

        JPanel widgetPanel = PanelHelper.northAndCenterElements(componentPanel, pagePanel);

        return widgetPanel;
    }

    private List<Component> getPageComponents(BitArgument[] arguments) {
        return getPageComponents(this.currentPage, arguments);
    }
    protected List<Component> getPageComponents(int page, BitArgument[] arguments) {
        int i = 0;
        List<Component> pageComponents = new ArrayList<>();
        for (BitWidget[] widgetArr : this.widgets) {
            if (i == page) {
                for (BitWidget widget : widgetArr) {
                    if (!this.widgetComponentMap.containsKey(widget)) {
                        Component[] component = widget.create();
                        this.widgetComponentMap.put(widget, component);
                        for (Bit pluginBit : this.plugins) {
                            for (BitWidget[] pluginPage : pluginBit.widgets) {
                                for (BitWidget pluginWidget : pluginPage) {
                                    if (pluginWidget == widget) {
                                        pluginBit.widgetComponentMap.put(widget, component);
                                    }
                                }
                            }
                        }
                        pageComponents.add(component[0]);
                        for (BitArgument argument : arguments) {
                            if (argument.getFlag().equalsIgnoreCase(widget.getFlag())) {
                                widget.setValue(component[1], argument);
                            }
                        }
                    }
                    else {
                        pageComponents.add(this.widgetComponentMap.get(widget)[0]);
                    }
                }
            }
            i++;
        }
        return pageComponents;
    }

    private JPanel getPageWidgets(JPanel componentPanel, BitArgument... arguments) {
        FadeOnHoverWidget prevPage = new FadeOnHoverWidget(ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), 0.01F);
        FadeOnHoverWidget nextPage = new FadeOnHoverWidget(ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow"), 50), ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow"), 50), 0.01F);
        final JPanel[] pagePanel = {PanelHelper.grid(2, prevPage, nextPage)};
        if (this.currentPage == 0 && this.widgets.size() == 1) {
            pagePanel[0] = PanelHelper.join();
        }
        else if (this.currentPage == 0) {
            pagePanel[0] = PanelHelper.grid(1, nextPage);
        }
        else if (this.currentPage == this.widgets.size() - 1) {
            pagePanel[0] = PanelHelper.grid(1, prevPage);
        }
        if (prevPage != null) {
            prevPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentPage--;

                    componentPanel.removeAll();
                    List<Component> components = getPageComponents(arguments);
                    componentPanel.setLayout(new GridLayout((int) Math.ceil(components.size() / 2F), 2));
                    for (Component component : components) {
                        componentPanel.add(component);
                    }
                    componentPanel.revalidate();
                    componentPanel.repaint();

                    pagePanel[0].removeAll();
                    JPanel newPagePanel = getPageWidgets(componentPanel, arguments);
                    pagePanel[0].add(newPagePanel);
                    pagePanel[0].revalidate();
                    pagePanel[0].repaint();
                }
            });
        }
        nextPage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentPage++;

                componentPanel.removeAll();
                componentPanel.revalidate();
                componentPanel.repaint();
                List<Component> components = getPageComponents(arguments);
                componentPanel.setLayout(new GridLayout((int) Math.ceil(components.size() / 2F), 2));
                for (Component component : components) {
                    componentPanel.add(component);
                }

                pagePanel[0].removeAll();
                JPanel newPagePanel = getPageWidgets(componentPanel, arguments);
                pagePanel[0].add(newPagePanel);
                pagePanel[0].revalidate();
                pagePanel[0].repaint();
            }
        });
        pagePanel[0].setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        return pagePanel[0];
    }
}
