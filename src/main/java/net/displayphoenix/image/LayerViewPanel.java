package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.image.elements.Layer;
import net.displayphoenix.image.interfaces.LayerListener;
import net.displayphoenix.ui.animation.Clipper;
import net.displayphoenix.util.ImageHelper;
import net.displayphoenix.util.ListHelper;
import net.displayphoenix.util.PanelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class LayerViewPanel extends JPanel implements LayerListener {

    private List<Layer> layers = new ArrayList<>();
    private CanvasPanel canvas;
    private JPanel layerPanel;

    public LayerViewPanel(CanvasPanel canvas) {
        for (Layer layer : canvas.getLayers().keySet()) {
            this.layers.add(layer);
        }
        this.canvas = canvas;
        this.canvas.addLayerListener(this);
        setOpaque(canvas.isOpaque());
        setBackground(canvas.getBackground());
        setForeground(Application.getTheme().getColorTheme().getSecondaryColor());
        this.layerPanel = PanelHelper.join();
        loadLayerPanel();

        CornerLayerButton addLayer = new CornerLayerButton(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus"), new ImageIcon(ImageHelper.overlay(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_plus").getImage(), Color.GREEN, 1F)));
        addLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.addLayer(canvas.getLayers().keySet().size());
            }
        });
        addLayer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addLayer.setPreferredSize(new Dimension(50, 50));
        CornerLayerButton removeLayer = new CornerLayerButton(new ImageIcon(ImageHelper.rotate(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_plus").getImage(), 45F)), new ImageIcon(ImageHelper.overlay(ImageHelper.rotate(ImageHelper.getImage(Application.getTheme().getWidgetStyle().getName() + "_hovered_plus").getImage(), 45F), Color.RED, 1F)));
        removeLayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.removeLayer(canvas.getSelectedLayer());
            }
        });
        removeLayer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeLayer.setPreferredSize(new Dimension(50, 50));
        JPanel layerManipulateLayer = PanelHelper.join(addLayer, removeLayer);

        add(PanelHelper.northAndSouthElements(this.layerPanel, layerManipulateLayer));
        //add(this.layerPanel);
    }

    public CanvasPanel getCanvas() {
        return canvas;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    protected void loadLayerPanel() {
        this.layerPanel.removeAll();
        this.layerPanel.setLayout(new GridLayout(6, 1));
        int i = 0;
        for (Layer layer : layers) {
            if (i < 6) {
                LayerWidget layerWidget = new LayerWidget(this, layer);
                layerWidget.setPreferredSize(new Dimension(150, 75));
                layerWidget.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        canvas.setLayer(layer.getIndex());
                        repaint();
                    }
                });
                layerWidget.setCursor(new Cursor(Cursor.HAND_CURSOR));
                JPanel layerPan = PanelHelper.join(layerWidget);
                layerPan.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
                this.layerPanel.add(layerPan);
                i++;
            }
        }
        repaint();
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(getForeground());
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
    }

    @Override
    public void onLayerRemoved(Layer layer) {
        this.layers.remove(layer);
        loadLayerPanel();
    }

    @Override
    public void onLayerAdded(Layer layer) {
        this.layers.add(layer);
        loadLayerPanel();
    }

    @Override
    public void onLayerPainted(Layer layer, Graphics g) {
        repaint();
    }

    private static class LayerWidget extends JPanel implements MouseListener, MouseMotionListener {

        private Clipper clipper =  new Clipper(this, 0.02F, 0.6F, 0F).smooth();

        private Layer layer;
        private LayerViewPanel layerView;

        private Point cachedPoint;

        public LayerWidget(LayerViewPanel layerView, Layer layer) {
            this.layerView = layerView;
            this.layer = layer;
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(this.layerView.getCanvas().getBackground());
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
            float cw = getWidth() / 2F;
            float ch = getHeight() / 2F;
            g.setColor(getForeground());
            g.drawString(String.valueOf(this.layer.getIndex()), 0, (int) (getHeight() - g.getFontMetrics().getStringBounds(String.valueOf(this.layer.getIndex()), g).getHeight()));
            if (this.layerView.getCanvas().getSelectedLayer() == this.layer.getIndex()) {
                g.setColor(Application.getTheme().getColorTheme().getPrimaryColor());
                g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            }
            for (Element element : this.layerView.getCanvas().getLayers().get(this.layer)) { // * (getWidth() / this.canvas.getWidth())
                ((Graphics2D) g).scale((element.getScaleFactor() * ((float) getWidth() / (float) this.layerView.getCanvas().getWidth())), (element.getScaleFactor() * ((float) getWidth() / (float) this.layerView.getCanvas().getWidth())));
                ((Graphics2D) g).translate((cw) / (element.getScaleFactor() * ((float) getWidth() / (float) this.layerView.getCanvas().getWidth())), (ch) / (element.getScaleFactor() * ((float) getWidth() / (float) this.layerView.getCanvas().getWidth())));
                ((Graphics2D) g).translate(element.getOffsetX(), element.getOffsetY());
                ((Graphics2D) g).translate(-element.getWidth(this.layerView.getCanvas(), g) / 2F, -element.getHeight(this.layerView.getCanvas(), g) / 2F);
                element.draw(this.layerView.getCanvas(), g);
                ((Graphics2D) g).setTransform(new AffineTransform());
            }
            Graphics2D g2d = (Graphics2D) g;
            if (this.clipper.getCurrentValue() != 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, this.clipper.getCurrentValue()));
                g2d.setColor(Application.getTheme().getColorTheme().getPrimaryColor());
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.cachedPoint = e.getPoint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            float dy = (float) (e.getY() - this.cachedPoint.getY());
            this.cachedPoint = e.getPoint();
            if (dy > 1.5F || dy < -1.5F) {
                int index = this.layerView.getLayers().indexOf(this.layer);
                if (index < this.layerView.getLayers().size() && dy > 1.5F) {
                    ListHelper.moveElement(this.layerView.getLayers(), index, index + 1);
                }
                else if (index > 0 && dy < -1.5F) {
                    ListHelper.moveElement(this.layerView.getLayers(), index, index - 1);
                }
                this.layerView.loadLayerPanel();
                repaint();
                revalidate();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.clipper.increment();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            this.clipper.decrement();
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {

        }
    }

    private static class CornerLayerButton extends JButton implements MouseListener {

        private Clipper clipper =  new Clipper(this, 0.01F, 0.6F, 0F).smooth();

        private ImageIcon cornerIcon;
        private ImageIcon hoveredCornerIcon;

        public CornerLayerButton(ImageIcon cornerIcon, ImageIcon hoveredCornerIcon) {
            this.cornerIcon = ImageHelper.resize(cornerIcon, 15);
            this.hoveredCornerIcon = ImageHelper.resize(hoveredCornerIcon, 15);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setBackground(Application.getTheme().getColorTheme().getAccentColor());
            setForeground(Application.getTheme().getColorTheme().getAccentColor().darker().darker());
            addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth() - 8, getHeight() - 8);
            g.setColor(getForeground());
            g.drawRect(0, 0, getWidth() - 8, getHeight() - 8);

            g.drawImage(this.cornerIcon.getImage(), getWidth() - 15, getHeight() - 15, null);
            Graphics2D g2d = (Graphics2D) g;
            if (clipper.getCurrentValue() != 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, clipper.getCurrentValue()));
                g2d.drawImage(this.hoveredCornerIcon.getImage(), getWidth() - 15, getHeight() - 15, null);
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {
            this.clipper.increment();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            this.clipper.decrement();
        }
    }
}
