package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyCreateEvent extends BlocklyEvent {
    public BlocklyCreateEvent(String type, BlocklyPanel workspace, Block block) {
        super(type, workspace, block);
    }
}
