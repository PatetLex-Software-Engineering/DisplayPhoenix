package net.displayphoenix.canvasly.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.displayphoenix.canvasly.Pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private List<String> hiddenLayers;
    private Map<String, Pixel[][]> pixels;
    private Map<String, List<StaticElement>> staticElements;

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
                this.staticElements.get(layerString).add(staticElement);
            }
        }
    }

    public Map<Layer, Pixel[][]> getPixels() {
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
        return newPixels;
    }

    public Map<Layer, List<StaticElement>> getStaticElements() {
        Map<Layer, List<StaticElement>> newElements = new HashMap<>();
        for (String layerString : this.staticElements.keySet()) {
            Layer layer = convertString(layerString);
            if (this.hiddenLayers.contains(layerString))
                layer.setHidden(true);
            newElements.put(layer, new ArrayList<>());
            for (StaticElement staticElement : this.staticElements.get(layerString)) {
                newElements.get(layer).add(staticElement);
            }
        }
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
}
