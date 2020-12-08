package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyDeleteEvent extends BlocklyEvent {
    public BlocklyDeleteEvent(String type, BlocklyPanel workspace, Block block) {
        super(type, workspace, block);
    }
}
