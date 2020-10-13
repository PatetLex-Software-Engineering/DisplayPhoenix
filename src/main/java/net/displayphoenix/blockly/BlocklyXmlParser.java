package net.displayphoenix.blockly;

import net.displayphoenix.blockly.elements.workspace.Field;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.elements.workspace.Mutation;
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

public interface BlocklyXmlParser {
    static ImplementedBlock[] fromWorkspaceXml(String xml) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            Document xmlDoc = documentBuilder.parse(is);
            xmlDoc.getDocumentElement().normalize();

            List<ImplementedBlock> implementedBlocks = new ArrayList<>();
            NodeList blocks = xmlDoc.getElementsByTagName("block");
            for (int i = 0; i < blocks.getLength(); i++) {
                Node block = blocks.item(i);
                ImplementedBlock implementedBlock = getImplementedBlock(block);
                if (implementedBlock.getX() != ImplementedBlock.INNER_BLOCK) {
                    implementedBlocks.add(implementedBlock);
                    //printBlock("", implementedBlock);
                }
            }

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

    static String parseWorkspaceXml(ImplementedBlock... blocks) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<xml xmlns=\"https://developers.google.com/blockly/xml\">");
        int i = 0;
        for (ImplementedBlock block : blocks) {
            if (i > 0) {
                xmlBuilder.append("<next>");
            }
            xmlBuilder.append(parseImplementedBlock(block));
            if (i > 0) {
                xmlBuilder.append("</next>");
            }
            i++;
        }
        xmlBuilder.append("</xml>");
        return xmlBuilder.toString();
    }

    static ImplementedBlock getImplementedBlock(Node block) {
        String type = block.getAttributes().item(0).getTextContent();
        int x = ImplementedBlock.INNER_BLOCK;
        int y = ImplementedBlock.INNER_BLOCK;
        for (int ia = 0; ia < block.getAttributes().getLength(); ia++) {
            Node attribute = block.getAttributes().item(ia);
            if (attribute.getNodeName().equalsIgnoreCase("x")) {
                x = Integer.parseInt(attribute.getTextContent());
            }
            else if (attribute.getNodeName().equalsIgnoreCase("y")) {
                y = Integer.parseInt(attribute.getTextContent());
            }
            else if (attribute.getNodeName().equalsIgnoreCase("type")) {
                type = attribute.getTextContent();
            }
        }
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

        ImplementedBlock validBlock = new ImplementedBlock(Blockly.getBlockFromType(type), x, y, fieldsArray);
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

    static String parseImplementedBlock(ImplementedBlock block) {
        StringBuilder blockBuilder = new StringBuilder();

        // Appending top wrapper
        if (block.getX() >= 0) {
            blockBuilder.append("<block type=\"" + block.getBlock().getType() + "\" deletable=\"" + !block.getBlock().doesPersist() + "\" x=\"" + block.getX() + "\" y=\"" + block.getY() + "\">");
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

    static void printBlock(String pre, ImplementedBlock implementedBlock) {
        if (implementedBlock.getX() == ImplementedBlock.NEXT_BLOCK) {
            System.out.println(pre + "-- Next Block");
        }
        System.out.println(pre + "[BLOCK OPEN]");
        if (implementedBlock.getX() == ImplementedBlock.INNER_BLOCK) {
            System.out.println(pre + "-- Inner Block");
        }
        System.out.println(pre + "Type: " + implementedBlock.getBlock().getType());
        if (implementedBlock.getX()  >= 0) {
            System.out.println(pre + "X: " + implementedBlock.getX());
            System.out.println(pre + "Y: " + implementedBlock.getY());
        }
        for (Field field : implementedBlock.getFields()) {
            System.out.println(pre + "Block Field ID: " + field.getKey() + " === " + field.getValue());
        }
        for (String statement : implementedBlock.getValueBlocks().keySet()) {
            System.out.println(pre + "Value ID: " + statement + ". Open");
            for (ImplementedBlock innerBlock : implementedBlock.getValueBlocks().get(statement)) {
                printBlock(pre + "\t", innerBlock);
            }
            System.out.println(pre + "Value ID: " + statement + ". Close");
        }
        for (String statement : implementedBlock.getStatementBlocks().keySet()) {
            System.out.println(pre + "Statement ID: " + statement + ". Open");
            for (ImplementedBlock innerBlock : implementedBlock.getStatementBlocks().get(statement)) {
                printBlock(pre + "\t", innerBlock);
            }
            System.out.println(pre + "Statement ID: " + statement + ". Close");
        }
        System.out.println(pre + "[BLOCK CLOSED]");
    }
}
