package net.displayphoenix.ui.widget.color;

import net.displayphoenix.ui.interfaces.ColorListener;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class RGBABrightnessColorWheel extends JPanel {

    private ColorWheel colorWheel;
    private BrightnessBar brightnessBar;
    private AlphaBar alphaBar;

    public RGBABrightnessColorWheel() {
        this.colorWheel = new ColorWheel();
        this.brightnessBar = new BrightnessBar();
        this.alphaBar = new AlphaBar();
        this.brightnessBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                colorWheel.setBrightness(1 - brightnessBar.getValue());
                colorWheel.repaint();
            }
        });
        this.brightnessBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                colorWheel.setBrightness(1 - brightnessBar.getValue());
                colorWheel.repaint();
            }
        });
        this.alphaBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                colorWheel.setAlpha(1 - alphaBar.getValue());
                colorWheel.repaint();
            }
        });
        this.alphaBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                colorWheel.setAlpha(1 - alphaBar.getValue());
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
                brightnessBar.setValue(i / 255F);
                alphaBar.setValue(color.getAlpha() / 255F);
                colorWheel.setAlpha(alphaBar.getValue());
                colorWheel.setBrightness(1 - brightnessBar.getValue());
                brightnessBar.repaint();
                alphaBar.repaint();
                colorWheel.repaint();
            }

            @Override
            public void onColorRetrieved(Color color) {

            }
        });
        add(PanelHelper.northAndCenterElements(PanelHelper.join(this.colorWheel), PanelHelper.northAndCenterElements(PanelHelper.join(this.brightnessBar), PanelHelper.join(this.alphaBar))));
        setOpaque(false);
    }

    public ColorWheel getColorWheel() {
        return colorWheel;
    }

    public BrightnessBar getBrightnessBar() {
        return brightnessBar;
    }

    public AlphaBar getAlphaBar() {
        return alphaBar;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        this.colorWheel.setPreferredSize(new Dimension(Math.round((float) preferredSize.getWidth()), Math.round((float) preferredSize.getWidth())));
        this.brightnessBar.setPreferredSize(new Dimension(Math.round((float) preferredSize.getWidth()), Math.round((float) (preferredSize.getHeight() - preferredSize.getWidth()) / 3F)));
        this.alphaBar.setPreferredSize(new Dimension(Math.round((float) preferredSize.getWidth()), Math.round((float) (preferredSize.getHeight() - preferredSize.getWidth()) / 3F)));
        super.setPreferredSize(preferredSize);
    }
}
