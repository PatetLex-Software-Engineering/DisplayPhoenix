package com.patetlex.displayphoenix.bitly.elements.workspace;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.bitly.elements.Bit;
import com.patetlex.displayphoenix.bitly.ui.BitArgument;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.canvasly.effects.ImageEffect;
import com.patetlex.displayphoenix.ui.widget.FadeOnHoverWidget;
import com.patetlex.displayphoenix.util.ImageHelper;
import com.patetlex.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImplementedBit {

    private transient Map<BitWidget, JPanel> widgetToPanel = new HashMap<>();
    private Map<BitWidget, Component> widgetToRawComponent = new HashMap<>();
    private List<ImplementedBit> implementedPlugins = new ArrayList<>();
    private transient int currentPage;
    private Bit bit;

    /**
     * An implemented bit, contains all widgets/fields.
     *
     * @param bit Base bit
     */
    public ImplementedBit(Bit bit) {
        this.bit = bit;
    }

    public Bit getBit() {
        return bit;
    }

    public List<ImplementedBit> getImplementedPlugins() {
        return implementedPlugins;
    }

    /**
     * Get widget panel
     *
     * @return
     */
    public JPanel getComponentPanel(BitWidget widget) {
        return this.widgetToPanel.get(widget);
    }

    /**
     * Get raw component
     *
     * @return
     */
    public Component getRawComponent(BitWidget widget) {
        return this.widgetToRawComponent.get(widget);
    }

    /**
     * Get value of specific bit
     *
     * @param flag Flag of bit widget
     * @return
     */
    public Object getValue(String flag) {
        for (BitWidget[] page : this.bit.getBits()) {
            for (BitWidget widget : page) {
                if (widget.getFlag().equalsIgnoreCase(flag)) {
                    return widget.getValue(getRawComponent(widget));
                }
            }
        }
        return null;
    }

    /**
     * Get component of specific bit
     *
     * @param flag Flag of bit widget
     * @return
     */
    public Component getComponent(String flag) {
        for (BitWidget[] page : this.bit.getBits()) {
            for (BitWidget widget : page) {
                if (widget.getFlag().equalsIgnoreCase(flag)) {
                    return getRawComponent(widget);
                }
            }
        }
        return null;
    }

    public java.util.List<Component> getPageComponents(BitArgument[] arguments) {
        return getPageComponents(this.currentPage, arguments);
    }

    protected java.util.List<Component> getPageComponents(int page, BitArgument[] arguments) {
        int i = 0;
        java.util.List<Component> pageComponents = new ArrayList<>();
        for (BitWidget[] widgetArr : this.bit.getBits()) {
            for (BitWidget widget : widgetArr) {
                if (!this.widgetToPanel.containsKey(widget)) {
                    Component[] component = widget.create();
                    this.widgetToPanel.put(widget, (JPanel) component[0]);
                    this.widgetToRawComponent.put(widget, component[1]);
                    for (Bit pluginBit : this.bit.getPlugins()) {
                        ImplementedBit implementedPlugin = new ImplementedBit(pluginBit);
                        for (BitWidget[] pluginPage : pluginBit.getBits()) {
                            for (BitWidget pluginWidget : pluginPage) {
                                if (pluginWidget == widget) {
                                    implementedPlugin.widgetToPanel.put(widget, (JPanel) component[0]);
                                    implementedPlugin.widgetToRawComponent.put(widget, component[1]);
                                }
                            }
                        }
                    }
                    if (i == page) {
                        pageComponents.add(component[0]);
                    }
                    for (BitArgument argument : arguments) {
                        if (argument.getFlag().equalsIgnoreCase(widget.getFlag())) {
                            widget.setValue(component[1], argument);
                        }
                    }
                } else {
                    if (i == page) {
                        pageComponents.add(this.widgetToPanel.get(widget));
                    }
                }
            }
            i++;
        }
        return pageComponents;
    }

    public JPanel getPageWidgets(JPanel componentPanel, BitArgument... arguments) {
        FadeOnHoverWidget prevPage = new FadeOnHoverWidget(ImageHelper.resize(new ImageIcon(ImageHelper.overlay(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), ImageEffect.HORIZONTAL), Application.getTheme().getColorTheme().getAccentColor(), 1F)), 50), ImageHelper.resize(new ImageIcon(ImageHelper.overlay(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), ImageEffect.HORIZONTAL), Application.getTheme().getColorTheme().getSecondaryColor(), 1F)), 50), 0.01F);
        FadeOnHoverWidget nextPage = new FadeOnHoverWidget(ImageHelper.resize(new ImageIcon(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), Application.getTheme().getColorTheme().getAccentColor(), 1F)), 50), ImageHelper.resize(new ImageIcon(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), Application.getTheme().getColorTheme().getSecondaryColor(), 1F)), 50), 0.01F);
        final JPanel[] pagePanel = {PanelHelper.grid(2, prevPage, nextPage)};
        if (this.currentPage == 0 && this.bit.getBits().size() == 1) {
            pagePanel[0] = PanelHelper.join();
        } else if (this.currentPage == 0) {
            pagePanel[0] = PanelHelper.grid(1, nextPage);
        } else if (this.currentPage == this.bit.getBits().size() - 1) {
            pagePanel[0] = PanelHelper.grid(1, prevPage);
        }
        if (prevPage != null) {
            prevPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentPage--;

                    componentPanel.removeAll();
                    java.util.List<Component> components = getPageComponents(arguments);
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
                ImplementedBit.this.currentPage++;

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
        pagePanel[0].setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        return pagePanel[0];
    }
}
