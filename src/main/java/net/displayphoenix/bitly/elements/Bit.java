package net.displayphoenix.bitly.elements;

import net.displayphoenix.Application;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.gen.BlocklyXmlParser;
import net.displayphoenix.generation.Module;
import net.displayphoenix.image.effects.ImageEffect;
import net.displayphoenix.ui.widget.*;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPanelUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bit {

    private Map<BitWidget, Component[]> widgetComponentMap = new HashMap<>();
    private String type;
    private List<BitWidget[]> widgets;
    private transient int currentPage;

    public Bit(String type, List<BitWidget[]> widgets) {
        this.widgets = widgets;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getValueOfWidget(Module module, BitWidget widget) {
        switch (widget.getStyle()) {
            case TOGGLE:
                return Boolean.toString(((Toggle) this.widgetComponentMap.get(widget)[1]).isToggled());
            case TEXT:
            case NUMBER:
                return ((TextField) this.widgetComponentMap.get(widget)[1]).getText();
            case BLOCKLY:
                ProvisionWidget provisionWidget = ((ProvisionWidget) this.widgetComponentMap.get(widget)[1]);
                if (provisionWidget.getXml() == null)
                    return null;
                ImplementedBlock[] implementedBlocks = BlocklyXmlParser.fromWorkspaceXml(provisionWidget.getXml());
                for (ImplementedBlock implementedBlock : implementedBlocks) {
                    if (implementedBlock.getBlock().getType().equalsIgnoreCase("event_wrapper")) {
                        return module.getCodeFromBlock(implementedBlock);
                    }
                }
                break;
            case RESOURCE:
                return ((ResourceWidget) this.widgetComponentMap.get(widget)[1]).getFile().getFile().getPath();
        }
        return null;
    }

    public List<BitWidget[]> getBits() {
        return widgets;
    }

    public JPanel open(Window parentFrame, BitArgument... arguments) {
        List<Component> pageComponents = getPageComponents(parentFrame, arguments);
        JPanel componentPanel = PanelHelper.join(pageComponents.toArray(new Component[pageComponents.size()]));
        componentPanel.setLayout(new GridLayout((int) Math.ceil(pageComponents.size() / 2F), 2));

        JPanel pagePanel = PanelHelper.join();
        pagePanel.add(getPageWidgets(parentFrame, componentPanel));

        JPanel widgetPanel = PanelHelper.northAndCenterElements(componentPanel, pagePanel);

        return widgetPanel;
    }

    private List<Component> getPageComponents(Window parentFrame, BitArgument[] arguments) {
        int i = 0;
        List<Component> pageComponents = new ArrayList<>();
        for (BitWidget[] widgetArr : this.widgets) {
            if (i == this.currentPage) {
                for (BitWidget widget : widgetArr) {
                    if (!this.widgetComponentMap.containsKey(widget)) {
                        Component[] component = widget.create(parentFrame);
                        this.widgetComponentMap.put(widget, component);
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

    private JPanel getPageWidgets(Window parentFrame, JPanel componentPanel, BitArgument... arguments) {
        FadeOnHoverWidget prevPage = this.currentPage > 0 ? new FadeOnHoverWidget(ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), ImageHelper.resize(new ImageIcon(ImageHelper.flip(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow").getImage(), ImageEffect.HORIZONTAL)), 50), 0.005F) : null;
        FadeOnHoverWidget nextPage = new FadeOnHoverWidget(ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_arrow"), 50), ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_arrow"), 50), 0.005F);
        final JPanel[] pagePanel = {this.currentPage + 1 < this.widgets.size() ? this.currentPage > 0 ? PanelHelper.westAndEastElements(PanelHelper.join(prevPage), PanelHelper.join(nextPage)) : PanelHelper.join(FlowLayout.RIGHT, nextPage) : PanelHelper.join(FlowLayout.LEFT, prevPage)};
        if (prevPage != null) {
            prevPage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentPage--;

                    componentPanel.removeAll();
                    List<Component> components = getPageComponents(parentFrame, arguments);
                    componentPanel.setLayout(new GridLayout((int) Math.ceil(components.size() / 2F), 2));
                    for (Component component : components) {
                        componentPanel.add(component);
                    }
                    componentPanel.revalidate();
                    componentPanel.repaint();

                    pagePanel[0].removeAll();
                    JPanel newPagePanel = getPageWidgets(parentFrame, componentPanel);
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
                List<Component> components = getPageComponents(parentFrame, arguments);
                componentPanel.setLayout(new GridLayout((int) Math.ceil(components.size() / 2F), 2));
                for (Component component : components) {
                    componentPanel.add(component);
                }

                pagePanel[0].removeAll();
                JPanel newPagePanel = getPageWidgets(parentFrame, componentPanel);
                pagePanel[0].add(newPagePanel);
                pagePanel[0].revalidate();
                pagePanel[0].repaint();
            }
        });
        pagePanel[0].setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        return pagePanel[0];
    }
}
