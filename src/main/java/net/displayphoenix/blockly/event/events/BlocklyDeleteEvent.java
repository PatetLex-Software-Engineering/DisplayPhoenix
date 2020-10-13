package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.event.BlocklyInvolveEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

public class BlocklyDeleteEvent extends BlocklyInvolveEvent {
    public BlocklyDeleteEvent(String type, BlocklyPanel workspace, Block block, ImplementedBlock[] blocksInvolved) {
        super(type, workspace, block, blocksInvolved);
    }
}
