package com.patetlex.displayphoenix.maps.js;

import com.patetlex.displayphoenix.blockly.js.BlocklyJS;
import com.patetlex.displayphoenix.util.FileHelper;

import java.net.URI;
import java.net.URISyntaxException;

public class TomTomJS {

    /**
     * Returns URI of TomTom compressed
     * @return
     */
    protected static URI getTomTomCompressed() {
        try {
            return new TomTomJS().getClass().getResource("tomtom_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTomTomCompressedContent() {
        return FileHelper.readAllLines(getTomTomCompressed());
    }

    /**
     * Returns URI of TomTom styling
     * @return
     */
    protected static URI getTomTomStyling() {
        try {
            return new TomTomJS().getClass().getResource("tomtom_styling.css").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getTomTomStylingContent() {
        return FileHelper.readAllLines(getTomTomStyling());
    }
}
