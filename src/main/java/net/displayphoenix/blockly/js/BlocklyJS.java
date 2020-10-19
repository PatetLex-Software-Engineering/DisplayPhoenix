package net.displayphoenix.blockly.js;

import java.net.URI;
import java.net.URISyntaxException;

public class BlocklyJS {

    public static URI getBlocklyCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blockly_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URI getBlocksCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blocks_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static URI getBlocklyLang(String lang) {
        try {
            return new BlocklyJS().getClass().getResource("msg/" + lang + ".js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
