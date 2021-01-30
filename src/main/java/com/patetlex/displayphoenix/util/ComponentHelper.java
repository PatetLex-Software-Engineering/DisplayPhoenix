package com.patetlex.displayphoenix.util;

import com.patetlex.displayphoenix.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.util.function.Consumer;

/**
 * @author TBroski
 */
public class ComponentHelper {

    public static void themeComponent(Component component) {
        component.setForeground(component instanceof JLabel ? Application.getTheme().getColorTheme().getTextColor() : Application.getTheme().getColorTheme().getAccentColor());
        component.setBackground(Application.getTheme().getColorTheme().getPrimaryColor());
        component.setFont(Application.getTheme().getFont());
    }

    public static void deriveFont(Component component, float size) {
        component.setFont(component.getFont().deriveFont(size));
        component.revalidate();
    }

    public static <T> JList<T> createJList(ListCellRenderer<T> cellRenderer) {
        return createJList(cellRenderer, null);
    }

    public static <T> JList<T> createJList(ListCellRenderer<T> cellRenderer, Iterable<T> values) {
        DefaultListModel<T> listModel = new DefaultListModel<>();
        if (values != null) {
            for (T val : values) {
                listModel.addElement(val);
            }
        }
        JList<T> list = new JList<>(listModel);
        list.setCellRenderer(cellRenderer);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setOpaque(false);
        return list;
    }

    public static <T> JScrollPane addScrollPane(JList<T> jList) {
        return addScrollPane(jList, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    }
    public static <T> JScrollPane addScrollPane(JList<T> jList, int horizontalPolicy) {
        JScrollPane scrollBar = new JScrollPane(jList);
        scrollBar.setHorizontalScrollBarPolicy(horizontalPolicy);
        return scrollBar;
    }

    public static void forEachSubComponentOf(Component component, Consumer<Component> componentListener) {
        componentListener.accept(component);
        if (component instanceof Container) {
            for (Component subComponent : ((Container) component).getComponents()) {
                forEachSubComponentOf(subComponent, componentListener);
            }
            ((Container) component).addContainerListener(new ContainerAdapter() {
                @Override
                public void componentAdded(ContainerEvent e) {
                    forEachSubComponentOf(e.getChild(), componentListener);
                }
            });
        }
    }
}
