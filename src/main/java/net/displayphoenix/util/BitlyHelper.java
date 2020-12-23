package net.displayphoenix.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.bitly.Bitly;
import net.displayphoenix.bitly.enums.BitWidgetStyle;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.file.Data;

import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitlyHelper {

    private static final Gson gson = new Gson();

    public static void loadBitResource(String identifier) {
        loadBitResource(identifier, identifier);
    }

    public static void loadBitResource(String identifier, String path) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("bitly/" + path + ".json")));

            StringBuilder output = new StringBuilder();
            String out;
            while ((out = reader.readLine()) != null) {
                output.append(out + "\n");
            }
            Bitly.loadBit(identifier, output.toString(), ImageHelper.renderImage(ImageHelper.getImage("bitly/" + identifier + ".png").getImage()));
            JsonObject bitObject = gson.fromJson(output.toString(), JsonObject.class);
            List<BitWidget[]> widgets = gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>() {}.getType());
            Data.cache(null, "/bitly/");
            Data.cache(null, "/bitly/elements/");
            for (BitWidget[] page : widgets) {
                for (BitWidget widget : page) {
                    if (widget.getStyle() == BitWidgetStyle.CANVAS) {
                        for (String fileName : widget.getExternalFiles(true, null).keySet()) {
                            Data.cache(widget.getExternalFiles(true, null).get(fileName), "/bitly/elements/" + fileName);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
