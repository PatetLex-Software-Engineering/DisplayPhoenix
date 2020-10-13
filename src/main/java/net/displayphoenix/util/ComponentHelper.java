package net.displayphoenix.util;

import net.displayphoenix.Application;

import java.awt.*;

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
}
