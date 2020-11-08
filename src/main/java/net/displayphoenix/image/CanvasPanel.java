package net.displayphoenix.image;

import net.displayphoenix.Application;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.file.FileDialog;
import net.displayphoenix.image.elements.Element;
import net.displayphoenix.image.elements.Layer;
import net.displayphoenix.image.elements.StaticElement;
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

    public static final float ZOOM_SENSITIVITY = 10F;

    private List<LayerListener> layerListeners = new ArrayList<>();
    private Map<Layer, Pixel[][]> layerToPixels = new HashMap<>();
    private Map<Layer, CanvasElement> elements = new HashMap<>();
    private Map<Layer, List<StaticElement>> staticElements = new HashMap<>();
    private int canvasWidth;
    private int canvasHeight;
    private int canvasMoveX;
    private int canvasMoveY;
    private boolean moveLocked;
    private boolean zoomLocked;
    private float zoom;
    private Layer selectedLayer;

    private Point cachedPoint;
    private int cachedButton;
    private boolean placedElement;

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
        this.setLayer(0);
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
        else {
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(0, 0, Math.round(convergeZoom(getCanvasWidth())), Math.round(convergeZoom(getCanvasHeight())));
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
        for (Layer layer : layers) {
            ((Graphics2D) g).scale(convergeZoom(1), convergeZoom(1));
            ((Graphics2D) g).translate((cw + this.canvasMoveX) / convergeZoom(1), (ch + this.canvasMoveY) / convergeZoom(1));
            ((Graphics2D) g).translate(-(this.canvasWidth / 2F), -(this.canvasHeight / 2F));
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

                if (this.elements.containsKey(layer)) {
                    CanvasElement element = this.elements.get(layer);
                    ((Graphics2D) g).translate(element.offX, element.offY);
                    ((Graphics2D) g).scale(element.element.getScaleFactor(), element.element.getScaleFactor());
                    element.element.draw(this, g);
                    //((Graphics2D) g).scale(element.element.getScaleFactor() / 1, element.element.getScaleFactor() / 1);
                    g.setColor(Application.getTheme().getColorTheme().getSecondaryColor());
                    g.drawRect(0, 0, element.element.getWidth(this, g), element.element.getHeight(this, g));
                }
            }
            //((Graphics2D) g).setTransform(new AffineTransform());
        }
        List<IRenderAttacher> renderAttachers = new ArrayList<>();
        for (IRenderAttacher renderAttacher : this.renderAttachers) {
            renderAttacher.rendered(g);
            renderAttachers.add(renderAttacher);
        }
        for (IRenderAttacher renderAttacherToDestroy : renderAttachers) {
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

    public void setPixel(int x, int y, Pixel pixel) {
        setPixel(this.selectedLayer, x, y, pixel);
    }
    public void setPixel(int layer, int x, int y, Pixel pixel) {
        setPixel(layerFromIndex(layer), x, y, pixel);
    }
    public void setPixel(Layer layer, int x, int y, Pixel pixel) {
        this.layerToPixels.get(layer)[x][y] = pixel;
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
        this.elements.remove(layer);
        repaint();
    }

    public void addLayer(int layer) {
        addLayer(layerFromIndex(layer));
    }
    public void addLayer(Layer layer) {
        Pixel[][] pixels = new Pixel[this.canvasWidth][this.canvasHeight];
        this.layerToPixels.put(layer, pixels);
        for (LayerListener layerListener : this.layerListeners) {
            layerListener.onLayerAdded(layer);
        }
    }

    public void setLayer(int layer) {
        this.selectedLayer = layerFromIndex(layer);
    }

    public Layer getSelectedLayer() {
        return this.selectedLayer;
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

    public Map<Layer, List<StaticElement>> getStaticElements() {
        return staticElements;
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

    public void setElement(Layer layer, Element element, int offX, int offY) {
        setElement(layer, element, offX, offY, false);
    }
    public void setElement(Layer layer, Element element, int offX, int offY, boolean isStatic) {
        if (this.elements.containsKey(layer)) {
            CanvasElement cElement = this.elements.get(this.getSelectedLayer());
            cElement.element.parse(this, Math.round((float) Math.floor(cElement.offX)), Math.round((float) Math.floor(cElement.offY)));
            this.elements.remove(layer);
        }
        CanvasElement canvasElement = new CanvasElement(element);
        canvasElement.offX = offX;
        canvasElement.offY = offY;
        canvasElement.isStatic = isStatic;
        this.elements.put(layer, canvasElement);
        repaint();
    }

    private CanvasElement rayTraceElement(Point point, Graphics2D graphics) {
        CanvasElement element = this.elements.get(this.getSelectedLayer());
        AffineTransform transform = graphics.getTransform();
        if (element != null) {
            return (point.getX() + element.element.defaultOffsetX(this, graphics) > transform.getTranslateX() && point.getX() + element.element.defaultOffsetX(this, graphics)< transform.getTranslateX() + convergeZoom(element.element.getWidth(this, graphics))) && (point.getY() + element.element.defaultOffsetY(this, graphics) > transform.getTranslateY() && point.getY() + element.element.defaultOffsetY(this, graphics) < transform.getTranslateY() + convergeZoom(element.element.getHeight(this, graphics))) ? element : null;
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        repaint();
        this.renderAttachers.add(g -> {
            CanvasElement element = this.elements.get(this.getSelectedLayer());
            if (rayTraceElement(e.getPoint(), (Graphics2D) g) == null && element != null) {
               element.element.parse(this, Math.round((float) Math.floor(element.offX)), Math.round((float) Math.floor(element.offY)));
               this.elements.remove(this.getSelectedLayer());
               if (element.isStatic) {
                   if (!this.staticElements.containsKey(this.getSelectedLayer())) {
                       this.staticElements.put(this.getSelectedLayer(), new ArrayList<>());
                   }
                   this.staticElements.get(this.getSelectedLayer()).add(new StaticElement(element.element, Math.round(element.offX), Math.round(element.offY)));
               }
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
        float dx = (float) (e.getX() - this.cachedPoint.getX());
        float dy = (float) (e.getY() - this.cachedPoint.getY());
        if (!this.moveLocked && this.cachedButton == MouseEvent.BUTTON3 && ((dx > 1 || dx < -1) || (dy > 1 || dy < -1))) {
            Layer layer = this.getSelectedLayer();
            dx /= convergeZoom(1);
            dy /= convergeZoom(1);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (this.elements.containsKey(layer)) {
                this.elements.get(this.getSelectedLayer()).offX += Math.round(dx);
                this.elements.get(this.getSelectedLayer()).offY += Math.round(dy);
            }
            else {
                for (int i = dx < 0 ? 0 : this.getCanvasWidth() - 1; dx < 0 ? i < this.getCanvasWidth() : i >=0; i += dx < 0 ? 1 : -1) {
                    for (int j = dy < 0 ? 0 : this.getCanvasHeight() - 1; dy < 0 ? j < this.getCanvasHeight() : j >= 0; j += dy < 0 ? 1 : -1) {
                        if ((Math.round(i + dx) >= 0 && Math.round(i + dx) < this.getCanvasWidth()) && (Math.round(j + dy) >= 0 && Math.round(j + dy) < this.getCanvasHeight())) {
                            this.layerToPixels.get(layer)[Math.round(i + dx)][Math.round(j + dy)] = this.layerToPixels.get(layer)[i][j];
                            if ((i + 1 == this.getCanvasWidth() || i == 0) || (j + 1 == this.getCanvasHeight() || j == 0)) {
                                this.layerToPixels.get(layer)[i][j] = null;
                            }
                        }
                    }
                }
            }
            this.cachedPoint = e.getPoint();
            repaint();
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
        CanvasElement canvasElement = this.elements.get(this.getSelectedLayer());
        if (canvasElement != null && canvasElement.element  instanceof KeyListener) {
            ((KeyListener) canvasElement.element).keyTyped(e);
            repaint();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        CanvasElement canvasElement = this.elements.get(this.getSelectedLayer());
        if (canvasElement != null && canvasElement.element  instanceof KeyListener) {
            ((KeyListener) canvasElement.element).keyPressed(e);
            repaint();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        CanvasElement canvasElement = this.elements.get(this.getSelectedLayer());
        if (canvasElement != null && canvasElement.element instanceof KeyListener) {
            ((KeyListener) canvasElement.element).keyReleased(e);
            repaint();
        }
    }

    private interface IRenderAttacher {
        void rendered(Graphics g);
    }

    private static class CanvasElement {

        private Element element;
        private float offX;
        private float offY;
        private boolean isStatic;

        public CanvasElement(Element element) {
            this.element = element;
        }
    }
}
