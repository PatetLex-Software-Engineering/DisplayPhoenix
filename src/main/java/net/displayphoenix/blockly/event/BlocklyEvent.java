package net.displayphoenix.blockly.event;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyEvent {

    private final String type;
    private final BlocklyPanel workspace;
    private final Block block;

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
