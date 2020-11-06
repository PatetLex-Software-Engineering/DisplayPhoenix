package net.displayphoenix.blockly.js;

import java.net.URI;
import java.net.URISyntaxException;

public class BlocklyJS {

    /**
     * Returns URI of blockly compressed
     * @return
     */
    public static URI getBlocklyCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blockly_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns URI of blocks compressed
     * @return
     */
    public static URI getBlocksCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blocks_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns URI of a blockly lang
     * @return
     */
    public static URI getBlocklyLang(String lang) {
        try {
            return new BlocklyJS().getClass().getResource("msg/" + lang + ".js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns URI of default block
     * @return
     */
    public static URI getDefaultBlock(String type) {
        try {
            return new BlocklyJS().getClass().getResource("blocks/" + type + ".json").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
