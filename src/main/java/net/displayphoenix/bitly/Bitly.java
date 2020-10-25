package net.displayphoenix.bitly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.displayphoenix.bitly.elements.Bit;
import net.displayphoenix.bitly.ui.BitWidget;
import net.displayphoenix.file.DetailedFile;
import net.displayphoenix.generation.Module;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Bitly {

    private static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();

    private static List<Bit> BITS = new ArrayList<>();

    public static void registerBit(Bit bit) {
        if (!BITS.contains(bit)) {
            BITS.add(bit);
        }
    }
    public static void registerBit(File bitFile) {
        try {
            JsonObject bitObject = gson.fromJson(new FileReader(bitFile), JsonObject.class);
            Bit bit = new Bit(new DetailedFile(bitFile).getFileName(), gson.fromJson(bitObject.get("widgets").toString(), new TypeToken<List<BitWidget[]>>(){}.getType()));
            registerBit(bit);
            Map<String, String> moduleToCode = gson.fromJson(bitObject.get("code").toString(), new TypeToken<Map<String, String>>(){}.getType());
            for (String module : moduleToCode.keySet()) {
                Module.getModuleFromName(module).registerBitCode(bit, moduleToCode.get(module));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Bit getBitFromType(String type) {
        for (Bit bit : BITS) {
            if (bit.getType().equalsIgnoreCase(type)) {
                return bit;
            }
        }
        return null;
    }

    public static List<Bit> getRegisteredBits() {
        return Collections.unmodifiableList(BITS);
    }
}
