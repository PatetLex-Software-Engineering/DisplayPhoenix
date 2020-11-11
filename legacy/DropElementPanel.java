package net.displayphoenix.canvasly;

import net.displayphoenix.Application;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * @author TBroski
 */
public class DropElementPanel extends JPanel {

    private CanvasPanel canvas;

    private ImageIcon hoveredIcon;

    public DropElementPanel(ImageIcon backgroundImage, ImageIcon... images) {
        this.canvas = new BackgroundCanvasPanel(backgroundImage);
        this.canvas.setBackground(Application.getTheme().getColorTheme().getPrimaryColor().darker());

        JPanel dropPanel = new JPanel();
        dropPanel.setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        DefaultListModel<ImageIcon> imageIconModel = new DefaultListModel<>();
        for (ImageIcon imageIcon : images) {
            imageIconModel.addElement(imageIcon);
        }
        JList<ImageIcon> imageIconList = new JList<>(imageIconModel);
        imageIconList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                canvas.addToCanvas(imageIconModel.elementAt(imageIconList.locationToIndex(e.getPoint())), 0);
            }
        });
        imageIconList.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoveredIcon = imageIconModel.elementAt(imageIconList.locationToIndex(e.getPoint()));
                imageIconList.repaint();
            }
        });
        imageIconList.setBackground(Application.getTheme().getColorTheme().getSecondaryColor());
        imageIconList.setCellRenderer(new ImageIconListRenderer(() -> this.hoveredIcon));
        JScrollPane scrollBar = new JScrollPane(imageIconList);
        scrollBar.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dropPanel.add(imageIconList);

        add(PanelHelper.westAndCenterElements(dropPanel, PanelHelper.join(this.canvas)));
    }

    public CanvasPanel getCanvas() {
        return canvas;
    }

    private static class ImageIconListRenderer extends JLabel implements ListCellRenderer<ImageIcon> {

        private IGetIcon getIcon;

        private ImageIcon value;

        public ImageIconListRenderer(IGetIcon getIcon) {
            this.getIcon = getIcon;
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends ImageIcon> list, ImageIcon value, int index, boolean isSelected, boolean cellHasFocus) {
            setOpaque(false);
            setIcon(ImageHelper.resize(value, Math.round(value.getIconWidth() * 0.25F), Math.round(value.getIconHeight() * 0.25F)));
            this.value = value;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.getIcon.getIcon() == this.value) {
                g.setColor(Application.getTheme().getColorTheme().getAccentColor());
                g.drawRect(0, 0, this.getWidth(), this.getHeight());
            }
        }
    }

    private interface IGetIcon {
        ImageIcon getIcon();
    }
}
