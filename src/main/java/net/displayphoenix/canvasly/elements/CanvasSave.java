package net.displayphoenix.canvasly.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.canvasly.Pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanvasSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<Layer, Pixel[][]> pixels;
    private Map<Layer, List<StaticElement>> staticElements;

    public CanvasSave(Map<Layer, Pixel[][]> pixels, Map<Layer, List<StaticElement>> staticElements) {
        this.pixels = pixels;
        this.staticElements = staticElements;
    }

    public Map<Layer, Pixel[][]> getPixels() {
        return pixels;
    }

    public Map<Layer, List<StaticElement>> getStaticElements() {
        return staticElements;
    }

    public String toSave() {
        return this.toString();
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        Map<String, String[][]> pixelsJson = new HashMap<>();
        Map<String, String[]> elementsJson = new HashMap<>();
        for (Layer layer : this.pixels.keySet()) {
            String layerJson = gson.toJson(layer);
            pixelsJson.put(layerJson, new String[this.pixels.get(layer).length][this.pixels.get(layer)[0].length]);
            for (int i = 0; i < this.pixels.get(layer).length; i++) {
                for (int j = 0; j < this.pixels.get(layer)[0].length; j++) {
                    pixelsJson.get(layerJson)[i][j] = this.pixels.get(layer)[i][j] != null ? gson.toJson(this.pixels.get(layer)[i][j]) : null;
                }
            }
        }
        for (Layer layer : this.staticElements.keySet()) {
            elementsJson.put(gson.toJson(layer), new String[this.staticElements.get(layer).size()]);
            for (int i = 0; i < this.staticElements.get(layer).size(); i++) {
                elementsJson.get(layer)[i] = gson.toJson(this.staticElements.get(layer).get(i));
            }
        }
        object.add("pixels", gson.fromJson(gson.toJson(pixelsJson), JsonObject.class));
        object.add("elements", gson.fromJson(gson.toJson(elementsJson), JsonObject.class));
        return gson.toJson(object);
    }

    public static CanvasSave fromSave(String string) {
        JsonObject object = gson.fromJson(string, JsonObject.class);
        Map<String, String[][]> pixelsSave = gson.fromJson(object.get("pixels"), new TypeToken<Map<String, String[][]>>() {}.getType());
        Map<String, String[]> elements = gson.fromJson(object.get("elements"), new TypeToken<Map<String, String[]>>() {}.getType());
        Map<Layer, Pixel[][]> newPixels = new HashMap<>();
        for (String layerJson : pixelsSave.keySet()) {
            Layer layer = gson.fromJson(layerJson, Layer.class);
            newPixels.put(layer, new Pixel[pixelsSave.get(layerJson).length][pixelsSave.get(layerJson)[0].length]);
            for (int i = 0; i < pixelsSave.get(layerJson).length; i++) {
                for (int j = 0; j < pixelsSave.get(layerJson)[0].length; j++) {
                    newPixels.get(layer)[i][j] = gson.fromJson(pixelsSave.get(layerJson)[i][j], Pixel.class);
                }
            }
        }
        Map<Layer, List<StaticElement>> newElements = new HashMap<>();
        for (String layer : elements.keySet()) {
            newElements.put(gson.fromJson(layer, Layer.class), new ArrayList<>());
            for (String element : elements.get(layer)) {
                newElements.get(layer).add(gson.fromJson(element, StaticElement.class));
            }
        }
        return new CanvasSave(newPixels, newElements);
    }
}
