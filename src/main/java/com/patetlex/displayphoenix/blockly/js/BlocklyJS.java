package com.patetlex.displayphoenix.blockly.js;

import com.patetlex.displayphoenix.util.FileHelper;

import java.net.URI;
import java.net.URISyntaxException;

public class BlocklyJS {

    /**
     * Returns URI of blockly compressed
     * @return
     */
    protected static URI getBlocklyCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blockly_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBlocklyCompressedContent() {
        return FileHelper.readAllLines(getBlocklyCompressed());
    }

    /**
     * Returns URI of blocks compressed
     * @return
     */
    protected static URI getBlocksCompressed() {
        try {
            return new BlocklyJS().getClass().getResource("blocks_compressed.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBlocksCompressedContent() {
        return FileHelper.readAllLines(getBlocksCompressed());
    }

    /**
     * Returns en translations
     * @return
     */
    protected static URI getLangEN() {
        try {
            return new BlocklyJS().getClass().getResource("en.js").toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLangENContent() {
        return FileHelper.readAllLines(getLangEN());
    }
}
