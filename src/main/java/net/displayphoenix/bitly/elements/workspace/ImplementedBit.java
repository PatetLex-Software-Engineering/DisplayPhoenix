package net.displayphoenix.bitly.elements.workspace;

import net.displayphoenix.Application;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.canvasly.effects.ImageEffect;
import net.displayphoenix.ui.widget.FadeOnHoverWidget;
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

public class ImplementedBit {

    private transient Map<BitWidget, JPanel> widgetToPanel = new HashMap<>();
    private Map<BitWidget, Component> widgetToRawComponent = new HashMap<>();
    private List<ImplementedBit> implementedPlugins = new ArrayList<>();
    private transient int currentPage;
    private Bit bit;

    /**
     * An implemented bit, contains all widgets/fields.
     *
     * @param bit  Base bit
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
     * @param flag  Flag of bit widget
     * @return
     */
    public Object getValueOfWidget(String flag) {
        for (BitWidget[] page : this.bit.getBits()) {
            for (BitWidget widget : page) {
                if (widget.getFlag().equalsIgnoreCase(flag)) {
                    return widget.getValue(getRawComponent(widget));
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
            if (i == page) {
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
                        pageComponents.add(component[0]);
                        for (BitArgument argument : arguments) {
                            if (argument.getFlag().equalsIgnoreCase(widget.getFlag())) {
                                widget.setValue(component[1], argument);
                            }
                        }
                    }
                    else {
                        pageComponents.add(this.widgetToPanel.get(widget));
                    }
                }
            }
            i++;
        }
        return pageComponents;
    }

    public JPanel getPageWidgets(JPanel componentPanel, BitArgument... arguments) {
        FadeOnHoverWidget prevPage = new FadeOnHoverWidget(ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), 0.01F);
        FadeOnHoverWidget nextPage = new FadeOnHoverWidget(ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow"), 50), ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow"), 50), 0.01F);
        final JPanel[] pagePanel = {PanelHelper.grid(2, prevPage, nextPage)};
        if (this.currentPage == 0 && this.bit.getBits().size() == 1) {
            pagePanel[0] = PanelHelper.join();
        }
        else if (this.currentPage == 0) {
            pagePanel[0] = PanelHelper.grid(1, nextPage);
        }
        else if (this.currentPage == this.bit.getBits().size() - 1) {
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
