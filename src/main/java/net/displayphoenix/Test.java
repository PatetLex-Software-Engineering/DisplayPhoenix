package net.displayphoenix;

import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.canvasly.*;
import net.displayphoenix.canvasly.elements.StaticElement;
import net.displayphoenix.canvasly.elements.impl.FontElement;
import net.displayphoenix.canvasly.elements.impl.ImageElement;
import net.displayphoenix.canvasly.tools.impl.*;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Test {
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 800);
        Application.create("sda", ImageHelper.getImage("blunt_warning"), theme, "kdsa");

        Application.openWindow(parentFrame -> {
            CanvasPanel canvas = new CanvasPanel(64, 64);
            canvas.setPreferredSize(new Dimension(parentFrame.getWidth() - 600, parentFrame.getHeight()));
            LayerViewPanel viewPanel = new LayerViewPanel(canvas);
            viewPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            ToolPanel toolPanel = new ToolPanel(canvas, new PencilTool(), new BucketTool(), new PickerTool(), new EraserTool(), new ImageTool(), new TextTool());
            toolPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight() - 50));
            ElementPanel elementPanel = new ElementPanel(canvas, new StaticElement(new ImageElement(ImageHelper.getImage("test/slot")), 0, 0, new StaticElement.Properties().setParse().setMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Application.prompt("Test", "You double clicked a GUI slot",true);
                }
            })), new StaticElement(new ImageElement(ImageHelper.getImage("test/button")), 0, 0, new StaticElement.Properties().setParse().setMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Application.prompt("Test", "You double clicked a GUI button",true);
                }
            })), new StaticElement(new FontElement("Label", Color.WHITE, 3F), 0, 0, new StaticElement.Properties().setOverlay().setMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Application.prompt("Test", "To add nbt put <number/string%NBT_FLAG>",true);
                }
            })));
            elementPanel.setPreferredSize(new Dimension(200, parentFrame.getHeight()));
            JLabel label = new JLabel("GUI components");
            ComponentHelper.themeComponent(label);
            ComponentHelper.deriveFont(label, 16F);

            JButton button = new JButton("save");
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.export(parentFrame);
                }
            });

            parentFrame.add(PanelHelper.northAndCenterElements(button, PanelHelper.westAndCenterElements(PanelHelper.westAndCenterElements(PanelHelper.northAndCenterElements(PanelHelper.join(label), elementPanel), toolPanel), PanelHelper.westAndCenterElements(canvas, viewPanel))));
        });
    }
}
