package net.displayphoenix.blockly.event;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.elements.workspace.ImplementedBlock;
import net.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyInvolveEvent extends BlocklyEvent {

    private final ImplementedBlock[] blocks;

    public BlocklyInvolveEvent(String type, BlocklyPanel workspace, Block block, ImplementedBlock[] blocksInvolved) {
        super(type, workspace, block);
        this.blocks = blocksInvolved;
    }


    public ImplementedBlock[] getBlocksInvolved() {
        return blocks;
    }
}
