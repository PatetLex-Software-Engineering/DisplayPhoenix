package com.patetlex.displayphoenix.canvasly.elements;

import com.google.gson.*;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
<<<<<<< HEAD
=======
import com.patetlex.displayphoenix.bitly.elements.BitSave;
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
import com.patetlex.displayphoenix.canvasly.Pixel;
import com.patetlex.displayphoenix.canvasly.elements.impl.FontElement;
import com.patetlex.displayphoenix.canvasly.elements.impl.ImageElement;
import com.patetlex.displayphoenix.canvasly.util.CanvasHelper;
import com.patetlex.displayphoenix.file.Data;
import com.patetlex.displayphoenix.util.GsonHelper;
<<<<<<< HEAD
=======
import com.patetlex.displayphoenix.util.ListHelper;
import jdk.nashorn.internal.ir.annotations.Ignore;
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
<<<<<<< HEAD
=======
import java.lang.reflect.Type;
>>>>>>> 47a47a09d2902902588a944b173e5c8c191c9a2d
import java.util.*;
import java.util.List;

@JsonAdapter(CanvasSave.Adapter.class)
public class CanvasSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private Map<Layer, List<StaticElement>> elements;
    private Map<Layer, Pixel[][]> pixels;

    public CanvasSave(Map<Layer, Pixel[][]> pixels, Map<Layer, List<StaticElement>> staticElements) {
        this.pixels = pixels;
        this.elements = staticElements;
    }

    public Map<Layer, Pixel[][]> getPixels() {
        return this.pixels;
    }

    public Map<Layer, List<StaticElement>> getStaticElements() {
        return this.elements;
    }

    public String toSave() {
        return this.toString();
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();

        JsonArray layerArray = new JsonArray();
        for (Layer layer : this.pixels.keySet()) {
            JsonObject layerObject = new JsonObject();
            layerObject.addProperty("layer", layer.getIndex());
            layerObject.addProperty("hidden", layer.isHidden());
            JsonArray elements = new JsonArray();
            for (Layer layer1 : this.elements.keySet()) {
                if (layer.getIndex() == layer1.getIndex()) {
                    for (StaticElement staticElement : this.elements.get(layer1)) {
                        CanvasElement canvasElement = new CanvasElement(staticElement);
                        elements.add(gson.fromJson(gson.toJson(canvasElement), JsonObject.class));
                    }
                    break;
                }
            }
            layer.setHidden(false);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(CanvasHelper.draw(this.pixels.get(layer)), "PNG", outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] image = outputStream.toByteArray();
            layerObject.addProperty("pixels", Base64.getEncoder().encodeToString(image));
            layerObject.add("elements", elements);
            layerArray.add(layerObject);
        }
        object.add("layers", layerArray);

        return gson.toJson(object);
    }

    public static CanvasSave fromSave(String string) {
        JsonObject object = gson.fromJson(string, JsonObject.class);

        Map<Layer, List<StaticElement>> elements = new HashMap<>();
        Map<Layer, Pixel[][]> pixels = new HashMap<>();
        for (JsonElement element : object.get("layers").getAsJsonArray()) {
            JsonObject elementObject = element.getAsJsonObject();
            Layer layer = new Layer(elementObject.get("layer").getAsInt());
            layer.setHidden(elementObject.get("hidden").getAsBoolean());

            List<StaticElement> parsedElements = new ArrayList<>();
            for (JsonElement staticElement : elementObject.get("elements").getAsJsonArray()) {
                JsonObject staticElementObject = staticElement.getAsJsonObject();
                CanvasElement canvasElement = gson.fromJson(staticElementObject, CanvasElement.class);
                Element cElement = null;
                if (canvasElement.type.equalsIgnoreCase("image")) {
                    try {
                        cElement = new ImageElement(ImageIO.read(Data.find("/bitly/elements/" + canvasElement.defaultValue)), canvasElement.defaultValue);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (canvasElement.type.equalsIgnoreCase("text")) {
                    cElement = new FontElement(canvasElement.defaultValue, new Color(canvasElement.r, canvasElement.g, canvasElement.b, canvasElement.a), 1);
                }
                cElement.setScaleFactor(canvasElement.scale);
                StaticElement.Properties properties = new StaticElement.Properties();
                if (canvasElement.parse) {
                    properties.setParse();
                }
                if (canvasElement.overlay) {
                    properties.setOverlay();
                }
                parsedElements.add(new StaticElement(cElement, canvasElement.staticElement.getX(), canvasElement.staticElement.getY(), properties));
            }
            elements.put(layer, parsedElements);

            byte[] image = Base64.getDecoder().decode(elementObject.get("pixels").getAsString());
            ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
            try {
                BufferedImage bufferedImage = ImageIO.read(inputStream);
                Pixel[][] pixelArray = new Pixel[bufferedImage.getWidth()][bufferedImage.getHeight()];
                for (int i = 0; i < bufferedImage.getWidth(); i++) {
                    for (int j = 0; j < bufferedImage.getHeight(); j++) {
                        Color color = new Color(bufferedImage.getRGB(i, j));
                        if (color.getRed() > 0 || color.getGreen() > 0 || color.getBlue() > 0) {
                            pixelArray[i][j] = new Pixel(color);
                        }
                    }
                }
                pixels.put(layer, pixelArray);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new CanvasSave(pixels, elements);
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

    public static class Adapter extends TypeAdapter<CanvasSave> {
        @Override
        public void write(JsonWriter jsonWriter, CanvasSave canvasSave) throws IOException {
            jsonWriter.jsonValue(canvasSave.toSave());
        }

        @Override
        public CanvasSave read(JsonReader jsonReader) throws IOException {
            return CanvasSave.fromSave(GsonHelper.read(jsonReader));
        }
    }
}
