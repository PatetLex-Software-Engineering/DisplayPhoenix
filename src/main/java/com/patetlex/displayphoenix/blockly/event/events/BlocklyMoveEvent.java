package com.patetlex.displayphoenix.blockly.event.events;

import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.event.BlocklyEvent;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;

import java.awt.*;

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
