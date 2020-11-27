package net.displayphoenix.ui.widget;

import net.displayphoenix.Application;
import net.displayphoenix.blockly.elements.workspace.Field;
import net.displayphoenix.lang.Localizer;
import net.displayphoenix.util.ComponentHelper;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ProvisionWidget extends JButton {

    private static Random rand = new Random();
    private Map<String, Integer> colorCache = new HashMap<>();
    private Map<String, Field[]> fieldProvisions = new HashMap<>();
    private String[] provisions;
    private String headBlock;

    private String xml;

    public ProvisionWidget(String[] provisions, String headBlock) {
        this.headBlock = headBlock;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setToolTipText(Localizer.translate("widget.provisions_on_right_click.text"));
        setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Application.openWindow(JFrame.DISPOSE_ON_CLOSE, parentFrame -> {
                        JLabel provisionsLabel = new JLabel(Localizer.translate("blockly.provision.text"));
                        ComponentHelper.themeComponent(provisionsLabel);
                        ComponentHelper.deriveFont(provisionsLabel, 20);
                        JPanel provisionList = PanelHelper.join();
                        if (provisions != null) {
                            for (String provision : provisions) {
                                JLabel label = new JLabel(provision);
                                ComponentHelper.themeComponent(label);
                                ComponentHelper.deriveFont(label, 17);
                                if (!colorCache.containsKey(provision))
                                    colorCache.put(provision, rand.nextInt(360));
                                float hue = colorCache.get(provision);
                                label.setForeground(Color.getHSBColor(hue / 360F, 0.45F, 0.65F));
                                JPanel labelPanel = PanelHelper.join(label);
                                labelPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                                provisionList = PanelHelper.northAndCenterElements(provisionList, labelPanel);
                            }
                        }
                        parentFrame.add(PanelHelper.northAndCenterElements(PanelHelper.join(provisionsLabel), provisionList));
                    }, Math.round(Application.getTheme().getWidth() * 0.3F), Math.round(Application.getTheme().getHeight() * 0.5F));
                }
            }
        });
        this.provisions = provisions;
    }

    public Map<String, Field[]> getFieldProvisions() {
        return fieldProvisions;
    }

    public String[] getProvisions() {
        return provisions;
    }

    public String getXml() {
        return xml;
    }

    public String getHeadBlock() {
        return headBlock;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public void setFieldProvisions(Map<String, Field[]> fieldProvisions) {
        this.fieldProvisions = fieldProvisions;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Application.getTheme().getColorTheme().getAccentColor());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
        g.setColor(getBackground());
        g.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, getHeight() - 12, getHeight() - 12);
        if (this.provisions != null) {
            g.drawImage(ImageHelper.resize(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_info"), 25, 25).getImage(), Math.round((getWidth() - 25) / 2F), Math.round((getHeight() - 25) / 1.5F), this);
        }
        ImageIcon blocklyIcon = ImageHelper.getImage("blockly/icon");
        g.drawImage(blocklyIcon.getImage(), getWidth() - blocklyIcon.getIconWidth(), getHeight() - blocklyIcon.getIconHeight(), null);
    }
}
