package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

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
