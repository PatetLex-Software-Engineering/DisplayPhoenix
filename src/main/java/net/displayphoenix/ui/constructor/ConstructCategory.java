package net.displayphoenix.ui.constructor;

import java.awt.*;

public class ConstructCategory extends ConstructElement {

    private ConstructBlock[] blocks;

    public ConstructCategory(String name, Color color, ConstructBlock... blocks) {
        super(name, color);
        this.blocks = blocks;
    }

    public ConstructBlock[] getBlocks() {
        return blocks;
    }
}
