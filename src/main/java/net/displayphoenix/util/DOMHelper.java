package net.displayphoenix.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TBroski
 */
public class DOMHelper {

    public static List<Node> getNodesBetween(Node startNode, Node endNode) {
        List<Node> nodes = new ArrayList<>();
        if (startNode == endNode)
            return nodes;
        NodeList childs = startNode.getChildNodes();
        for (int i = 0; i < childs.getLength(); i++) {
            Node child = childs.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child == endNode) {
                    return nodes;
                }
                else {
                    List<Node> childNodes = getNodesBetween(child, endNode);
                    if (childNodes != null) {
                        childNodes.add(startNode);
                        return childNodes;
                    }
                }
            }
        }
        return null;
    }

    public static Node getNearestParentNodeByType(String type, Node node) {
        Node finalNode = node;
        while (!finalNode.getNodeName().equalsIgnoreCase(type)) {
            if (finalNode.getParentNode() == null)
                return null;
            finalNode = finalNode.getParentNode();
        }
        return finalNode;
    }
}
