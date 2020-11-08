package net.displayphoenix.image.tools;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FontSetting extends Setting {

    private Font[] fonts;

    public FontSetting(String translationKey) {
        super(translationKey);
        List<Font> fontList = new ArrayList<>();
        for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
            fontList.add(font);
        }
        this.fonts = fontList.toArray(new Font[fontList.size()]);
    }
    public FontSetting(String translationKey, Font[] fonts) {
        super(translationKey);
        this.fonts = fonts;
    }

    public Font[] getFonts() {
        return fonts;
    }
}
