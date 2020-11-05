package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.ui.widget.BrightnessColorWheel;
import net.displayphoenix.ui.widget.ColorWheel;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ToolPanel extends JPanel {

    private BrightnessColorWheel colorWheel;
    private int cachedButton;

    public ToolPanel(CanvasPanel canvas, Tool... tools) {
        this.colorWheel = new BrightnessColorWheel();
        this.colorWheel.setPreferredSize(new Dimension(150, 200));
        JPanel defaultPanel = PanelHelper.join(this.colorWheel);
        JPanel toolPanel = PanelHelper.join();
        for (Tool tool : tools) {
            ToolButton toolButton = new ToolButton(this, tool);
            toolButton.setPreferredSize(new Dimension(25, 25));
            toolButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toolPanel.add(PanelHelper.join(toolButton));
        }
        toolPanel.setLayout(new GridLayout(5, 2));
        add(PanelHelper.northAndSouthElements(toolPanel, defaultPanel));
        ToolPanel toolkit = this;
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cachedButton = -1;
                if (ToolButton.selectedTool != null) {
                    int x = Math.round(e.getX() - (canvas.getWidth() / 2F));
                    int y = Math.round(e.getY() - (canvas.getHeight() / 2F));
                    x = Math.round((float) Math.floor(((x - canvas.getCanvasX()) / canvas.convergeZoom(1)) + (canvas.getCanvasWidth() / 2)));
                    y = Math.round((float) Math.floor(((y - canvas.getCanvasY()) / canvas.convergeZoom(1)) + (canvas.getCanvasHeight() / 2)));
                    if ((x >= 0 && x < canvas.getCanvasWidth()) && (y >= 0 && y < canvas.getCanvasHeight())) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            cachedButton = e.getButton();
                            ToolButton.selectedTool.onLeftClick(toolkit, canvas, x, y);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            cachedButton = e.getButton();
                            ToolButton.selectedTool.onRightClick(toolkit, canvas, x, y);
                        }
                    }
                }
            }
        });
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (cachedButton > 0 && ToolButton.selectedTool != null) {
                    int x = Math.round(e.getX() - (canvas.getWidth() / 2F));
                    int y = Math.round(e.getY() - (canvas.getHeight() / 2F));
                    x = Math.round((float) Math.floor(((x - canvas.getCanvasX()) / canvas.convergeZoom(1)) + (canvas.getCanvasWidth() / 2)));
                    y = Math.round((float) Math.floor(((y - canvas.getCanvasY()) / canvas.convergeZoom(1)) + (canvas.getCanvasHeight() / 2)));
                    if ((x >= 0 && x < canvas.getCanvasWidth()) && (y >= 0 && y < canvas.getCanvasHeight())) {
                        if (cachedButton == MouseEvent.BUTTON1) {
                            ToolButton.selectedTool.onLeftClick(toolkit, canvas, x, y);
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            ToolButton.selectedTool.onRightClick(toolkit, canvas, x, y);
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
        g.setColor(getForeground());
        g.fillRect(0, 0, getWidth(), getHeight());
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
