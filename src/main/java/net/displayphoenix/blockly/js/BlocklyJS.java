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
     * Returns en translations
     * @return
     */
    public static URI getLangEN() {
        try {
            return new BlocklyJS().getClass().getResource("en.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
