package net.displayphoenix.blockly.event.events;

import com.sun.javafx.geom.Vec2d;
import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

public class BlocklyMoveEvent extends BlocklyEvent {

    private final Vec2d oldPoint;
    private final Vec2d newPoint;

    public BlocklyMoveEvent(String type, BlocklyPanel workspace, Block block, int oldX, int oldY, int newX, int newY) {
        super(type, workspace, block);
        this.oldPoint = new Vec2d(oldX, oldY);
        this.newPoint = new Vec2d(newX, newY);
    }

    public Vec2d getOldCoords() {
        return oldPoint;
    }

    public Vec2d getNewCoords() {
        return newPoint;
    }
}
