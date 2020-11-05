package net.displayphoenix.ui.widget;

import net.displayphoenix.ui.interfaces.ColorListener;
import net.displayphoenix.util.ColorHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class BrightnessColorWheel extends JPanel {

    private ColorWheel colorWheel;
    private BrightnessBar brightnessBar;

    public BrightnessColorWheel() {
        this.colorWheel = new ColorWheel();
        this.brightnessBar = new BrightnessBar();
        this.brightnessBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                colorWheel.setBrightness(1 - brightnessBar.getBrightness());
                colorWheel.repaint();
            }
        });
        this.colorWheel.addColorListener(new ColorListener() {
            @Override
            public void onColorSet(Color color) {
                int i = 1;
                Color inc = color;
                while (inc.getRed() < 255 && inc.getGreen() < 255 & inc.getBlue() < 255) {
                    inc = new Color(inc.getRed() + 1, inc.getGreen() + 1, inc.getBlue() + 1);
                    i++;
                }
                brightnessBar.setBrightness(i / 255F);
                colorWheel.setBrightness(1 - brightnessBar.getBrightness());
                brightnessBar.repaint();
                colorWheel.repaint();
            }

            @Override
            public void onColorRetrieved(Color color) {

            }
        });
        add(PanelHelper.northAndCenterElements(PanelHelper.join(this.colorWheel), PanelHelper.join(this.brightnessBar)));
        setOpaque(false);
    }

    public ColorWheel getColorWheel() {
        return colorWheel;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.colorWheel.setPreferredSize(new Dimension(Math.round((float) preferredSize.getWidth()), Math.round((float) preferredSize.getWidth())));
        this.brightnessBar.setPreferredSize(new Dimension(Math.round((float) preferredSize.getWidth()), Math.round((float) (preferredSize.getHeight() - preferredSize.getWidth()))));
        super.setPreferredSize(preferredSize);
    }
}
