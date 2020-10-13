package net.displayphoenix.blockly;

public interface BlocklyHtmlGenerator {
    static void appendTopWrapper(StringBuilder builder) {
        builder.append("<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>Blockly</title>\n" +
                "    <script src=\"jar:file:./libs/blockly.jar!/jsdist/blockly_compressed.js\"></script>\n" + //https://blockly-demo.appspot.com/static/
                "    <script src=\"jar:file:./libs/blockly.jar!/jsdist/blocks_compressed.js\"></script>\n" + //jar:file:./libs/blockly.jar!/jsdist/
                "    <script src=\"jar:file:./libs/blockly.jar!/jsdist/msg/en.js\"></script>\n" +
                "    <style>\n" +
                "    body {\n" +
                "      background-color: #fff;\n" +
                "      font-family: sans-serif;\n" +
                "    }\n" +
                "    h1 {\n" +
                "      font-weight: normal;\n" +
                "      font-size: 140%;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "<div id=\"blocklyDiv\" style=\"height: @HEIGHTpx; width: @WIDTHpx;\"></div>\n" +
                "\n" +
                "<xml id=\"toolbox\" style=\"display: none\">\n");
    }

    static void appendBottomWrapper(StringBuilder builder, String blocksArray) { //480, 600
        builder.append("</xml>\n" +
                "\n" +
                "<script>\n" +
                "    var workspace = Blockly.inject('blocklyDiv',\n" +
                "        {media: '../../media/',\n" +
                "         toolbox: document.getElementById('toolbox')});\n" +
                "Blockly.defineBlocksWithJsonArray(\n");
        builder.append(blocksArray);
        builder.append(");\n" +
                "  </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>");
    }
}
