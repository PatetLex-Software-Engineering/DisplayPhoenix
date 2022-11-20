package com.patetlex.displayphoenix.test.misc;

import com.patetlex.displayphoenix.test.obj.BlockObject;
import org.joml.Vector3f;

import java.awt.*;

public class Biome {

    public BlockObject getDefaultBlock() {
        BlockObject obj = new BlockObject();
        //obj.setReflectance(0.3F);
        return obj;
    }

    public Color getDayColor() {
        return Color.BLUE;
    }

    public Color getNightColor() {
        return Color.RED.darker().darker().darker().darker().darker();
    }

    public Vector3f getSunlightIntensity() {
        return new Vector3f(2.5F, 1.5F, 1.5F);
    }

    public float minimumDarkness() {
        return 0.35F;
    }

    public static Biome getBiome(long temperatureSeed, long humiditySeed, int x, int y) {
        return new Biome();
    }
}
