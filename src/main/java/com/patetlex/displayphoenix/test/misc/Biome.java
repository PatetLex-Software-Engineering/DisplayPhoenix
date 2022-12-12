package com.patetlex.displayphoenix.test.misc;

import com.patetlex.displayphoenix.test.obj.BlockObject;
import com.patetlex.displayphoenix.util.ColorHelper;
import org.joml.Vector3f;

import java.awt.*;
import java.util.Random;

public class Biome {

    private static final Random rand = new Random();

    public BlockObject getDefaultBlock() {
        BlockObject obj = new BlockObject(new Vector3f(0, 0, 0), rand.nextBoolean() ? "grass" : "stone");
        //obj.setReflectance(0.3F);
        return obj;
    }

    public Color getDayColor() {
        return Color.BLUE;
    }

    public Color getNightColor() {
        return Color.RED.darker().darker().darker().darker().darker();
    }

    public Vector3f getSunlightIntensity(float dayTime) {
        Color sunColor = ColorHelper.mixColors(ColorHelper.mixColors(this.getNightColor(), this.getDayColor(), dayTime), Color.WHITE, 0.35F);
        return new Vector3f((sunColor.getRed() / 255F) * 1.5F, (sunColor.getGreen() / 255F) * 1.5F, (sunColor.getBlue() / 255F) * 1.5F).mul(Math.max(dayTime, dayTime + this.minimumDarkness() * 2));
    }

    public float minimumDarkness() {
        return 0.35F;
    }

    public static Biome getBiome(long temperatureSeed, long humiditySeed, int x, int y) {
        return new Biome();
    }
}
