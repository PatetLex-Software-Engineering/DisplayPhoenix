package net.displayphoenix.blockly.gen;

import net.displayphoenix.blockly.js.BlocklyJS;

/**
 * @author TBroski
 */
public interface BlocklyHtmlGenerator {

    /**
     * Appends top part of html
     *
     * @param builder  StringBuilder to append
     */
    static void appendTopWrapper(StringBuilder builder) {
        appendTopWrapper(builder, "");
    }

    /**
     * Appends top part of html with css styling
     *
     * @param builder  StringBuilder to append
     * @param css  Css style
     */
    static void appendTopWrapper(StringBuilder builder, String css) {
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Blockly</title>\n" +
                "    <script>" + BlocklyJS.getBlocklyCompressedContent() + "</script>\n" +
                "    <script>" + BlocklyJS.getBlocksCompressedContent() + "</script>\n" +
                "    <script>" + BlocklyJS.getLangENContent() + "</script>\n" +
                "    <style>\n" +
                css + "\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id=\"blocklyDiv\" style=\"height: @HEIGHTpx; width: @WIDTHpx;\"></div>\n" +
                "\n" +
                "<xml id=\"toolbox\" style=\"display: none\">\n");
    }

    /**
     * Appends bottom part of html
     *
     * @param builder  StringBuilder to append
     * @param blocksArray  Blocks to define
     */
    static void appendBottomWrapper(StringBuilder builder, String blocksArray) {
        builder.append("</xml>\n" +
                "\n" +
                "<script>\n" +
                "    var workspace = Blockly.inject('blocklyDiv',\n" +
                "        {media: '../../media/',\n" +
                "         toolbox: document.getElementById('toolbox')," +
                "         comments : false," +
                "         renderer : 'thrasos'," +
                "         collapse : true," +
                "         trashcan : false," +
                "});\n" +
                "Blockly.defineBlocksWithJsonArray(\n");
        builder.append(blocksArray);
        builder.append(");" +
                "  </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}
