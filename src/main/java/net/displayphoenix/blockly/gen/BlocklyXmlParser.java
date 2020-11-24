package net.displayphoenix.blockly.gen;

import net.displayphoenix.blockly.Blockly;
import net.displayphoenix.blockly.elements.workspace.Field;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.elements.workspace.Mutation;
import net.displayphoenix.blockly.ui.BlocklyPanel;
import net.displayphoenix.util.DOMHelper;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * @author TBroski
 */
public interface BlocklyXmlParser {

    /**
     * Parses an xml string to java objects
     *
     * @see ImplementedBlock
     * @see BlocklyPanel#getRawWorkspace()
     *
     * @param xml  Xml to parse
     * @return
     */
    static ImplementedBlock[] fromWorkspaceXml(String xml) {
        try {
            // Building document
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document xmlDoc = documentBuilder.parse(is);
            xmlDoc.getDocumentElement().normalize();

            List<ImplementedBlock> implementedBlocks = new ArrayList<>();

            // Get all blocks
            NodeList blocks = xmlDoc.getElementsByTagName("block");
            for (int i = 0; i < blocks.getLength(); i++) {
                Node block = blocks.item(i);

                // Parse node to ImplementBlock object
                ImplementedBlock implementedBlock = getImplementedBlock(block);

                // Checking if implement block is not a inner block
                if (implementedBlock.getX() != ImplementedBlock.INNER_BLOCK) {
                    // Adding the block
                    implementedBlocks.add(implementedBlock);
                }
            }

            // Converting blocks to array
            ImplementedBlock[] array = new ImplementedBlock[implementedBlocks.size()];
            array = implementedBlocks.toArray(array);
            return array;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Parses blocks to an xml string
     *
     * @see BlocklyPanel#addBlocks(String)
     *
     * @param blocks  Blocks to parse
     * @return
     */
    static String parseWorkspaceXml(ImplementedBlock... blocks) {
        // Create StringBuilder
        StringBuilder xmlBuilder = new StringBuilder();

        // Append base
        xmlBuilder.append("<xml xmlns=\"https://developers.google.com/blockly/xml\">");

        // Iterate each block
        int i = 0;
        for (ImplementedBlock block : blocks) {
            // State if next block
            if (i > 0) {
                xmlBuilder.append("<next>");
            }

            // Append block
            xmlBuilder.append(parseImplementedBlock(block));

            // Close if nect block
            if (i > 0) {
                xmlBuilder.append("</next>");
            }
            i++;
        }

        // Close
        xmlBuilder.append("</xml>");
        return xmlBuilder.toString();
    }

    /**
     * Obtains information from Node object and converts to ImplementedBlock object
     *
     * @param block  Node of block
     *
     * @return
     */
    static ImplementedBlock getImplementedBlock(Node block) {

        // Get block type
        String type = block.getAttributes().item(0).getTextContent();

        // Get x and y
        int x = ImplementedBlock.INNER_BLOCK;
        int y = ImplementedBlock.INNER_BLOCK;
        boolean deletable = true;
        boolean movable = true;

        // Iterating each node attribute
        for (int ia = 0; ia < block.getAttributes().getLength(); ia++) {
            Node attribute = block.getAttributes().item(ia);

            // Setting x if found
            if (attribute.getNodeName().equalsIgnoreCase("x")) {
                x = Integer.parseInt(attribute.getTextContent());
            }

            // Setting y if found
            else if (attribute.getNodeName().equalsIgnoreCase("y")) {
                y = Integer.parseInt(attribute.getTextContent());
            }

            // Setting type if found
            else if (attribute.getNodeName().equalsIgnoreCase("type")) {
                type = attribute.getTextContent();
            }

            // Setting deletable if found
            else if (attribute.getNodeName().equalsIgnoreCase("deletable")) {
                deletable = Boolean.parseBoolean(attribute.getTextContent());
            }

            // Setting movable if found
            else if (attribute.getNodeName().equalsIgnoreCase("movable")) {
                movable = Boolean.parseBoolean(attribute.getTextContent());
            }
        }

        // Checking if next block
        if (block.getParentNode() != null && block.getParentNode().getNodeName().equalsIgnoreCase("next")) {
            x = ImplementedBlock.NEXT_BLOCK;
            y = ImplementedBlock.NEXT_BLOCK;
        }

        // Get fields, statements, and values if applicable
        List<Field> fields = new ArrayList<>();
        Map<String, List<ImplementedBlock>> innerBlockMap = new HashMap<>();
        Map<String, List<ImplementedBlock>> valueBlockMap = new HashMap<>();
        List<Mutation> mutations = new ArrayList<>();
        if (block.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) block;

            // Searching for fields
            NodeList fieldList = element.getElementsByTagName("field");
            for (int ifi = 0; ifi < fieldList.getLength(); ifi++) {
                Node field = fieldList.item(ifi);
                if (DOMHelper.getNodesBetween(block, field).size() == 0) {
                    fields.add(new Field(field.getAttributes().item(0).getTextContent(), field.getTextContent()));
                }
            }

            // Searching for statement blocks
            NodeList statementList = element.getElementsByTagName("statement");
            for (int ist = 0; ist < statementList.getLength(); ist++) {
                Node statementNode = statementList.item(ist);

                // Checking if statement is in reach of block
                if (DOMHelper.getNodesBetween(block, statementNode).size() == 0 && statementNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element statement = (Element) statementNode;
                    NodeList blockList = statement.getElementsByTagName("block");
                    for (int ibl = 0; ibl < blockList.getLength(); ibl++) {
                        Node blockNode = blockList.item(ibl);
                        List<Node> nodesBetween = DOMHelper.getNodesBetween(block, blockNode);
                        boolean flag = false;
                        if (blockNode.getParentNode().getNodeName().equalsIgnoreCase("next")) {
                            Node blockBlockNode = DOMHelper.getNearestParentNodeByType("block", blockNode.getParentNode());
                            while (blockBlockNode != null && blockBlockNode.getParentNode().getNodeName().equalsIgnoreCase("next")) {
                                blockBlockNode = DOMHelper.getNearestParentNodeByType("block", blockBlockNode.getParentNode());
                            }
                            if (blockBlockNode.getParentNode().getParentNode() == block) {
                                flag = true;
                            }
                        }
                        if (nodesBetween.size() == 1 || flag) {
                            if (!innerBlockMap.containsKey(statementNode.getAttributes().item(0).getTextContent())) {
                                innerBlockMap.put(statementNode.getAttributes().item(0).getTextContent(), new ArrayList<>());
                            }
                            ImplementedBlock statementBlock = getImplementedBlock(blockNode);
                            innerBlockMap.get(statementNode.getAttributes().item(0).getTextContent()).add(statementBlock);
                        }
                    }
                }
            }

            // Searching for values
            NodeList valueList = element.getElementsByTagName("value");
            for (int ist = 0; ist < valueList.getLength(); ist++) {
                Node valueNode = valueList.item(ist);
                if (DOMHelper.getNodesBetween(block, valueNode).size() == 0 && valueNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element statement = (Element) valueNode;
                    NodeList blockList = statement.getElementsByTagName("block");
                    for (int ibl = 0; ibl < blockList.getLength(); ibl++) {
                        Node blockNode = blockList.item(ibl);
                        if (DOMHelper.getNodesBetween(block, blockNode).size() == 1) {
                            if (!valueBlockMap.containsKey(valueNode.getAttributes().item(0).getTextContent())) {
                                valueBlockMap.put(valueNode.getAttributes().item(0).getTextContent(), new ArrayList<>());
                            }
                            ImplementedBlock valueBlock = getImplementedBlock(blockNode);
                            valueBlockMap.get(valueNode.getAttributes().item(0).getTextContent()).add(valueBlock);
                        }
                    }
                }
            }

            // Searching for mutations
            NodeList mutationList = element.getElementsByTagName("mutation");
            for (int im = 0; im < mutationList.getLength(); im++) {
                Node mutationNode = mutationList.item(im);
                if (DOMHelper.getNodesBetween(block, mutationNode).size() == 0) {
                    NamedNodeMap mutationNodeAttributes = mutationNode.getAttributes();
                    for (int ima = 0; ima < mutationNodeAttributes.getLength(); ima++) {
                        mutations.add(new Mutation(mutationNodeAttributes.item(ima).getNodeName(), Integer.parseInt(mutationNodeAttributes.item(ima).getTextContent())));
                    }
                }
            }
        }
        Field[] fieldsArray = new Field[fields.size()];
        fieldsArray = fields.toArray(fieldsArray);

        ImplementedBlock validBlock = new ImplementedBlock(Blockly.getBlockFromType(type), x, y, deletable, movable, fieldsArray);
        for (String key : innerBlockMap.keySet()) {
            for (ImplementedBlock innerBlock : innerBlockMap.get(key)) {
                validBlock.addStatementBlock(key, innerBlock);
            }
        }
        for (String key : valueBlockMap.keySet()) {
            for (ImplementedBlock innerBlock : valueBlockMap.get(key)) {
                validBlock.addValueBlock(key, innerBlock);
            }
        }
        Collections.reverse(mutations);
        for (Mutation mutation : mutations) {
            validBlock.addMutation(mutation);
        }
        return validBlock;
    }

    /**
     * Parses block to xml string
     *
     * @param block  Block to parse
     *
     * @return
     */
    static String parseImplementedBlock(ImplementedBlock block) {
        StringBuilder blockBuilder = new StringBuilder();

        // Appending top wrapper
        if (block.getX() >= 0) {
            blockBuilder.append("<block type=\"" + block.getBlock().getType() + "\" deletable=\"" + (block.getBlock().doesPersist() ? false : block.isDeletable()) + "\" movable=\"" + block.isMovable() + "\" x=\"" + block.getX() + "\" y=\"" + block.getY() + "\">");
        }
        else {
            blockBuilder.append("<block type=\"" + block.getBlock().getType() + "\">");
        }

        // Appending mutations
        if (block.getMutations().size() > 0) {
            String mutations = "<mutation";
            for (Mutation mutation : block.getMutations()) {
                mutations += " " + mutation.getKey() + "\"" + mutation.getAmount() + "\"";
            }
            mutations += "></mutation>";
            blockBuilder.append(mutations);
        }

        // Appending fields
        for (Field field : block.getFields()) {
            blockBuilder.append("<field name=\"" + field.getKey() + "\">" + field.getValue() + "</field>");
        }

        // Appending statements
        for (String statement : block.getStatementBlocks().keySet()) {
            blockBuilder.append("<statement name=\"" + statement + "\">");
            for (ImplementedBlock statementBlock : block.getStatementBlocks().get(statement)) {
                blockBuilder.append(parseImplementedBlock(statementBlock));
            }
            blockBuilder.append("</statement>");
        }

        // Appending values
        for (String value : block.getValueBlocks().keySet()) {
            blockBuilder.append("<value name=\"" + value + "\">");
            for (ImplementedBlock valueBlock : block.getValueBlocks().get(value)) {
                blockBuilder.append(parseImplementedBlock(valueBlock));
            }
            blockBuilder.append("</value>");
        }

        //Appending bottom wrapper
        blockBuilder.append("</block>");
        return blockBuilder.toString();
    }
}
