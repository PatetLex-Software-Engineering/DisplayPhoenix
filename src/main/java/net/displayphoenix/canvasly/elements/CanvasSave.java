package net.displayphoenix.canvasly.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.displayphoenix.canvasly.Pixel;
import net.displayphoenix.canvasly.elements.impl.FontElement;
import net.displayphoenix.canvasly.elements.impl.ImageElement;
import net.displayphoenix.util.ImageHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private transient Map<Layer, List<StaticElement>> cacheElements;
    private transient Map<Layer, Pixel[][]> cachedPixels;
    private List<String> hiddenLayers;
    private Map<String, Pixel[][]> pixels;
    private Map<String, List<CanvasElement>> staticElements;

    public CanvasSave(Map<Layer, Pixel[][]> pixels, Map<Layer, List<StaticElement>> staticElements) {
        this.pixels = new HashMap<>();
        this.staticElements = new HashMap<>();
        this.hiddenLayers = new ArrayList<>();
        for (Layer layer : pixels.keySet()) {
            String layerString = convertLayer(layer);
            if (layer.isHidden())
                this.hiddenLayers.add(layerString);
            this.pixels.put(layerString, new Pixel[pixels.get(layer).length][pixels.get(layer)[0].length]);
            for (int i = 0; i < pixels.get(layer).length; i++) {
                for (int j = 0; j < pixels.get(layer)[0].length; j++) {
                    this.pixels.get(layerString)[i][j] = pixels.get(layer)[i][j];
                }
            }
        }
        for (Layer layer : staticElements.keySet()) {
            String layerString = convertLayer(layer);
            this.staticElements.put(layerString, new ArrayList<>());
            for (StaticElement staticElement : staticElements.get(layer)) {
                this.staticElements.get(layerString).add(new CanvasElement(staticElement));
            }
        }
    }

    public Map<Layer, Pixel[][]> getPixels() {
        if (this.cachedPixels != null) {
            return this.cachedPixels;
        }
        Map<Layer, Pixel[][]> newPixels = new HashMap<>();
        for (String layerString : this.pixels.keySet()) {
            Layer layer = convertString(layerString);
            if (this.hiddenLayers.contains(layerString))
                layer.setHidden(true);
            newPixels.put(layer, new Pixel[this.pixels.get(layerString).length][this.pixels.get(layerString)[0].length]);
            for (int i = 0; i < this.pixels.get(layerString).length; i++) {
                for (int j = 0; j < this.pixels.get(layerString)[0].length; j++) {
                    newPixels.get(layer)[i][j] = this.pixels.get(layerString)[i][j];
                }
            }
        }
        this.cachedPixels = newPixels;
        return newPixels;
    }

    public Map<Layer, List<StaticElement>> getStaticElements() {
        if (this.cacheElements != null) {
            return this.cacheElements;
        }
        Map<Layer, List<StaticElement>> newElements = new HashMap<>();
        for (String layerString : this.staticElements.keySet()) {
            Layer layer = convertString(layerString);
            if (this.hiddenLayers.contains(layerString))
                layer.setHidden(true);
            newElements.put(layer, new ArrayList<>());
            for (CanvasElement canvasElement : this.staticElements.get(layerString)) {
                Element element = null;
                if (canvasElement.type.equalsIgnoreCase("image")) {
                    element = new ImageElement(ImageHelper.fromPath(canvasElement.defaultValue).getImage(), canvasElement.defaultValue);
                } else if (canvasElement.type.equalsIgnoreCase("text")) {
                    element = new FontElement(canvasElement.defaultValue, new Color(canvasElement.r, canvasElement.g, canvasElement.b, canvasElement.a), 1);
                }
                element.setScaleFactor(canvasElement.scale);
                StaticElement.Properties properties = new StaticElement.Properties();
                if (canvasElement.parse) {
                    properties.setParse();
                }
                if (canvasElement.overlay) {
                    properties.setOverlay();
                }
                newElements.get(layer).add(new StaticElement(element, canvasElement.staticElement.getX(), canvasElement.staticElement.getY(), properties));
            }
        }
        this.cacheElements = newElements;
        return newElements;
    }

    public String toSave() {
        return this.toString();
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    private String convertLayer(Layer layer) {
        return String.valueOf(layer.getIndex());
    }

    private Layer convertString(String string) {
        return new Layer(Integer.parseInt(string));
    }

    public static CanvasSave fromSave(String string) {
        return gson.fromJson(string, CanvasSave.class);
    }

    private static class CanvasElement {
        public StaticElement staticElement;
        public String type;
        public String defaultValue;
        public float scale;
        public int r;
        public int g;
        public int b;
        public int a = 255;
        public boolean parse;
        public boolean overlay;

        public CanvasElement(StaticElement staticElement) {
            this.staticElement = staticElement;
            this.type = staticElement.getElement() instanceof FontElement ? "font" : "image";
            this.defaultValue = staticElement.getElement() instanceof FontElement ? ((FontElement) staticElement.getElement()).getText() : ((ImageElement) staticElement.getElement()).getPath();
            this.scale = staticElement.getElement().getScaleFactor();
            if (staticElement.getElement() instanceof ColorableElement) {
                this.r = ((ColorableElement) staticElement.getElement()).getColor().getRed();
                this.g = ((ColorableElement) staticElement.getElement()).getColor().getGreen();
                this.b = ((ColorableElement) staticElement.getElement()).getColor().getBlue();
                this.a = ((ColorableElement) staticElement.getElement()).getColor().getAlpha();
            }
            this.parse = staticElement.getProperties().shouldParse();
            this.overlay = staticElement.getProperties().isOverlay();
        }
    }
}
