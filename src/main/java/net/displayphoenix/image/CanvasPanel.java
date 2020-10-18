package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.image.interfaces.LayerListener;
import net.displayphoenix.util.ImageHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {

    public static final float ZOOM_SENSITIVITY = 1.8F;
    public static final float MOVE_SENSITIVITY = 1.0F;

    private Map<Integer, List<Element>> elements = new HashMap<>();
    private List<LayerListener> layerListeners = new ArrayList<>();
    private Element selectedElement;
    private Element hoveredElement;
    protected float mouseRotation;
    private int currLayer;

    private Point prevMousePoint;
    private int cachedMouseDown;

    public CanvasPanel() {
        this.elements.put(0, new ArrayList<>());
        this.addMouseWheelListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    public void addToCanvas(ImageIcon image, int layer) {
        addToCanvas(image, layer, 0, 0);
    }
    public void addToCanvas(ImageIcon image, int layer, int x, int y) {
        if (!this.elements.containsKey(layer)) {
            this.elements.put(layer, new ArrayList<>());
            for (LayerListener layerListener : this.layerListeners) {
                layerListener.layerAdded(layer);
            }
        }
        this.elements.get(layer).add(new Element(ImageHelper.resize(image, getWidth(), getHeight()), x, y));
        repaint();
    }

    public void toggleLayer(int layer) {
        this.currLayer = layer;
    }

    public void addLayerListener(LayerListener listener) {
        this.layerListeners.add(listener);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintElements(g);
    }

    protected void paintElements(Graphics g) {
        float r = (this.mouseRotation + 100) / 100;
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        for (int layer : this.elements.keySet()) {
            for (Element element : this.elements.get(layer)) {
                float w = element.getImage().getIconWidth() * r;
                float h = element.getImage().getIconHeight() * r;
                g.drawImage(element.getImage().getImage(), Math.round(cw - (w / 2F)) + element.getOffX(), Math.round(ch - (h / 2F)) + element.getOffY(), Math.round(w), Math.round(h), this);
                if (element == this.selectedElement) {
                    g.setColor(Application.getTheme().getColorTheme().getSecondaryColor());
                    g.drawRect(Math.round(cw - (w / 2F)) + element.getOffX(), Math.round(ch - (h / 2F)) + element.getOffY(), Math.round(w), Math.round(h));
                }
                else if (element == this.hoveredElement) {
                    g.setColor(Application.getTheme().getColorTheme().getAccentColor());
                    g.drawRect(Math.round(cw - (w / 2F)) + element.getOffX(), Math.round(ch - (h / 2F)) + element.getOffY(), Math.round(w), Math.round(h));
                }
            }
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.mouseRotation += e.getPreciseWheelRotation() * ZOOM_SENSITIVITY;
        if (this.mouseRotation > 100) {
            this.mouseRotation = 100;
        }
        else if (this.mouseRotation < -100) {
            this.mouseRotation = -100;
        }
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (this.cachedMouseDown == MouseEvent.BUTTON3) {
            float dx = (float) (e.getX() - this.prevMousePoint.getX());
            float dy = (float) (e.getY() - this.prevMousePoint.getY());
            dx *= MOVE_SENSITIVITY;
            dy *= MOVE_SENSITIVITY;
            if (this.selectedElement == null) {
                for (Element element : this.elements.get(this.currLayer)) {
                    element.setOffX(Math.round(element.getOffX() + dx));
                    element.setOffY(Math.round(element.getOffY() + dy));
                }
            }
            else {
                this.selectedElement.setOffX(Math.round(this.selectedElement.getOffX() + dx));
                this.selectedElement.setOffY(Math.round(this.selectedElement.getOffY() + dy));
            }
            this.prevMousePoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.hoveredElement = raytraceElement(e.getPoint());
        repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Element raytracedElement = raytraceElement(e.getPoint());
        if (e.getButton()  == MouseEvent.BUTTON1 && raytracedElement != null) {
            this.selectedElement = raytracedElement;
            repaint();
            return;
        }
        else if (this.selectedElement != null && e.getButton() == MouseEvent.BUTTON3) {
            Application.promptYesOrNo("Delete Element", "Delete this element?", true, new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (int layer : elements.keySet()) {
                        for (Element element : elements.get(layer)) {
                            if (element == selectedElement) {
                                elements.get(layer).remove(selectedElement);
                                if (elements.get(layer).size() == 0) {
                                    elements.remove(layer);
                                    for (LayerListener layerListener : layerListeners) {
                                        layerListener.layerRemoved(layer);
                                    }
                                }
                                break;
                            }
                        }
                    }
                    repaint();
                }
            });
            return;
        }
        this.selectedElement = null;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.prevMousePoint = e.getPoint();
        this.cachedMouseDown = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.cachedMouseDown = 0;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private Element raytraceElement(Point point) {
        float r = (this.mouseRotation + 100) / 100;
        for (Element element : this.elements.get(this.currLayer)) {
            float dx = (float) (point.getX() - (element.getOffX() + (getWidth() / 2F)));
            float dy = (float) (point.getY() - (element.getOffY() + (getHeight() / 2F)));
            if (dx < 0)
                dx *= -1;
            if (dy < 0)
                dy += -1;
            if (dx <= (element.getImage().getIconWidth() * r) / 2F && dy <= (element.getImage().getIconHeight() * r) / 2F) {
                return element;
            }
        }
        return null;
    }

    private static class Element {

        private ImageIcon image;
        private int offX;
        private int offY;

        public Element(ImageIcon image, int offX, int offY) {
            this.image = image;
            this.offX = offX;
            this.offY = offY;
        }

        public ImageIcon getImage() {
            return image;
        }

        public int getOffX() {
            return offX;
        }

        public int getOffY() {
            return offY;
        }

        public void setOffX(int offX) {
            this.offX = offX;
        }

        public void setOffY(int offY) {
            this.offY = offY;
        }
    }
}
