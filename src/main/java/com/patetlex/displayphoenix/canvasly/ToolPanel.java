package com.patetlex.displayphoenix.canvasly;

import com.patetlex.displayphoenix.Application;
import com.patetlex.displayphoenix.canvasly.components.FontSettingComponent;
import com.patetlex.displayphoenix.canvasly.components.IntegerSettingComponent;
import com.patetlex.displayphoenix.canvasly.interfaces.ISettingComponent;
import com.patetlex.displayphoenix.canvasly.tools.FontSetting;
import com.patetlex.displayphoenix.canvasly.tools.IntegerSetting;
import com.patetlex.displayphoenix.canvasly.tools.Setting;
import com.patetlex.displayphoenix.canvasly.tools.Tool;
import com.patetlex.displayphoenix.canvasly.tools.impl.*;
import com.patetlex.displayphoenix.lang.Localizer;
import com.patetlex.displayphoenix.ui.widget.TextField;
import com.patetlex.displayphoenix.ui.widget.color.RGBABrightnessColorWheel;
import com.patetlex.displayphoenix.ui.widget.color.ColorWheel;
import com.patetlex.displayphoenix.canvasly.util.CanvasHelper;
import com.patetlex.displayphoenix.util.ComponentHelper;
import com.patetlex.displayphoenix.util.ImageHelper;
import com.patetlex.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class ToolPanel extends JPanel {

    private RGBABrightnessColorWheel colorWheel;

    private int cachedButton;
    private JPanel settingsPanel;
    private List<ISettingComponent> settingComponents = new ArrayList<>();

    public ToolPanel(CanvasPanel canvas, Tool... tools) {
        this.colorWheel = new RGBABrightnessColorWheel();
        this.colorWheel.setPreferredSize(new Dimension(150, 300));
        JPanel defaultPanel = PanelHelper.join(this.colorWheel);
        JPanel toolPanel = new JPanel(new GridLayout(Math.round((float) Math.ceil(tools.length / 2F)), 2));
        toolPanel.setOpaque(false);
        for (Tool tool : tools) {
            ToolButton toolButton = new ToolButton(this, tool);
            toolButton.setPreferredSize(new Dimension(25, 25));
            toolButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toolPanel.add(PanelHelper.join(toolButton));
        }
        this.settingsPanel = PanelHelper.join();
        TextField rField = new TextField("R");
        rField.setPreferredSize(new Dimension(50, 30));
        rField.setHorizontalAlignment(SwingConstants.CENTER);
        TextField gField = new TextField("G");
        gField.setPreferredSize(new Dimension(50, 30));
        gField.setHorizontalAlignment(SwingConstants.CENTER);
        TextField bField = new TextField("B");
        bField.setPreferredSize(new Dimension(50, 30));
        bField.setHorizontalAlignment(SwingConstants.CENTER);
        rField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colorWheel.getColorWheel().setColor(new Color(Integer.parseInt(rField.getText()), Integer.parseInt(gField.getText()), Integer.parseInt(bField.getText())), 2F);
            }
        });
        gField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colorWheel.getColorWheel().setColor(new Color(Integer.parseInt(rField.getText()), Integer.parseInt(gField.getText()), Integer.parseInt(bField.getText())), 2F);
            }
        });
        bField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colorWheel.getColorWheel().setColor(new Color(Integer.parseInt(rField.getText()), Integer.parseInt(gField.getText()), Integer.parseInt(bField.getText())), 2F);
            }
        });
        colorWheel.getColorWheel().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Color color = colorWheel.getColorWheel().getColor();
                rField.setText(String.valueOf(Math.round(color.getRed() * (1 - colorWheel.getBrightnessBar().getValue()))));
                gField.setText(String.valueOf(Math.round(color.getGreen() * (1 - colorWheel.getBrightnessBar().getValue()))));
                bField.setText(String.valueOf(Math.round(color.getBlue() * (1 - colorWheel.getBrightnessBar().getValue()))));
            }
        });
        colorWheel.getBrightnessBar().addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Color color = colorWheel.getColorWheel().getColor();
                rField.setText(String.valueOf(Math.round(color.getRed() * (1 - colorWheel.getBrightnessBar().getValue()))));
                gField.setText(String.valueOf(Math.round(color.getGreen() * (1 - colorWheel.getBrightnessBar().getValue()))));
                bField.setText(String.valueOf(Math.round(color.getBlue() * (1 - colorWheel.getBrightnessBar().getValue()))));
            }
        });
        add(PanelHelper.northAndSouthElements(toolPanel, PanelHelper.northAndCenterElements(this.settingsPanel, PanelHelper.northAndCenterElements(defaultPanel, PanelHelper.join(rField, gField, bField)))));
        ToolPanel toolkit = this;
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cachedButton = -1;
                if (ToolButton.selectedTool != null) {
                    Point pixel = CanvasHelper.getCanvasPixelFromPoint(canvas, e.getX(), e.getY());
                    int x = pixel.x;
                    int y = pixel.y;
                    if (CanvasHelper.isPointInBounds(canvas, x, y)) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            cachedButton = e.getButton();
                            ToolButton.selectedTool.onLeftClick(toolkit, canvas, x, y, settingComponents.toArray(new ISettingComponent[settingComponents.size()]));
                        }
                    }
                }
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (cachedButton > 0 && ToolButton.selectedTool != null) {
                    Point pixel = CanvasHelper.getCanvasPixelFromPoint(canvas, e.getX(), e.getY());
                    int x = pixel.x;
                    int y = pixel.y;
                    if (CanvasHelper.isPointInBounds(canvas, x, y)) {
                        if (cachedButton == MouseEvent.BUTTON1) {
                            ToolButton.selectedTool.onLeftClick(toolkit, canvas, x, y, settingComponents.toArray(new ISettingComponent[settingComponents.size()]));
                        }
                    }
                }
            }
        });
        setOpaque(canvas.isOpaque());
        setBackground(canvas.getBackground());
        setForeground(Application.getTheme().getColorTheme().getPrimaryColor());
    }

    public ColorWheel getColorWheel() {
        return colorWheel.getColorWheel();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    protected void loadSettings() {
        this.settingsPanel.removeAll();
        this.settingComponents.clear();
        if (ToolButton.selectedTool != null && ToolButton.selectedTool.getSettings() != null) {
            JPanel panel = PanelHelper.join();
            for (Setting setting : ToolButton.selectedTool.getSettings()) {
                JLabel label = new JLabel(Localizer.translate(setting.getTranslationKey()));
                ComponentHelper.themeComponent(label);
                ComponentHelper.deriveFont(label, 14);
                if (setting instanceof IntegerSetting) {
                    IntegerSettingComponent integerSettingComponent = new IntegerSettingComponent((IntegerSetting) setting);
                    integerSettingComponent.setPreferredSize(new Dimension(getWidth(), Math.round(getWidth() * 0.2F)));
                    this.settingComponents.add(integerSettingComponent);
                    panel = PanelHelper.northAndCenterElements(panel, PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(integerSettingComponent)));
                }
                else if (setting instanceof FontSetting) {
                    FontSettingComponent fontSettingComponent = new FontSettingComponent((FontSetting) setting);
                    fontSettingComponent.setPreferredSize(new Dimension(getWidth(), Math.round(getWidth() * 0.2F)));
                    this.settingComponents.add(fontSettingComponent);
                    panel = PanelHelper.northAndCenterElements(panel, PanelHelper.northAndCenterElements(PanelHelper.join(label), PanelHelper.join(fontSettingComponent)));
                }
            }
            this.settingsPanel.add(panel);
        }
        this.settingsPanel.revalidate();
        this.settingsPanel.repaint();
    }

    public static Tool[] getBasicTools() {
        return new Tool[] {new PencilTool(), new BucketTool(), new PickerTool(), new EraserTool(), new ImageTool(), new TextTool()};
    }

    private static class ToolButton extends JButton implements MouseListener {

        public static Tool selectedTool;
        private Tool tool;
        private ToolPanel toolkit;

        public ToolButton(ToolPanel toolkit, Tool tool) {
            this.tool = tool;
            this.toolkit = toolkit;
            setForeground(Application.getTheme().getColorTheme().getAccentColor().darker().darker());
            setBackground(Application.getTheme().getColorTheme().getAccentColor());
            setBorderPainted(false);
            setContentAreaFilled(false);
            addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (selectedTool == this.tool) {
                g.setColor(getBackground());
                g.fillRect(0,0, getWidth(), getWidth());
            }
            ImageIcon toolIcon = ImageHelper.resize(this.tool.getIcon(), getWidth());
            g.drawImage(toolIcon.getImage(), 0, 0, toolIcon.getIconWidth(), toolIcon.getIconHeight(), null);
            g.setColor(getForeground());
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                selectedTool = selectedTool == this.tool ? null : this.tool;
                this.toolkit.repaint();
                this.toolkit.loadSettings();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
