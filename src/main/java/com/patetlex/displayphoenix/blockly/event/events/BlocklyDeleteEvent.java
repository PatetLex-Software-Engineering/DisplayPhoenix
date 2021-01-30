package com.patetlex.displayphoenix.blockly.event.events;

import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.event.BlocklyEvent;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyDeleteEvent extends BlocklyEvent {
    public BlocklyDeleteEvent(String type, BlocklyPanel workspace, Block block) {
        super(type, workspace, block);
    }
}
