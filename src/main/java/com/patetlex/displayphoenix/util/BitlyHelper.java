package com.patetlex.displayphoenix.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.patetlex.displayphoenix.bitly.Bitly;
import com.patetlex.displayphoenix.bitly.enums.BitWidgetStyle;
import com.patetlex.displayphoenix.bitly.ui.BitWidget;
import com.patetlex.displayphoenix.file.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
