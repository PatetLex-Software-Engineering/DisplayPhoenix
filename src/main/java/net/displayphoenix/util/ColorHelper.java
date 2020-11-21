package net.displayphoenix.util;

import java.awt.*;

/**
 * @author TBroski
 */
public class ColorHelper {
    public static String convertColorToHexadeimal(Color color)
    {
        String hex = Integer.toHexString(color.getRGB() & 0xffffff);
        if(hex.length() < 6)
        {
            if(hex.length()==5)
                hex = "0" + hex;
            if(hex.length()==4)
                hex = "00" + hex;
            if(hex.length()==3)
                hex = "000" + hex;
        }
        hex = "#" + hex;
        return hex;
    }

    public static boolean isColorTolerated(Color key, Color input, float tolerance) {
        float t = tolerance / 100;
        if (input == key)
            return true;
        if (input == null)
            return t >= 1 ? true : false;
        if (key == null)
            return false;
        float r = input.getRed();
        float g = input.getGreen();
        float b = input.getBlue();
        float kr = key.getRed();
        float kg = key.getGreen();
        float kb = key.getBlue();
        float raR = r - kr;
        float raG = g - kg;
        float raB = b - kb;
        if (raR < 0)
            raR *= -1;
        if (raG < 0)
            raG *= -1;
        if (raB < 0)
            raB *= -1;
        return ((raR / 255F) <= t) && ((raG / 255F) <= t) && ((raB / 255F) <= t);
    }

    public static Color max(Color input) {
        float v = input.getRed() > input.getBlue() ? input.getRed() : input.getBlue() > input.getGreen() ? input.getBlue() : input.getGreen();
        float r = 255 / v;
        Color color = new Color(Math.round(input.getRed() * r), Math.round(input.getGreen() * r), Math.round(input.getBlue() * r));
/*        while (color.getRed() < 255 & color.getGreen() < 255 & color.getBlue() < 255) {
            int red = Math.round(color.getRed() * r);
            int green = Math.round(color.getGreen() * r);
            int blue = Math.round(color.getBlue() * r);
            red = Math.max(red, 255);
            green = Math.max(green, 255);
            blue = Math.max(blue, 255);
            color = new Color(red, green, blue);
        }*//*
        System.out.println(color);
        System.out.println(input);*/
        return color;
    }

    public static Color min(Color input) {
        Color color = input;
        while (color.getRed() > 0 & color.getGreen() > 0 & color.getBlue() > 0) {
            color = new Color(color.getRed() - 1, color.getGreen() - 1, color.getBlue() - 1);
        }
        return color;
    }
}
