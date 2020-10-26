package net.displayphoenix;

import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.image.CanvasPanel;
import net.displayphoenix.image.LayerViewPanel;
import net.displayphoenix.image.ToolPanel;
import net.displayphoenix.image.elements.impl.BackgroundElement;
import net.displayphoenix.image.elements.impl.FontElement;
import net.displayphoenix.image.tools.impl.PencilTool;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.ui.widget.ColorWheel;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import java.awt.*;

public class Test {
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 800);
        Application.create("sda", ImageHelper.getImage("blunt_warning"), theme, "kdsa");

        Application.openWindow(parentFrame -> {
            CanvasPanel canvas = new CanvasPanel(16, 15);
            canvas.addElement(0, new BackgroundElement(Color.CYAN));
            canvas.setSelectedElement(null);
            canvas.setZoom(100);
            canvas.setPreferredSize(new Dimension(parentFrame.getWidth() - 400, parentFrame.getHeight()));
            LayerViewPanel viewPanel = new LayerViewPanel(canvas);
            viewPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            ToolPanel toolPanel = new ToolPanel(canvas, new PencilTool());
            toolPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            parentFrame.add(PanelHelper.westAndCenterElements(toolPanel, PanelHelper.westAndCenterElements(canvas, viewPanel)));
        });
    }
}
