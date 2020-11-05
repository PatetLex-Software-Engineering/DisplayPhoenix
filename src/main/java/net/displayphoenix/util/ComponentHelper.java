package net.displayphoenix.util;

import net.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author TBroski
 */
public class ComponentHelper {

    public static void themeComponent(Component component) {
        component.setForeground(Application.getTheme().getColorTheme().getAccentColor());
        component.setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
        component.setFont(Application.getTheme().getFont());
    }

    public static void deriveFont(Component component, float size) {
        component.setFont(component.getFont().deriveFont(size));
    }

    public static <T> JList<T> createJList(ListCellRenderer<T> cellRenderer, Iterable<T> values) {
        DefaultListModel<T> listModel = new DefaultListModel<>();
        for (T val : values) {
            listModel.addElement(val);
        }
        JList<T> list = new JList<>(listModel);
        list.setCellRenderer(cellRenderer);
        return list;
    }

    public static void addScrollPane(JList jList) {
        addScrollPane(jList, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    public static void addScrollPane(JList jList, int horizontalPolicy) {
        JScrollPane scrollBar = new JScrollPane(jList);
        scrollBar.setHorizontalScrollBarPolicy(horizontalPolicy);
    }


}
