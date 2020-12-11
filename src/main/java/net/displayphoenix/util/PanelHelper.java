package net.displayphoenix.util;

import javax.swing.*;
import java.awt.*;

/**
 * @author TBroski
 */
public class PanelHelper {

    public static JPanel northAndCenterElements(Component top, Component bottom) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("North", top);
        panel.add("Center", bottom);
        return panel;
    }

    public static JPanel centerAndSouthElements(Component top, Component bottom) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("Center", top);
        panel.add("South", bottom);
        return panel;
    }

    public static JPanel westAndEastElements(Component left, Component right) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("West", left);
        panel.add("East", right);
        return panel;
    }

    public static JPanel westAndCenterElements(Component left, Component center) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("West", left);
        panel.add("Center", center);
        return panel;
    }

    public static JPanel centerAndEastElements(Component center, Component east) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("Center", center);
        panel.add("East", east);
        return panel;
    }

    public static JPanel northAndSouthElements(Component top, Component bottom) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add("North", top);
        panel.add("South", bottom);
        return panel;
    }

    public static JPanel grid(int columns, Component... components) {
        return grid(0, 0, columns, components);
    }
    public static JPanel grid(int horizontalGap, int verticalGap, int columns, Component... components) {
        //System.out.println(Math.round((float) Math.ceil((float) components.length / (float) rows)));
        JPanel skup = new JPanel(new GridLayout(Math.round((float) Math.ceil((float) components.length / (float) columns)), columns, horizontalGap, verticalGap));
        skup.setOpaque(false);
        for (Component c : components) {
            skup.add(c);
        }
        return skup;
    }

    public static JPanel join(int align, Component... components) {
        JPanel skup = new JPanel(new FlowLayout(align));
        skup.setOpaque(false);
        for (Component c : components) {
            skup.add(c);
        }
        return skup;
    }
    public static JPanel join(int align, int horizontalGap, int verticalGap, Component... components) {
        JPanel skup = new JPanel(new FlowLayout(align, horizontalGap, verticalGap));
        skup.setOpaque(false);
        for (Component c : components) {
            skup.add(c);
        }
        return skup;
    }
    public static JPanel join(Component... components) {
        return join(FlowLayout.CENTER, components);
    }

    public static JPanel createSeperator(Color color, int height, int seperation) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(true);
        panel.setBackground(color);
        panel.setPreferredSize(new Dimension(panel.getWidth(), height));
        panel.setBorder(BorderFactory.createEmptyBorder(seperation, 0, seperation, 0));
        return panel;
    }

    public static JPanel createVerticalSeperator(Color color, int width, int seperation) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(true);
        panel.setBackground(color);
        panel.setPreferredSize(new Dimension(width, panel.getHeight()));
        panel.setBorder(BorderFactory.createEmptyBorder(0, seperation, 0, seperation));
        return panel;
    }
}
