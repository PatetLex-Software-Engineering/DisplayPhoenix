package net.displayphoenix;

import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.image.*;
import net.displayphoenix.image.elements.impl.ImageElement;
import net.displayphoenix.image.tools.impl.*;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test {
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 800);
        Application.create("sda", ImageHelper.getImage("blunt_warning"), theme, "kdsa");

        Application.openWindow(parentFrame -> {
            CanvasPanel canvas = new CanvasPanel(16, 16);
            //canvas.addElement(0, new FontElement("Hello!", Color.BLACK, 4F));
            //canvas.setSelectedElement(null);
            canvas.setZoom(50);
            canvas.setPreferredSize(new Dimension(parentFrame.getWidth() - 400, parentFrame.getHeight()));
            LayerViewPanel viewPanel = new LayerViewPanel(canvas);
            viewPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            ToolPanel toolPanel = new ToolPanel(canvas, new PencilTool(), new BucketTool(), new PickerTool(), new EraserTool(), new ImageTool());
            toolPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            //ElementPanel elementPanel = new ElementPanel(canvas, new ImageElement(ImageHelper.resize(ImageHelper.getImage("blunt_warning"), 100, 100)));
            //elementPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));

            JButton button = new JButton("save");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.export(parentFrame);
                }
            });

            parentFrame.add(PanelHelper.northAndCenterElements(button, PanelHelper.westAndCenterElements(toolPanel, PanelHelper.westAndCenterElements(canvas, viewPanel))));
        });
    }
}
