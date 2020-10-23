package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.image.elements.Layer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener {

    public static final float ZOOM_SENSITIVITY = 1F;
    public static final float MOVE_SENSITIVITY = 1F;

    private Map<Layer, List<Element>> layerToElements = new HashMap<>();
    private int canvasWidth;
    private int canvasHeight;
    private int canvasMoveX;
    private int canvasMoveY;
    private boolean moveLocked;
    private boolean zoomLocked;
    private float zoom;
    private int selectedLayer;

    private Element cachedElement;
    private Point cachedPoint;
    private int cachedButton;

    private List<IRenderAttacher> renderAttachers = new ArrayList<>();

    public CanvasPanel(int canvasWidth, int canvasHeight) {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.cachedPoint = new Point(0,0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        float cw = this.getWidth() / 2F;
        float ch = this.getHeight() / 2F;
        if (convergeZoom(1) > 15) {
            g.translate(Math.round((cw - convergeZoom(this.getCanvasWidth()) / 2F) + convergeZoom(this.canvasMoveX)), Math.round((ch - convergeZoom(this.getCanvasHeight()) / 2F) + convergeZoom(this.canvasMoveY)));
            g.setColor(Color.LIGHT_GRAY);
            for (int xp = 0; xp < convergeZoom(this.getCanvasWidth()); xp += convergeZoom(1)) {
                for (int yp = 0; yp < convergeZoom(this.getCanvasHeight()); yp += convergeZoom(1)) {
                    g.drawLine(xp, yp, xp, Math.round(yp + convergeZoom(1)));
                    g.drawLine(xp, Math.round(yp + convergeZoom(1)), Math.round(xp + convergeZoom(1)), Math.round(yp + convergeZoom(1)));
                    g.drawLine(Math.round(xp + convergeZoom(1)), Math.round(yp + convergeZoom(1)), Math.round(xp + convergeZoom(1)), yp);
                    g.drawLine(xp, yp, Math.round(xp + convergeZoom(1)), yp);
                }
            }
            ((Graphics2D) g).setTransform(new AffineTransform());
        }
        paintElements(g);
    }

    protected void paintElements(Graphics g) {
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        for (Layer layer : this.layerToElements.keySet()) {
            if (!layer.isHidden()) {
                for (Element element : this.layerToElements.get(layer)) {
                    ((Graphics2D) g).scale(convergeZoom(element.getScaleFactor()), convergeZoom(element.getScaleFactor()));
                    //((Graphics2D) g).translate(((cw + element.getOffsetX()) / convergeZoom(element.getScaleFactor())) + (convergeZoom(element.getOffsetX() * element.getScaleFactor() - (element.getWidth(this, g) / 2F))), ((ch + element.getOffsetY()) / convergeZoom(element.getScaleFactor())) + (convergeZoom(element.getOffsetY() * element.getScaleFactor() - (element.getHeight(this, g) / 2F))));
                    ((Graphics2D) g).translate(((cw - convergeZoom(element.getWidth(this, g)) / convergeZoom(2F) + this.canvasMoveX) / convergeZoom(element.getScaleFactor())), (ch - convergeZoom(element.getHeight(this, g)) / convergeZoom(2F) + this.canvasMoveY) / convergeZoom(element.getScaleFactor()));
                    element.draw(this, g);
                    if (element == this.cachedElement) {
                        g.setColor(rayTraceElement(this.cachedPoint, g) == element ? Application.getTheme().getColorTheme().getPrimaryColor() : Application.getTheme().getColorTheme().getSecondaryColor());
                        g.drawRect(element.getOffsetX(), element.getOffsetY(), element.getWidth(this, g), element.getHeight(this, g));
                    }
                    IRenderAttacher renderAttacherToDestroy = null;
                    for (IRenderAttacher renderAttacher : this.renderAttachers) {
                        renderAttacher.rendered(g);
                        renderAttacherToDestroy = renderAttacher;
                        break;
                    }
                    if (renderAttacherToDestroy != null)
                        this.renderAttachers.remove(renderAttacherToDestroy);
                    ((Graphics2D) g).setTransform(new AffineTransform());
                }
            }
        }
    }

    public void addElement(Element element) {
        addElement(this.selectedLayer, element);
    }
    public void addElement(int layer, Element element) {
        addElement(layer, element, 0, 0);
    }
    public void addElement(int layer, Element element, int offX, int offY) {
        Layer layerFrom = layerFromIndex(layer);
        if (!this.layerToElements.containsKey(layerFrom)) {
            this.layerToElements.put(layerFrom, new ArrayList<>());
        }
        element.setOffsetX(offX);
        element.setOffsetY(offY);
        this.layerToElements.get(layerFrom).add(element);
        this.cachedElement = element;
        repaint();
    }

    public void deleteLayer(int layer) {
        this.layerToElements.remove(layerFromIndex(layer));
        repaint();
    }

    public void selectLayer(int layer) {
        this.selectedLayer = layer;
    }

    public int getSelectedLayer() {
        return selectedLayer;
    }

    public void merge() {
        List<Element> elements = new ArrayList<>();
        List<Layer> layers = new ArrayList<>();
        int i = 0;
        for (Layer layer : this.layerToElements.keySet()) {
            if (i != 0) {
                layers.add(layer);
            }
            i++;
        }
        for (Element element : elements) {
            this.layerToElements.get(layerFromIndex(0)).add(element);
        }
        for (Layer layer : layers) {
            this.layerToElements.remove(layer);
        }
        repaint();
    }

    public int getLayerAmount() {
        return this.layerToElements.keySet().size();
    }

    public Layer layerFromIndex(int index) {
        int i = 0;
        for (Layer layer : this.layerToElements.keySet()) {
            if (i == index) {
                return layer;
            }
            i++;
        }
        return new Layer();
    }

    public float convergeZoom(float num) {
        float r = (this.zoom + (ZOOM_SENSITIVITY)) / ZOOM_SENSITIVITY;
        return num * r;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public int getCanvasX() {
        return canvasMoveX;
    }

    public int getCanvasY() {
        return canvasMoveY;
    }

    public void setMoveLocked(boolean locked) {
        this.moveLocked = locked;
    }

    public void setZoomLocked(boolean locked) {
        this.zoomLocked = locked;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public void export(Window parentWindow) {
        float cw = getCanvasWidth() / 2F;
        float ch = getCanvasHeight() / 2F;
        BufferedImage image = new BufferedImage(this.canvasWidth, this.canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();

        for (Layer layer : this.layerToElements.keySet()) {
            if (!layer.isHidden()) {
                for (Element element : this.layerToElements.get(layer)) {
                    graphics2D.scale(element.getScaleFactor(), element.getScaleFactor());
                    graphics2D.translate(((cw - element.getWidth(this, graphics2D) / 2F) / element.getScaleFactor()) + this.canvasMoveX, ((ch - element.getHeight(this, graphics2D) / 2F) / element.getScaleFactor()) + this.canvasMoveY);
                    element.parse(this, graphics2D);
                    graphics2D.setTransform(new AffineTransform());
                }
            }
        }

        DetailedFile savedFile = FileDialog.saveFile(parentWindow, ".png");
        if (savedFile != null) {
            try {
                ImageIO.write(image, "PNG", savedFile.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Element rayTraceElement(Point point, Graphics graphics) {
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        for (Layer layer : this.layerToElements.keySet()) {
            if (!layer.isHidden()) {
                for (Element element : this.layerToElements.get(layer)) {
                    float cwx = element.getOffsetX() + (element.getWidth(this, graphics) / 2F);
                    float cwh = element.getOffsetY() + (element.getHeight(this, graphics) / 2F);
                    cwx += cw;
                    cwh += ch;
                    float dx = (float) (point.getX() - cwx);
                    float dy = (float) (point.getY() - cwh);
                    if (dx < 0)
                        dx *= -1;
                    if (dy < 0)
                        dy += -1;
                    if (dx <= convergeZoom(element.getWidth(this, graphics)) / 2F && dy <= convergeZoom(element.getHeight(this, graphics)) / 2F) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        repaint();
        this.renderAttachers.add(g -> {
           if (rayTraceElement(e.getPoint(), g) != this.cachedElement) {
               this.cachedElement = null;
               repaint();
           }
        });
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.cachedPoint = e.getPoint();
        this.cachedButton = e.getButton();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.cachedButton = 0;
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!this.moveLocked && this.cachedButton == MouseEvent.BUTTON3) {
            float dx = (float) (e.getX() - this.cachedPoint.getX());
            float dy = (float) (e.getY() - this.cachedPoint.getY());
            dx *= MOVE_SENSITIVITY;
            dy *= MOVE_SENSITIVITY;
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (this.cachedElement == null) {
                Layer layer = layerFromIndex(this.selectedLayer);
                if (!layer.isLocked()) {
                    for (Element element : this.layerToElements.get(layer)) {
                        element.setOffsetX(Math.round(element.getOffsetX() + dx));
                        element.setOffsetY(Math.round(element.getOffsetY() + dy));
                    }
                }
            }
            else {
                this.cachedElement.setOffsetX(Math.round(this.cachedElement.getOffsetX() + dx));
                this.cachedElement.setOffsetY(Math.round(this.cachedElement.getOffsetY() + dy));
            }
            this.cachedPoint = e.getPoint();
            repaint();
        }
        else if (this.cachedButton == MouseEvent.BUTTON2) {
            float dx = (float) (e.getX() - this.cachedPoint.getX());
            float dy = (float) (e.getY() - this.cachedPoint.getY());
            dx *= MOVE_SENSITIVITY;
            dy *= MOVE_SENSITIVITY;
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
            this.canvasMoveX += dx;
            this.canvasMoveY += dy;
            this.cachedPoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        this.cachedPoint = e.getPoint();
        repaint();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!this.zoomLocked) {
            this.zoom += e.getPreciseWheelRotation();
/*            if (this.zoom > ZOOM_MAX) {
                this.zoom = ZOOM_MAX;
            } else if (this.zoom < -ZOOM_MAX) {
                this.zoom = -ZOOM_MAX;
            }*/
            if (this.zoom < -(ZOOM_SENSITIVITY * 0.9F))
                this.zoom = -(ZOOM_SENSITIVITY * 0.9F);
            repaint();
        }
    }

    private interface IRenderAttacher {
        void rendered(Graphics g);
    }
}
