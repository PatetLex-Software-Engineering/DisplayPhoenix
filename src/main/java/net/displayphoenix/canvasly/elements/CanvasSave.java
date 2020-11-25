package net.displayphoenix.canvasly.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.displayphoenix.canvasly.Pixel;

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
        return gson.toJson(this);
    }

    public static CanvasSave fromSave(String string) {
        return gson.fromJson(string, CanvasSave.class);
    }
}
