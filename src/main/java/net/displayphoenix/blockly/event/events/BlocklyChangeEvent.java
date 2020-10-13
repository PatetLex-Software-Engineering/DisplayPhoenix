package net.displayphoenix.blockly.event.events;

import net.displayphoenix.blockly.elements.Block;
import net.displayphoenix.blockly.event.BlocklyEvent;
import net.displayphoenix.blockly.ui.BlocklyPanel;

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
