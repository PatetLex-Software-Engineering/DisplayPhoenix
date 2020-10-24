package net.displayphoenix.bitly.elements;

import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.ui.widget.TextField;
import net.displayphoenix.ui.widget.Toggle;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Bit {

    private Map<BitWidget, Component> widgetComponentMap = new HashMap<>();
    private String type;
    private BitWidget[] widgets;
    private ImageIcon image;

    public Bit(String type, BitWidget... widgets) {
        this.widgets = widgets;
        this.type = type;
    }

    public void addImage(ImageIcon image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public String getValueOfWidget(BitWidget widget) {
        switch (widget.getStyle()) {
            case TOGGLE:
                return Boolean.toString(((Toggle) this.widgetComponentMap.get(widget)).isToggled());
            case TEXT_FIELD:
            case NUMBER_FIELD:
                return ((TextField) this.widgetComponentMap.get(widget)).getText();
        }
        return null;
    }

    public BitWidget[] getBits() {
        return widgets;
    }

    public JPanel open() {
        boolean flag = false;
        JPanel leftWidgetPanel = PanelHelper.join();
        JPanel rightWidgetPanel = PanelHelper.join();
        for (BitWidget widget : this.widgets) {
            Component[] component = widget.create();
            this.widgetComponentMap.put(widget, component[1]);
            if (!flag) {
                leftWidgetPanel = PanelHelper.northAndCenterElements(leftWidgetPanel, PanelHelper.join(component[0]));
            }
            else {
                rightWidgetPanel = PanelHelper.northAndCenterElements(rightWidgetPanel, PanelHelper.join(component[0]));
            }
            flag = !flag;
        }

        JPanel widgetPanel = PanelHelper.westAndEastElements(leftWidgetPanel, rightWidgetPanel);

        if (this.image != null) {
            JLabel image = new JLabel(this.image);
            float r = this.image.getIconWidth() / this.image.getIconHeight();
            this.image = ImageHelper.resize(this.image, widgetPanel.getWidth(), Math.round(r * widgetPanel.getWidth()));
            image.setPreferredSize(new Dimension(widgetPanel.getWidth(), Math.round(r * widgetPanel.getWidth())));
            return PanelHelper.northAndCenterElements(PanelHelper.join(image), widgetPanel);
        }
        return widgetPanel;
    }
}
