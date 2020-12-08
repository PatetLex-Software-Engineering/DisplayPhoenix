package net.displayphoenix.blockly.gen;

import net.displayphoenix.blockly.js.BlocklyJS;

/**
 * @author TBroski
 */
public interface BlocklyHtmlGenerator {

    /**
     * Appends top part of html with css styling
     *
     * @param builder  StringBuilder to append
     */
    default void appendTopWrapper(StringBuilder builder) {
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Blockly</title>\n" +
                "    <script>window.onerror = function(message, url, lineNumber) {return true};</script>\n" +
                "    <script>" + BlocklyJS.getBlocklyCompressedContent() + "</script>\n" +
                "    <script>" + BlocklyJS.getBlocksCompressedContent() + "</script>\n" +
                "    <script>" + BlocklyJS.getLangENContent() + "</script>\n" +
                "    <style>\n" +
                getCss() + "\n" +
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
    default void appendBottomWrapper(StringBuilder builder, String blocksArray) {
        builder.append("</xml>\n" +
                "\n" +
                "<script>\n" +
                "    var workspace = Blockly.inject('blocklyDiv',\n" +
                "        {media: '',\n" +
                "         toolbox: document.getElementById('toolbox')," +
                "         comments : false," +
                "         renderer : 'thrasos'," +
                "         collapse : true," +
                "         trashcan : false," +
                "         zoom: {" +
                "          startScale: " + getScale() + "," +
                "         }," +
                "});\n" +
                "Blockly.defineBlocksWithJsonArray(\n");
        builder.append(blocksArray);
        builder.append(");" +
                "  </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }

    void setScale(double scale);

    double getScale();

    String getCss();
}
