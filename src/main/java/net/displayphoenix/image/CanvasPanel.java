package net.displayphoenix.image;

import com.sun.org.apache.bcel.internal.generic.LADD;
import net.displayphoenix.Application;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.image.elements.Layer;
import net.displayphoenix.image.interfaces.LayerListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CanvasPanel extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener, KeyListener {

    public static final float ZOOM_SENSITIVITY = 1F;
    public static final float MOVE_SENSITIVITY = 1F;

    private List<LayerListener> layerListeners = new ArrayList<>();
    //private Map<Layer, List<Element>> layerToElements = new HashMap<>();
    private Map<Layer, Pixel[][]> layerToPixels = new HashMap<>();
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
        this.addKeyListener(this);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.cachedPoint = new Point(0,0);
        this.addLayer(0);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setFocusable(true);
        this.requestFocusInWindow();
        float cw = (this.getWidth() - convergeZoom(this.getCanvasWidth())) / 2F;
        float ch = (this.getHeight() - convergeZoom(this.getCanvasHeight())) / 2F;
        ((Graphics2D) g).translate((cw + this.canvasMoveX), (ch + this.canvasMoveY));
        if (convergeZoom(1) > 2) {
            int j = 0;
            for (int xp = 0; xp < convergeZoom(this.getCanvasWidth() - 0.5F); xp += convergeZoom(1)) {
                int k = 0;
                for (int yp = 0; yp < convergeZoom(this.getCanvasHeight() - 0.5F); yp += convergeZoom(1)) {
                    float v = k + j;
                    g.setColor(v % 2 == 0 ? Color.WHITE : Color.GRAY);
                    g.fillRect(xp, yp, Math.round(convergeZoom(1)), Math.round(convergeZoom(1)));
                    k++;
                }
                j++;
            }
        }
        ((Graphics2D) g).setTransform(new AffineTransform());
        paintPixels(g);
        ((Graphics2D) g).setTransform(new AffineTransform());
        if (convergeZoom(1) > 15) {
            ((Graphics2D) g).translate((cw + this.canvasMoveX), (ch + this.canvasMoveY));
            g.setColor(Color.LIGHT_GRAY);
            for (int xp = 0; xp < convergeZoom(this.getCanvasWidth() - 0.5F); xp += convergeZoom(1)) {
                for (int yp = 0; yp < convergeZoom(this.getCanvasHeight() - 0.5F); yp += convergeZoom(1)) {
                    g.drawLine(xp, yp, xp, Math.round(yp + convergeZoom(1)));
                    g.drawLine(xp, Math.round(yp + convergeZoom(1)), Math.round(xp + convergeZoom(1)), Math.round(yp + convergeZoom(1)));
                    g.drawLine(Math.round(xp + convergeZoom(1)), Math.round(yp + convergeZoom(1)), Math.round(xp + convergeZoom(1)), yp);
                    g.drawLine(xp, yp, Math.round(xp + convergeZoom(1)), yp);
                }
            }
            ((Graphics2D) g).setTransform(new AffineTransform());
        }
    }

    protected void paintPixels(Graphics g) {
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        List<Layer> layers = Arrays.asList(this.layerToPixels.keySet().toArray(new Layer[this.layerToPixels.keySet().size()]));
        Collections.sort(layers, Comparator.comparingInt(Layer::getIndex));
        ((Graphics2D) g).scale(convergeZoom(1), convergeZoom(1));
        ((Graphics2D) g).translate((cw + this.canvasMoveX) / convergeZoom(1), (ch + this.canvasMoveY) / convergeZoom(1));
        ((Graphics2D) g).translate(-(this.canvasWidth / 2F), -(this.canvasHeight / 2F));
        for (Layer layer : layers) {
            if (!layer.isHidden()) {
                for (Pixel[] x : this.layerToPixels.get(layer)) {
                    for (Pixel y : x) {
                        if (y != null) {
                            y.draw(g);
                        }
                        g.translate(0, 1);
                    }
                    g.translate(1, -this.canvasHeight);
                }
                g.translate(-this.canvasWidth, 0);
            }
            IRenderAttacher renderAttacherToDestroy = null;
            for (IRenderAttacher renderAttacher : this.renderAttachers) {
                renderAttacher.rendered(g);
                renderAttacherToDestroy = renderAttacher;
                break;
            }
            if (renderAttacherToDestroy != null)
                this.renderAttachers.remove(renderAttacherToDestroy);
        }
    }

    public Map<Layer, Pixel[][]> getLayers() {
        return this.layerToPixels;
    }

    public void addLayerListener(LayerListener listener) {
        if (!this.layerListeners.contains(listener)) {
            this.layerListeners.add(listener);
        }
    }

    public void setSelectedElement(Element element) {
        this.cachedElement = element;
    }

    public void setPixel(int x, int y, Pixel pixel) {
        setPixel(this.selectedLayer, x, y, pixel);
    }
    public void setPixel(int layer, int x, int y, Pixel pixel) {
        setPixel(layerFromIndex(layer), x, y, pixel);
    }
    public void setPixel(Layer layer, int x, int y, Pixel pixel) {
        this.layerToPixels.get(layer)[x][y] = pixel;
        repaint();
        revalidate();
    }

    public void addElement(Element element) {
        addElement(this.selectedLayer, element);
    }
    public void addElement(int layer, Element element) {
        Layer layerFrom = layerFromIndex(layer);
        if (!this.layerToPixels.containsKey(layerFrom)) {
            addLayer(layerFrom);
        }
        this.cachedElement = element;
        repaint();
        revalidate();
    }

    public void removeLayer(int layer) {
        removeLayer(layerFromIndex(layer));
    }
    public void removeLayer(Layer layer) {
        this.layerToPixels.remove(layer);
        for (Layer otherLayer : this.layerToPixels.keySet()) {
            if (otherLayer.getIndex() > layer.getIndex()) {
                otherLayer.setIndex(otherLayer.getIndex() - 1);
            }
        }
        for (LayerListener layerListener : this.layerListeners) {
            layerListener.onLayerRemoved(layer);
        }
        repaint();
    }

    public void addLayer(int layer) {
        addLayer(layerFromIndex(layer));
    }
    public void addLayer(Layer layer) {
        Pixel[][] pixels = new Pixel[this.canvasWidth][this.canvasHeight];
/*        for (int j = 0; j < this.canvasWidth; j++) {
            for (int k = 0; k < this.canvasHeight; k++) {
                pixels[j][k] = null;
            }
        }*/
        this.layerToPixels.put(layer, pixels);
        for (LayerListener layerListener : this.layerListeners) {
            layerListener.onLayerAdded(layer);
        }
    }

    public void setLayer(int layer) {
        this.selectedLayer = layer;
    }

    public Layer getSelectedLayer() {
        return layerFromIndex(this.selectedLayer);
    }

    public void merge() {
        List<Pixel[]> xPixelsList = new ArrayList<>();
        List<Layer> layers = new ArrayList<>();
        int i = 0;
        for (Layer layer : this.layerToPixels.keySet()) {
            if (i != 0) {
                layers.add(layer);
                for (Pixel[] xPixels : this.layerToPixels.get(layer)) {
                    xPixelsList.add(xPixels);
                }
            }
            i++;
        }
        int j = 0;
        for (Pixel[] xPixels : xPixelsList) {
            int k = 0;
            for (Pixel yPixel : xPixels) {
                this.layerToPixels.get(layers)[j][k] = yPixel;
                k++;
            }
            j++;
        }
        for (Layer layer : layers) {
            removeLayer(layer);
        }
        repaint();
    }

    public int getLayerAmount() {
        return this.layerToPixels.keySet().size();
    }

    private Layer layerFromIndex(int index) {
        for (Layer layer : this.layerToPixels.keySet()) {
            if (layer.getIndex() == index) {
                return layer;
            }
        }
        return new Layer(index);
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
        DetailedFile savedFile = FileDialog.saveFile(parentWindow, ".png");
        if (savedFile != null) {
            try {
                ImageIO.write(getImage(), "PNG", savedFile.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(this.canvasWidth, this.canvasHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics2D = image.createGraphics();

        List<Layer> layers = Arrays.asList(this.layerToPixels.keySet().toArray(new Layer[this.layerToPixels.keySet().size()]));
        Collections.sort(layers, Comparator.comparingInt(Layer::getIndex));
        for (Layer layer : layers) {
            if (!layer.isHidden()) {
                for (Pixel[] x : this.layerToPixels.get(layer)) {
                    for (Pixel y : x) {
                        if (y != null) {
                            y.draw(graphics2D);
                        }
                        graphics2D.translate(0, 1);
                    }
                    graphics2D.translate(1, -this.canvasHeight);
                }
                graphics2D.translate(-this.canvasWidth, 0);
            }
        }
        return image;
    }

    public int getCanvasX() {
        return canvasMoveX;
    }

    public int getCanvasY() {
        return canvasMoveY;
    }

    /*    private Element rayTraceElement(Point point, Graphics graphics) {
        float cw = getWidth() / 2F;
        float ch = getHeight() / 2F;
        List<Layer> layers = Arrays.asList(this.layerToElements.keySet().toArray(new Layer[this.layerToElements.keySet().size()]));
        Collections.reverse(layers);
        for (Layer layer : layers) {
            if (!layer.isHidden()) {
                List<Element> elements = this.layerToElements.get(layer);
                Collections.reverse(elements);
                for (Element element : elements) {
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
    }*/

    @Override
    public void mouseClicked(MouseEvent e) {
        repaint();
/*        this.renderAttachers.add(g -> {
           if (rayTraceElement(e.getPoint(), g) != this.cachedElement) {
               this.cachedElement = null;
               repaint();
           }
        });*/
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
        float dx = (float) (e.getX() - this.cachedPoint.getX());
        float dy = (float) (e.getY() - this.cachedPoint.getY());
        dx *= MOVE_SENSITIVITY;
        dy *= MOVE_SENSITIVITY;
        if (!this.moveLocked && this.cachedButton == MouseEvent.BUTTON3) {
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (this.cachedElement == null) {
                Layer layer = layerFromIndex(this.selectedLayer);
                if (!layer.isLocked()) {

                }
                repaint();
            }
            else if (this.cachedButton == MouseEvent.BUTTON2) {

                repaint();
            }
            this.cachedPoint = e.getPoint();
        }
        else if (this.cachedButton == MouseEvent.BUTTON2) {
            setCursor(new Cursor(Cursor.MOVE_CURSOR));
            this.canvasMoveX += dx;
            this.canvasMoveY += dy;
            this.cachedPoint = e.getPoint();
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        //this.cachedPoint = e.getPoint();
        //repaint();
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

    @Override
    public void keyTyped(KeyEvent e) {
        if (this.cachedElement instanceof KeyListener) {
            ((KeyListener) this.cachedElement).keyTyped(e);
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (this.cachedElement instanceof KeyListener) {
            ((KeyListener) this.cachedElement).keyPressed(e);
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (this.cachedElement instanceof KeyListener) {
            ((KeyListener) this.cachedElement).keyReleased(e);
            repaint();
        }
    }

    private interface IRenderAttacher {
        void rendered(Graphics g);
    }
}
