package com.patetlex.displayphoenix.blockly.event.events;

import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.event.BlocklyEvent;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyUIEvent extends BlocklyEvent {

    private final String element;
    private final String oldValue;
    private final String newValue;

    public BlocklyUIEvent(String type, BlocklyPanel workspace, Block block, String element, String oldValue, String newValue) {
        super(type, workspace, block);
        this.element = element;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getElement() {
        return element;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }
}
