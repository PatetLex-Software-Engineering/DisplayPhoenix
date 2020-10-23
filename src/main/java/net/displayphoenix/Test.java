package net.displayphoenix;

import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.elements.impl.BackgroundElement;
import net.displayphoenix.image.elements.impl.ImageElement;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.util.ImageHelper;

import java.awt.*;

public class Test {
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 800);
        Application.create("sda", ImageHelper.getImage("blunt_warning"), theme, "kdsa");

        Application.openWindow(parentFrame -> {
            CanvasPanel canvas = new CanvasPanel(200, 200);
            //canvas.setCanvasScale(16F);
            canvas.addElement(-1, new BackgroundElement(Color.CYAN));
            canvas.addElement(new ImageElement(ImageHelper.resize(ImageHelper.getImage("blunt_warning"), 50, 50)));
            canvas.addElement(new ImageElement(ImageHelper.resize(ImageHelper.getImage("popping_warning"), 50, 50)));
            parentFrame.add(canvas);
            canvas.export(parentFrame);
        });
    }
}
