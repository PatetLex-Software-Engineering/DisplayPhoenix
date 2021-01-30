package com.patetlex.displayphoenix.blockly.event.events;

import com.patetlex.displayphoenix.blockly.elements.Block;
import com.patetlex.displayphoenix.blockly.event.BlocklyEvent;
import com.patetlex.displayphoenix.blockly.ui.BlocklyPanel;

/**
 * @author TBroski
 */
public class BlocklyChangeEvent extends BlocklyEvent {

    private final String element;
    private final String name;
    private final String oldVal;
    private final String newVal;

    public BlocklyChangeEvent(String type, BlocklyPanel workspace, Block block, String element, String name, String oldValue, String newValue) {
        super(type, workspace, block);
        this.element = element;
        this.name = name;
        this.oldVal = oldValue;
        this.newVal = newValue;
    }

    public String getElement() {
        return element;
    }

    public String getName() {
        return name;
    }

    public String getOldValue() {
        return oldVal;
    }

    public String getNewValue() {
        return newVal;
    }
}
