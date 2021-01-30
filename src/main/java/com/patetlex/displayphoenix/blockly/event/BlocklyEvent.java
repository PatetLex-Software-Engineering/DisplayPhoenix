package com.patetlex.displayphoenix.blockly.event;

import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyEvent {

    private final String type;
    private final BlocklyPanel workspace;
    private final Block block;

    /**
     * Base block event
     *
     * @param type  Type of event
     * @param workspace  Workspace of event
     * @param block  Block of event
     */
    public BlocklyEvent(String type, BlocklyPanel workspace, Block block) {
        this.type = type;
        this.workspace = workspace;
        this.block = block;
    }

    public String getType() {
        return type;
    }

    public Block getBlock() {
        return block;
    }

    public BlocklyPanel getWorkspace() {
        return this.workspace;
    }
}
