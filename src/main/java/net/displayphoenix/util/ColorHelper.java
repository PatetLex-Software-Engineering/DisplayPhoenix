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
        return color;
    }

    public static Color min(Color input) {
        float v = input.getRed() < input.getBlue() ? input.getRed() : input.getBlue() < input.getGreen() ? input.getBlue() : input.getGreen();
        float r = v / 255;
        Color color = new Color(Math.round(input.getRed() * r), Math.round(input.getGreen() * r), Math.round(input.getBlue() * r));
        return color;
    }

    public static float getColorDifference(Color color1, Color color2) {
        float r1 = color1.getRed() > color2.getRed() ? color1.getRed() : color2.getRed();
        float r2 = color1.getRed() < color2.getRed() ? color1.getRed() : color2.getRed();
        float g1 = color1.getGreen() > color2.getGreen() ? color1.getGreen() : color2.getGreen();
        float g2 = color1.getGreen() < color2.getGreen() ? color1.getGreen() : color2.getGreen();
        float b1 = color1.getBlue() > color2.getBlue() ? color1.getBlue() : color2.getBlue();
        float b2 = color1.getBlue() < color2.getBlue() ? color1.getBlue() : color2.getBlue();
        float r = (r1 / r2) - 1F;
        float g = (g1 / g2) - 1F;
        float b = (b1 / b2) - 1F;
        if (r > g && r > b) {
            return r;
        }
        if (g > r && g > b) {
            return g;
        }
        return b;
    }
}
