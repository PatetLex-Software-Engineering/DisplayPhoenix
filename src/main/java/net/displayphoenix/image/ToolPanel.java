package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.image.tools.Tool;
import net.displayphoenix.ui.widget.ColorWheel;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;

public class ToolPanel extends JPanel {

    private ColorWheel colorWheel;
    private int cachedButton;

    public ToolPanel(CanvasPanel canvas, Tool... tools) {
        this.colorWheel = new ColorWheel();
        this.colorWheel.setPreferredSize(new Dimension(150, 150));
        JPanel defaultPanel = PanelHelper.join(this.colorWheel);
        for (Tool tool : tools) {
            ToolButton toolButton = new ToolButton(tool);
            toolButton.setPreferredSize(new Dimension(25, 25));
            toolButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            add(PanelHelper.northAndSouthElements(PanelHelper.join(toolButton), defaultPanel));
        }
        ToolPanel toolkit = this;
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cachedButton = -1;
                if (ToolButton.selectedTool != null) {
                    int x = Math.round(e.getX() - (canvas.getWidth() / 2F));
                    int y = Math.round(e.getY() - (canvas.getHeight() / 2F));
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        cachedButton = e.getButton();
                        ToolButton.selectedTool.onLeftClick(toolkit, canvas, Math.round(x / canvas.convergeZoom(1)), Math.round(y / canvas.convergeZoom(1)));
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3) {
                        cachedButton = e.getButton();
                        ToolButton.selectedTool.onRightClick(toolkit, canvas, Math.round(x / canvas.convergeZoom(1)), Math.round(y / canvas.convergeZoom(1)));
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
                    if (cachedButton == MouseEvent.BUTTON1) {
                        ToolButton.selectedTool.onLeftClick(toolkit, canvas, Math.round(x / canvas.convergeZoom(1)), Math.round(y / canvas.convergeZoom(1)));
                    }
                    else if (e.getButton() == MouseEvent.BUTTON3) {
                        ToolButton.selectedTool.onRightClick(toolkit, canvas, Math.round(x / canvas.convergeZoom(1)), Math.round(y / canvas.convergeZoom(1)));
                    }
                }
            }
        });
        setOpaque(canvas.isOpaque());
        setBackground(canvas.getBackground());
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
    }

    public ColorWheel getColorWheel() {
        return colorWheel;
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(getForeground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    }

    private static class ToolButton extends JButton implements MouseListener {

        public static Tool selectedTool;
        private Tool tool;

        public ToolButton(Tool tool) {
            this.tool = tool;
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
