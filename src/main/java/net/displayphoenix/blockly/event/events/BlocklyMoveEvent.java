package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Vector;

/**
 * @author TBroski
 */
public class BlocklyMoveEvent extends BlocklyEvent {

    private final Point oldPoint;
    private final Point newPoint;

    public BlocklyMoveEvent(String type, BlocklyPanel workspace, Block block, int oldX, int oldY, int newX, int newY) {
        super(type, workspace, block);
        this.oldPoint = new Point(oldX, oldY);
        this.newPoint = new Point(newX, newY);
    }

    public Point getOldCoords() {
        return oldPoint;
    }

    public Point getNewCoords() {
        return newPoint;
    }
}
