package net.displayphoenix.blockly.event;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyInvolveEvent extends BlocklyEvent {

    private final ImplementedBlock[] blocks;

    /**
     * Blockly event with multiple blocks
     *
     * @see BlocklyEvent
     *
     * @param type  Type of event
     * @param workspace  Workspace of event
     * @param block  Block of event
     * @param blocksInvolved  All blocks involved
     */
    public BlocklyInvolveEvent(String type, BlocklyPanel workspace, Block block, ImplementedBlock[] blocksInvolved) {
        super(type, workspace, block);
        this.blocks = blocksInvolved;
    }


    public ImplementedBlock[] getBlocksInvolved() {
        return blocks;
    }
}
