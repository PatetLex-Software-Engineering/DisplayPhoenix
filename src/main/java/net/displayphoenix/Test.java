package net.displayphoenix;

import com.google.gson.GsonBuilder;
import net.displayphoenix.bitly.Bitly;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.ui.BlocklyDependencyPanel;
import net.displayphoenix.enums.WidgetStyle;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.generation.Module;
import net.displayphoenix.ui.ColorTheme;
import net.displayphoenix.ui.Theme;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Test {
    public static void main(String[] args) {
        Theme theme = new Theme(new ColorTheme(new Color(38, 38, 38), new Color(192, 226, 113), new Color(255, 255, 255), Color.GRAY), WidgetStyle.POPPING, new Font(Font.MONOSPACED, Font.PLAIN, 14), 1200, 800);
        Application.create("sda", ImageHelper.getImage("blunt_warning"), theme, "kdsa");

        Blockly.queueText();
        Bitly.registerBit(new File("src/main/resources/bit/test.json"));
        Application.openWindow(parentFrame -> {
            Bit bit = Bitly.getBitFromType("test");
            parentFrame.add("South", bit.open(parentFrame));
            JButton getCode = new JButton("get code");
            getCode.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(Module.JAVASCRIPT.getCodeFromBit(bit));
                }
            });
            parentFrame.add(PanelHelper.join(getCode));
        });
        /*        Application.openWindow(parentFrame -> {
            CanvasPanel canvas = new CanvasPanel(400, 400);
            canvas.addElement(0, new BackgroundElement(Color.CYAN));
            canvas.addElement(1, new ImageElement(ImageHelper.resize(ImageHelper.getImage("blunt_warning"), 50, 50)));
            canvas.addElement(1, new ImageElement(ImageHelper.resize(ImageHelper.getImage("popping_warning"), 50, 50)));
            canvas.setLayer(1);
            canvas.setPreferredSize(new Dimension(parentFrame.getWidth(), parentFrame.getHeight() - 100));
            parentFrame.add("South", canvas);

            JButton export = new JButton("Export as .PNG");
            export.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    canvas.export(parentFrame);
                }
            });
            parentFrame.add("North", export);
        });*/
    }
}
