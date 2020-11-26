package net.displayphoenix.bitly.elements;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.bitly.elements.workspace.ImplementedBit;
import net.displayphoenix.bitly.ui.BitArgument;
import net.displayphoenix.bitly.ui.BitWidget;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BitSave {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private ImplementedBit implementedBit;
    private JPanel bitPanel;

    /**
     * Class used to bridge an implemented bit and its respective JPanel
     *
     * @param implementedBit  Implemented bit
     * @param bitPanel  Respective JPanel
     */
    public BitSave(ImplementedBit implementedBit, JPanel bitPanel) {
        this.implementedBit = implementedBit;
        this.bitPanel = bitPanel;
    }

    public ImplementedBit getImplementedBit() {
        return implementedBit;
    }

    public JPanel getBitPanel() {
        return bitPanel;
    }

    public String toSave() {
        return this.toString();
    }

    @Override
    public String toString() {
        JsonObject object = new JsonObject();
        Map<String, Object> widgetToValue = new HashMap<>();
        for (BitWidget[] page : this.implementedBit.getBit().getBits()) {
            for (BitWidget widget : page) {
                widgetToValue.put(widget.getFlag(), widget.getValue(this.implementedBit.getRawComponent(widget)));
            }
        }
        object.add("widgets", gson.fromJson(gson.toJson(widgetToValue), JsonObject.class));
        object.add("bit", gson.fromJson(gson.toJson(this.implementedBit.getBit()), JsonObject.class));
        return gson.toJson(object);
    }

    public static BitSave fromSave(String string) {
        JsonObject object = gson.fromJson(string, JsonObject.class);
        Map<String, Object> widgetToValue = gson.fromJson(object.get("widgets"), new TypeToken<Map<String, Object>>() {}.getType());
        Bit bit = gson.fromJson(object.get("bit"), Bit.class);
        List<BitArgument> arguments = new ArrayList<>();
        for (String widgetJson : widgetToValue.keySet()) {
            arguments.add(new BitArgument(widgetJson, widgetToValue.get(widgetJson)));
        }
        return bit.get(arguments.toArray(new BitArgument[arguments.size()]));
    }
}
